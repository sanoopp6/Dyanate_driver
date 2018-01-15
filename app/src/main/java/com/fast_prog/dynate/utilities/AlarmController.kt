package com.fast_prog.dynate.utilities

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri

import java.io.IOException

/**
 * Created by sarathk on 2/18/17.
 */

class AlarmController(private val context: Context) {

    private val mAudioManager: AudioManager
    private val userVolume: Int
    private val mpRingTone: MediaPlayer

    init { // constructor for my alarm controller class
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //remeber what the user's volume was set to before we change it.
        //userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        userVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        mpRingTone = MediaPlayer()
    }

    fun playSound(soundURI: String) {

        var alarmSound: Uri? = null
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        try {
            alarmSound = Uri.parse(soundURI)
        } catch (e: Exception) {
            alarmSound = ringtoneUri
        } finally {
            if (alarmSound == null) {
                alarmSound = ringtoneUri
            }
        }

        try {

            if (!mpRingTone.isPlaying) {
                mpRingTone.setDataSource(context, alarmSound!!)
                mpRingTone.setAudioStreamType(AudioManager.STREAM_ALARM)
                mpRingTone.isLooping = true // set loop
                mpRingTone.prepare()
                mpRingTone.start()
            }

        } catch (e: IOException) {
            //Toast.makeText(HomeActivity.this, "Your alarm sound was unavailable.", Toast.LENGTH_LONG).show();

        }

        // set the volume to what we want it to be.  In this case it's max volume for the alarm stream.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND)

    }

    fun stopSound() {
        // reset the volume to what it was before we changed it.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND)
        mpRingTone.stop()
        mpRingTone.reset()

    }

    fun releasePlayer() {
        mpRingTone.release()
    }
}
