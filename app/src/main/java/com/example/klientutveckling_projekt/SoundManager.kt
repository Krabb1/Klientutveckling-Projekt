package com.example.klientutveckling_projekt


import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.provider.MediaStore

class BackgroundMusicManager(private val context: Context){

    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.backgroundmusic).apply {
        isLooping = true
    }


    fun playBackgroundMusic(){
        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    fun pauseBackgroundMusic(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }
    }

    fun stopBackgroundMusic(){
        mediaPlayer.release()
    }

}