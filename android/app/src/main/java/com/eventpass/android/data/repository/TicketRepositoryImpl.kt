package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.Ticket
import com.eventpass.android.domain.models.TicketAvailabilityStatus
import com.eventpass.android.domain.models.TicketScanStatus
import com.eventpass.android.domain.models.TicketType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of TicketRepository.
 */
@Singleton
class TicketRepositoryImpl @Inject constructor() : TicketRepository {

    private val _userTickets = MutableStateFlow<List<Ticket>>(emptyList())

    override fun getUserTickets(): Flow<List<Ticket>> = _userTickets

    override suspend fun getMyTickets(): Result<List<Ticket>> {
        delay(300)
        // Return mock tickets if empty for demo purposes
        if (_userTickets.value.isEmpty()) {
            val mockTickets = createMockTickets()
            _userTickets.value = mockTickets
        }
        return Result.success(_userTickets.value)
    }

    private fun createMockTickets(): List<Ticket> {
        val ticketType = TicketType(
            id = "tt-regular",
            name = "Regular",
            price = 75000.0,
            quantity = 500,
            sold = 200
        )
        return listOf(
            Ticket(
                id = UUID.randomUUID().toString(),
                ticketNumber = Ticket.generateTicketNumber(),
                orderNumber = Ticket.generateOrderNumber(),
                eventId = "event-1",
                eventTitle = "Uganda Music Festival 2024",
                eventDate = LocalDateTime.now().plusDays(14),
                eventEndDate = LocalDateTime.now().plusDays(14).plusHours(6),
                eventVenue = "Kampala Serena Hotel",
                eventVenueAddress = "Kintu Road",
                eventVenueCity = "Kampala",
                venueLatitude = 0.3136,
                venueLongitude = 32.5811,
                eventDescription = "The biggest music festival in Uganda",
                eventOrganizerName = "EventMasters UG",
                ticketType = ticketType,
                userId = "current_user",
                purchaseDate = LocalDateTime.now().minusDays(7)
            ),
            Ticket(
                id = UUID.randomUUID().toString(),
                ticketNumber = Ticket.generateTicketNumber(),
                orderNumber = Ticket.generateOrderNumber(),
                eventId = "event-2",
                eventTitle = "Tech Summit Kampala",
                eventDate = LocalDateTime.now().plusDays(30),
                eventEndDate = LocalDateTime.now().plusDays(30).plusHours(8),
                eventVenue = "Kololo Independence Grounds",
                eventVenueAddress = "Kololo",
                eventVenueCity = "Kampala",
                venueLatitude = 0.3312,
                venueLongitude = 32.5875,
                eventDescription = "Annual technology conference",
                eventOrganizerName = "TechUG",
                ticketType = ticketType,
                userId = "current_user",
                purchaseDate = LocalDateTime.now().minusDays(3)
            )
        )
    }

    override suspend fun getTicketById(id: String): Result<Ticket> {
        delay(300)
        val ticket = _userTickets.value.find { it.id == id }
        return if (ticket != null) {
            Result.success(ticket)
        } else {
            Result.failure(Exception("Ticket not found"))
        }
    }

    override suspend fun getTicketTypesForEvent(eventId: String): Result<List<TicketType>> {
        delay(300)
        val ticketTypes = listOf(
            TicketType(
                id = "tt1",
                name = "Early Bird",
                description = "Limited early bird tickets at discounted price",
                price = 50000.0,
                quantity = 200,
                sold = 150,
                saleStartDate = LocalDateTime.now().minusDays(30),
                saleEndDate = LocalDateTime.now().plusDays(7),
                perks = listOf("Priority Entry", "Event T-Shirt")
            ),
            TicketType(
                id = "tt2",
                name = "Regular",
                description = "Standard admission ticket",
                price = 75000.0,
                quantity = 500,
                sold = 200,
                saleStartDate = LocalDateTime.now().minusDays(30),
                saleEndDate = LocalDateTime.now().plusDays(30)
            ),
            TicketType(
                id = "tt3",
                name = "VIP",
                description = "Premium access with exclusive perks",
                price = 200000.0,
                quantity = 100,
                sold = 45,
                saleStartDate = LocalDateTime.now().minusDays(30),
                saleEndDate = LocalDateTime.now().plusDays(30),
                perks = listOf(
                    "VIP Lounge Access",
                    "Free Drinks",
                    "Meet & Greet",
                    "Reserved Seating",
                    "Event Merchandise"
                )
            )
        )
        return Result.success(ticketTypes)
    }

    override suspend fun purchaseTickets(
        eventId: String,
        ticketTypeId: String,
        quantity: Int,
        paymentMethodId: String
    ): Result<List<Ticket>> {
        delay(1500) // Simulate payment processing

        val ticketType = TicketType(
            id = ticketTypeId,
            name = "Regular",
            price = 75000.0,
            quantity = 500,
            sold = 200
        )

        val orderNumber = Ticket.generateOrderNumber()
        val newTickets = (1..quantity).map {
            Ticket(
                id = UUID.randomUUID().toString(),
                ticketNumber = Ticket.generateTicketNumber(),
                orderNumber = orderNumber,
                eventId = eventId,
                eventTitle = "Sample Event",
                eventDate = LocalDateTime.now().plusDays(7),
                eventEndDate = LocalDateTime.now().plusDays(7).plusHours(4),
                eventVenue = "Kampala Serena Hotel",
                eventVenueAddress = "Kintu Road",
                eventVenueCity = "Kampala",
                venueLatitude = 0.3136,
                venueLongitude = 32.5811,
                eventDescription = "An amazing event",
                eventOrganizerName = "EventMasters UG",
                ticketType = ticketType,
                userId = "current_user",
                purchaseDate = LocalDateTime.now(),
                scanStatus = TicketScanStatus.UNUSED,
                qrCodeData = "TICKET:${UUID.randomUUID()}"
            )
        }

        _userTickets.value = _userTickets.value + newTickets
        return Result.success(newTickets)
    }

    override suspend fun scanTicket(ticketId: String, eventId: String): Result<Ticket> {
        delay(500)

        val ticket = _userTickets.value.find { it.id == ticketId }
            ?: return Result.failure(Exception("Ticket not found"))

        if (ticket.eventId != eventId) {
            return Result.failure(Exception("Ticket is not for this event"))
        }

        if (ticket.scanStatus == TicketScanStatus.SCANNED) {
            return Result.failure(Exception("Ticket already scanned"))
        }

        val scannedTicket = ticket.copy(
            scanStatus = TicketScanStatus.SCANNED,
            scanDate = LocalDateTime.now()
        )

        _userTickets.value = _userTickets.value.map {
            if (it.id == ticketId) scannedTicket else it
        }

        return Result.success(scannedTicket)
    }

    override fun getTicketsForEvent(eventId: String): Flow<List<Ticket>> {
        return _userTickets.map { tickets ->
            tickets.filter { it.eventId == eventId }
        }
    }

    override suspend fun requestRefund(ticketId: String, reason: String): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}
