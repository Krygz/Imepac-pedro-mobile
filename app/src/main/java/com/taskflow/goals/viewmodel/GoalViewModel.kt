package com.taskflow.goals.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.taskflow.goals.data.GoalsRepository
import com.taskflow.goals.data.computeNextDeadlineMillis
import com.taskflow.goals.data.toGoalOrNull
import com.taskflow.goals.model.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "GoalViewModel"

/**
 * Estado único da lista + criação de metas (Firestore em tempo real).
 */
class GoalViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val goalsRepository: GoalsRepository = GoalsRepository(),
) : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _goalCreated = MutableStateFlow(false)
    val goalCreated: StateFlow<Boolean> = _goalCreated.asStateFlow()

    private var goalsListener: ListenerRegistration? = null

    init {
        observeGoals()
        viewModelScope.launch {
            runCatching { goalsRepository.ensureUserDocument() }
                .onFailure { e ->
                    Log.e(TAG, "ensureUserDocument failed", e)
                }
        }
    }

    fun observeGoals() {
        goalsListener?.remove()
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e(TAG, "observeGoals: sem usuário logado")
            _errorMessage.value = "Sessão expirada. Faça login novamente."
            _goals.value = emptyList()
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        goalsListener = db.collection("goals")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "observeGoals listener error", e)
                    _errorMessage.value = e.localizedMessage ?: "Erro ao carregar metas."
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener

                val list = snapshot.documents.mapNotNull { doc -> doc.toGoalOrNull() }
                    .sortedByDescending { it.createdAt }
                _goals.value = list
                _isLoading.value = false
                _errorMessage.value = null

                viewModelScope.launch {
                    runCatching { goalsRepository.reconcileFailures(list) }
                        .onFailure { err -> Log.e(TAG, "reconcileFailures failed", err) }
                }
            }
    }

    fun createGoal(
        title: String,
        description: String,
        penaltyAmount: Double,
        consequence: String,
        deadlineHour: Int,
        frequency: String = "daily",
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            val msg = "Você precisa estar logado para criar uma meta."
            Log.e(TAG, "createGoal: $msg")
            _errorMessage.value = msg
            return
        }

        val t = title.trim()
        if (t.isEmpty()) {
            _errorMessage.value = "Título obrigatório."
            return
        }
        if (penaltyAmount <= 0.0) {
            _errorMessage.value = "Valor da punição deve ser maior que zero."
            return
        }
        val c = consequence.trim()
        if (c.isEmpty()) {
            _errorMessage.value = "Descreva a consequência se falhar."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            runCatching {
                val next = computeNextDeadlineMillis(deadlineHour.coerceIn(0, 23))
                val payload = hashMapOf(
                    "userId" to uid,
                    "title" to t,
                    "description" to description.trim(),
                    "penaltyAmount" to penaltyAmount,
                    "consequence" to c,
                    "frequency" to frequency.ifBlank { "daily" },
                    "deadlineHour" to deadlineHour.coerceIn(0, 23),
                    "status" to "active",
                    "streak" to 0,
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                    "lastProofAt" to null,
                    "nextDeadline" to next,
                )
                db.collection("goals").add(payload).await()
            }.onSuccess {
                _goalCreated.value = true
            }.onFailure { e ->
                Log.e(TAG, "createGoal failed", e)
                _errorMessage.value = e.localizedMessage ?: "Não foi possível salvar a meta."
            }
            _isSaving.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun consumeGoalCreated() {
        _goalCreated.value = false
    }

    override fun onCleared() {
        super.onCleared()
        goalsListener?.remove()
    }
}
