package com.alfanthariq.skeleton.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    val REQUEST_TIMEOUT = 240

    fun getClient(context: Context, apiToken: String?, apiURL : String): Retrofit {
        val sharedPreferences = context.getSharedPreferences("setting.conf", Context.MODE_PRIVATE)
        //val apiAddress = sharedPreferences.getString("api", "http://deka-api.duakelinci.id:13511/sfa/")

        val apiAddress = apiURL

        val okHttpClient = initOkHttp(context, apiToken)
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(apiAddress)
                .client(okHttpClient)
                // .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit
    }

    private fun initOkHttp(context: Context, apiToken: String?): OkHttpClient {

        val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                if (apiToken != null) {
                    requestBuilder.addHeader("token", apiToken)
                }

                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        })

        //setup cache and add cache to the client
        /*val cacheSize = 10 * 1024 * 1024 // 10 Mb
        httpClient.cache(Cache(context.cacheDir, cacheSize.toLong()))
        httpClient.addNetworkInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalResponse = chain.proceed(chain.request())
                val cacheControl = originalResponse.header("Cache-Control")
                return if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                    originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, max-age=" + 60 * 60 * 24 * 7)
                            .build()
                } else {
                    originalResponse
                }
            }
        })

        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()
                if (!NetworkUtil.isNetworkConnected(context)) {
                    request = request.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, only-if-cached")
                            .build()
                }
                return chain.proceed(request)
            }
        })*/
//        okHttpClient = httpClient.build()
        val okHttpClient = httpClient.build()
        return okHttpClient
    }
}