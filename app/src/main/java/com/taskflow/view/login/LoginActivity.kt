package com.taskflow.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.taskflow.databinding.ActivityLoginBinding
import com.taskflow.view.home.HomeActivity
import com.taskflow.view.register.RegisterActivity
import com.taskflow.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.loginEntrarButton.setOnClickListener {
            val email = binding.loginEmailInput.text.toString().trim()
            val senha = binding.loginSenhaInput.text.toString()
            viewModel.login(email, senha)
        }

        binding.loginCadastroButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.loading.observe(this) { carregando ->
            binding.loginProgress.visibility = if (carregando) View.VISIBLE else View.GONE
            binding.loginEntrarButton.isEnabled = !carregando
            binding.loginCadastroButton.isEnabled = !carregando
        }

        viewModel.mensagem.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginSucesso.observe(this) { sucesso ->
            if (sucesso == true) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}

