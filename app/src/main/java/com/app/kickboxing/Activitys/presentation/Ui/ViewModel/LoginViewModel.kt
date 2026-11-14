package com.app.kickboxing.Activitys.presentation.Ui.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.app.kickboxing.Activitys.Data.reposistory.AppRepositoryImpl
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
import com.app.kickboxing.Activitys.domain.response.SessionData
import com.app.kickboxing.Activitys.domain.response.SessionDetailResponse
import com.app.kickboxing.Activitys.domain.response.SessionResponse
import com.app.kickboxing.Activitys.domain.response.VerifyOTPResponse
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.presentation.state.UIAction
import com.app.kickboxing.Activitys.presentation.state.UIState
import com.app.kickboxing.Activitys.utils.Event
import com.app.kickboxing.Activitys.utils.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private var repositoryImpl: AppRepositoryImpl
) :BaseViewModel<UIState , UIAction>(){

//    private val _loginResponseData = MutableStateFlow<List<LoginResponse>>(emptyList())
//    val loginResponseData: StateFlow<List<LoginResponse>> = _loginResponseData.asStateFlow()
    private val _sessionData = MutableStateFlow<SessionData?>(null)
    val sessionData = _sessionData.asStateFlow()


    private val _loginResponseData = MutableStateFlow<Event<Results<LoginResponse>>?>(null)
    val loginResponseData: StateFlow<Event<Results<LoginResponse>>?> = _loginResponseData

    private val _homeResponseData = MutableStateFlow<Event<Results<HomeResponse>>?>(null)
    val homeResponseData: StateFlow<Event<Results<HomeResponse>>?> = _homeResponseData


    private val _userRegisterData = MutableStateFlow<Event<Results<RegisterResponse>>?>(null)
    var userRegisterData: StateFlow<Event<Results<RegisterResponse>>?> = _userRegisterData

    private val _sendOtTPResponseData = MutableStateFlow<Event<Results<SendOTPResponse>>?>(null)
    val sendOtTPResponseData: StateFlow<Event<Results<SendOTPResponse>>?> = _sendOtTPResponseData

    private val _sessionResponseData = MutableStateFlow<Event<Results<SessionResponse>>?>(null)
    val sessionResponseData : StateFlow<Event<Results<SessionResponse>>?> = _sessionResponseData

    private val _sessionDetailResponseData = MutableStateFlow<Event<Results<SessionDetailResponse>>?>(null)
    val sessionDetailResponseData : StateFlow<Event<Results<SessionDetailResponse>>?> = _sessionDetailResponseData

    private val _scheduleWorkoutResponseData = MutableStateFlow<Event<Results<ScheduledWorkoutResponse>>?>(null)
    val scheduleWorkoutResponseData : StateFlow<Event<Results<ScheduledWorkoutResponse>>?> = _scheduleWorkoutResponseData

    private val _scheduledWorkouts = MutableStateFlow<List<WorkoutData>>(emptyList())
    val scheduledWorkouts: StateFlow<List<WorkoutData>> = _scheduledWorkouts


    private val _joinWorkoutResponseData = MutableStateFlow<Event<Results<JoinWorkoutResponse>>?>(null)
    val joinWorkoutResponseData : StateFlow<Event<Results<JoinWorkoutResponse>>?> = _joinWorkoutResponseData

    private val _cancelWorkoutResponseData = MutableStateFlow<Event<Results<CancelWorkoutResponse>>?>(null)
    val cancelWorkoutResponseData : StateFlow<Event<Results<CancelWorkoutResponse>>?> = _cancelWorkoutResponseData

    private val _leaderboardDetailResponseData = MutableStateFlow<Event<Results<LeaderBoardResponse>>?>(null)
    val leaderboardDetailResponseData: StateFlow<Event<Results<LeaderBoardResponse>>?> = _leaderboardDetailResponseData

    private val _verifyOtTPResponseData = MutableStateFlow<Event<Results<VerifyOTPResponse>>?>(null)
    val verifyOtTPResponseData: StateFlow<Event<Results<VerifyOTPResponse>>?> = _verifyOtTPResponseData

    private val _logoutResponseData = MutableStateFlow<Event<Results<LogoutResponse>>?>(null)
    val logoutResponseData: StateFlow<Event<Results<LogoutResponse>>?> = _logoutResponseData

    private val _userResetPasswordResponseData = MutableStateFlow<Event<Results<ResetPasswordResponse>>?>(null)
    val userResetPasswordResponseData: StateFlow<Event<Results<ResetPasswordResponse>>?> = _userResetPasswordResponseData

    private val _userCreateProfileResponseData = MutableStateFlow<Results<CreateProfileResponse>?>(null)
    val userCreateProfileResponseData: StateFlow<Results<CreateProfileResponse>?> = _userCreateProfileResponseData

    private val _userGetProfileResponseData = MutableStateFlow<Event<Results<GetProfileResponse>>?>(null)
    val userGetProfileResponseData: StateFlow<Event<Results<GetProfileResponse>>?> = _userGetProfileResponseData

    private val _userUpdateProfileResponseData = MutableStateFlow<Event<Results<GetProfileResponse>>?>(null)
    val userUpdateProfileResponseData: StateFlow<Event<Results<GetProfileResponse>>?> = _userUpdateProfileResponseData

    private val _notificationResponseData = MutableStateFlow<Event<Results<NotificationResponse>>?>(null)
    val notificationResponseData: StateFlow<Event<Results<NotificationResponse>>?> = _notificationResponseData




    init {
        sendAction(UIAction.LoadData)
    }
    fun userLogIn(payLoad: LoginRequest) {
        _loginResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userLoginReq(payLoad)
            _loginResponseData.value = Event(result)
        }
    }

    fun home(payLoad: HomeRequest) {
        _homeResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.home(payLoad)
            try {
                _homeResponseData.value = Event(result)
                Log.e("Sessiohnfgjfgjgfvtail", "Response = ${Event(result).getContentIfNotHandled()}")

            }catch (e: Exception){
                Log.e("Sessiohnfgjfgjgfvtail", "Error = ${e.message}")
            }
            Log.e("Sessiohnfgjfgjgfvtail", "homeState = $result")

        }
    }

    fun userLogOut() {
        _logoutResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.logout()
            _logoutResponseData.value = Event(result)
        }
    }

    fun userRegister(payLoad: RegisterRequest) {
        _userRegisterData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userRegisterAccount(payLoad)
            _userRegisterData.value = Event(result)
        }
    }

    fun sendOtTP(payLoad: SendOTPRequest) {
        _sendOtTPResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userSendOTPRequest(payLoad)
            _sendOtTPResponseData.value = Event(result)
        }
    }

    fun session(payLoad: SessionRequest) {
        _sessionResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.session(payLoad)
            _sessionResponseData.value = Event(result)


            Log.e("session req", result.toString())

        }
    }

    fun sessionDetail(payLoad: SessionDetailRequest) {
        _sessionDetailResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.sessionDetails(payLoad)
            _sessionDetailResponseData.value = Event(result)

             Log.e("dsjjmkhkhbjdf", result.toString())

        }
    }
    fun scheduledWorkout(payLoad: ScheduledWorkoutRequest) {
        _scheduleWorkoutResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.scheduledWorkout(payLoad)
            _scheduleWorkoutResponseData.value = Event(result)

            if (result is Results.Success) {
                _scheduledWorkouts.value = result.data.data?.list ?: emptyList()
            }

            Log.e("dsjbjdf", result.toString())
        }
    }



    fun leaderboardDetail(payLoad: LeaderboardDetailRequest) {
        _leaderboardDetailResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.leaderboardDetails(payLoad)
            _leaderboardDetailResponseData.value = Event(result)

            Log.e("isResultOfSessionTab", result.toString())
        }
    }


    fun verifyOtTP(payLoad: VerifyOtpRequest) {
        _verifyOtTPResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userVerifyOTPRequest(payLoad)
            _verifyOtTPResponseData.value = Event( result)
        }
    }

    fun userResetPassword(payLoad: ResetPasswordRequest) {
        _userResetPasswordResponseData.value =  Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userResetPassword(payLoad)
            _userResetPasswordResponseData.value =  Event(result)
        }
    }

    fun userCreateProfile(payLoad: CreateProfileRequest, context: Context) {
        _userCreateProfileResponseData.value = Results.Loading
        viewModelScope.launch {
            val result = repositoryImpl.userCreateProfile(payLoad, context)
            _userCreateProfileResponseData.value = result
        }
    }
    fun userGetProfile(){
        _userGetProfileResponseData.value =Event( Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userGetProfile()
            _userGetProfileResponseData.value = Event( result)
        }

    }

    fun userUpdateProfile(payLoad: CreateProfileRequest, context: Context) {
        _userUpdateProfileResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.userUpdateProfile(payLoad, context)
            _userUpdateProfileResponseData.value = Event(result)
            Log.d("UpdateProfileResponse","Response: ${result.toString()}")
        }
    }



    private val _joinSession = MutableStateFlow<WorkoutData?>(null)
    val joinSession = _joinSession.asStateFlow()

    private val _currentlyJoinedSessionId = MutableStateFlow<String?>(null)
    val currentlyJoinedSessionId: StateFlow<String?> = _currentlyJoinedSessionId


    fun joinWorkoutWithApi(payload: JoinWorkoutRequest, workoutData: WorkoutData) {
        _joinWorkoutResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.joinWorkout(payload)
            _joinWorkoutResponseData.value = Event(result)

            if (result is Results.Success) {
                val joinData = result.data.data
                _joinSession.value = workoutData.copy(
                    joined = true,
                    startTime = result.data.data?.startTime,
                    endTime = result.data.data?.endTime
                )
                _currentlyJoinedSessionId.value = payload.id

                Prefs.init().isJoiningID= payload.id.toString()
                Prefs.init().joinedSession= workoutData
                Log.d("JoinAPI", "Set joinSession: ${_joinSession.value}")
                Log.d("JoinAPI", "Set currentlyJoinedSessionId: ${_currentlyJoinedSessionId.value}")
            }
        }
    }

    fun cancelWorkoutWithApi(payload: CancelWorkoutRequest) {
        _cancelWorkoutResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.cancelWorkout(payload)
            _cancelWorkoutResponseData.value = Event(result)

            if (result is Results.Success) {
                _joinSession.value = null
                _currentlyJoinedSessionId.value = null
                Prefs.init().isJoiningID= ""
                Prefs.init().joinedSession= null
            }
            Log.e("this","${_joinSession.value}")
            Log.e("CancelWorkoutAPI", "API Result: $result")
        }
    }


    fun notification(payLoad: NotificationRequest) {
        _notificationResponseData.value = Event(Results.Loading)
        viewModelScope.launch {
            val result = repositoryImpl.notification(payLoad)
            _notificationResponseData.value = Event(result)

        }
    }



    fun updateCurrentIdIsNull(){
        _currentlyJoinedSessionId.value = null
        Prefs.init().isJoiningID= ""
        Prefs.init().joinedSession= null
    }



}