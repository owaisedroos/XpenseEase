package com.codewithfk.expensetracker.android.feature.add_expense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.codewithfk.expensetracker.android.viewmodel.SharedExpenseViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSharedExpenseScreen(navController: NavController, viewModel: SharedExpenseViewModel) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var payer by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf("") } // Comma-separated for now

    var selectedDate by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Shared Expense") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = payer,
                    onValueChange = { payer = it },
                    label = { Text("Payer") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = participants,
                    onValueChange = { participants = it },
                    label = { Text("Participants (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Picker
                OutlinedTextField(
                    value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)),
                    onValueChange = {},
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerDialog = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                )

                if (showDatePickerDialog) {
                    DatePicker(
                        onDateSelected = { newDate ->
                            selectedDate = newDate
                            showDatePickerDialog = false
                        },
                        initialDate = selectedDate
                    )
                }

                Button(
                    onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        if (description.isNotBlank() && amountDouble != null && payer.isNotBlank() && participants.isNotBlank()) {
                            val participantList = participants.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            viewModel.insertExpense(description, amountDouble, payer, participantList, selectedDate)
                            navController.popBackStack() // Go back after adding
                        } else {
                            // Handle invalid input (e.g., show a Snackbar)
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Expense")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(onDateSelected: (Long) -> Unit, initialDate: Long) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(it)
                }
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
        DatePicker(state = datePickerState)
    }
}