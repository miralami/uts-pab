package com.example.taskmanager

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ActivityTaskListBinding
import com.example.taskmanager.databinding.ItemTaskBinding

class TaskAdapter189(
    private val onChecked: (Task189, Boolean) -> Unit,
    private val onClick: (Task189) -> Unit,
    private val onDelete: (Task189) -> Unit
) : RecyclerView.Adapter<TaskAdapter189.Holder>() {

    private var items: List<Task189> = emptyList()

    fun submit(list: List<Task189>) {
        items = list
        notifyDataSetChanged()
    }

    class Holder(val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val task = items[position]
        val b = holder.b
        b.cbDone.setOnCheckedChangeListener(null)
        b.cbDone.isChecked = task.isDone
        b.tvName.text = task.namaTugas
        b.tvName.paintFlags = if (task.isDone) {
            b.tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            b.tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        val prioritas = when (task.prioritas) { 3 -> "Tinggi"; 2 -> "Sedang"; else -> "Rendah" }
        b.tvMeta.text = "Prioritas: $prioritas | Deadline: ${task.deadline}"
        b.cbDone.setOnCheckedChangeListener { _, checked -> onChecked(task, checked) }
        b.root.setOnClickListener { onClick(task) }
        b.btnDelete.setOnClickListener { onDelete(task) }
    }
}

class TaskListActivity189 : AppCompatActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private lateinit var adapter: TaskAdapter189
    private var userId = -1
    private var sortByPriority = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = Session189(this).userId()
        val dao = AppDatabase189.get(this).taskDao()

        adapter = TaskAdapter189(
            onChecked = { task, checked ->
                dao.update(task.copy(isDone = checked))
                load()
            },
            onClick = { task ->
                startActivity(
                    Intent(this, TaskFormActivity189::class.java).putExtra("task_id", task.id)
                )
            },
            onDelete = { task ->
                AlertDialog.Builder(this)
                    .setTitle("Hapus tugas?")
                    .setMessage(task.namaTugas)
                    .setNegativeButton("Batal", null)
                    .setPositiveButton("Hapus") { _, _ ->
                        dao.delete(task)
                        load()
                    }.show()
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.etSearch.doAfterTextChanged { load() }
        setupNavbar189(this, binding.bottomNav, R.id.nav_tasks)
        binding.btnSort.setOnClickListener {
            sortByPriority = !sortByPriority
            binding.btnSort.text = if (sortByPriority) "Urut: Prioritas" else "Urut: Terbaru"
            load()
        }
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, TaskFormActivity189::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.selectedItemId = R.id.nav_tasks
        load()
    }

    private fun load() {
        val dao = AppDatabase189.get(this).taskDao()
        val search = binding.etSearch.text.toString().trim()
        val list = if (sortByPriority) dao.getAllByPriority(userId, search) else dao.getAll(userId, search)
        adapter.submit(list)
        binding.tvEmpty.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }
}
