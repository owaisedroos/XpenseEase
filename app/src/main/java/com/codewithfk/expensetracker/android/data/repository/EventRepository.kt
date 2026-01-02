package com.codewithfk.expensetracker.android.data.repository

import com.codewithfk.expensetracker.android.data.dao.EventDao
import com.codewithfk.expensetracker.android.data.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun insertEvent(event: Event) = eventDao.insertEvent(event)

    suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event)

    suspend fun updateEvent(event: Event) = eventDao.updateEvent(event)
}