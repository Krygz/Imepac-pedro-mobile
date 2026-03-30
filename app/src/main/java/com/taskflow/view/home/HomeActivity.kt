package com.taskflow.view.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.taskflow.R
import com.taskflow.databinding.ActivityHomeBinding
import com.taskflow.view.login.LoginActivity
import com.taskflow.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.homeSairButton.setOnClickListener {
            viewModel.sair()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        viewModel.usuario.observe(this) { user ->
            val nome = user?.nome?.takeIf { it.isNotBlank() } ?: user?.email ?: "Usuário"
            binding.homeBoasVindasText.text = getString(R.string.boas_vindas, nome)
        }

        viewModel.carregarUsuarioLogado()
    }
}

