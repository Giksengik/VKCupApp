package ru.vlasov.vkcupapp.di.network

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VkApiUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MapApiUrl