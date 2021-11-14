package com.example.musicplayerdemo.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.databinding.ActivityPlayerBinding
import com.example.musicplayerdemo.ui.background.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicList: ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerDemo_NoActionBar)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        initializeLayout()
        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }
        binding.btnPrevious.setOnClickListener { prevNextSong(false) }
        binding.btnNext.setOnClickListener { prevNextSong(true) }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
        binding.iBtnRepeat.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.iBtnRepeat.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                repeat = false
                binding.iBtnRepeat.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }

    }

    private fun setLayout() {
        val data = musicList[songPosition]
        Glide.with(this)
            .load(data.artUri)
            .apply(RequestOptions.placeholderOf(R.drawable.splash_screen).centerCrop())
            .into(binding.ivImage)
        binding.tvSongName.text = data.title
        if (repeat) binding.iBtnRepeat.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.purple_500
            )
        )

    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.btnPlayPause.setIconResource(R.drawable.ic_pause_icon)
            musicService!!.showNotification(R.drawable.ic_pause_icon)
            binding.tvSeekBarStart.text =
                songDurationConvert(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text =
                songDurationConvert(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        } catch (e: Exception) {
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicList = ArrayList()
                musicList.addAll(MainActivity.MusicList)
                setLayout()
                createMediaPlayer()
            }
            "MainActivity" -> {
                musicList = ArrayList()
                musicList.addAll(MainActivity.MusicList)
                musicList.shuffle()
                setLayout()
                createMediaPlayer()
            }
        }
    }

    private fun playMusic() {
        binding.btnPlayPause.setIconResource(R.drawable.ic_pause_icon)
        musicService!!.showNotification(R.drawable.ic_pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnPlayPause.setIconResource(R.drawable.ic_play_icon)
        musicService!!.showNotification(R.drawable.ic_play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(true)
            setLayout()
            createMediaPlayer()

        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()

        }

    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetUp()

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }
}