package com.taskflow.goals.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskflow.goals.data.DeadlineHelper
import com.taskflow.goals.model.Goal

@Composable
fun GoalCard(
    goal: Goal,
    onOpenDetails: () -> Unit,
    onSendProof: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val active = goal.status.equals("active", ignoreCase = true)
    val atRisk = active && DeadlineHelper.isAtRisk(goal.nextDeadline)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenDetails),
                )
                StatusBadge(status = goal.status)
                if (atRisk) AtRiskBadge()
            }

            Text(
                text = "R$ %.2f".format(goal.penaltyAmount),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = "Streak · ${goal.streak}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (goal.consequence.isNotBlank()) {
                Text(
                    text = goal.consequence,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Text(
                text = "Prazo: ${DeadlineHelper.formatDeadlineMillis(goal.nextDeadline)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onSendProof,
                    enabled = active && !DeadlineHelper.isPastDeadline(goal.nextDeadline),
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text("Enviar prova")
                }
                OutlinedButton(
                    onClick = onOpenDetails,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text("Detalhes")
                }
            }
        }
    }
}
