package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.databinding.ActivityRegisterBinding

class RegisterActivity189 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogin.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val dao = AppDatabase189.get(this).userDao()

            binding.tilUsername.error = when {
                username.isEmpty() -> "Username wajib diisi"
                dao.findByUsername(username) != null -> "Username sudah dipakai"
                else -> null
            }
            binding.tilPassword.error = when {
                password.isEmpty() -> "Password wajib diisi"
                password.length < 6 -> "Password minimal 6 karakter"
                else -> null
            }
            if (binding.tilUsername.error != null || binding.tilPassword.error != null) return@setOnClickListener

            dao.insert(User189(username = username, password = password))
            Toast.makeText(this, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
