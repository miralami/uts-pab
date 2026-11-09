package com.example.taskmanager

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

/** Navbar bawah: Home, Tugas, Logout. [selected] = id menu halaman yang sedang dibuka. */
fun setupNavbar189(activity: Activity, nav: BottomNavigationView, selected: Int) {
    nav.selectedItemId = selected
    nav.setOnItemSelectedListener { item ->
        if (item.itemId == selected) return@setOnItemSelectedListener true
        when (item.itemId) {
            R.id.nav_home -> activity.startActivity(
                Intent(activity, DashboardActivity189::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
            R.id.nav_tasks -> activity.startActivity(Intent(activity, TaskListActivity189::class.java))
            R.id.nav_logout -> {
                Session189(activity).logout()
                activity.startActivity(
                    Intent(activity, LoginActivity189::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }
        true
    }
}
