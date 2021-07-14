package ru.vlasov.vkcupapp.di.network.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.FragmentScoped
import ru.vlasov.vkcupapp.network.json.vkapi.RemoteContentDataSource
import ru.vlasov.vkcupapp.network.json.vkapi.VkDataSource
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class ContentDataSourceModule {

    @Binds
    abstract fun bindContentDataSource(vkDataSource : VkDataSource) : RemoteContentDataSource

}