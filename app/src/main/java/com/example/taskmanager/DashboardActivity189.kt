package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.databinding.ActivityDashboardBinding

class DashboardActivity189 : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var session: Session189

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = Session189(this)

        binding.tvGreeting.text = "Halo, ${session.username()}!"
        binding.btnSeeMore.setOnClickListener {
            startActivity(Intent(this, TaskListActivity189::class.java))
        }
        setupNavbar189(this, binding.bottomNav, R.id.nav_home)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.selectedItemId = R.id.nav_home
        // 3 tugas belum selesai dengan deadline terdekat.
        val tasks = AppDatabase189.get(this).taskDao().getUpcoming(session.userId())
        binding.llUpcoming.removeAllViews()
        for (task in tasks) {
            val row = TextView(this)
            row.text = "• ${task.namaTugas} (${task.deadline})"
            row.textSize = 16f
            row.setPadding(0, 12, 0, 12)
            binding.llUpcoming.addView(row)
        }
        binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
    }
}
