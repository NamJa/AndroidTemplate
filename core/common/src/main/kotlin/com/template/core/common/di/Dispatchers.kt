package com.template.core.common.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: TemplateDispatchers)

enum class TemplateDispatchers {
    Default,
    IO,
}
