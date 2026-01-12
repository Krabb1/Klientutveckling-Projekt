package com.example.klientutveckling_projekt


import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow

/**
 * Ansvarar för kontrollen över spelets bakgrundsmusik
 */
class BackgroundMusicManager(private val context: Context){

    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.backgroundmusic).apply {
        isLooping = true
    }

    /**
     * Startar musiken om den inte redan är på
     */
    fun playBackgroundMusic(){
        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    /**
     * Pausar musiken om den inte redan är på
     */
    fun pauseBackgroundMusic(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }
    }

    /**
     * Sätter volymen till ett värde av typen float
     */
    fun setVolume(volume: Float){
        mediaPlayer.setVolume(volume, volume)
    }

    /**
     * Stoppar musiken
     *
     * Måste användas vid destruction av app för att förhindra minnesläckor
     */
    fun stopBackgroundMusic(){
        mediaPlayer.release()
    }

}