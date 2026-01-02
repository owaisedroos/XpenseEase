package com.codewithfk.expensetracker.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codewithfk.expensetracker.android.data.dao.ExpenseDao
import com.codewithfk.expensetracker.android.data.dao.SharedExpenseDao
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.data.model.SharedExpense
import com.codewithfk.expensetracker.android.data.model.Participant
import com.codewithfk.expensetracker.android.data.model.ExpenseParticipantCrossRef
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import com.codewithfk.expensetracker.android.data.dao.EventDao
import com.codewithfk.expensetracker.android.data.model.Event
import com.codewithfk.expensetracker.android.data.model.Expense
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import com.codewithfk.expensetracker.android.data.model.EventStatus //Import EventStatus

class Converters {
    @TypeConverter
    fun fromExpenseList(value: List<Expense>?): String? {
        return if (value == null) null else Json.encodeToString(value)
    }

    @TypeConverter
    fun toExpenseList(value: String?): List<Expense>? {
        return if (value == null) null else Json.decodeFromString(ListSerializer(Expense.serializer()), value)
    }

    @TypeConverter
    fun fromEventStatus(value: EventStatus?): String? {
        return if (value == null) null else Json.encodeToString(value)
    }

    @TypeConverter
    fun toEventStatus(value: String?): EventStatus? {
        return if (value == null) null else Json.decodeFromString(EventStatus.serializer(), value)
    }
}

@Database(
    entities = [
        ExpenseEntity::class,
        SharedExpense::class,
        Participant::class,
        ExpenseParticipantCrossRef::class,
        Event::class
    ],
    version = 7, // Increment the version number
    exportSchema = false
)
@TypeConverters(Converters::class)
@Singleton
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun sharedExpenseDao(): SharedExpenseDao
    abstract fun eventDao(): EventDao
    companion object {
        const val DATABASE_NAME = "expense_database"
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null
        fun getInstance(@ApplicationContext context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS expense_table_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                type TEXT NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            INSERT INTO expense_table_new (id, title, amount, date, type)
            SELECT id, title, amount, date, type FROM expense_table
            """.trimIndent()
        )
        database.execSQL("DROP TABLE expense_table")
        database.execSQL("ALTER TABLE expense_table_new RENAME TO expense_table")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //No changes in this migration
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS shared_expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                description TEXT NOT NULL,
                amount REAL NOT NULL,
                payer TEXT NOT NULL,
                date INTEGER NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS participants (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS expense_participant_cross_ref (
                expenseId INTEGER NOT NULL,
                participantId INTEGER NOT NULL,
                PRIMARY KEY(expenseId, participantId)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                dateTime INTEGER NOT NULL,
                location TEXT NOT NULL,
                budget REAL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE events ADD COLUMN expenses TEXT NOT NULL DEFAULT '[]'")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {  // Add this migration
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE events ADD COLUMN status TEXT NOT NULL DEFAULT 'Upcoming'")
    }
}