package com.codewithfk.expensetracker.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codewithfk.expensetracker.android.data.model.Event
import com.codewithfk.expensetracker.android.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.codewithfk.expensetracker.android.data.model.Expense
import com.codewithfk.expensetracker.android.data.model.EventStatus

class EventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            repository.allEvents.collect { events ->
                _events.value = events
            }
        }
    }

    fun insertEvent(title: String, description: String, dateTime: Long, location: String, budget: Double?) {
        val event = Event(title = title, description = description, dateTime = dateTime, location = location, budget = budget)
        viewModelScope.launch {
            repository.insertEvent(event)
        }
    }

    fun addExpenseToEvent(eventId: Int, category: String, amount: Double) {
        viewModelScope.launch {
            val currentEvents = _events.value.toMutableList()
            val eventIndex = currentEvents.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = currentEvents[eventIndex]
                val updatedExpenses = event.expenses.toMutableList().apply {
                    add(Expense(category, amount))
                }
                val updatedEvent = event.copy(expenses = updatedExpenses)
                currentEvents[eventIndex] = updatedEvent
                _events.value = currentEvents
                repository.updateEvent(updatedEvent)
            }
        }
    }
    fun updateEventStatus(eventId: Int, newStatus: EventStatus) {
        viewModelScope.launch {
            val currentEvents = _events.value.toMutableList()
            val eventIndex = currentEvents.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = currentEvents[eventIndex]
                val updatedEvent = event.copy(status = newStatus)
                currentEvents[eventIndex] = updatedEvent
                _events.value = currentEvents
                repository.updateEvent(updatedEvent)
            }
        }
    }
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repository.updateEvent(event)
        }
    }
}

class EventViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}