package com.eventpass.android.core.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Extensions for collecting state in Compose with lifecycle awareness.
 *
 * SwiftUI → Compose state collection mapping:
 * - .onReceive(publisher) → collectAsStateWithLifecycle
 * - @ObservedObject → collectAsState
 * - @EnvironmentObject → CompositionLocal + collectAsState
 */

/**
 * Collects a StateFlow as Compose State with lifecycle awareness.
 * This is the recommended way to collect state in Compose UI.
 */
@Composable
fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> {
    return this.flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
        .collectAsState(initial = this.value, context = context)
}

/**
 * Collects flow events with lifecycle awareness.
 * Use for one-time events like navigation or snackbar messages.
 */
@Composable
fun <T> CollectEventsWithLifecycle(
    events: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: suspend (T) -> Unit
) {
    LaunchedEffect(events, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            events.collect { event ->
                collector(event)
            }
        }
    }
}

/**
 * Handles UiState in a composable with loading, error, and success callbacks.
 */
@Composable
fun <T> UiState<T>.HandleState(
    onLoading: @Composable () -> Unit = {},
    onError: @Composable (String) -> Unit = {},
    onSuccess: @Composable (T) -> Unit
) {
    when (this) {
        is UiState.Idle -> { /* No-op */ }
        is UiState.Loading -> onLoading()
        is UiState.Error -> onError(message)
        is UiState.Success -> onSuccess(data)
    }
}

/**
 * Handles ActionState in a composable.
 */
@Composable
fun ActionState.HandleAction(
    onLoading: @Composable () -> Unit = {},
    onError: @Composable (String) -> Unit = {},
    onSuccess: @Composable () -> Unit = {}
) {
    when (this) {
        is ActionState.Idle -> { /* No-op */ }
        is ActionState.Loading -> onLoading()
        is ActionState.Error -> onError(message)
        is ActionState.Success -> onSuccess()
    }
}

/**
 * Extension for safely getting data from UiState.
 */
inline fun <T, R> UiState<T>.mapSuccess(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Idle -> UiState.Idle
        is UiState.Loading -> UiState.Loading
        is UiState.Error -> UiState.Error(message, throwable)
        is UiState.Success -> UiState.Success(transform(data))
    }
}

/**
 * Extension for combining two UiState values.
 */
fun <A, B, R> combineUiState(
    state1: UiState<A>,
    state2: UiState<B>,
    transform: (A, B) -> R
): UiState<R> {
    return when {
        state1 is UiState.Error -> UiState.Error(state1.message, state1.throwable)
        state2 is UiState.Error -> UiState.Error(state2.message, state2.throwable)
        state1 is UiState.Loading || state2 is UiState.Loading -> UiState.Loading
        state1 is UiState.Success && state2 is UiState.Success -> {
            UiState.Success(transform(state1.data, state2.data))
        }
        else -> UiState.Idle
    }
}
