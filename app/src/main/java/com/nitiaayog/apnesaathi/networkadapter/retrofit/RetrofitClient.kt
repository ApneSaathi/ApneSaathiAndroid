package com.nitiaayog.apnesaathi.networkadapter.retrofit

import android.app.Application
import com.google.gson.GsonBuilder
import com.nitiaayog.apnesaathi.BuildConfig
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.ApiInterface
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val TAG: String = RetrofitClient::class.java.simpleName

    private const val REQUEST_TIMEOUT:Long = 10

    private var apiInterface: ApiInterface? = null
    private var httpClient: OkHttpClient? = null

    fun createApiClient(application: Application): ApiInterface = initClient(application)

    private fun initClient(application: Application): ApiInterface {
        if (httpClient == null) setupOkHttpWithCache(application)

        if (apiInterface == null) {
            apiInterface = Retrofit.Builder().baseUrl(BuildConfig.HOST_URL)
                .client(httpClient!!)
                .addConverterFactory(
                    GsonConverterFactory.create(GsonBuilder().setLenient().create())
                )
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build()
                .create(ApiInterface::class.java)
        }

        return apiInterface!!
    }

    private fun setupOkHttpWithCache(application: Application) {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cacheDir = File(application.cacheDir, "HttpCache")
        val cache = Cache(cacheDir, cacheSize.toLong())
        // TODO : Replace sample_certificate.pem with your server public
        //  certificate in raw resource and uncomment .setupNetworkSecurity(context)
        val httpBuilder = OkHttpClient().newBuilder()
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            //.setupNetworkSecurity(context)
            .cache(cache)

        initOkHttp(httpBuilder)

        httpClient = httpBuilder.build()

        /*if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            httpBuilder.addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Request-Type", "Android")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("User-Agent", "Android")
                        .build()
                    chain.proceed(request)
                }
        }
        httpClient = httpBuilder.build()*/
    }

    private fun initOkHttp(httpBuilder: OkHttpClient.Builder) {
        httpBuilder.connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpBuilder.addInterceptor(interceptor)
        }

        httpBuilder.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                /*.addHeader("Accept", "application/json")
                .addHeader("Request-Type", "Android")
                .addHeader("User-Agent", "Android")*/
                .build()
            chain.proceed(request)
        }
    }
}