package com.eventpass.android.features.attendee.eventdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.EventChannel
import com.eventpass.android.core.state.UiEvent
import com.eventpass.android.core.state.UiState
import com.eventpass.android.data.repository.EventRepository
import com.eventpass.android.data.repository.TicketRepository
import com.eventpass.android.domain.models.Event
import com.eventpass.android.domain.models.TicketType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Event Details Screen.
 *
 * SwiftUI → Compose state mapping:
 * - @State private var selectedTicketType → MutableStateFlow
 * - @State private var ticketQuantity → Included in UI state
 * - Navigation with .sheet() → EventChannel for one-time events
 */
@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    // MARK: - State

    private val _eventState = MutableStateFlow<UiState<Event>>(UiState.Loading)
    val eventState: StateFlow<UiState<Event>> = _eventState.asStateFlow()

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    // MARK: - Events

    private val _uiEvents = EventChannel<EventDetailsEvent>()
    val uiEvents = _uiEvents.events

    init {
        loadEvent()
    }

    // MARK: - Actions

    /**
     * Load event details.
     */
    fun loadEvent() {
        viewModelScope.launch {
            _eventState.value = UiState.Loading

            eventRepository.getEventById(eventId)
                .onSuccess { event ->
                    _eventState.value = UiState.Success(event)
                    // Select first available ticket type by default
                    event.ticketTypes.firstOrNull { it.isAvailable }?.let { ticketType ->
                        _uiState.update { it.copy(selectedTicketType = ticketType) }
                    }
                }
                .onFailure { error ->
                    _eventState.value = UiState.Error(error.message ?: "Failed to load event")
                }
        }
    }

    /**
     * Toggle like status.
     */
    fun toggleLike() {
        _uiState.update { it.copy(isLiked = !it.isLiked) }
        // TODO: Persist like status to repository
    }

    /**
     * Select ticket type.
     */
    fun selectTicketType(ticketType: TicketType) {
        _uiState.update {
            it.copy(
                selectedTicketType = ticketType,
                quantity = 1 // Reset quantity when changing ticket type
            )
        }
    }

    /**
     * Increment ticket quantity.
     */
    fun incrementQuantity() {
        val state = _uiState.value
        val maxQuantity = state.selectedTicketType?.maxPerOrder ?: 10
        if (state.quantity < maxQuantity) {
            _uiState.update { it.copy(quantity = it.quantity + 1) }
        }
    }

    /**
     * Decrement ticket quantity.
     */
    fun decrementQuantity() {
        val state = _uiState.value
        if (state.quantity > 1) {
            _uiState.update { it.copy(quantity = it.quantity - 1) }
        }
    }

    /**
     * Set ticket quantity directly.
     */
    fun setQuantity(quantity: Int) {
        val maxQuantity = _uiState.value.selectedTicketType?.maxPerOrder ?: 10
        _uiState.update {
            it.copy(quantity = quantity.coerceIn(1, maxQuantity))
        }
    }

    /**
     * Start ticket purchase flow.
     */
    fun startPurchase() {
        val state = _uiState.value
        val ticketType = state.selectedTicketType ?: return

        if (!state.isAuthenticated) {
            _uiEvents.trySend(EventDetailsEvent.ShowAuthPrompt)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPurchasing = true) }

            // Navigate to purchase screen
            _uiEvents.send(
                EventDetailsEvent.NavigateToPurchase(
                    eventId = eventId,
                    ticketTypeId = ticketType.id,
                    quantity = state.quantity
                )
            )

            _uiState.update { it.copy(isPurchasing = false) }
        }
    }

    /**
     * Share event.
     */
    fun shareEvent() {
        val event = (_eventState.value as? UiState.Success)?.data ?: return
        _uiEvents.trySend(EventDetailsEvent.ShareEvent(event))
    }

    /**
     * Open venue in maps.
     */
    fun openMaps() {
        val event = (_eventState.value as? UiState.Success)?.data ?: return
        _uiEvents.trySend(
            EventDetailsEvent.OpenMaps(
                latitude = event.venue.latitude,
                longitude = event.venue.longitude,
                name = event.venue.name
            )
        )
    }

    /**
     * Add event to calendar.
     */
    fun addToCalendar() {
        val event = (_eventState.value as? UiState.Success)?.data ?: return
        _uiEvents.trySend(EventDetailsEvent.AddToCalendar(event))
    }

    /**
     * Set authentication status.
     */
    fun setAuthenticated(isAuthenticated: Boolean) {
        _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
    }

    /**
     * Get total price for selected tickets.
     */
    fun getTotalPrice(): Double {
        val state = _uiState.value
        val ticketType = state.selectedTicketType ?: return 0.0
        return ticketType.price * state.quantity
    }
}

/**
 * UI State for Event Details Screen.
 */
data class EventDetailsUiState(
    val selectedTicketType: TicketType? = null,
    val quantity: Int = 1,
    val isLiked: Boolean = false,
    val isPurchasing: Boolean = false,
    val isAuthenticated: Boolean = false,
    val showAuthPrompt: Boolean = false
)

/**
 * One-time events for Event Details Screen.
 */
sealed class EventDetailsEvent {
    data class NavigateToPurchase(
        val eventId: String,
        val ticketTypeId: String,
        val quantity: Int
    ) : EventDetailsEvent()

    data object ShowAuthPrompt : EventDetailsEvent()
    data class ShareEvent(val event: Event) : EventDetailsEvent()
    data class OpenMaps(
        val latitude: Double,
        val longitude: Double,
        val name: String
    ) : EventDetailsEvent()

    data class AddToCalendar(val event: Event) : EventDetailsEvent()
}
