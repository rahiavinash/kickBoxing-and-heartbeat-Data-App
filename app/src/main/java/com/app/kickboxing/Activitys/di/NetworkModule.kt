package com.app.kickboxing.Activitys.di

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.app.kickboxing.Activitys.Data.api.ApiService
import com.app.kickboxing.Activitys.Data.reposistory.AppRepositoryImpl
import com.app.kickboxing.Activitys.domain.response.LoggedInFromAnotherDevice
import com.app.kickboxing.Activitys.presentation.MainApplication
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.MyUtils.BASE_URL
import com.app.kickboxing.Activitys.utils.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(3000, TimeUnit.SECONDS)
            .readTimeout(3000, TimeUnit.SECONDS)
            .writeTimeout(3000, TimeUnit.SECONDS)
            .addInterceptor(provideHeaderInterceptor())
            .addInterceptor(ExceptionInterceptorWithoutEncryption())
            .addInterceptor(provideHttpLoggingInterceptor())
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        httpClient: OkHttpClient
    ) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun providerRepository(
        apiService: ApiService
    ) : AppRepositoryImpl {
        return AppRepositoryImpl(apiService)
    }
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
          //  .addHeader("Authorization", "Bearer ${Prefs}") // or "x-api-key"
            .addHeader("x-api-key", "K75A65T84N78E69S83T84J74S83") // or "x-api-key"
            .build()
        return chain.proceed(request)
    }



}
class ExceptionInterceptorWithoutEncryption : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            500 -> {
                return response
            }
            400 -> {
                return response
            }
            402 -> {
                return response
            }

            401, 403 -> {
                MainApplication.get().applicationContext.startActivity(
                    Intent(
                        MainApplication.get().applicationContext,
                        AuthActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        . putExtra("EXTRA_IS_LOGOUT_DUE_TO_SINGLE_SESSION", true)
                )
                Prefs.init().clear()
                return response
            }

            else -> return response
        }
    }
}

private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor =
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("API Logging", "response => $message")
            }
        })
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    return httpLoggingInterceptor
}

private fun provideHeaderInterceptor(): Interceptor {
    return Interceptor { chain ->
        val accessToken = if (Prefs.init().accessToken.isNullOrEmpty()) {
            ""
        } else if (!MyUtils.isTokenExpired()) {
            Prefs.init().accessToken
        }
        else {
            ""
        }

        Log.e("accessToken", accessToken ?: "null")

        val requestBuilder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")

        if (!TextUtils.isEmpty(accessToken)) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        } else {
            requestBuilder.addHeader("x-api-key", "K75A65T84N78E69S83T84J74S83")
        }

        val request = requestBuilder.build()
        return@Interceptor chain.proceed(request)
    }
}
