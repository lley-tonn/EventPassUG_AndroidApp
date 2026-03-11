package com.eventpass.android.core.di

import com.eventpass.android.data.repository.*
import com.eventpass.android.data.service.ScannerSessionService
import com.eventpass.android.data.service.ScannerSessionServiceProtocol
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository dependency injection module.
 * Binds repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        impl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        impl: TicketRepositoryImpl
    ): TicketRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindCancellationRepository(
        impl: CancellationRepositoryImpl
    ): CancellationRepository

    @Binds
    @Singleton
    abstract fun bindRefundRepository(
        impl: RefundRepositoryImpl
    ): RefundRepository

    @Binds
    @Singleton
    abstract fun bindRefundProcessor(
        impl: MockRefundProcessor
    ): RefundProcessor

    @Binds
    @Singleton
    abstract fun bindScannerSessionService(
        impl: ScannerSessionService
    ): ScannerSessionServiceProtocol
}
