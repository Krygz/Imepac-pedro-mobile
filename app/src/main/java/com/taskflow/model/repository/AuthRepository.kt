package com.taskflow.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.taskflow.model.data.AuthUser

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    interface AuthCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    fun login(email: String, senha: String, callback: AuthCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e ->
                callback.onError(e.message ?: "Falha no login.")
            }
    }

    fun cadastrar(nome: String, email: String, senha: String, callback: AuthCallback) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val request = userProfileChangeRequest {
                    displayName = nome
                }
                result.user?.updateProfile(request)
                    ?.addOnSuccessListener {
                        val usuario = result.user
                        if (usuario == null) {
                            callback.onError("Usuário criado, mas não foi possível obter dados do usuário.")
                            return@addOnSuccessListener
                        }

                        val uid = usuario.uid
                        val dadosUsuario = hashMapOf<String, Any>(
                            "nome" to nome,
                            "email" to (usuario.email ?: email),
                            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(dadosUsuario, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener { callback.onSuccess() }
                            .addOnFailureListener { e ->
                                callback.onError(e.message ?: "Usuário criado, mas falhou ao salvar no Firestore.")
                            }
                    }
                    ?.addOnFailureListener { e ->
                        callback.onError(e.message ?: "Usuário criado, mas falhou ao salvar nome.")
                    }
            }
            .addOnFailureListener { e ->
                callback.onError(e.message ?: "Falha no cadastro.")
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun usuarioLogado(): AuthUser? {
        val user = firebaseAuth.currentUser ?: return null
        return AuthUser(
            uid = user.uid,
            nome = user.displayName ?: "Usuário",
            email = user.email ?: ""
        )
    }
}

