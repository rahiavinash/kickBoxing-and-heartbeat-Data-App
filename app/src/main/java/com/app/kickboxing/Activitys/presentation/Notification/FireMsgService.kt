package com.app.kickboxing.Activitys.presentation.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.app.kickboxing.Activitys.Ui.Activities.SplashScreenActivity
import com.app.kickboxing.Activitys.presentation.MainApplication
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.util.Date
import kotlin.coroutines.resume

class FireMsgService : FirebaseMessagingService() {
    val TAG = "FireMsgService"


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FcmTokenMessage", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "MessageNotification: ${message.data}")
        Companion.createNotificationChannel(this)
        createNotificationChannel(message)
    }

    private fun createNotificationChannel(remoteMessage: RemoteMessage?) {
        Companion.createNotificationChannel(this)
        val intent = if (MainApplication.AppStateManager.isAppInForeground()) {
            Log.d("ChatMessage", "App is active")
            Intent(this, HomesActivity::class.java)
        } else {
            Log.d("ChatMessage", "App is not active")
            Intent(this, SplashScreenActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(HomesActivity::class.java)

        val data = remoteMessage?.data
        Log.d("FireMsgService", "DATA: $data")

        val rawJsonString = data?.get("data")
        val dataObject = JSONObject(rawJsonString ?: "{}")
        val notificationDetail = dataObject.optJSONObject("notificationDetail")
        var userId: String? = null


        stackBuilder.addNextIntent(intent)

        val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
        val pIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        createNotificationLayout(dataObject, pIntent, userId)
    }

    private fun createNotificationLayout(dataObject: JSONObject, pIntent: PendingIntent, userId: String?) {
        val notificationDetail = dataObject.optJSONObject("notificationDetail")
        val sender = notificationDetail?.optJSONObject("sender")
        val notificationType = notificationDetail?.optString("type", null)
        val description = dataObject.optString("description", "New Message")
        val message = dataObject.optString("message", getString(R.string.app_name))
        val senderName = sender?.optString("name") ?: getString(R.string.app_name)
        val imageUrl = sender?.optString("image", null)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val translatedDescription = description
                Log.d(TAG, "Translated description: $translatedDescription")

                val remoteViews = RemoteViews(packageName, R.layout.layout_custom_push_notification).apply {
                    setTextViewText(R.id.title, getString(R.string.app_name))
                    setTextViewText(R.id.message, translatedDescription)
                    setImageViewResource(R.id.large_icon, R.drawable.app_logo)
                }

                val remoteExpandedViews = RemoteViews(packageName, R.layout.layout_custom_push_notification_expanded).apply {
                    setTextViewText(R.id.title, getString(R.string.app_name))
                    setTextViewText(R.id.message, translatedDescription)
                    setImageViewResource(R.id.large_icon, R.drawable.app_logo)
                }

                val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()

                val notificationBuilder = NotificationCompat.Builder(this@FireMsgService, getString(R.string.app_name)).apply {
                    setSmallIcon(R.drawable.app_logo)
                    setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    setCustomContentView(remoteViews)
                    setCustomBigContentView(remoteExpandedViews)
                    setContentIntent(pIntent)
                    setAutoCancel(true)
                    setPriority(NotificationCompat.PRIORITY_HIGH)
                    setCategory(NotificationCompat.CATEGORY_MESSAGE)
                }

                // SHOW the notification
                if (ActivityCompat.checkSelfPermission(
                        this@FireMsgService,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //  FIX: Actually notify
                    notificationManagerr.notify(notificationId, notificationBuilder.build())
                } else {
                    Log.e(TAG, "Notification permission not granted")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error creating notification: ${e.message}")
            }
        }
    }


    override fun handleIntent(intent: Intent?) {
        try {
            if (intent?.extras != null) {
                val builder = RemoteMessage.Builder("MessagingService")
                for (key in intent.extras!!.keySet()) {
                    builder.addData(key, intent.extras!!.get(key).toString())
                }
                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling intent: ${e.message}")
            super.handleIntent(intent)
        }
    }




    companion object {
        const val NOTIFICATION_TYPE_CHAT = "chat"
        const val NOTIFICATION_TYPE_POLYGON = "polygon"
        lateinit var notificationManagerr: NotificationManager

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val description = context.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(
                    context.getString(R.string.app_name),
                    name,
                    importance
                ).apply {
                    this.description = description
                }
                notificationManagerr = context.getSystemService(NotificationManager::class.java)
                notificationManagerr.createNotificationChannel(channel)
                Log.d("FireMsgService", "Notification channel created")
            }
        }
    }
}