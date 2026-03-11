package com.eventpass.android.features.attendee.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.UiState
import com.eventpass.android.data.repository.EventRepository
import com.eventpass.android.domain.models.Event
import com.eventpass.android.domain.models.EventCategory
import com.eventpass.android.domain.models.TimeCategory
import com.eventpass.android.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for Attendee Home Screen with personalized recommendations.
 * Migrated from iOS Features/Attendee/AttendeeHomeViewModel.swift
 *
 * SwiftUI → Compose state mapping:
 * - @Published private(set) var events → StateFlow<List<Event>>
 * - @MainActor → viewModelScope (main-safe coroutines)
 * - Combine subscribers → Flow.combine + stateIn
 */
@HiltViewModel
class AttendeeHomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    // MARK: - State

    private val _uiState = MutableStateFlow(AttendeeHomeUiState())
    val uiState: StateFlow<AttendeeHomeUiState> = _uiState.asStateFlow()

    private val _eventsState = MutableStateFlow<UiState<List<Event>>>(UiState.Idle)
    val eventsState: StateFlow<UiState<List<Event>>> = _eventsState.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _filters = MutableStateFlow(EventFilters())
    private val _searchQuery = MutableStateFlow("")

    /**
     * Filtered events based on current filters and search query.
     */
    val filteredEvents: StateFlow<List<Event>> = combine(
        _events,
        _filters,
        _searchQuery
    ) { events, filters, query ->
        var filtered = events

        // Filter out past events
        filtered = filtered.filter { it.endDate >= LocalDateTime.now() }

        // Filter by time category
        filters.timeCategory?.let { timeCategory ->
            filtered = filtered.filter { event ->
                event.timeCategory == timeCategory
            }
        }

        // Filter by event category
        filters.eventCategory?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val lowercaseQuery = query.lowercase()
            filtered = filtered.filter { event ->
                event.title.lowercase().contains(lowercaseQuery) ||
                        event.organizerName.lowercase().contains(lowercaseQuery) ||
                        event.venue.name.lowercase().contains(lowercaseQuery) ||
                        event.venue.city.lowercase().contains(lowercaseQuery) ||
                        event.description.lowercase().contains(lowercaseQuery)
            }
        }

        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Event sections for recommendation display.
     */
    val eventSections: StateFlow<List<RecommendationSection>> = combine(
        _events,
        _uiState
    ) { events, state ->
        buildRecommendationSections(events, state.currentUser)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // MARK: - Actions

    /**
     * Load events initially.
     */
    fun loadEvents() {
        if (_uiState.value.hasLoadedInitialData) return

        viewModelScope.launch {
            _eventsState.value = UiState.Loading

            eventRepository.getEvents()
                .onSuccess { events ->
                    _events.value = events
                    _eventsState.value = UiState.Success(events)
                    _uiState.update { it.copy(hasLoadedInitialData = true) }
                }
                .onFailure { error ->
                    _eventsState.value = UiState.Error(error.message ?: "Failed to load events")
                }
        }
    }

    /**
     * Refresh events (pull-to-refresh).
     */
    fun refreshEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            eventRepository.getEvents()
                .onSuccess { events ->
                    _events.value = events
                    _eventsState.value = UiState.Success(events)
                }
                .onFailure { error ->
                    // Keep existing events on refresh failure
                }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    /**
     * Set current user for personalized recommendations.
     */
    fun setCurrentUser(user: User?) {
        _uiState.update { it.copy(currentUser = user) }
    }

    /**
     * Update search query.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(isSearchExpanded = query.isNotBlank()) }
    }

    /**
     * Toggle search expansion.
     */
    fun toggleSearch() {
        _uiState.update { it.copy(isSearchExpanded = !it.isSearchExpanded) }
    }

    /**
     * Select time category filter.
     */
    fun selectTimeCategory(category: TimeCategory?) {
        _filters.update { it.copy(timeCategory = category) }
    }

    /**
     * Select event category filter.
     */
    fun selectEventCategory(category: EventCategory?) {
        _filters.update { it.copy(eventCategory = category) }
    }

    /**
     * Clear all filters.
     */
    fun clearFilters() {
        _filters.value = EventFilters()
        _searchQuery.value = ""
        _uiState.update { it.copy(isSearchExpanded = false) }
    }

    /**
     * Toggle event like status.
     */
    fun toggleEventLike(eventId: String) {
        _uiState.update { state ->
            val currentLikes = state.likedEventIds.toMutableSet()
            if (currentLikes.contains(eventId)) {
                currentLikes.remove(eventId)
            } else {
                currentLikes.add(eventId)
            }
            state.copy(likedEventIds = currentLikes)
        }
    }

    /**
     * Check if event is liked.
     */
    fun isEventLiked(eventId: String): Boolean {
        return _uiState.value.likedEventIds.contains(eventId)
    }

    // MARK: - Private Methods

    private fun buildRecommendationSections(
        events: List<Event>,
        user: User?
    ): List<RecommendationSection> {
        if (events.isEmpty()) return emptyList()

        val sections = mutableListOf<RecommendationSection>()
        val now = LocalDateTime.now()

        // Happening Now section
        val happeningNow = events.filter { it.isHappeningNow }.take(5)
        if (happeningNow.isNotEmpty()) {
            sections.add(
                RecommendationSection(
                    category = RecommendationCategory.HAPPENING_NOW,
                    events = happeningNow
                )
            )
        }

        // For You section (top events)
        val forYou = events.take(10)
        if (forYou.isNotEmpty()) {
            sections.add(
                RecommendationSection(
                    category = RecommendationCategory.FOR_YOU,
                    events = forYou
                )
            )
        }

        // This Weekend section
        val weekend = events.filter { event ->
            val dayOfWeek = event.startDate.dayOfWeek.value
            dayOfWeek >= 5 && event.startDate.isAfter(now) &&
                    event.startDate.isBefore(now.plusDays(7))
        }.take(6)
        if (weekend.isNotEmpty()) {
            sections.add(
                RecommendationSection(
                    category = RecommendationCategory.THIS_WEEKEND,
                    events = weekend
                )
            )
        }

        // Popular section
        val popular = events.sortedByDescending {
            it.ticketTypes.sumOf { ticket -> ticket.sold }
        }.take(8)
        if (popular.isNotEmpty()) {
            sections.add(
                RecommendationSection(
                    category = RecommendationCategory.POPULAR_NOW,
                    events = popular
                )
            )
        }

        // Free Events section
        val freeEvents = events.filter { it.isFree }.take(6)
        if (freeEvents.isNotEmpty()) {
            sections.add(
                RecommendationSection(
                    category = RecommendationCategory.FREE_EVENTS,
                    events = freeEvents
                )
            )
        }

        // Near You section (if user has city)
        user?.city?.let { city ->
            val nearbyEvents = events.filter { it.venue.city == city }.take(8)
            if (nearbyEvents.isNotEmpty()) {
                sections.add(
                    RecommendationSection(
                        category = RecommendationCategory.NEAR_YOU,
                        events = nearbyEvents
                    )
                )
            }
        }

        return sections
    }
}

/**
 * UI State for Attendee Home Screen.
 */
data class AttendeeHomeUiState(
    val currentUser: User? = null,
    val hasLoadedInitialData: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSearchExpanded: Boolean = false,
    val likedEventIds: Set<String> = emptySet()
)

/**
 * Event filters state.
 */
data class EventFilters(
    val timeCategory: TimeCategory? = null,
    val eventCategory: EventCategory? = null
)

/**
 * Recommendation category for sections.
 */
enum class RecommendationCategory(
    val displayName: String,
    val icon: String
) {
    FOR_YOU("For You", "star"),
    HAPPENING_NOW("Happening Now", "play_circle"),
    BASED_ON_INTERESTS("Based on Your Interests", "favorite"),
    NEAR_YOU("Near You", "location_on"),
    POPULAR_NOW("Popular Right Now", "trending_up"),
    THIS_WEEKEND("This Weekend", "weekend"),
    FREE_EVENTS("Free Events", "money_off")
}

/**
 * Recommendation section model.
 */
data class RecommendationSection(
    val category: RecommendationCategory,
    val events: List<Event>
) {
    val title: String get() = category.displayName
    val icon: String get() = category.icon
}
