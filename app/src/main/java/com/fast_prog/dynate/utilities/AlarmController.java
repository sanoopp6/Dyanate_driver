package com.fast_prog.dynate.utilities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by sarathk on 2/18/17.
 */

public class AlarmController {

    private AudioManager mAudioManager;
    private int userVolume;
    private Context context;
    private MediaPlayer mpRingTone;

    public AlarmController(Context context) { // constructor for my alarm controller class
        this.context = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //remeber what the user's volume was set to before we change it.
        //userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        userVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        mpRingTone = new MediaPlayer();
    }

    public void playSound(String soundURI) {

        Uri alarmSound = null;
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            alarmSound = Uri.parse(soundURI);
        } catch (Exception e) {
            alarmSound = ringtoneUri;
        } finally {
            if (alarmSound == null) {
                alarmSound = ringtoneUri;
            }
        }

        try {

            if (!mpRingTone.isPlaying()) {
                mpRingTone.setDataSource(context, alarmSound);
                mpRingTone.setAudioStreamType(AudioManager.STREAM_ALARM);
                mpRingTone.setLooping(true); // set loop
                mpRingTone.prepare();
                mpRingTone.start();
            }

        } catch (IOException e) {
            //Toast.makeText(HomeActivity.this, "Your alarm sound was unavailable.", Toast.LENGTH_LONG).show();

        }
        // set the volume to what we want it to be.  In this case it's max volume for the alarm stream.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);

    }

    public void stopSound() {
        // reset the volume to what it was before we changed it.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND);
        mpRingTone.stop();
        mpRingTone.reset();

    }

    public void releasePlayer() {
        mpRingTone.release();
    }
}
