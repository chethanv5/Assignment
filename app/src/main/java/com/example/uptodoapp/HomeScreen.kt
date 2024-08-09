package com.example.uptodoapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Index Letter at the Top Middle
            Text(
                text = viewModel.indexLetter,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Todo Image in the Center
            Image(
                painter = painterResource(id = R.drawable.checklist),
                contentDescription = "ToDo Image",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // "What do you want to do today?" Text
            Text(
                text = "What do you want to do today?",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Tap + to add your tasks" Text
            Text(
                text = "Tap + to add your tasks",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display the list of tasks
            Text(
                text = "Your Tasks:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TaskList(
                viewModel = viewModel,
                onEditTask = { taskToEdit = it; showDialog = true },
                onDeleteTask = { task -> viewModel.deleteTask(task) }
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // Floating Action Button at the Bottom Right Corner
        FloatingActionButton(
            onClick = {
                taskToEdit = null  // Ensure it's null so it knows we're adding a new task
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+", fontSize = 24.sp)
        }

        // Edit Task Dialog or Add Task Dialog
        if (showDialog) {
            if (taskToEdit == null) {
                AddTaskDialog(
                    onDismiss = { showDialog = false },
                    onTaskAdded = { title, description, date, time, category ->
                        viewModel.addTask(title, description, date, time, category)
                        showDialog = false
                    }
                )
            } else {
                EditTaskDialog(
                    task = taskToEdit!!,
                    onDismiss = { showDialog = false },
                    onTaskUpdated = { updatedTask ->
                        viewModel.updateTask(
                            oldTask = taskToEdit!!,
                            newTitle = updatedTask.title,
                            newDescription = updatedTask.description,
                            newDate = updatedTask.date,
                            newTime = updatedTask.time,
                            newCategory = updatedTask.category
                        )
                        showDialog = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (String, String, LocalDate, LocalTime, Category) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = { showDatePicker = false }
        )
    } else if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
                showCategoryPicker = true
            },
            onDismiss = { showTimePicker = false }
        )
    } else if (showCategoryPicker) {
        CategoryPickerDialog(
            onCategorySelected = { category ->
                selectedCategory = category
                showCategoryPicker = false
                if (selectedCategory != null) {
                    onTaskAdded(title, description, selectedDate, selectedTime, selectedCategory!!)
                    onDismiss()
                }
            },
            onDismiss = { showCategoryPicker = false }
        )
    } else {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Add Task", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDatePicker = true
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(
    viewModel: HomeViewModel,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    val tasks = viewModel.tasks

    Column {
        tasks.forEach { task ->
            TaskItem(
                task = task,
                onEditTask = onEditTask,
                onDeleteTask = onDeleteTask
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(
    task: Task,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = task.category.imageRes),
                    contentDescription = task.category.name,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = task.description, fontSize = 14.sp)
                    Text(
                        text = "${task.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} ${task.time.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
            // Edit and Delete Buttons
            Row {
                IconButton(onClick = { onEditTask(task) }) {
                    Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Edit Task")
                }
                IconButton(onClick = { onDeleteTask(task) }) {
                    Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete Task")
                }
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onTaskUpdated: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(task.date) }
    var selectedTime by remember { mutableStateOf(task.time) }
    var selectedCategory by remember { mutableStateOf(task.category) }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = { showDatePicker = false }
        )
    } else if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
                showCategoryPicker = true
            },
            onDismiss = { showTimePicker = false }
        )
    } else if (showCategoryPicker) {
        CategoryPickerDialog(
            onCategorySelected = { category ->
                selectedCategory = category
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    } else {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Edit Task", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDatePicker = true
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CategoryPickerDialog(onCategorySelected: (Category) -> Unit, onDismiss: () -> Unit) {
    val categories = listOf(
        Category("Grocery", R.drawable.grocery),
        Category("Work", R.drawable.work),
        Category("Sport", R.drawable.sport),
        Category("Design", R.drawable.design),
        Category("University", R.drawable.university),
        Category("Social", R.drawable.social),
        Category("Music", R.drawable.music),
        Category("Health", R.drawable.health),
        Category("Movie", R.drawable.movie),
        Category("Home", R.drawable.home)
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Select Category", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .clickable { onCategorySelected(category) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = category.imageRes),
                            contentDescription = category.name,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = category.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val datePickerDialog = android.app.DatePickerDialog(context)
    datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
        val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        onDateSelected(selectedDate)
    }
    datePickerDialog.setOnDismissListener { onDismiss() }
    datePickerDialog.show()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(onTimeSelected: (LocalTime) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val timePickerDialog = android.app.TimePickerDialog(context, { _, hourOfDay, minute ->
        val selectedTime = LocalTime.of(hourOfDay, minute)
        onTimeSelected(selectedTime)
    }, LocalTime.now().hour, LocalTime.now().minute, true)
    timePickerDialog.setOnDismissListener { onDismiss() }
    timePickerDialog.show()
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
