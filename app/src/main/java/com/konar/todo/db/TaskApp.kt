package com.konar.todo.db

import android.app.Application

class TaskApp : Application() {

    val db by lazy {
        TaskDatabase.getInstance(this)
    }

}