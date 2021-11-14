package com.example.musicplayerdemo.ui.background

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.ui.activity.PlayerActivity
import com.example.musicplayerdemo.ui.activity.getImageArt
import com.example.musicplayerdemo.ui.activity.songDurationConvert
import com.example.musicplayerdemo.ui.application.ApplicationClass
import com.example.musicplayerdemo.ui.notification.NotificationReceiver

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    override fun onBind(p0: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int) {
        val preIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PLAY)
        val playPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.NEXT)
        val nextPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.EXIT)
        val exitPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = getImageArt(PlayerActivity.musicList[PlayerActivity.songPosition].path)
        val image = if(imgArt != null){
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        }else {
            BitmapFactory.decodeResource(resources, R.drawable.splash_screen)
        }

        val notification =
            NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                .setContentTitle(PlayerActivity.musicList[PlayerActivity.songPosition].title)
                .setContentText(PlayerActivity.musicList[PlayerActivity.songPosition].artist)
                .setSmallIcon(R.drawable.ic_music_icon)
                .setLargeIcon(image)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_previous_icon, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.ic_next_icon, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
                .build()

        startForeground(1, notification)

    }
    fun createMediaPlayer() {
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayerActivity.musicList[PlayerActivity.songPosition].path)
            mediaPlayer!!.prepare()
            PlayerActivity.binding.btnPlayPause.setIconResource(R.drawable.ic_pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text = songDurationConvert(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = songDurationConvert(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBar.progress = 0
            PlayerActivity.binding.seekBar.max = mediaPlayer!!.duration
        } catch (e: Exception) {
        }
    }
    fun seekBarSetUp(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = songDurationConvert(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)

    }
}