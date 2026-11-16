package co.edu.uniquindio.apptours.di

import co.edu.uniquindio.apptours.data.repository.CloudinaryImageRepository
import co.edu.uniquindio.apptours.data.repository.FirebaseAuthRepository
import co.edu.uniquindio.apptours.data.repository.FirestoreCommentRepository
import co.edu.uniquindio.apptours.data.repository.FirestoreModerationRepository
import co.edu.uniquindio.apptours.data.repository.FirestoreNotificationRepository
import co.edu.uniquindio.apptours.data.repository.FirestorePlaceRepository
import co.edu.uniquindio.apptours.data.repository.FirestoreUserRepository
import co.edu.uniquindio.apptours.data.repository.GeminiDuplicateRepository
import co.edu.uniquindio.apptours.data.repository.FirestoreVisitRepository
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.CommentRepository
import co.edu.uniquindio.apptours.domain.repository.DuplicateDetectionRepository
import co.edu.uniquindio.apptours.domain.repository.ImageRepository
import co.edu.uniquindio.apptours.domain.repository.ModerationRepository
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Interfaz de dominio -> implementación: sesión en Firebase Auth, datos en Firestore, fotos en Cloudinary y duplicados con Gemini. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: FirestoreUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(impl: FirestorePlaceRepository): PlaceRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(impl: FirestoreCommentRepository): CommentRepository

    @Binds
    @Singleton
    abstract fun bindVisitRepository(impl: FirestoreVisitRepository): VisitRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: FirestoreNotificationRepository): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindModerationRepository(impl: FirestoreModerationRepository): ModerationRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(impl: CloudinaryImageRepository): ImageRepository

    @Binds
    @Singleton
    abstract fun bindDuplicateDetectionRepository(impl: GeminiDuplicateRepository): DuplicateDetectionRepository
}
