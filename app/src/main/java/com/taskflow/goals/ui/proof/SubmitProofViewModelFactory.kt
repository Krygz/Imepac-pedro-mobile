package com.taskflow.goals.ui.proof

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SubmitProofViewModelFactory(
    private val goalId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SubmitProofViewModel(goalId) as T
    }
}
