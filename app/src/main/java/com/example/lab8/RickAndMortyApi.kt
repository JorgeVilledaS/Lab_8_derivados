package com.example.lab8

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface RickAndMortyApi {

    @GET("character")
    suspend fun getAllCharacters(): CharactersResponse

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto

    @GET("location")
    suspend fun getAllLocations(): LocationsResponse

    @GET("location/{id}")
    suspend fun getLocationById(@Path("id") id: Int): LocationDto

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"

        fun create(): RickAndMortyApi {
            // Logging interceptor para debug
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Cliente HTTP con timeouts
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RickAndMortyApi::class.java)
        }
    }
}