package com.rhodes.republicservices.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rhodes.republicservices.data.DriverDatabase
import com.rhodes.republicservices.data.DriverRepository
import com.rhodes.republicservices.data.DriverRepositoryImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import org.koin.dsl.module
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
val dataModule = module {

    //Kotlin serialization  JSON Parser
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = true
            allowStructuredMapKeys = true
            allowSpecialFloatingPointValues = true
        }
    }

    //Retrofit
    single {
        val contentType = MediaType.get("application/json")
        Retrofit.Builder()
            .baseUrl("https://d49c3a78-a4f2-437d-bf72-569334dea17c.mock.pstmn.io")
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
    }

    //Room DB
    single { DriverDatabase.getInstance(context = get()) }

    //Repository
    single<DriverRepository> {
        DriverRepositoryImpl(retrofit = get(), driverDb = get())
    }

}