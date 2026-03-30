package com.taskflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskflow.model.data.AuthUser
import com.taskflow.model.repository.AuthRepository

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _usuario = MutableLiveData<AuthUser?>()
    val usuario: LiveData<AuthUser?> = _usuario

    fun carregarUsuarioLogado() {
        _usuario.value = authRepository.usuarioLogado()
    }

    fun sair() {
        authRepository.logout()
        _usuario.value = null
    }
}

