package com.eventpass.android.data.repository

import com.eventpass.android.domain.models.AgeRestriction
import com.eventpass.android.domain.models.Event
import com.eventpass.android.domain.models.EventCategory
import com.eventpass.android.domain.models.EventStatus
import com.eventpass.android.domain.models.TicketType
import com.eventpass.android.domain.models.Venue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of EventRepository.
 * Will be replaced with actual backend implementation.
 */
@Singleton
class EventRepositoryImpl @Inject constructor() : EventRepository {

    private val _events = MutableStateFlow(sampleEvents)
    private val _likedEventIds = MutableStateFlow<Set<String>>(emptySet())

    override suspend fun getEvents(): Result<List<Event>> {
        delay(500)
        return Result.success(_events.value)
    }

    override fun getAllEvents(): Flow<List<Event>> = _events

    override fun getEventsByCategory(category: EventCategory): Flow<List<Event>> {
        return _events.map { events ->
            events.filter { it.category == category }
        }
    }

    override suspend fun getEventById(id: String): Result<Event> {
        delay(300)
        val event = _events.value.find { it.id == id }
        return if (event != null) {
            Result.success(event)
        } else {
            Result.failure(Exception("Event not found"))
        }
    }

    override fun searchEvents(query: String): Flow<List<Event>> {
        return _events.map { events ->
            events.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.venue.name.contains(query, ignoreCase = true)
            }
        }
    }

    override fun getFeaturedEvents(): Flow<List<Event>> {
        return _events.map { events ->
            events.filter { it.status == EventStatus.PUBLISHED }
                .sortedByDescending { it.likeCount }
                .take(5)
        }
    }

    override fun getHappeningNowEvents(): Flow<List<Event>> {
        return _events.map { events ->
            events.filter { it.isHappeningNow }
        }
    }

    override fun getEventsByOrganizer(organizerId: String): Flow<List<Event>> {
        return _events.map { events ->
            events.filter { it.organizerId == organizerId }
        }
    }

    override suspend fun createEvent(event: Event): Result<Event> {
        delay(500)
        val newEvent = event.copy(id = UUID.randomUUID().toString())
        _events.value = _events.value + newEvent
        return Result.success(newEvent)
    }

    override suspend fun updateEvent(event: Event): Result<Event> {
        delay(500)
        _events.value = _events.value.map { if (it.id == event.id) event else it }
        return Result.success(event)
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        delay(500)
        _events.value = _events.value.filter { it.id != eventId }
        return Result.success(Unit)
    }

    override suspend fun toggleLike(eventId: String): Result<Boolean> {
        val currentLiked = _likedEventIds.value
        val isNowLiked = if (eventId in currentLiked) {
            _likedEventIds.value = currentLiked - eventId
            false
        } else {
            _likedEventIds.value = currentLiked + eventId
            true
        }
        return Result.success(isNowLiked)
    }

    override fun getLikedEvents(): Flow<List<Event>> {
        return _events.map { events ->
            events.filter { it.id in _likedEventIds.value }
        }
    }

    companion object {
        private val sampleEvents = listOf(
            Event(
                id = "1",
                title = "Summer Music Festival",
                description = "Join us for an unforgettable night of music featuring top artists from across East Africa. Experience live performances, food vendors, and an amazing atmosphere under the stars.",
                organizerId = "org1",
                organizerName = "EventMasters UG",
                posterUrl = null,
                category = EventCategory.MUSIC,
                startDate = LocalDateTime.now().minusHours(1),
                endDate = LocalDateTime.now().plusHours(3),
                venue = Venue(
                    name = "Kampala Serena Hotel",
                    address = "Kintu Road",
                    city = "Kampala",
                    latitude = 0.3136,
                    longitude = 32.5811
                ),
                ticketTypes = listOf(
                    TicketType(
                        name = "General Admission",
                        price = 50000.0,
                        quantity = 500,
                        sold = 320,
                        saleStartDate = LocalDateTime.now().minusDays(30),
                        saleEndDate = LocalDateTime.now().plusHours(1)
                    ),
                    TicketType(
                        name = "VIP",
                        price = 150000.0,
                        quantity = 100,
                        sold = 75,
                        perks = listOf("VIP Lounge Access", "Free Drinks", "Meet & Greet"),
                        saleStartDate = LocalDateTime.now().minusDays(30),
                        saleEndDate = LocalDateTime.now().plusHours(1)
                    )
                ),
                status = EventStatus.PUBLISHED,
                rating = 4.5,
                totalRatings = 120,
                likeCount = 450
            ),
            Event(
                id = "2",
                title = "Tech Innovators Summit 2024",
                description = "Connect with industry leaders and explore the latest in technology and innovation. Featuring keynote speakers, workshops, and networking opportunities.",
                organizerId = "org2",
                organizerName = "TechHub Kampala",
                posterUrl = null,
                category = EventCategory.TECHNOLOGY,
                startDate = LocalDateTime.now().plusDays(7),
                endDate = LocalDateTime.now().plusDays(7).plusHours(8),
                venue = Venue(
                    name = "Uganda Museum",
                    address = "Kira Road",
                    city = "Kampala",
                    latitude = 0.3301,
                    longitude = 32.5729
                ),
                ticketTypes = listOf(
                    TicketType(
                        name = "Early Bird",
                        price = 75000.0,
                        quantity = 200,
                        sold = 150,
                        saleStartDate = LocalDateTime.now().minusDays(14),
                        saleEndDate = LocalDateTime.now().plusDays(2)
                    ),
                    TicketType(
                        name = "Regular",
                        price = 100000.0,
                        quantity = 300,
                        sold = 50,
                        saleStartDate = LocalDateTime.now().plusDays(2),
                        saleEndDate = LocalDateTime.now().plusDays(7)
                    )
                ),
                status = EventStatus.PUBLISHED,
                rating = 4.8,
                totalRatings = 95,
                likeCount = 320
            ),
            Event(
                id = "3",
                title = "Charity Run for Education",
                description = "Run for a cause! Support education initiatives across Uganda. Choose between 5K and 10K routes.",
                organizerId = "org3",
                organizerName = "Hope Foundation",
                posterUrl = null,
                category = EventCategory.FUNDRAISING,
                startDate = LocalDateTime.now().plusDays(2),
                endDate = LocalDateTime.now().plusDays(2).plusHours(4),
                venue = Venue(
                    name = "Kololo Independence Grounds",
                    address = "Kololo",
                    city = "Kampala",
                    latitude = 0.3270,
                    longitude = 32.5970
                ),
                ticketTypes = listOf(
                    TicketType(
                        name = "5K Run",
                        price = 20000.0,
                        quantity = 1000,
                        sold = 650,
                        saleStartDate = LocalDateTime.now().minusDays(7),
                        saleEndDate = LocalDateTime.now().plusDays(2)
                    ),
                    TicketType(
                        name = "10K Run",
                        price = 35000.0,
                        quantity = 500,
                        sold = 280,
                        saleStartDate = LocalDateTime.now().minusDays(7),
                        saleEndDate = LocalDateTime.now().plusDays(2)
                    )
                ),
                status = EventStatus.PUBLISHED,
                rating = 4.7,
                totalRatings = 210,
                likeCount = 580
            ),
            Event(
                id = "4",
                title = "Comedy Night Live",
                description = "Laugh out loud with Uganda's top comedians! An evening of stand-up comedy, live music, and entertainment.",
                organizerId = "org4",
                organizerName = "Comedy Central UG",
                posterUrl = null,
                category = EventCategory.COMEDY,
                startDate = LocalDateTime.now().plusDays(3),
                endDate = LocalDateTime.now().plusDays(3).plusHours(4),
                venue = Venue(
                    name = "Theatre La Bonita",
                    address = "Kamwokya",
                    city = "Kampala",
                    latitude = 0.3400,
                    longitude = 32.5900
                ),
                ticketTypes = listOf(
                    TicketType(
                        name = "Regular",
                        price = 30000.0,
                        quantity = 200,
                        sold = 180,
                        saleStartDate = LocalDateTime.now().minusDays(14),
                        saleEndDate = LocalDateTime.now().plusDays(3)
                    )
                ),
                status = EventStatus.PUBLISHED,
                rating = 4.6,
                totalRatings = 85,
                likeCount = 290,
                ageRestriction = AgeRestriction.AGE_18_PLUS
            )
        )
    }
}
