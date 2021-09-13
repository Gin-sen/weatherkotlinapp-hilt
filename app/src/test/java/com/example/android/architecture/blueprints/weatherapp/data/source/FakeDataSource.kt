package com.example.android.architecture.blueprints.weatherapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.weatherapp.data.Result
import com.example.android.architecture.blueprints.weatherapp.data.Result.Error
import com.example.android.architecture.blueprints.weatherapp.data.Result.Success
import com.example.android.architecture.blueprints.weatherapp.data.Task

class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()) : TasksDataSource {
    override suspend fun getTasks(): Result<List<Task>> {
        tasks?.let { return Success(ArrayList(it)) }
        return Error(
            Exception("Tasks not found")
        )
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        tasks?.firstOrNull { it.id == taskId }?.let { return Success(it) }
        return Error(
            Exception("Task not found")
        )
    }

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
    }

    override suspend fun completeTask(task: Task) {
        tasks?.firstOrNull { it.id == task.id }?.let { it.isCompleted = true }
    }

    override suspend fun completeTask(taskId: String) {
        tasks?.firstOrNull { it.id == taskId }?.let { it.isCompleted = true }
    }

    override suspend fun activateTask(task: Task) {
        tasks?.firstOrNull { it.id == task.id }?.let { it.isCompleted = false }
    }

    override suspend fun activateTask(taskId: String) {
        tasks?.firstOrNull { it.id == taskId }?.let { it.isCompleted = false }
    }

    override suspend fun clearCompletedTasks() {
        tasks?.removeIf { it.isCompleted }
    }

    override suspend fun deleteAllTasks() {
        tasks?.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        tasks?.removeIf { it.id == taskId }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("not implemented")
    }

    override suspend fun refreshTasks() {
        TODO("not implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("not implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("not implemented")
    }
}
