package com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout

import android.health.connect.datatypes.Device
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kickboxing.Activitys.domain.response.HomeData
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.utils.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HeartRateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()


    private val _sessionID = MutableStateFlow("")
    val sessionId = _sessionID.asStateFlow()

    private val _deviceName = MutableStateFlow("")
    val deviceName = _deviceName.asStateFlow()

    private val _boxingDeviceName = MutableStateFlow("")
    val boxingDeviceName = _boxingDeviceName.asStateFlow()

    private val _secondBoxingDeviceName = MutableStateFlow("")
    val secondBoxingDeviceName = _secondBoxingDeviceName.asStateFlow()

    private val _deviceStatus = MutableStateFlow("")
    val deviceStatus = _deviceStatus.asStateFlow()

    private val _punches = MutableStateFlow(0)
    val punches = _punches.asStateFlow()

    private var previousPunches: Int = 0

    private val _punchIncrementCounter = MutableStateFlow(0)
    val punchIncrementCounter = _punchIncrementCounter.asStateFlow()


    private val _punchPerMinutes = MutableStateFlow(0)
    val punchPerMinutes = _punchPerMinutes.asStateFlow()

    private val _localPunches = MutableStateFlow(0)
    val localPunches = _localPunches.asStateFlow()

    private val _punchingType = MutableStateFlow("")
    val punchingType = _punchingType.asStateFlow()

    private val _punchingTime = MutableStateFlow(0L)
    val punchingTime = _punchingTime.asStateFlow()


    private val _withdrawingTime = MutableStateFlow(0L)
    val withdrawingTime = _withdrawingTime.asStateFlow()

    private val _gForce = MutableStateFlow(0f)
    val gForce = _gForce.asStateFlow()

    private val _punchingSpeedForUI = MutableStateFlow(0f)
    val punchingSpeedForUI = _punchingSpeedForUI.asStateFlow()

    private val _punchingSpeed = MutableStateFlow(0f)
    val punchingSpeed = _punchingSpeed.asStateFlow()

    private val _punchingDistance = MutableStateFlow(0f)
    val punchingDistance = _punchingDistance.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _heartRateHistory = MutableStateFlow<List<Int>>(emptyList())
    val heartRateHistory = _heartRateHistory.asStateFlow()

    private val _avgGForceHistory = MutableStateFlow<List<Float>>(emptyList())
    val avgGForceHistory = _avgGForceHistory.asStateFlow()

    private val _timeInSeconds = MutableStateFlow(0)
    val timeInSeconds: StateFlow<Int> = _timeInSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isRunningStart= MutableStateFlow(false)
    val isRunningStart = _isRunningStart.asStateFlow()

    private val _connectedDeviceList = mutableStateListOf<String>()
    val connectedDeviceList: List<String> get() = _connectedDeviceList

    var scheduleList = mutableStateOf<List<WorkoutData>>(emptyList())
        private set
    private var job: Job? = null

    init {

        scheduleList.value = Prefs.init().scheduleList

        viewModelScope.launch {
            Prefs.init().scheduleListFlow.collect { newList ->
                scheduleList.value = newList
                Log.d("HeartRateViewModel01", "scheduleList updated from Prefs: $newList")
            }
        }

    }

    // Add session start and end times (in millis)
    var sessionStartTime: Long = 0L
    var sessionEndTime: Long = 0L

    fun setSessionTimes(start: Long, end: Long) {
        sessionStartTime = start
        sessionEndTime = end
    }

    fun startTimer(onTick: (Int) -> Unit, onSession: (Boolean) -> Unit) {

        if (_isRunning.value) return
        _isRunning.value = true
        onSession(true)
        _isRunningStart.value=true
        job = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _timeInSeconds.value += 1
                onTick(_timeInSeconds.value)
            }

        }

    }

    fun stopTimer(onSession: (Boolean) -> Unit) {
        _isRunning.value = false
        _isRunningStart.value=false
        _timeInSeconds.value = 0
        job?.cancel()
        onSession(false)
    }
    private var deviceListenerJob: Job? = null

    private val _gForcePercentage = MutableStateFlow(0.0)
    val gForcePercentage: StateFlow<Double> = _gForcePercentage

    private val _gForceIncrease = MutableStateFlow(false)
    val gForceIncrease: StateFlow<Boolean> = _gForceIncrease

    private val _scorePercentage = MutableStateFlow(0.0)
    val scorePercentage: StateFlow<Double> = _scorePercentage

    private val _scoreIncrease = MutableStateFlow(false)
    val scoreIncrease: StateFlow<Boolean> = _scoreIncrease

    private val _punchPercentage = MutableStateFlow(0.0)
    val punchPercentage: StateFlow<Double> = _punchPercentage

    private val _punchIncrease = MutableStateFlow(false)
    val punchIncrease: StateFlow<Boolean> = _punchIncrease

    private val _isInsideGeofence = MutableStateFlow(false)
    val isInsideGeofence: StateFlow<Boolean> = _isInsideGeofence.asStateFlow()

    fun setComparisonResults(

        gForcePercentage: Double, gForceIncrease: Boolean,
        scorePercentage: Double, scoreIncrease: Boolean,
        punchPercentage: Double, punchIncrease: Boolean
    ) {
        _gForcePercentage.value = gForcePercentage
        _gForceIncrease.value = gForceIncrease
        _scorePercentage.value = scorePercentage
        _scoreIncrease.value = scoreIncrease
        _punchPercentage.value = punchPercentage
        _punchIncrease.value = punchIncrease
    }

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private var punchCollectionJob: Job? = null
    private val _sessionStarted = MutableStateFlow(false)
    val sessionStarted: StateFlow<Boolean> = _sessionStarted

    var homeData by mutableStateOf<HomeData?>(null)
        private set

    fun updateHomeData(data: HomeData?) {
        homeData = data
        Log.d("HeartViewModel", "Updated homeData: $homeData")
    }

    fun updatePunchSpeed(value: Float){
        viewModelScope.launch {
            _punchingSpeedForUI.value = value
        }
    }

    fun updateHeartRate(value: Int) {
        viewModelScope.launch {
            _heartRate.value = value
        }
    }

    fun updateRunningStart(value: Boolean) {
        viewModelScope.launch {
            _isRunningStart.value = value
        }
    }

    fun updateConnectedDevice(deviceName: String, isConnected: Boolean) {
        if (isConnected) {
            // Add if not already present
            if (!_connectedDeviceList.contains(deviceName)) {
                _connectedDeviceList.add(deviceName)
            }
        } else {
            // Remove if present
            _connectedDeviceList.remove(deviceName)
        }
    }


    fun updateLocalPunchValue(value: Int) {
        viewModelScope.launch {
            _localPunches.value = value

            Log.e("local","${_localPunches.value}")
        }
    }

    fun updatePunchesPerMinutes(value: Int) {
        viewModelScope.launch {
            _punchPerMinutes.value = value

            Log.e("these","${_punchPerMinutes.value}")

        }
    }


    fun resetPunchingSpeed() {
        _punchingSpeed.value = 0f
    }

    fun updateSessionID(value: String) {
        viewModelScope.launch {
            _sessionID.value = value
        }
    }

    fun updatePunchIncrementValue(value: Int) {
        viewModelScope.launch {
            _punchIncrementCounter.value = value
        }
    }

    fun connectedDeviceName(value: String) {
        viewModelScope.launch {
            _deviceName.value = value
        }
    }
    fun connectedBoxingDeviceName(value: String) {
        viewModelScope.launch {
            _boxingDeviceName.value = value
        }
    }

    fun connectedSecondBoxingDeviceName(value: String) {
        viewModelScope.launch {
            _secondBoxingDeviceName.value = value
        }
    }


    fun connectedDeviceStatus(value: String) {
        viewModelScope.launch {
            _deviceStatus.value = value
        }
    }

    fun updateBoxingDate(totalPunches: Int ,
                          punchingType: String,
                          punchingTime: Long,
                          withdrawingTime: Long,
                          gForce: Float,
                          punchingSpeed: Float,
                          punchingDistance: Float,
                         ) {
        viewModelScope.launch {
            _punches.value = totalPunches
            _punchingType.value = punchingType
            _punchingTime.value = punchingTime
            _withdrawingTime.value = withdrawingTime
            _gForce.value = gForce
            _punchingSpeed.value = punchingSpeed
            _punchingDistance.value = punchingDistance
        }
    }



    fun addHeartRateToHistory(heartRateValue: Int , gForceValue:Float ) {
        viewModelScope.launch {
            _heartRateHistory.value += heartRateValue
            _avgGForceHistory.value += gForceValue


        }
    }
    fun updateConnectionStatus(status: String) {
        viewModelScope.launch {
            _connectionStatus.value = status


        }
    }

    fun updateGeofenceStatus(isInside: Boolean) {
        viewModelScope.launch {
            _isInsideGeofence.value = isInside
            Log.d("HeartRateViewModel", "Geofence status updated: $isInside")
        }
    }

    //  Auto control timer here
    fun handleGeofenceTimer(onTick: (Int) -> Unit, onSession: (Boolean) -> Unit) {
        viewModelScope.launch {
            isInsideGeofence.collect { inside ->
                if (inside && !_isRunning.value) {
                    startTimer(onTick, onSession)
                } else if (!inside && _isRunning.value) {
                    stopTimer(onSession)
                }
            }
        }
    }


    fun resetPunchIncrementCounter() {
        _punchIncrementCounter.value = 0
    }

    fun canStartSession(): Boolean {
        return !deviceName.value.isNullOrEmpty() &&
                !boxingDeviceName.value.isNullOrEmpty() &&
                sessionStarted.value
    }


}

