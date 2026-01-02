package com.codewithfk.expensetracker.android.data.repository

import com.codewithfk.expensetracker.android.data.dao.SharedExpenseDao
import com.codewithfk.expensetracker.android.data.model.*
import kotlinx.coroutines.flow.Flow

class SharedExpenseRepository(private val dao: SharedExpenseDao) {
    val allExpensesWithParticipants: Flow<List<ExpenseWithParticipants>> = dao.getAllExpensesWithParticipants()

    suspend fun insertExpenseWithParticipants(expense: SharedExpense, participantNames: List<String>) {
        val expenseId = dao.insertExpense(expense)
        val existingParticipants = dao.getParticipantsByNames(participantNames)
        val newParticipants = participantNames.filter { name -> existingParticipants.none { it.name == name } }
            .map { Participant(name = it) }
        val newParticipantIds = newParticipants.map { dao.insertParticipant(it) }
        val allParticipants = existingParticipants + newParticipants.zip(newParticipantIds).map{ (participant, id) -> participant.copy(id = id.toInt()) }

        allParticipants.forEach { participant ->
            dao.insertExpenseParticipantCrossRef(ExpenseParticipantCrossRef(expenseId.toInt(), participant.id))
        }
    }
}