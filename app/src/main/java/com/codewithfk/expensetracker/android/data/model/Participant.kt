package com.codewithfk.expensetracker.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "participants")
data class Participant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)