package com.app.kickboxing.Activitys.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.kickboxing.Activitys.domain.model.HomeRequest
import com.app.kickboxing.Activitys.domain.response.BluetoothDeviceModel
import com.app.kickboxing.Activitys.domain.response.HomeData
import com.app.kickboxing.Activitys.domain.response.JoinData
import com.app.kickboxing.Activitys.domain.response.JoinWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.ProfileData
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.MainApplication
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.FilterType
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.Model.PunchData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Prefs {

    var sharedPreferences: SharedPreferences =
        MainApplication.get().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)

    val gson = Gson()

    init {
        instance = this
    }

    companion object {
        private const val APP_NAME = "KBXC"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN)"
        private const val IS_PROFILE_COMPLETED = "IS_PROFILE_COMPLETED"
        private const val IS_ROUTE_COMPLETED = "IS_ROUTE_COMPLETED"
        private const val MY_PROFILE = "MY_PROFILE"
        private const val IS_JOINED = "IS_JOINED"
        private const val JOINED_SESSION = "JOINED_SESSION"
        private const val HOME = "HOME"
        private const val PUNCH_DATA = "PUNCH_DATA"
        private const val DEVICE_TOKEN = "DEVICE_TOKEN"
        private const val SELECTED_FILTER = "SELECTED_FILTER"
        private const val KO_POINT = "KO_POINT"
        private const val DEVICE_LIST = "DEVICE_LIST"
        private const val KEY_BLE_DEVICE_LIST = "KEY_BLE_DEVICE_LIST"
        private const val SCHEDULE_LIST = "SCHEDULE_LIST"

        private var instance: Prefs? = null
        fun init(): Prefs {
            if (instance == null) {
                instance = Prefs()
            }
            return instance!!
        }
    }

    private val _scheduleListFlow = MutableStateFlow(getObject<List<WorkoutData>>(SCHEDULE_LIST) ?: emptyList())
    val scheduleListFlow = _scheduleListFlow.asStateFlow()

    fun saveObject(key: String, obj: Any) {
        val json = Gson().toJson(obj)
        sharedPreferences.edit().putString(key, json).apply()
    }

    inline fun <reified T> getObject(key: String): T? {
        val json = sharedPreferences.getString(key, null) ?: return null
        return Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    var accessToken: String
        get() {
            val sF = MainApplication.get().applicationContext
                .getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            return sF.getString(ACCESS_TOKEN, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().applicationContext
                .getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            sF.edit().putString(ACCESS_TOKEN, value).apply()
        }

    var isProfileCompleted: Boolean
        get() {
            val sF = MainApplication.get().applicationContext
                .getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            return sF.getBoolean(IS_PROFILE_COMPLETED, false) ?: false
        }
        set(value) {
            val sF = MainApplication.get().applicationContext
                .getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            sF.edit().putBoolean(IS_PROFILE_COMPLETED, value).apply()
        }

    var myProfileData: ProfileData
        get() {
            return getObject<ProfileData>(MY_PROFILE) ?: ProfileData()
        }
        set(value) {
            saveObject(MY_PROFILE, value)
        }

    var deviceToken: String
        get() {
            return sharedPreferences.getString(DEVICE_TOKEN, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(DEVICE_TOKEN, value).apply()
        }

    var isJoiningID: String
        get() {
            return sharedPreferences.getString(IS_JOINED, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(IS_JOINED, value).apply()
        }

    var isSelectedFilter : String?
        get() {
            return sharedPreferences.getString(SELECTED_FILTER, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(SELECTED_FILTER, value).apply()
        }

    var joinedSession: WorkoutData?
        get() {
            val json = sharedPreferences.getString(JOINED_SESSION, null)
            return if (json != null) gson.fromJson(json, WorkoutData::class.java) else null
        }
        set(value) {
            val json = gson.toJson(value)
            sharedPreferences.edit().putString(JOINED_SESSION, json).apply()
        }

    var recentStat: PunchData?
        get() {
            val json = sharedPreferences.getString(PUNCH_DATA, null)
            return if (json != null) gson.fromJson(json, PunchData::class.java) else null
        }
        set(value) {
            val json = gson.toJson(value)
            sharedPreferences.edit().putString(PUNCH_DATA, json).apply()
        }

    var koPoint : String?
        get() {
            return sharedPreferences.getString(KO_POINT, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(KO_POINT, value).apply()
        }

    var homeData: HomeData
        get() {
            return getObject<HomeData>(HOME) ?: HomeData()
        }
        set(value) {
            saveObject(HOME, value)
        }


    var scheduleList: List<WorkoutData>
    get() = getObject(SCHEDULE_LIST) ?: emptyList()
    set(value) {
        saveObject(SCHEDULE_LIST, value)
        _scheduleListFlow.value = value // update flow
    }


    fun saveDeviceList(list: List<String>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(DEVICE_LIST, json).apply()
    }

    fun getRecentDeviceList(): List<String> {
        val json = sharedPreferences.getString(DEVICE_LIST, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addBluetoothDeviceToList(context: Context, newDevice: BluetoothDeviceModel) {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Load existing list
        val existingJson = prefs.getString(KEY_BLE_DEVICE_LIST, null)
        val type = object : TypeToken<MutableList<BluetoothDeviceModel>>() {}.type
        val existingList: MutableList<BluetoothDeviceModel> =
            if (existingJson != null) gson.fromJson(existingJson, type) else mutableListOf()

        // Check if device already exists (based on MAC or ID, adjust as needed)
        if (existingList.none { it.deviceMac == newDevice.deviceMac }) {
            existingList.add(newDevice)
        }

        // Save updated list
        val updatedJson = gson.toJson(existingList)
        prefs.edit().putString(KEY_BLE_DEVICE_LIST, updatedJson).apply()
    }
    fun getBluetoothDeviceList(context: Context): List<BluetoothDeviceModel> {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_BLE_DEVICE_LIST, null)
        return if (json != null) {
            val type = object : TypeToken<List<BluetoothDeviceModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun scheduleFromPrefs() {
        val savedList = Prefs.init().scheduleList
        scheduleList = savedList
        Log.d("HeartRateViewModel", "Loaded scheduleList from Prefs: $savedList")
    }






}