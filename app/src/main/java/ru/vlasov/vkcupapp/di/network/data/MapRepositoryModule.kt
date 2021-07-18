package ru.vlasov.vkcupapp.di.network.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.vlasov.vkcupapp.repository.MapRepository
import ru.vlasov.vkcupapp.repository.MapRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class MapRepositoryModule {

    @Binds
    abstract fun bindMapRepository(mapRepositoryImpl: MapRepositoryImpl) : MapRepository
}