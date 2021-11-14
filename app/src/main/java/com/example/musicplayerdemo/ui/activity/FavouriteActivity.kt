package com.example.musicplayerdemo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.databinding.ActivityFavouriteBinding

class FavouriteActivity: AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerDemo_NoActionBar)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}