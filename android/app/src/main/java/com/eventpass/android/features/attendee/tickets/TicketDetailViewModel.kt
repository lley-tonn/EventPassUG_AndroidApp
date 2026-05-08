package com.eventpass.android.features.attendee.tickets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpass.android.core.state.UiState
import com.eventpass.android.data.repository.TicketRepository
import com.eventpass.android.domain.models.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Ticket Details screen. Loads a single [Ticket] by id from
 * the savedStateHandle (`ticketId` route arg).
 */
@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val ticketId: String = checkNotNull(savedStateHandle["ticketId"])

    private val _ticketState = MutableStateFlow<UiState<Ticket>>(UiState.Idle)
    val ticketState: StateFlow<UiState<Ticket>> = _ticketState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _ticketState.value = UiState.Loading
            ticketRepository.getTicketById(ticketId)
                .onSuccess { _ticketState.value = UiState.Success(it) }
                .onFailure {
                    _ticketState.value = UiState.Error(it.message ?: "Ticket not found")
                }
        }
    }
}
