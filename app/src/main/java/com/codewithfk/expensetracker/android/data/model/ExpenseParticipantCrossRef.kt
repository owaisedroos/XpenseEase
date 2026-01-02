package com.codewithfk.expensetracker.android.data.model

import androidx.room.Entity

@Entity(tableName = "expense_participant_cross_ref", primaryKeys = ["expenseId", "participantId"])
data class ExpenseParticipantCrossRef(
    val expenseId: Int,
    val participantId: Int
)