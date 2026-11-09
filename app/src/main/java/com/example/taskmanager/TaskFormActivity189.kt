package com.example.taskmanager

import android.app.DatePickerDialog
import android.os.Bundle
import java.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.databinding.ActivityTaskFormBinding

class TaskFormActivity189 : AppCompatActivity() {
    private var editing: Task189? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = Session189(this).userId()
        val dao = AppDatabase189.get(this).taskDao()

        // Edit: tugas dicari dengan user_id, jadi hanya milik user yang login yang bisa dibuka.
        val id = intent.getIntExtra("task_id", -1)
        if (id != -1) {
            val task = dao.getById(id, userId)
            if (task == null) {
                finish()
                return
            }
            editing = task
            binding.tvTitle.text = "Edit Tugas"
            binding.etName.setText(task.namaTugas)
            binding.etDeadline.setText(task.deadline)
            binding.rgPriority.check(
                when (task.prioritas) { 3 -> R.id.rbHigh; 2 -> R.id.rbMedium; else -> R.id.rbLow }
            )
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.etDeadline.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                binding.etDeadline.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val deadline = binding.etDeadline.text.toString().trim()
            binding.tilName.error = if (name.isEmpty()) "Nama tugas wajib diisi" else null
            binding.tilDeadline.error = if (deadline.isEmpty()) "Deadline wajib diisi" else null
            if (name.isEmpty() || deadline.isEmpty()) return@setOnClickListener

            val prioritas = when (binding.rgPriority.checkedRadioButtonId) {
                R.id.rbHigh -> 3
                R.id.rbMedium -> 2
                else -> 1
            }
            val old = editing
            if (old == null) {
                dao.insert(Task189(userId = userId, namaTugas = name, prioritas = prioritas, deadline = deadline))
            } else {
                dao.update(old.copy(namaTugas = name, prioritas = prioritas, deadline = deadline))
            }
            finish()
        }
    }
}
