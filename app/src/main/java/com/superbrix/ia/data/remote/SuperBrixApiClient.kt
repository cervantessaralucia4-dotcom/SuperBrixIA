package com.superbrix.ia.data.remote

import com.superbrix.ia.core.config.AppConfig
import com.superbrix.ia.data.remote.models.ClasificacionRequest
import com.superbrix.ia.data.remote.models.ClasificacionResponse
import com.superbrix.ia.data.remote.models.EventoSyncDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface SuperBrixApiService {

    @POST("ia/clasificar")
    suspend fun clasificarNovedad(
        @Body request: ClasificacionRequest
    ): Response<ClasificacionResponse>

    @POST("eventos/")
    suspend fun registrarEvento(
        @Body evento: EventoSyncDto
    ): Response<Map<String, Any>>
}

object SuperBrixApiClient {

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    val apiService: SuperBrixApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SuperBrixApiService::class.java)
    }
}
