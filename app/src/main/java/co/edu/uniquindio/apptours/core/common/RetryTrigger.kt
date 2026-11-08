package co.edu.uniquindio.apptours.core.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

/**
 * Botón "Reintentar": un flujo que terminó con error no vuelve a emitir, así que
 * [retry] lo crea de nuevo desde [restartable].
 */
class RetryTrigger {

    private val attempts = MutableStateFlow(0)

    fun retry() = attempts.update { it + 1 }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> restartable(block: () -> Flow<T>): Flow<T> = attempts.flatMapLatest { block() }
}
