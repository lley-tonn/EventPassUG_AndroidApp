package com.eventpass.android.features.attendee.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.UiState
import com.eventpass.android.data.repository.TicketRepository
import com.eventpass.android.domain.models.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for My Tickets Screen.
 *
 * Filtering matches the design reference: All / Active / Expired.
 * A ticket is Active when its event end date is still in the future; once
 * the event ends the ticket flips to Expired.
 */
@HiltViewModel
class MyTicketsViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _ticketsState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Idle)
    val ticketsState: StateFlow<UiState<List<Ticket>>> = _ticketsState.asStateFlow()

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    private val _selectedFilter = MutableStateFlow(TicketFilter.ALL)

    val selectedFilter: StateFlow<TicketFilter> = _selectedFilter.asStateFlow()

    val filteredTickets: StateFlow<List<Ticket>> = combine(
        _tickets,
        _selectedFilter
    ) { tickets, filter ->
        when (filter) {
            TicketFilter.ALL -> tickets.sortedByDescending { it.purchaseDate }
            TicketFilter.ACTIVE -> tickets.filterNot { it.isExpired }.sortedBy { it.eventDate }
            TicketFilter.EXPIRED -> tickets.filter { it.isExpired }.sortedByDescending { it.eventDate }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ticketCounts: StateFlow<TicketCounts> = _tickets.map {
        TicketCounts(
            all = it.size,
            active = it.count { t -> !t.isExpired },
            expired = it.count { t -> t.isExpired }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketCounts())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

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

    fun setFilter(filter: TicketFilter) {
        _selectedFilter.value = filter
    }

    fun getTicketById(ticketId: String): Ticket? =
        _tickets.value.find { it.id == ticketId }
}

enum class TicketFilter(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    EXPIRED("Expired")
}

data class TicketCounts(
    val all: Int = 0,
    val active: Int = 0,
    val expired: Int = 0
)
