package com.konar.todo

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konar.todo.databinding.ActivityMainBinding
import com.konar.todo.databinding.DialogUpdateBinding
import com.konar.todo.db.TaskApp
import com.konar.todo.db.TaskDao
import com.konar.todo.db.TaskEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val checkList = mutableSetOf<Int>()


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

        binding.btnEdit.setOnClickListener {
            if (checkList.isEmpty()) {
                Toast.makeText(this, "No task selected!!", Toast.LENGTH_SHORT).show()
            } else if (checkList.size > 1) {
                Toast.makeText(this, "Only one task can be updated at a time!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                updateTaskDialog(checkList.elementAt(0), priorityOptions, taskDao)
            }
        }


        lifecycleScope.launch {
            taskDao.fetchAllTasks().collect {
                val list = ArrayList(it)
                setupRecyclerView(list, taskDao)
            }
        }


    }

    //Add Record
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

    //Check
    private fun setChecked(id: Int, statusChecked: Boolean) {
        if (statusChecked) {
            checkList.add(id)
        } else {
            checkList.remove(id)
        }
    }

    //Delete Checked Record
    private fun deleteTask(taskDao: TaskDao) {
        lifecycleScope.launch {
            taskDao.setCheckedAllFalse()
            taskDao.setCheckedTrue(ArrayList(checkList))
            taskDao.deleteSelected()
            checkList.clear()
        }

    }

    private fun changeStatus(taskDao: TaskDao) {
        lifecycleScope.launch {
            taskDao.setCheckedAllFalse()
            taskDao.setCheckedTrue(ArrayList(checkList))
            taskDao.changeStatus("Completed", "Not Yet Completed")
        }
    }

    private fun updateTaskDialog(id: Int, choices: Array<String>, taskDao: TaskDao) {
        val updateDialog = Dialog(this, R.style.my_dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            choices
        )

        binding.spPriority.adapter = adapter

        lifecycleScope.launch {
            taskDao.fetchTaskById(id).collect {
                if (it != null) {
                    binding.etTitle.setText(it.title)
                    binding.etDesc.setText(it.desc)
                    binding.spPriority.setSelection(choices.indexOf(it.priority))
                }
            }
        }

        binding.tvUpdateRecord.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val desc = binding.etDesc.text.toString()
            val priority = binding.spPriority.selectedItem.toString()

            if (title.isNotEmpty()) {
                lifecycleScope.launch {
                    taskDao.update(TaskEntity(id, title, desc, priority))
                    Toast.makeText(
                        applicationContext,
                        "Task successfully updated!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(applicationContext, "Title can't be empty!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()

    }

    //Setup Recycler View
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

    //Setup Priority Spinner
    private fun setUpSpinnerOptions(choices: Array<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, choices
        )

        binding.spPriority.adapter = adapter
        binding.spPriority.setSelection(0)
    }

}