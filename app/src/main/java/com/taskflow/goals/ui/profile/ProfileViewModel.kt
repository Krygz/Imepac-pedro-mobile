package com.taskflow.goals.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.taskflow.goals.data.GoalsRepository
import com.taskflow.goals.model.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val email: String = "",
    val totalGoals: Int = 0,
    val completedGoals: Int = 0,
    val bestStreak: Int = 0,
)

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val repository: GoalsRepository = GoalsRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.goalsFlow().collect { goals ->
                _state.value = buildState(goals)
            }
        }
    }

    private fun buildState(goals: List<Goal>): ProfileUiState {
        val email = auth.currentUser?.email.orEmpty()
        val completed = goals.count { it.status.equals("completed", ignoreCase = true) }
        val best = goals.maxOfOrNull { it.streak } ?: 0
        return ProfileUiState(
            email = email,
            totalGoals = goals.size,
            completedGoals = completed,
            bestStreak = best,
        )
    }
}
