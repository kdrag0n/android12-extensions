package dev.kdrag0n.android12ext.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltDataModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder().run {
        baseUrl(PatreonDlService.BASE_URL)
        addConverterFactory(MoshiConverterFactory.create())
        build()
    }

    @Provides
    @Singleton
    fun providePatreonDlService(retrofit: Retrofit): PatreonDlService =
        retrofit.create(PatreonDlService::class.java)
}
