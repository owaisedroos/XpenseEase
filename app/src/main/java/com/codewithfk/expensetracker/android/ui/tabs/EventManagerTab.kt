package com.codewithfk.expensetracker.android.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codewithfk.expensetracker.android.data.model.Event
import com.codewithfk.expensetracker.android.viewmodel.EventViewModel
import com.codewithfk.expensetracker.android.viewmodel.EventViewModelFactory
import com.codewithfk.expensetracker.android.data.ExpenseDatabase
import com.codewithfk.expensetracker.android.data.repository.EventRepository
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import com.codewithfk.expensetracker.android.data.model.Expense
import com.codewithfk.expensetracker.android.data.model.EventStatus
import com.codewithfk.expensetracker.android.ui.theme.Typography

@Composable
fun EventManagerTab() {
    val context = LocalContext.current
    val eventRepository = EventRepository(ExpenseDatabase.getInstance(context).eventDao())
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(eventRepository)
    )
    val events by eventViewModel.events.collectAsState()
    var showAddEventDialog by remember { mutableStateOf(false) }
    var selectedEventId by remember { mutableStateOf(-1) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showStatusMenuForEventId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEventDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, "Add Event", tint = Color.White)
            }
        }
    ) { paddingValues ->
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No events found. Add one!",
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
            ) {
                item {
                    Text(
                        text = "Events",
                        style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(events) { event ->
                    EventCard(
                        event = event,
                        onAddExpenseClick = {
                            selectedEventId = event.id
                            showAddExpenseDialog = true
                        },
                        onStatusClick = {
                            showStatusMenuForEventId = event.id
                        }
                    )
                    if (showStatusMenuForEventId == event.id) {
                        DropdownMenu(
                            expanded = true,
                            onDismissRequest = { showStatusMenuForEventId = null },
                            offset = DpOffset(0.dp, 0.dp) // Corrected line: Using DpOffset
                        ) {
                            DropdownMenuItem(
                                text = { Text("Upcoming") },
                                onClick = {
                                    eventViewModel.updateEventStatus(event.id, EventStatus.Upcoming)
                                    showStatusMenuForEventId = null
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("In Progress") },
                                onClick = {
                                    eventViewModel.updateEventStatus(event.id, EventStatus.InProgress)
                                    showStatusMenuForEventId = null
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Completed") },
                                onClick = {
                                    eventViewModel.updateEventStatus(event.id, EventStatus.Completed)
                                    showStatusMenuForEventId = null
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAddEventDialog) {
            AddEventDialog(
                onDismiss = { showAddEventDialog = false },
                onEventAdded = { title, description, date, location, budget ->
                    eventViewModel.insertEvent(title, description, date, location, budget)
                    showAddEventDialog = false
                }
            )
        }
        if (showAddExpenseDialog) {
            AddExpenseDialog(
                onDismiss = { showAddExpenseDialog = false },
                onExpenseAdded = { category, amount ->
                    if (selectedEventId != -1) {
                        eventViewModel.addExpenseToEvent(selectedEventId, category, amount)
                        showAddExpenseDialog = false
                        selectedEventId = -1
                    }
                }
            )
        }
    }
}

@Composable
fun EventCard(event: Event, onAddExpenseClick: () -> Unit, onStatusClick: () -> Unit) {
    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(event.dateTime))
    val totalExpenses = event.expenses.sumOf { it.amount }
    val statusColor = when (event.status) {
        EventStatus.Upcoming -> MaterialTheme.colorScheme.surfaceVariant
        EventStatus.InProgress -> Color.Yellow.copy(alpha = 0.3f)
        EventStatus.Completed -> Color.Green.copy(alpha = 0.3f)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: $formattedDate",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Location: ${event.location}",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (event.budget != null) {
                Text(
                    text = "Expense of the event: ₹${event.budget}",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (event.description.isNotBlank()) {
                Text(
                    text = "Description: ${event.description}",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Expenses: ₹${totalExpenses}",
                style = Typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            event.expenses.forEach { expense ->
                Text(
                    text = "${expense.category}: ₹${expense.amount}",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onAddExpenseClick) {
                    Text("Add Expense")
                }
                Button(onClick = onStatusClick) {
                    Text(text = "Status: ${event.status}")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(onDismiss: () -> Unit, onEventAdded: (String, String, Long, String, Double?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var location by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Event", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = {},
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerDialog = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                )
                OutlinedTextField(value = budget, onValueChange = { budget = it }, label = { Text("Expense of the event") })

                if (showDatePickerDialog) {
                    DatePicker(
                        onDateSelected = {
                            calendar.timeInMillis = it
                            selectedDate = it
                            showDatePickerDialog = false
                        },
                        initialDate = calendar.timeInMillis
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budgetDouble = budget.toDoubleOrNull()
                    onEventAdded(title, description, selectedDate, location, budgetDouble)
                },
                enabled = title.isNotBlank() && location.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(onDateSelected: (Long) -> Unit, initialDate: Long) {
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialDate } }
    val year = remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val month = remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    val day = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(onClick = {
                calendar.set(year.value, month.value, day.value)
                onDateSelected(calendar.timeInMillis)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = { onDateSelected(initialDate) }) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = rememberDatePickerState(initialSelectedDateMillis = initialDate))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onExpenseAdded: (String, Double) -> Unit) {
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column {
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.toDoubleOrNull() != null || it.isEmpty()) amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (category.isNotBlank() && amountDouble != null) {
                        onExpenseAdded(category, amountDouble)
                    }
                },
                enabled = category.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}