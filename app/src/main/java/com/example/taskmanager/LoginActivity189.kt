package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.databinding.ActivityLoginBinding

class LoginActivity189 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val session = Session189(this)
        if (session.isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity189::class.java))
            finish()
            return
        }
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            binding.tilUsername.error = if (username.isEmpty()) "Username wajib diisi" else null
            binding.tilPassword.error = if (password.isEmpty()) "Password wajib diisi" else null
            if (username.isEmpty() || password.isEmpty()) return@setOnClickListener

            val user = AppDatabase189.get(this).userDao().login(username, password)
            if (user == null) {
                binding.tilPassword.error = "Username atau password salah"
            } else {
                session.save(user)
                startActivity(Intent(this, DashboardActivity189::class.java))
                finish()
            }
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity189::class.java))
        }
    }
}
