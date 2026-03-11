package com.eventpass.android.core.state

/**
 * Generic UI State wrapper for async operations.
 *
 * SwiftUI → Compose state mapping:
 * - Swift enum AuthState { case idle, loading, success, error(String) }
 * - Kotlin sealed class UiState<T>
 *
 * This provides a consistent pattern for handling loading, success, and error states.
 */
sealed class UiState<out T> {
    /**
     * Initial idle state before any operation.
     */
    data object Idle : UiState<Nothing>()

    /**
     * Loading state while operation is in progress.
     */
    data object Loading : UiState<Nothing>()

    /**
     * Success state with data.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error state with message.
     */
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()

    /**
     * Check if currently loading.
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Check if operation succeeded.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Check if operation failed.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Get data if success, null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Get error message if error, null otherwise.
     */
    fun errorOrNull(): String? = (this as? Error)?.message
}

/**
 * Simplified state for operations without return data.
 */
sealed class ActionState {
    data object Idle : ActionState()
    data object Loading : ActionState()
    data object Success : ActionState()
    data class Error(val message: String) : ActionState()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}

/**
 * Extension to map Result to UiState.
 */
fun <T> Result<T>.toUiState(): UiState<T> {
    return fold(
        onSuccess = { UiState.Success(it) },
        onFailure = { UiState.Error(it.message ?: "Unknown error", it) }
    )
}

/**
 * Extension to handle UiState in composables.
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) {
        action(data)
    }
    return this
}

inline fun <T> UiState<T>.onError(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) {
        action(message)
    }
    return this
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) {
        action()
    }
    return this
}
