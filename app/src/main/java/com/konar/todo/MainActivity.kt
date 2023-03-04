package com.konar.todo

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konar.todo.databinding.ActivityMainBinding
import com.konar.todo.db.TaskApp
import com.konar.todo.db.TaskDao
import com.konar.todo.db.TaskEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val checkList = ArrayList<Pair<Int, Boolean>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskDao = (application as TaskApp).db.taskDao()
        val priorityOptions = resources.getStringArray(R.array.priority_choice)

        setUpSpinnerOptions(priorityOptions)

        binding.btnAdd.setOnClickListener {
            addRecord(taskDao)
        }

        binding.btnDelete.setOnClickListener {
            deleteTask(taskDao)
        }

        binding.btnChangeStatus.setOnClickListener {
            changeStatus(taskDao)
        }

        lifecycleScope.launch {
            taskDao.fetchAllTasks().collect {
                val list = ArrayList(it)
                setupRecyclerView(list, taskDao)
            }
        }


    }

    private fun addRecord(taskDao: TaskDao) {
        val taskTitle = binding.etTitle.text.toString()
        val taskDesc = binding.etDesc.text.toString()
        val priority = binding.spPriority.selectedItem.toString()

        if (taskTitle.isNotEmpty()) {
            lifecycleScope.launch {
                taskDao.insert(TaskEntity(title = taskTitle, desc = taskDesc, priority = priority))
            }
            binding.etTitle.text.clear()
            binding.etDesc.text.clear()
            Toast.makeText(this, "Task successfully entered!!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task title can't be blank!!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setChecked(id: Int, statusCheck: Boolean) {
        checkList.add(Pair(id, statusCheck))
    }

    private fun deleteTask(taskDao: TaskDao) {
        lifecycleScope.launch {
            for (i in checkList) {
                taskDao.setChecked(i.first, i.second)
            }
            taskDao.deleteSelected()
        }
    }

    private fun changeStatus(taskDao: TaskDao) {
        lifecycleScope.launch {
            for (i in checkList) {
                taskDao.setChecked(i.first, i.second)
            }
            taskDao.changeStatus("Completed","Not Yet Completed")
        }
    }

    private fun setupRecyclerView(taskList: ArrayList<TaskEntity>, taskDao: TaskDao) {

        if (taskList.isNotEmpty()) {
            val itemAdapter = TaskItemAdapter(
                taskList,
                { id, statusCheck ->
                    setChecked(id, statusCheck)
                }
            )

            binding.rvTaskList.layoutManager = LinearLayoutManager(this)
            binding.rvTaskList.adapter = itemAdapter
            binding.tvNoTasks.visibility = View.GONE
            binding.rvTaskList.visibility = View.VISIBLE

        } else {
            binding.rvTaskList.visibility = View.GONE
            binding.tvNoTasks.visibility = View.VISIBLE
        }

    }

    private fun setUpSpinnerOptions(choices: Array<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, choices
        )

        binding.spPriority.adapter = adapter
        binding.spPriority.setSelection(0)
    }

}