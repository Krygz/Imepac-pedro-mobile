package com.taskflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskflow.model.repository.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {


    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _mensagem = MutableLiveData<String>()
    val mensagem: LiveData<String> = _mensagem

    private val _loginSucesso = MutableLiveData<Boolean>()
    val loginSucesso: LiveData<Boolean> = _loginSucesso

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _mensagem.value = "Preencha email e senha."
            return
        }

        _loading.value = true
        authRepository.login(email, senha, object : AuthRepository.AuthCallback {
            override fun onSuccess() {
                _loading.postValue(false)
                _loginSucesso.postValue(true)
            }

            override fun onError(message: String) {
                _loading.postValue(false)
                _mensagem.postValue(message)
            }
        })
    }
}

