package com.lotusreichhart.audily.core.common.coroutines

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val audilyDispatcher: AudilyDispatchers)

enum class AudilyDispatchers {
    Default,
    IO,
    Main
}