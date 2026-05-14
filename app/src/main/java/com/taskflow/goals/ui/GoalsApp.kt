package com.taskflow.goals.ui

import androidx.compose.runtime.Composable
import com.taskflow.goals.ui.navigation.GoalsNavHost
import com.taskflow.goals.ui.theme.AppTheme

/** Raiz Compose do MVP de metas (entrada após login). */
@Composable
fun GoalsApp(onLogout: () -> Unit) {
    AppTheme {
        GoalsNavHost(onLogout = onLogout)
    }
}
