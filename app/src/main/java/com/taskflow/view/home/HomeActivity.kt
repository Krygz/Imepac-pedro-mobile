package com.taskflow.view.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.taskflow.goals.ui.GoalsApp
import com.taskflow.view.login.LoginActivity
import com.taskflow.viewmodel.HomeViewModel

/**
 * Pós-login: hospeda o MVP de metas em Jetpack Compose sem alterar o fluxo de autenticação.
 * O logout reutiliza [HomeViewModel] + [LoginActivity] como antes.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var homeShellViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeShellViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeShellViewModel.carregarUsuarioLogado()

        setContent {
            GoalsApp(
                onLogout = {
                    homeShellViewModel.sair()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                },
            )
        }
    }
}
