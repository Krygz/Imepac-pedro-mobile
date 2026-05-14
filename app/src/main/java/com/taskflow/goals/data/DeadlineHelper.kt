package com.taskflow.goals.data

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DeadlineHelper {

    private val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm")

    fun formatDeadlineMillis(epochMillis: Long): String {
        if (epochMillis <= 0L) return "—"
        val z = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
        return fmt.format(z)
    }

    fun isPastDeadline(nextDeadlineMillis: Long): Boolean =
        nextDeadlineMillis > 0L && System.currentTimeMillis() > nextDeadlineMillis

    /** Menos de 4h para o prazo (metas ativas). */
    fun isAtRisk(nextDeadlineMillis: Long): Boolean {
        if (nextDeadlineMillis <= 0L) return false
        val end = Instant.ofEpochMilli(nextDeadlineMillis)
        val now = Instant.now()
        if (!now.isBefore(end)) return false
        val hours = ChronoUnit.HOURS.between(now, end)
        return hours in 0..3
    }

    fun advanceAfterProof(currentNextMillis: Long, frequency: String, deadlineHour: Int): Long {
        val zone = ZoneId.systemDefault()
        val base = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentNextMillis), zone)
        val days = if (frequency.equals("weekly", ignoreCase = true)) 7L else 1L
        var next = base.plusDays(days).withHour(deadlineHour.coerceIn(0, 23)).withMinute(0).withSecond(0).withNano(0)
        val now = ZonedDateTime.now(zone)
        while (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        return next.toInstant().toEpochMilli()
    }
}
