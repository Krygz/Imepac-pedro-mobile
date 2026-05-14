package com.taskflow.goals.model

/**
 * Meta em `goals/{goalId}`.
 * Valores padrão usados apenas no app; leitura do Firestore via [com.taskflow.goals.data.GoalFirestoreMapper].
 */
data class Goal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val penaltyAmount: Double = 0.0,
    val consequence: String = "",
    val frequency: String = "daily",
    val deadlineHour: Int = 22,
    val status: String = "active",
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastProofAt: Long? = null,
    val nextDeadline: Long = 0L,
)
