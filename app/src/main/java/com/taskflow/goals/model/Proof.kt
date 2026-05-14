package com.taskflow.goals.model

import com.google.firebase.Timestamp

/** Prova em `proofs/{proofId}`. */
data class Proof(
    val id: String,
    val goalId: String,
    val userId: String,
    val imageUrl: String,
    val note: String,
    val createdAt: Timestamp?,
    val approved: Boolean,
)
