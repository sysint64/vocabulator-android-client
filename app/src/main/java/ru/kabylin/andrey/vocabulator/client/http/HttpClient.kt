package ru.kabylin.andrey.vocabulator.client.http

import io.grpc.ManagedChannelBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.kabylin.andrey.vocabulator.BuildConfig
import ru.kabylin.andrey.vocabulator.Settings
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.services.DataStorage
import java.util.concurrent.TimeUnit

object HttpClient : Client() {
    val dataState = DataStorage()

    private val mainApiRetrofit by lazy {
        retrofitBuilder(Settings.serverUrl)
            .client(createHttpClientInstance())
            .build()
    }

    fun grpcChannel() =
        ManagedChannelBuilder.forAddress(Settings.serverUrl, Settings.serverPort)
            .usePlaintext()
            .build()

    private fun retrofitBuilder(endpoint: String): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(endpoint)
    }

    private fun createHttpClientInstance(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }


        httpClient.addNetworkInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())

            originalResponse.newBuilder()
                .header("Cache-Control", "public, max-age=10")
                .build()
        }

        return httpClient.build()
    }

    enum class Dest {
        MAIN_API,
    }

    fun <T> createRetrofitGateway(cls: Class<T>, dest: Dest = Dest.MAIN_API): T {
        return when (dest) {
            Dest.MAIN_API -> mainApiRetrofit.create(cls)
        }
    }
}
