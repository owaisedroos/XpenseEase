// ExpenseRepository.kt
package com.codewithfk.expensetracker.android.data.repository

import com.codewithfk.expensetracker.android.data.dao.ExpenseDao // Adjust import path if needed
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpense() // Adapt to your actual DAO method name if needed
    }

    // You can add other methods here to interact with the DAO,
    // such as inserting, updating, or deleting expenses.  For example:
    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense) // Adapt to your actual DAO method name
    }
    // ... other methods ...
}