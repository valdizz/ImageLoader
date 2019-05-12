package com.valdizz.imageloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.net.URL

/**
 * Foreground service loads a big image.
 *
 * @author Vlad Kornev
 */
class ImageLoaderService : Service() {

    private var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
    private val localBinder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return localBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    fun loadImage(imageUrl: String): Bitmap? {
        showProgressBar(true)
        val url = URL(imageUrl)
        return BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }

    fun stopProgress() {
        showProgressBar(false)
    }

    private fun showProgressBar(isShow: Boolean) {
        NotificationManagerCompat.from(this).apply {
            notificationBuilder.apply {
                setContentText(if (isShow) {getString(R.string.msg_download_start)} else {getString(R.string.msg_download_complete)})
                setProgress(0, 0, isShow)
            }
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
        }
        notificationBuilder.apply {
            setContentTitle(getString(R.string.image_loader_service))
            setSmallIcon(R.drawable.ic_file_download_white_24dp)
        }
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        with(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            createNotificationChannel(channel)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): ImageLoaderService {
            return this@ImageLoaderService
        }
    }

    private companion object {
        private const val CHANNEL_ID = "com.valdizz.imageloaderservice"
        private const val CHANNEL_NAME = "ImageLoaderChannel"
        private const val NOTIFICATION_ID = 1
    }
}