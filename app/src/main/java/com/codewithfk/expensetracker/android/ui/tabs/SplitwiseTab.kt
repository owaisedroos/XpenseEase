// SplitwiseTab.kt
package com.codewithfk.expensetracker.android.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codewithfk.expensetracker.android.data.model.ExpenseWithParticipants
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.style.TextAlign
import com.codewithfk.expensetracker.android.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SplitwiseTab(sharedExpenses: List<ExpenseWithParticipants>, onAddExpenseClick: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, "Add Shared Expense", tint = Color.White)
            }
        }
    ) { paddingValues ->
        if (sharedExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No shared expenses found. Add one!",
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
                        text = "Shared Expenses",
                        style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(sharedExpenses) { expenseWithParticipants ->
                    SharedExpenseCard(expenseWithParticipants)
                }
            }
        }
    }
}

@Composable
fun SharedExpenseCard(expenseWithParticipants: ExpenseWithParticipants) {
    val expense = expenseWithParticipants.sharedExpense
    val participants = expenseWithParticipants.participants
    val perParticipantAmount = expense.amount / (participants.size + 1)
    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(expense.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${expense.payer} paid ₹${expense.amount}",
                style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Description: ${expense.description}",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Date: $formattedDate",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (participants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Each person owes: ₹${String.format("%.2f", perParticipantAmount)}",
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Split with: ${participants.joinToString { it.name }}",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "No one to split with.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}