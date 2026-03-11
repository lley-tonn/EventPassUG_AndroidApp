package com.eventpass.android.features.attendee.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.UiState
import com.eventpass.android.data.repository.TicketRepository
import com.eventpass.android.domain.models.Ticket
import com.eventpass.android.domain.models.TicketScanStatus
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
 * ViewModel for My Tickets Screen.
 *
 * Manages ticket listing with filtering by status (upcoming, past, all).
 */
@HiltViewModel
class MyTicketsViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    // MARK: - State

    private val _ticketsState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Idle)
    val ticketsState: StateFlow<UiState<List<Ticket>>> = _ticketsState.asStateFlow()

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    private val _selectedFilter = MutableStateFlow(TicketFilter.UPCOMING)

    val selectedFilter: StateFlow<TicketFilter> = _selectedFilter.asStateFlow()

    /**
     * Filtered tickets based on selected filter.
     */
    val filteredTickets: StateFlow<List<Ticket>> = combine(
        _tickets,
        _selectedFilter
    ) { tickets, filter ->
        val now = LocalDateTime.now()
        when (filter) {
            TicketFilter.UPCOMING -> tickets.filter { ticket ->
                ticket.eventDate.isAfter(now) &&
                        ticket.scanStatus != TicketScanStatus.SCANNED
            }.sortedBy { it.eventDate }

            TicketFilter.PAST -> tickets.filter { ticket ->
                ticket.eventDate.isBefore(now) ||
                        ticket.scanStatus == TicketScanStatus.SCANNED
            }.sortedByDescending { it.eventDate }

            TicketFilter.ALL -> tickets.sortedByDescending { it.purchaseDate }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Ticket counts by status.
     */
    val ticketCounts: StateFlow<TicketCounts> = _tickets.combine(_selectedFilter) { tickets, _ ->
        val now = LocalDateTime.now()
        TicketCounts(
            upcoming = tickets.count { it.eventDate.isAfter(now) && it.scanStatus != TicketScanStatus.SCANNED },
            past = tickets.count { it.eventDate.isBefore(now) || it.scanStatus == TicketScanStatus.SCANNED },
            total = tickets.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketCounts())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // MARK: - Actions

    /**
     * Load user's tickets.
     */
    fun loadTickets() {
        viewModelScope.launch {
            _ticketsState.value = UiState.Loading

            ticketRepository.getMyTickets()
                .onSuccess { tickets ->
                    _tickets.value = tickets
                    _ticketsState.value = UiState.Success(tickets)
                }
                .onFailure { error ->
                    _ticketsState.value = UiState.Error(error.message ?: "Failed to load tickets")
                }
        }
    }

    /**
     * Refresh tickets (pull-to-refresh).
     */
    fun refreshTickets() {
        viewModelScope.launch {
            _isRefreshing.value = true

            ticketRepository.getMyTickets()
                .onSuccess { tickets ->
                    _tickets.value = tickets
                    _ticketsState.value = UiState.Success(tickets)
                }

            _isRefreshing.value = false
        }
    }

    /**
     * Set filter.
     */
    fun setFilter(filter: TicketFilter) {
        _selectedFilter.value = filter
    }

    /**
     * Get ticket by ID.
     */
    fun getTicketById(ticketId: String): Ticket? {
        return _tickets.value.find { it.id == ticketId }
    }
}

/**
 * Ticket filter options.
 */
enum class TicketFilter(val displayName: String) {
    UPCOMING("Upcoming"),
    PAST("Past"),
    ALL("All Tickets")
}

/**
 * Ticket counts by status.
 */
data class TicketCounts(
    val upcoming: Int = 0,
    val past: Int = 0,
    val total: Int = 0
)
