package com.example.musicplayerdemo.ui.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.ui.activity.PlayerActivity
import com.example.musicplayerdemo.ui.activity.setSongPosition
import com.example.musicplayerdemo.ui.application.ApplicationClass
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> prevNextSong(false, context!!)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true, context!!)
            ApplicationClass.EXIT -> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_icon)
        PlayerActivity.binding.btnPlayPause.setIconResource(R.drawable.ic_pause_icon)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play_icon)
        PlayerActivity.binding.btnPlayPause.setIconResource(R.drawable.ic_play_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        val data = PlayerActivity.musicList[PlayerActivity.songPosition]
        Glide.with(context)
            .load(data.artUri)
            .apply(RequestOptions.placeholderOf(R.drawable.splash_screen).centerCrop())
            .into(PlayerActivity.binding.ivImage)
        PlayerActivity.binding.tvSongName.text = data.title
        playMusic()
    }
}