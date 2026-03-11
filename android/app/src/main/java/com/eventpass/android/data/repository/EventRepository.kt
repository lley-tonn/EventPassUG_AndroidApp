package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.Event
import com.eventpass.android.domain.models.EventCategory
import kotlinx.coroutines.flow.Flow

/**
 * Event repository interface.
 * Mirrors iOS EventRepository protocol.
 */
interface EventRepository {

    /**
     * Get all events (suspend function for one-shot).
     */
    suspend fun getEvents(): Result<List<Event>>

    /**
     * Get all events as flow.
     */
    fun getAllEvents(): Flow<List<Event>>

    /**
     * Get events by category.
     */
    fun getEventsByCategory(category: EventCategory): Flow<List<Event>>

    /**
     * Get event by ID.
     */
    suspend fun getEventById(id: String): Result<Event>

    /**
     * Search events by query.
     */
    fun searchEvents(query: String): Flow<List<Event>>

    /**
     * Get featured/recommended events.
     */
    fun getFeaturedEvents(): Flow<List<Event>>

    /**
     * Get events happening now or soon.
     */
    fun getHappeningNowEvents(): Flow<List<Event>>

    /**
     * Get events by organizer.
     */
    fun getEventsByOrganizer(organizerId: String): Flow<List<Event>>

    /**
     * Create a new event (organizer only).
     */
    suspend fun createEvent(event: Event): Result<Event>

    /**
     * Update an existing event (organizer only).
     */
    suspend fun updateEvent(event: Event): Result<Event>

    /**
     * Delete an event (organizer only).
     */
    suspend fun deleteEvent(eventId: String): Result<Unit>

    /**
     * Toggle like/favorite status for an event.
     */
    suspend fun toggleLike(eventId: String): Result<Boolean>

    /**
     * Get user's liked/favorite events.
     */
    fun getLikedEvents(): Flow<List<Event>>
}
