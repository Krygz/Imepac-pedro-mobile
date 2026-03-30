package com.taskflow.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.taskflow.model.data.AuthUser

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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
                    ?.addOnSuccessListener { callback.onSuccess() }
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

