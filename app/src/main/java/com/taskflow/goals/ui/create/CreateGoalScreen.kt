package com.taskflow.goals.ui.create

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskflow.goals.ui.components.AppTextField
import com.taskflow.goals.ui.components.PrimaryButton
import com.taskflow.goals.viewmodel.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    val vm: GoalViewModel = viewModel(viewModelStoreOwner = activity)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var penaltyText by remember { mutableStateOf("") }
    var consequence by remember { mutableStateOf("") }
    var hourText by remember { mutableStateOf("22") }

    val isSaving by vm.isSaving.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()
    val goalCreated by vm.goalCreated.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        val msg = errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        vm.clearError()
    }

    LaunchedEffect(goalCreated) {
        if (goalCreated) {
            vm.consumeGoalCreated()
            onCreated()
        }
    }

    val previewPenalty = penaltyText.replace(',', '.').toDoubleOrNull()
    val previewText = if ((previewPenalty ?: 0.0) > 0.0) {
        "Se você falhar, você perde R$ %.2f".format(previewPenalty)
    } else {
        "Se você falhar, você perde R$ …"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Novo contrato", fontWeight = FontWeight.Black) },
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Text(
                    text = previewText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(18.dp),
                )
            }

            AppTextField(value = title, onValueChange = { title = it }, label = "Título da meta")
            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descrição",
                singleLine = false,
            )
            AppTextField(
                value = penaltyText,
                onValueChange = { penaltyText = it },
                label = "Valor da punição (R$)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            AppTextField(
                value = consequence,
                onValueChange = { consequence = it },
                label = "Consequência se falhar",
                singleLine = false,
            )
            AppTextField(
                value = hourText,
                onValueChange = { hourText = it.filter { ch -> ch.isDigit() }.take(2) },
                label = "Horário limite (0–23h)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            PrimaryButton(
                text = if (isSaving) "Criando..." else "Criar contrato",
                onClick = {
                    val penalty = penaltyText.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 22
                    vm.createGoal(
                        title = title,
                        description = description,
                        penaltyAmount = penalty,
                        consequence = consequence,
                        deadlineHour = hour,
                        frequency = "daily",
                    )
                },
                enabled = !isSaving,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            )
        }
    }
}
