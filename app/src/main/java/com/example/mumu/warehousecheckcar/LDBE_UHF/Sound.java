package com.example.mumu.warehousecheckcar.LDBE_UHF;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;


import com.example.mumu.warehousecheckcar.R;

import java.util.ArrayList;


public class Sound {
    public int max;
    public int current;
    MediaPlayer player_fail;
    MediaPlayer player_success;
    Vibrator vibrator;
    ArrayList<MediaPlayer> musicList = new ArrayList<>();

    public  Sound(Context context) {
        super();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE); // 播放提示音
        max = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        current = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        player_success = MediaPlayer.create(context, R.raw.duka3);
        player_success.setVolume((float) current / (float) max, (float) current / (float) max);
//        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); // 振动100毫秒
    }

    /**
     * @param ms   振动时间变量，单位ms
     */
    public void callAlarm() {
//        vibrator.vibrate(ms); // 振动100毫秒
            if (!player_success.isPlaying()) {
                player_success.start();
            }
        }
}
