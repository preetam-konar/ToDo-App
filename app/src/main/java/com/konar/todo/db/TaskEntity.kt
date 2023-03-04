package com.konar.todo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task-table")
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "task-title")
    var title: String = "",

    @ColumnInfo(name = "task-desc")
    var desc: String = "",

    @ColumnInfo(name = "Priority")
    var priority: String = "Low",

    @ColumnInfo(name = "isChecked")
    var isChecked: Boolean = false,

    @ColumnInfo(name = "Status")
    var status: String = "Not Yet Completed"
)