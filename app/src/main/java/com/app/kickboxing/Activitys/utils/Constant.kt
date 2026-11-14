package com.app.kickboxing.Activitys.utils

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.auth0.android.jwt.JWT
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.convertToMMddyyyy
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.FilterDates
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object MyUtils{
//    const val BASE_URL="http://192.168.3.223:7004/"
//    const val IMAGE_URL="http://192.168.3.223:7004/"
    //Live URL
    const val BASE_URL = "http://1.6.98.141:9152/"
   const val IMAGE_URL = "http://1.6.98.141:9152/"

    @SuppressLint("NewApi")

    fun getRelativeNotificationDateLabel(inputDate: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date = LocalDate.parse(inputDate, formatter)
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            when {
                date.isEqual(today) -> "Today"
                date.isEqual(yesterday) -> "Yesterday"
                else -> date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            }
        } catch (e: Exception) {
            ""
        }
    }



    fun isTokenExpired(): Boolean {
        val token: String = Prefs.init().accessToken
        return run {
            val jwt = JWT(token)
            Log.e(
                TAG,
                "isTokenExpired: " + System.currentTimeMillis() + "-" + jwt.expiresAt!!
                    .time + "=" + (System.currentTimeMillis() - jwt.expiresAt!!.time)
            )
            if (System.currentTimeMillis() > jwt.expiresAt!!.time && System.currentTimeMillis() - jwt.expiresAt!!
                    .time > 0
            ) {
                Log.e(TAG, "isTokenExpired: true")
                true
            } else {
                Log.e(TAG, "isTokenExpired: false")
                false
            }
        }
    }

     fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }



    fun parseDayAndMonth(dateString: String): Pair<Int, String> {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(dateString)!!
            val day = SimpleDateFormat("dd", Locale.getDefault()).format(date).toInt()
            val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date)

            Pair(day, month)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(0, "")
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getDefaultFilterDates(): FilterDates {
        val locale = Locale.getDefault()

        // Formatter for display format (e.g., "12 Jun 2025")
        val displayFormat = SimpleDateFormat("dd MMM yyyy", locale)

        // Formatter for UTC format (e.g., "2025-06-12T00:00:00.000Z")
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val calendar = Calendar.getInstance()

        // Get current date
        val endDate = calendar.time
        val endDateStr = displayFormat.format(endDate)
        val endDateUTCStr = utcFormat.format(endDate)

        // Go 15 days back
        calendar.add(Calendar.DAY_OF_MONTH, -15)
        val startDate = calendar.time
        val startDateStr = displayFormat.format(startDate)
        val startDateUTCStr = utcFormat.format(startDate)

        return FilterDates(
            startDate = startDateStr,
            startDateUTC = startDateUTCStr,
            endDate = endDateStr,
            endDateUTC = endDateUTCStr
        )
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToUTC(inputDate: String): String? {
        return try {
            // Parse the input date (no time-of-day, assumed start of day)
            val localDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
            val zoned = localDate.atStartOfDay(ZoneOffset.UTC)

            // Format as ISO 8601 UTC instant: yyyy‑MM‑dd'T'HH:mm:ss.SSS'Z'
            zoned.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    fun parseTimeAmPm(timeString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(timeString)!!
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date).uppercase()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}

