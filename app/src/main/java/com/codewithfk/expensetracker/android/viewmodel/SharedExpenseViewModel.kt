package com.codewithfk.expensetracker.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codewithfk.expensetracker.android.data.model.ExpenseWithParticipants
import com.codewithfk.expensetracker.android.data.model.SharedExpense
import com.codewithfk.expensetracker.android.data.repository.SharedExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedExpenseViewModel(private val repository: SharedExpenseRepository) : ViewModel() {
    private val _sharedExpenses = MutableStateFlow<List<ExpenseWithParticipants>>(emptyList())
    val sharedExpenses = _sharedExpenses.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.allExpensesWithParticipants.collect { expenses ->
                _sharedExpenses.value = expenses
            }
        }
    }

    fun insertExpense(description: String, amount: Double, payer: String, participantNames: List<String>, date: Long) {
        val expense = SharedExpense(description = description, amount = amount, payer = payer, date = date)
        viewModelScope.launch {
            repository.insertExpenseWithParticipants(expense, participantNames)
        }
    }
}

class SharedExpenseViewModelFactory(
    private val repository: SharedExpenseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}