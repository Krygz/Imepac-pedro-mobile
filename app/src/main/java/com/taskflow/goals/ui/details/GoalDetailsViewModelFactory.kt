package com.taskflow.goals.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GoalDetailsViewModelFactory(
    private val goalId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalDetailsViewModel(goalId) as T
    }
}
