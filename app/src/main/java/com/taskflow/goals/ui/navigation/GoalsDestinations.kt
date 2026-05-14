package com.taskflow.goals.ui.navigation

import android.net.Uri

object GoalsDestinations {
    const val HOME = "home"
    const val CREATE = "create"
    const val PROFILE = "profile"
    const val DETAILS = "details/{goalId}"
    const val PROOF = "proof/{goalId}"

    fun details(goalId: String) = "details/${Uri.encode(goalId)}"
    fun proof(goalId: String) = "proof/${Uri.encode(goalId)}"
}