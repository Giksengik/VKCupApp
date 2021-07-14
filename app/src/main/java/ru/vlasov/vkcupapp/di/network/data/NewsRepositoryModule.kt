package ru.vlasov.vkcupapp.di.network.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn

import dagger.hilt.android.components.ViewModelComponent
import ru.vlasov.vkcupapp.repository.NewsRepository
import ru.vlasov.vkcupapp.repository.NewsRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class NewsRepositoryModule {

    @Binds
    abstract fun bindNewsRepository(newsRepository: NewsRepositoryImpl) : NewsRepository
}