package co.edu.uniquindio.apptours.di

import co.edu.uniquindio.apptours.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.apptours.data.repository.InMemoryCommentRepository
import co.edu.uniquindio.apptours.data.repository.InMemoryNotificationRepository
import co.edu.uniquindio.apptours.data.repository.InMemoryPlaceRepository
import co.edu.uniquindio.apptours.data.repository.InMemoryUserRepository
import co.edu.uniquindio.apptours.data.repository.InMemoryVisitRepository
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.CommentRepository
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Interfaz de dominio -> implementación. En IT-2/IT-3 se cambian por las de Firebase. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: InMemoryAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: InMemoryUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(impl: InMemoryPlaceRepository): PlaceRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(impl: InMemoryCommentRepository): CommentRepository

    @Binds
    @Singleton
    abstract fun bindVisitRepository(impl: InMemoryVisitRepository): VisitRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: InMemoryNotificationRepository): NotificationRepository
}
