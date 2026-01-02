package com.codewithfk.expensetracker.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shared_expenses")
data class SharedExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val amount: Double,
    val payer: String,
    val date: Long
)