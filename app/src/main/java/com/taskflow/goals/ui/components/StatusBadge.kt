package com.taskflow.goals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val normalized = status.lowercase()
    val (label, color) = when (normalized) {
        "active" -> "Ativa" to Color(0xFF43A047)
        "failed" -> "Falhou" to MaterialTheme.colorScheme.primary
        "completed" -> "Concluída" to Color(0xFF90CAF9)
        else -> status to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = modifier
            .border(1.dp, color, RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
fun AtRiskBadge(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.tertiary
    Text(
        text = "EM RISCO",
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = modifier
            .border(1.dp, color, RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
