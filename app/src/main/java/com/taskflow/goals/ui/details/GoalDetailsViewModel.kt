package com.taskflow.goals.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.goals.data.GoalsRepository
import com.taskflow.goals.model.Goal
import com.taskflow.goals.model.Proof
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GoalDetailsViewModel(
    goalId: String,
    private val repository: GoalsRepository = GoalsRepository(),
) : ViewModel() {

    val goal: StateFlow<Goal?> = repository.goalFlow(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val proofs: StateFlow<List<Proof>> = repository.proofsFlow(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
