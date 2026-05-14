package com.taskflow.goals.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.taskflow.goals.model.Goal

fun DocumentSnapshot.toGoalOrNull(): Goal? {
    val uid = getString("userId") ?: return null
    val title = getString("title") ?: return null
    val description = getString("description") ?: ""
    val penalty = (get("penaltyAmount") as? Number)?.toDouble() ?: return null
    val consequence = getString("consequence") ?: ""
    val frequency = getString("frequency") ?: "daily"
    val hour = (get("deadlineHour") as? Number)?.toInt() ?: 22
    val status = getString("status") ?: "active"
    val streak = (get("streak") as? Number)?.toInt() ?: 0

    val createdAtMillis = when (val v = get("createdAt")) {
        is Timestamp -> v.toDate().time
        is Number -> v.toLong()
        else -> 0L
    }

    val lastProofMillis = when (val v = get("lastProofAt")) {
        is Timestamp -> v.toDate().time
        is Number -> v.toLong()
        else -> null
    }

    val nextDeadlineMillis = when (val v = get("nextDeadline")) {
        is Timestamp -> v.toDate().time
        is Number -> v.toLong()
        else -> 0L
    }

    return Goal(
        id = id,
        userId = uid,
        title = title,
        description = description,
        penaltyAmount = penalty,
        consequence = consequence,
        frequency = frequency,
        deadlineHour = hour,
        status = status,
        streak = streak,
        createdAt = createdAtMillis,
        lastProofAt = lastProofMillis,
        nextDeadline = nextDeadlineMillis,
    )
}
