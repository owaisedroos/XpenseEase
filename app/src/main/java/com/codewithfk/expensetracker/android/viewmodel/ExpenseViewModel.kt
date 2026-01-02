// ExpenseViewModel.kt
package com.codewithfk.expensetracker.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.data.repository.ExpenseRepository // Adjust import if needed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _allExpenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val allExpenses: StateFlow<List<ExpenseEntity>> = _allExpenses.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenses ->
                _allExpenses.value = expenses
            }
        }
    }

    // You can add other functions to handle user interactions,
    // such as adding, updating, or deleting expenses.  For example:
    fun insertExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
            // Optionally, refresh the list of expenses after inserting:
            loadExpenses()
        }
    }
    // ... other functions ...
}

// Optional: ViewModel Factory (recommended for dependency injection)
class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}