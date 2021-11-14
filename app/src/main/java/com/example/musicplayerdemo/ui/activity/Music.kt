package com.example.musicplayerdemo.ui.activity

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String
)

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = TimeUnit.SECONDS.convert(
        duration,
        TimeUnit.MILLISECONDS
    ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)

    return String.format("%02d:%02d", minutes, seconds)

}

fun songDurationConvert(value: Long): String {
    val audioTime: String
    val dur = value.toInt()
    val hrs = dur / 3600000
    val mns = dur / 60000 % 60000
    val scs = dur % 60000 / 1000
    audioTime = if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mns, scs)
    } else {
        String.format("%02d:%02d", mns, scs)
    }
    return audioTime
}

fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture

}

fun setSongPosition(increment: Boolean) {
    if(!PlayerActivity.repeat){
        if (increment) {
            if (PlayerActivity.musicList.size - 1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = 0
            else ++PlayerActivity.songPosition

        } else {

            if (0 == PlayerActivity.songPosition) PlayerActivity.songPosition = PlayerActivity.musicList.size - 1
            else --PlayerActivity.songPosition
        }
    }
}


