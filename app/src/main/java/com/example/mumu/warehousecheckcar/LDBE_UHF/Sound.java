package com.example.mumu.warehousecheckcar.LDBE_UHF;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;


import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.App;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;


public class Sound {
    private static final int BEEPER = 1;
    private static final SoundPool mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private static int scanSoundId;
    private static int failSoundId;

    public static void init(Context context) {
        scanSoundId = mSoundPool.load(context, R.raw.duka3, BEEPER);
        failSoundId = mSoundPool.load(context, R.raw.upload_fail, BEEPER);
    }

    /**
     * 震动milliseconds毫秒
     *
     * @param milliseconds 震动时间
     */
    public static void vibrate(long milliseconds) {
        try {
            Vibrator vib = (Vibrator) App.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, DEFAULT_AMPLITUDE);
                vib.vibrate(vibrationEffect);
            } else {
                vib.vibrate(milliseconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ms 振动时间变量，单位ms
     */
    public static void scanAlarm() {
        mSoundPool.play(scanSoundId, 1, 1, 0, 0, 1);
    }

    public static void faillarm() {
        vibrate(1000);
        mSoundPool.play(failSoundId, 1, 1, 0, 0, 1);
    }
}
