package com.codewithfk.expensetracker.android.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExpenseWithParticipants(
    @Embedded val sharedExpense: SharedExpense,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ExpenseParticipantCrossRef::class, parentColumn = "expenseId", entityColumn = "participantId")
    )
    val participants: List<Participant>
)