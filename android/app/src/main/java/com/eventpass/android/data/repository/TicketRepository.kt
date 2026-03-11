package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.Ticket
import com.eventpass.android.domain.models.TicketType
import kotlinx.coroutines.flow.Flow

/**
 * Ticket repository interface.
 * Mirrors iOS TicketRepository protocol.
 */
interface TicketRepository {

    /**
     * Get all tickets for the current user.
     */
    fun getUserTickets(): Flow<List<Ticket>>

    /**
     * Get all tickets for current user (one-shot).
     */
    suspend fun getMyTickets(): Result<List<Ticket>>

    /**
     * Get ticket by ID.
     */
    suspend fun getTicketById(id: String): Result<Ticket>

    /**
     * Get ticket types for an event.
     */
    suspend fun getTicketTypesForEvent(eventId: String): Result<List<TicketType>>

    /**
     * Purchase tickets.
     */
    suspend fun purchaseTickets(
        eventId: String,
        ticketTypeId: String,
        quantity: Int,
        paymentMethodId: String
    ): Result<List<Ticket>>

    /**
     * Scan a ticket (for organizers/scanners).
     */
    suspend fun scanTicket(ticketId: String, eventId: String): Result<Ticket>

    /**
     * Get tickets for an event (organizer view).
     */
    fun getTicketsForEvent(eventId: String): Flow<List<Ticket>>

    /**
     * Request ticket refund.
     */
    suspend fun requestRefund(ticketId: String, reason: String): Result<Unit>
}
