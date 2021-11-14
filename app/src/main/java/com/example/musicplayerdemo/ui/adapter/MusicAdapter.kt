package com.example.musicplayerdemo.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.databinding.MusicViewBinding
import com.example.musicplayerdemo.ui.activity.Music
import com.example.musicplayerdemo.ui.activity.PlayerActivity
import com.example.musicplayerdemo.ui.activity.songDurationConvert

class MusicAdapter(private val context: Context, private val musicList: ArrayList<Music>) :
    RecyclerView.Adapter<MusicAdapter.SongViewHolder>() {
    class SongViewHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvSongName
        val image = binding.ivSongImage
        val album = binding.tvAlbum
        val duration = binding.tvSongDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layout = MusicViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val itemAtIndex = musicList[position]
        holder.title.text = itemAtIndex.title
        holder.album.text = itemAtIndex.album
        holder.duration.text = songDurationConvert(itemAtIndex.duration)
        Glide.with(context).load(itemAtIndex.artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}