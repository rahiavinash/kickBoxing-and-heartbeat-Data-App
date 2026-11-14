package com.app.kickboxing.Activitys.Data.api

import com.app.kickboxing.Activitys.domain.model.CancelWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.HomeRequest
import com.app.kickboxing.Activitys.domain.model.JoinWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.LeaderboardDetailRequest
import com.app.kickboxing.Activitys.domain.model.LoginRequest
import com.app.kickboxing.Activitys.domain.model.NotificationRequest
import com.app.kickboxing.Activitys.domain.model.RegisterRequest
import com.app.kickboxing.Activitys.domain.model.ResetPasswordRequest
import com.app.kickboxing.Activitys.domain.model.ScheduledWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.SendOTPRequest
import com.app.kickboxing.Activitys.domain.model.SessionDetailRequest
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.domain.model.VerifyOtpRequest
import com.app.kickboxing.Activitys.domain.response.BaseResponse
import com.app.kickboxing.Activitys.domain.response.CancelWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.CreateProfileResponse
import com.app.kickboxing.Activitys.domain.response.GetProfileResponse
import com.app.kickboxing.Activitys.domain.response.HomeResponse
import com.app.kickboxing.Activitys.domain.response.JoinWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.LeaderBoardResponse
import com.app.kickboxing.Activitys.domain.response.LoginResponse
import com.app.kickboxing.Activitys.domain.response.LogoutResponse
import com.app.kickboxing.Activitys.domain.response.NotificationResponse
import com.app.kickboxing.Activitys.domain.response.RegisterResponse
import com.app.kickboxing.Activitys.domain.response.ResetPasswordResponse
import com.app.kickboxing.Activitys.domain.response.SendOTPResponse
import com.app.kickboxing.Activitys.domain.response.SessionDetailResponse
import com.app.kickboxing.Activitys.domain.response.SessionResponse
import com.app.kickboxing.Activitys.domain.response.VerifyOTPResponse
import com.app.kickboxing.Activitys.domain.response.ScheduledWorkoutResponse
import com.app.kickboxing.Activitys.presentation.MainApplication
import com.app.kickboxing.R
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

interface ApiService {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body payload: LoginRequest): LoginResponse

    @POST("/api/v1/auth/register")
    suspend fun registerAccount(@Body payload: RegisterRequest): RegisterResponse

    @POST("/api/v1/boxing/home")
    suspend fun home(@Body payload: HomeRequest): HomeResponse

    @POST("/api/v1/auth/sendOtp")
    suspend fun sendOtp(@Body payload: SendOTPRequest): SendOTPResponse

    @POST("/api/v1/auth/verifyOtp")
    suspend fun verifyOtp(@Body payload:VerifyOtpRequest ):VerifyOTPResponse

    @POST("/api/v1/auth/resetPassword")
    suspend fun resetPassword(@Body payload: ResetPasswordRequest): ResetPasswordResponse

    @PATCH("/api/v1/auth/logout")
    suspend fun logout(): LogoutResponse


    @POST("/api/v1/boxing/sessions")
    suspend fun session(@Body payload: SessionRequest): SessionResponse

    @POST("/api/v1/boxing/leaderboard")
    suspend fun leaderboardDetails(@Body payload: LeaderboardDetailRequest): LeaderBoardResponse

    @POST("/api/v1/boxing/sessionDetail")
    suspend fun sessionDetails(@Body payload: SessionDetailRequest): SessionDetailResponse

    @POST("/api/v1/reservation/workouts")
    suspend fun scheduledWorkout(@Body payload: ScheduledWorkoutRequest): ScheduledWorkoutResponse

    @POST("/api/v1/reservation/join")
    suspend fun joinWorkout(@Body payload: JoinWorkoutRequest): JoinWorkoutResponse

    @POST("/api/v1/reservation/cancel")
    suspend fun cancelWorkout(@Body payload: CancelWorkoutRequest): CancelWorkoutResponse

    @POST("/api/v1/notification/list")
    suspend fun notification(@Body payload: NotificationRequest): NotificationResponse

    @Multipart
    @PUT("/api/v1/user/profile")
    suspend fun createProfile(
//        @Body payload: CreateProfileRequest,
        @Part("userName") userName: RequestBody?,
        @Part("fullName") fullName: RequestBody?,
        @Part("DOB") DOB : RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("weight") weight: RequestBody?,
        @Part("weightGoals") weightGoal: RequestBody?,
        @Part("punchAverageGoal") punchAverageGoal  : RequestBody?,
        @Part profile: MultipartBody.Part? = null,
        @Part("completeProfile") completeProfile: RequestBody?,

    ): CreateProfileResponse

    @GET("/api/v1/user/profile")
    suspend fun getProfile() : GetProfileResponse

    @Multipart
    @PUT("/api/v1/user/profile")
    suspend fun updateProfile(
        @Part("userName") userName: RequestBody?,
        @Part("fullName") fullName: RequestBody?,
        @Part("DOB") DOB: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("weight") weight: RequestBody?,
        @Part("weightGoals") weightGoal: RequestBody?,
        @Part("punchAverageGoal") punchAverageGoal   : RequestBody?,
        @Part profile: MultipartBody.Part? = null
    ) : GetProfileResponse


    fun getErrorMessageFromGenericResponse(
        exception: Exception,
    ): String? {
        var error = ""
        try {
            when (exception) {
                is HttpException -> {
                    val body = exception.response()?.errorBody()
                    val adapter = Gson().getAdapter(BaseResponse::class.java)
                    val errorParser = adapter.fromJson(body?.string())
                    val msg = errorParser.message ?: exception.message()
                    error = if (msg == "Unauthenticated.") {
                        MainApplication.instance.applicationContext.getString(R.string.auth_error)
                    } else {
                        msg
                    }
                }
                is ConnectException -> {
                    error =
                        MainApplication.instance.applicationContext.getString(R.string.no_internet_message)
                }

                is SocketTimeoutException -> {
                    error =
                        MainApplication.instance.applicationContext.getString(R.string.no_internet_message)

                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            return error
        }
    }


}

