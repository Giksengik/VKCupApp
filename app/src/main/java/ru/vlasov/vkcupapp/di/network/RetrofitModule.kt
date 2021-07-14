package ru.vlasov.vkcupapp.di.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.vlasov.vkcupapp.network.json.vkapi.JsonVkApiPlaceholder
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val CONNECT_TIMEOUT = 10L
    private const val WRITE_TIMEOUT = 30L
    private const val READ_TIMEOUT = 10L

    @Provides
    @VkApiUrlAnnotation
    fun provideVkApiUrl() : String =
        "https://api.vk.com/method/"


    @Provides
    fun provideJson() : Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }


    @Provides
    fun provideHttpClient()  : OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @VkRetrofitAnnotation
    fun provideVkApiRetrofit(httpClient: OkHttpClient, json: Json,
                             @VkApiUrlAnnotation baseUrl: String) : Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(httpClient)
            .build()



    @Provides
    @VkAPI
    fun provideVkApi (@VkRetrofitAnnotation retrofit: Retrofit) : JsonVkApiPlaceholder =
        retrofit.create(JsonVkApiPlaceholder::class.java)



}