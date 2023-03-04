package com.konar.todo.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(taskEntity: TaskEntity)

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Query("select * from 'task-table'")
    fun fetchAllTasks(): Flow<List<TaskEntity>>

    @Query("select * from 'task-table' where id=:id")
    fun fetchTaskById(id: Int): Flow<TaskEntity>

    @Query("update 'task-table' set isChecked =:isChecked where id=:id")
    suspend fun setChecked(id: Int, isChecked: Boolean)

    @Query("delete from 'task-table' where isChecked=true")
    suspend fun deleteSelected()

    @Query("update 'task-table' set status = case when status=:value2 then :value1 when status=:value1 then :value2 end where isChecked=true")
    suspend fun changeStatus(value1: String, value2: String)

}