package com.taskflow.goals.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.taskflow.goals.data.DeadlineHelper
import com.taskflow.goals.model.Goal
import com.taskflow.goals.model.Proof
import com.taskflow.goals.ui.components.AtRiskBadge
import com.taskflow.goals.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(
    goalId: String,
    onBack: () -> Unit,
    onSubmitProof: () -> Unit,
) {
    if (goalId.isBlank()) {
        LaunchedEffect(Unit) { onBack() }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        val vm: GoalDetailsViewModel = viewModel(
            key = goalId,
            factory = GoalDetailsViewModelFactory(goalId),
        )
        val goal by vm.goal.collectAsState()
        val proofs by vm.proofs.collectAsState()
        val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(goal?.title ?: "Contrato") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            },
        ) { padding ->
            val g = goal
            when {
                g == null -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> {
                    GoalDetailsContent(
                        padding = padding,
                        g = g,
                        proofs = proofs,
                        dateFmt = dateFmt,
                        onSubmitProof = onSubmitProof,
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalDetailsContent(
    padding: PaddingValues,
    g: Goal,
    proofs: List<Proof>,
    dateFmt: SimpleDateFormat,
    onSubmitProof: () -> Unit,
) {
    val active = g.status.equals("active", ignoreCase = true)
    val atRisk = active && DeadlineHelper.isAtRisk(g.nextDeadline)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(status = g.status)
                        if (atRisk) AtRiskBadge()
                    }
                    Text(g.description.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 10.dp))
                    Text("Punição: R$ %.2f".format(g.penaltyAmount), modifier = Modifier.padding(top = 8.dp))
                    if (g.consequence.isNotBlank()) {
                        Text(g.consequence, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    }
                    Text("Streak: ${g.streak}", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 8.dp))
                    Text("Prazo: ${DeadlineHelper.formatDeadlineMillis(g.nextDeadline)}", modifier = Modifier.padding(top = 4.dp))
                    Text("Frequência: ${g.frequency}", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        item {
            Button(
                onClick = onSubmitProof,
                enabled = active && !DeadlineHelper.isPastDeadline(g.nextDeadline),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Text("Enviar prova")
            }
        }

        item {
            Text("Histórico de provas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
        }

        if (proofs.isEmpty()) {
            item { Text("Nenhuma prova ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(proofs, key = { it.id }) { p ->
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = p.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Text(p.note.ifBlank { "Sem observação" }, Modifier.padding(top = 8.dp))
                        val label = p.createdAt?.toDate()?.let { dateFmt.format(it) }.orEmpty()
                        if (label.isNotBlank()) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
