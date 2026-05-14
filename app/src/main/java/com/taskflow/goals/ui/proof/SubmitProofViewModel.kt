package com.taskflow.goals.ui.proof

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.goals.data.GoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubmitProofViewModel(
    private val goalId: String,
    private val repository: GoalsRepository = GoalsRepository(),
) : ViewModel() {

    private val _uploading = MutableStateFlow(false)
    val uploading: StateFlow<Boolean> = _uploading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _done = MutableStateFlow(false)
    val done: StateFlow<Boolean> = _done.asStateFlow()

    fun consumeDone() {
        _done.value = false
    }

    fun submit(imageUri: Uri, note: String) {
        if (_uploading.value) return
        viewModelScope.launch {
            _uploading.value = true
            _message.value = null
            runCatching {
                repository.submitProof(goalId, imageUri, note)
            }.onSuccess {
                _done.value = true
            }.onFailure { e ->
                _message.value = e.message ?: "Falha no envio."
            }
            _uploading.value = false
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}
