package com.taskflow.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.taskflow.databinding.ActivityRegisterBinding
import com.taskflow.view.home.HomeActivity
import com.taskflow.view.login.LoginActivity
import com.taskflow.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.registerConfirmarButton.setOnClickListener {
            val nome = binding.registerNomeInput.text.toString().trim()
            val email = binding.registerEmailInput.text.toString().trim()
            val senha = binding.registerSenhaInput.text.toString()
            viewModel.cadastrar(nome, email, senha)
        }

        binding.registerVoltarButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModel.loading.observe(this) { carregando ->
            binding.registerProgress.visibility = if (carregando) View.VISIBLE else View.GONE
            binding.registerConfirmarButton.isEnabled = !carregando
            binding.registerVoltarButton.isEnabled = !carregando
        }

        viewModel.mensagem.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.cadastroSucesso.observe(this) { sucesso ->
            if (sucesso == true) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}

