package com.app.kickboxing.Activitys.Data.reposistory

import android.content.Context
import com.app.kickboxing.Activitys.domain.model.CancelWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.CreateProfileRequest
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
import com.app.kickboxing.Activitys.domain.response.ScheduledWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.SendOTPResponse
import com.app.kickboxing.Activitys.domain.response.SessionDetailResponse
import com.app.kickboxing.Activitys.domain.response.SessionResponse
import com.app.kickboxing.Activitys.domain.response.VerifyOTPResponse
import com.app.kickboxing.Activitys.presentation.state.Results

interface AppRepository {
    suspend fun userLoginReq(payload: LoginRequest): Results<LoginResponse>
    suspend fun home(payload: HomeRequest): Results<HomeResponse>
    suspend fun userRegisterAccount(payload: RegisterRequest): Results<RegisterResponse>
    suspend fun userSendOTPRequest(payload: SendOTPRequest): Results<SendOTPResponse>
    suspend fun userVerifyOTPRequest(payload: VerifyOtpRequest): Results<VerifyOTPResponse>
    suspend fun userResetPassword(payload: ResetPasswordRequest): Results<ResetPasswordResponse>
    suspend fun userCreateProfile(
        payload: CreateProfileRequest,
        context: Context
    ): Results<CreateProfileResponse>

    suspend fun userGetProfile(): Results<GetProfileResponse>
    suspend fun userUpdateProfile(payload: CreateProfileRequest, context: Context): Results<GetProfileResponse>
    suspend fun session(payload: SessionRequest): Results<SessionResponse>
    suspend fun sessionDetails(payload: SessionDetailRequest): Results<SessionDetailResponse>
    suspend fun scheduledWorkout(payload: ScheduledWorkoutRequest): Results<ScheduledWorkoutResponse>
    suspend fun joinWorkout(payload: JoinWorkoutRequest): Results<JoinWorkoutResponse>
    suspend fun cancelWorkout(payload: CancelWorkoutRequest): Results<CancelWorkoutResponse>
    suspend fun leaderboardDetails(payload: LeaderboardDetailRequest): Results<LeaderBoardResponse>
    suspend fun logout(): Results<LogoutResponse>
    suspend fun notification(payload:NotificationRequest): Results<NotificationResponse>
}