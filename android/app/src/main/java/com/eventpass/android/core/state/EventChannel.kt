package com.eventpass.android.core.state

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * One-time event channel for UI events that should only be consumed once.
 *
 * Use this for:
 * - Navigation events
 * - Toast/Snackbar messages
 * - Dialog triggers
 *
 * This prevents events from being re-consumed on configuration changes.
 */
class EventChannel<T> {
    private val _events = Channel<T>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    suspend fun send(event: T) {
        _events.send(event)
    }

    fun trySend(event: T) {
        _events.trySend(event)
    }
}

/**
 * Common UI events that can be sent from ViewModels.
 */
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class ShowToast(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data object NavigateBack : UiEvent()
    data class ShowDialog(val title: String, val message: String) : UiEvent()
}
