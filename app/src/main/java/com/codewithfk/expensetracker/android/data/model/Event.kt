package com.codewithfk.expensetracker.android.data.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Expense(
    val category: String,
    val amount: Double
)

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dateTime: Long, // Store as milliseconds since epoch
    val location: String,
    val budget: Double?, // Rename to 'expenseOfEvent'
    val expenses: List<Expense> = emptyList(), // List of expenses for the event
    val status: EventStatus = EventStatus.Upcoming // Add event status
)

@Serializable
enum class EventStatus {
    Upcoming,
    InProgress,
    Completed
}