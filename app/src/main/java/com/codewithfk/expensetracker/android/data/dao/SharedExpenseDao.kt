package com.codewithfk.expensetracker.android.data.dao

import androidx.room.*
import com.codewithfk.expensetracker.android.data.model.ExpenseParticipantCrossRef
import com.codewithfk.expensetracker.android.data.model.ExpenseWithParticipants
import com.codewithfk.expensetracker.android.data.model.Participant
import com.codewithfk.expensetracker.android.data.model.SharedExpense
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedExpenseDao {
    @Transaction
    @Query("SELECT * FROM shared_expenses")
    fun getAllExpensesWithParticipants(): Flow<List<ExpenseWithParticipants>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: SharedExpense): Long //Return inserted expense ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant): Long //Return inserted participant ID

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenseParticipantCrossRef(crossRef: ExpenseParticipantCrossRef)

    @Query("SELECT * FROM participants WHERE name IN (:names)")
    suspend fun getParticipantsByNames(names: List<String>): List<Participant>
}