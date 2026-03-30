package com.taskflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskflow.model.repository.AuthRepository

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _mensagem = MutableLiveData<String>()
    val mensagem: LiveData<String> = _mensagem

    private val _cadastroSucesso = MutableLiveData<Boolean>()
    val cadastroSucesso: LiveData<Boolean> = _cadastroSucesso

    fun cadastrar(nome: String, email: String, senha: String) {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            _mensagem.value = "Preencha nome, email e senha."
            return
        }

        _loading.value = true
        authRepository.cadastrar(nome, email, senha, object : AuthRepository.AuthCallback {
            override fun onSuccess() {
                _loading.postValue(false)
                _cadastroSucesso.postValue(true)
            }

            override fun onError(message: String) {
                _loading.postValue(false)
                _mensagem.postValue(message)
            }
        })
    }
}

