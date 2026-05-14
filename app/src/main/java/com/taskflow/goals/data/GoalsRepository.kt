package com.taskflow.goals.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.taskflow.goals.model.Goal
import com.taskflow.goals.model.Proof
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Metas: leitura em tempo real e reconciliação de falhas.
 * Criação de meta fica em [com.taskflow.goals.viewmodel.GoalViewModel].
 */
class GoalsRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) {

    private fun currentUid(): String =
        auth.currentUser?.uid ?: error("Usuário não autenticado")

    suspend fun ensureUserDocument() {
        val user = auth.currentUser ?: return
        val data = hashMapOf<String, Any>(
            "email" to (user.email ?: ""),
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
        )
        db.collection("users").document(user.uid).set(data, SetOptions.merge()).await()
    }

    fun goalsFlow(): Flow<List<Goal>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration: ListenerRegistration = db.collection("goals")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener
                val goals = snapshot.documents.mapNotNull { it.toGoalOrNull() }
                    .sortedByDescending { it.createdAt }
                trySend(goals)
            }
        awaitClose { registration.remove() }
    }

    suspend fun reconcileFailures(goals: List<Goal>) {
        for (g in goals) {
            if (!g.status.equals("active", ignoreCase = true)) continue
            if (!DeadlineHelper.isPastDeadline(g.nextDeadline)) continue
            db.collection("goals").document(g.id)
                .update("status", "failed")
                .await()
        }
    }

    fun goalFlow(goalId: String): Flow<Goal?> = callbackFlow {
        if (goalId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val reg = db.collection("goals").document(goalId)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                trySend(snap?.toGoalOrNull())
            }
        awaitClose { reg.remove() }
    }

    fun proofsFlow(goalId: String): Flow<List<Proof>> = callbackFlow {
        if (goalId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val reg = db.collection("proofs")
            .whereEqualTo("goalId", goalId)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { it.toProof() }.orEmpty()
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun submitProof(goalId: String, imageUri: Uri, note: String) {
        val uid = currentUid()
        val goalRef = db.collection("goals").document(goalId)
        val goalSnap = goalRef.get().await()
        val goal = goalSnap.toGoalOrNull() ?: error("Meta não encontrada.")
        if (goal.userId != uid) error("Acesso negado.")
        if (!goal.status.equals("active", ignoreCase = true)) error("Meta não está ativa.")
        if (DeadlineHelper.isPastDeadline(goal.nextDeadline)) {
            error("Prazo expirado.")
        }

        val proofUuid = UUID.randomUUID().toString()
        val fileName = "$proofUuid.jpg"
        val storageRef = storage.reference
            .child("users")
            .child(uid)
            .child("goals")
            .child(goalId)
            .child("proofs")
            .child(fileName)

        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()

        val proofRef = db.collection("proofs").document()
        val batch = db.batch()
        batch.set(
            proofRef,
            hashMapOf(
                "goalId" to goalId,
                "userId" to uid,
                "imageUrl" to downloadUrl,
                "note" to note.trim(),
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "approved" to true,
            ),
        )

        val newNext = DeadlineHelper.advanceAfterProof(
            currentNextMillis = goal.nextDeadline,
            frequency = goal.frequency,
            deadlineHour = goal.deadlineHour,
        )
        val nowMs = System.currentTimeMillis()

        batch.update(
            goalRef,
            mapOf(
                "streak" to goal.streak + 1,
                "lastProofAt" to nowMs,
                "nextDeadline" to newNext,
            ),
        )
        batch.commit().await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toProof(): Proof? {
        val gid = getString("goalId") ?: return null
        val uid = getString("userId") ?: return null
        val url = getString("imageUrl") ?: return null
        val n = getString("note") ?: ""
        return Proof(
            id = id,
            goalId = gid,
            userId = uid,
            imageUrl = url,
            note = n,
            createdAt = getTimestamp("createdAt"),
            approved = getBoolean("approved") ?: true,
        )
    }
}
