package com.example.uptodoapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime

class HomeViewModel : ViewModel() {
    // Index letter (for example purposes, hard-coded as "A")
    val indexLetter: String = "ToDoApp"

    // Store tasks temporarily in a list
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    fun addTask(
        title: String,
        description: String,
        date: LocalDate,
        time: LocalTime,
        category: Category
    ) {
        val newTask = Task(title, description , date, time, category)
        _tasks.add(newTask)
    }
    fun updateTask(
        oldTask: Task,
        newTitle: String,
        newDescription: String,
        newDate: LocalDate,
        newTime: LocalTime,
        newCategory: Category
    ) {
        val index = _tasks.indexOf(oldTask)
        if (index != -1) {
            _tasks[index] = oldTask.copy(
                title = newTitle,
                description = newDescription,
                date = newDate,
                time = newTime,
                category = newCategory
            )
        }
    }

    fun deleteTask(task: Task) {
        _tasks.remove(task)
    }
}
data class Task(
    val title: String,
    val description: String,
    val date: LocalDate,
    val time: LocalTime,
    val category: Category
)

data class Category(val name: String, val imageRes: Int)

