package com.alfanthariq.skeleton.utils

import android.app.NotificationManager
import android.app.ActivityManager
import android.app.Notification
import android.os.Build
import android.media.RingtoneManager
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.app.PendingIntent
import android.content.Context
import android.text.TextUtils
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.alfanthariq.skeleton.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat


object NotificationUtils {
    private val TAG = NotificationUtils::class.java.simpleName

    fun showNotificationMessage(mContext : Context, title: String, message: String, timeStamp: String, intent: Intent, removable: Boolean) {
        showNotificationMessage(mContext, title, message, timeStamp, intent, null, removable)
    }

    fun showNotificationMessage(mContext : Context, title: String, message: String, timeStamp: String,
                                intent: Intent, imageUrl: String?, removable : Boolean) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return

        // notification icon
        val icon = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.mipmap.ic_launcher
        } else {
            R.mipmap.ic_launcher
        }

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val resultPendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(
                mContext)

        /*val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.packageName + "/raw/notification")

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                val bitmap = getBitmapFromURL(imageUrl)

                if (bitmap != null) {
                    showBigNotification(mContext, bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, removable)
                } else {
                    showSmallNotification(mContext, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, removable)
                }
            }
        } else {
            showSmallNotification(mContext, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, removable)
            playNotificationSound(mContext)
        }*/
        showBigNotification(mContext, null, mBuilder, icon, title, message, timeStamp, resultPendingIntent, null, removable)
    }


    private fun showSmallNotification(mContext : Context, mBuilder: NotificationCompat.Builder,
                                      icon: Int, title: String, message: String, timeStamp: String,
                                      resultPendingIntent: PendingIntent, alarmSound: Uri?, removable : Boolean) {

        val inboxStyle = NotificationCompat.InboxStyle()

        inboxStyle.addLine(message)

        val notification: Notification
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                //.setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                .setContentText(message)
                .setOngoing(!removable)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .build()

        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(99, notification)
    }

    private fun showBigNotification(mContext : Context, bitmap: Bitmap?, mBuilder: NotificationCompat.Builder,
                                    icon: Int, title: String, message: String, timeStamp: String,
                                    resultPendingIntent: PendingIntent, alarmSound: Uri?, removable : Boolean) {
        val bigPictureStyle = NotificationCompat.BigTextStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.bigText(message)

        val notification: Notification
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                //.setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                //.setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                .setOngoing(!removable)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .build()

        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(100, notification)
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    // Playing notification sound
    fun playNotificationSound(mContext : Context) {
        try {
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification")
            val r = RingtoneManager.getRingtone(mContext, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Method checks if the app is in background or not
     */
    fun isAppIsInBackground(mContext : Context): Boolean {
        var isInBackground = true
        val am = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == mContext.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo.packageName == mContext.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }

    // Clears notification tray messages
    fun clearNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun getTimeMilliSec(timeStamp: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = format.parse(timeStamp)
            return date.getTime()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }
}