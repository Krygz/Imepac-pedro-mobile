package com.taskflow.goals.data

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Próximo prazo em millis: hoje à [deadlineHour] (minuto 0); se já passou, amanhã.
 */
fun computeNextDeadlineMillis(deadlineHour: Int, nowMillis: Long = System.currentTimeMillis()): Long {
    val zone = ZoneId.systemDefault()
    val now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), zone)
    val hour = deadlineHour.coerceIn(0, 23)
    var deadline = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(hour, 0), zone)
    if (!now.isBefore(deadline)) {
        deadline = deadline.plusDays(1)
    }
    return deadline.toInstant().toEpochMilli()
}
