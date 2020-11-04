package com.example.mumu.warehousecheckcar.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.AppExecutors;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.App;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

/**
 * APP工具类
 *
 * @author Administrator
 */
public class AppUtil {
    private static final int BEEPER = 1;
    private static final SoundPool SOUND_POOL;

    static {
        SOUND_POOL = new SoundPool.Builder()
                .setMaxStreams(1)
                .build();
        SOUND_POOL.load(App.getContext(), R.raw.duka3, BEEPER);
    }

    /**
     * 关闭该控件的软键盘
     *
     * @param context  上下文
     * @param editText 控件
     */
    public static void closeKeyBoard(Context context, EditText editText) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity activity
     */
    public static void hideSoftKeyboard(@NonNull Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focus = activity.getCurrentFocus();
        manager.hideSoftInputFromWindow(
                focus == null ? null : focus.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    /**
     * 隐藏软键盘
     *
     * @param token   token
     * @param context context
     */
    public static void hideSoftKeyboard(IBinder token, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
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
     * 提示音
     */
    private static void beep() {
        SOUND_POOL.play(BEEPER, 1, 1, 0, 0, 1);
    }

    /**
     * 手持机扫描成功
     */
    public static void scanSuccess() {
        beep();
        vibrate(200);
    }


    public static void closeDrawerLayout(DrawerLayout drawerLayout) {
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }

    public static void showToast(final String text) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取版本号
     *
     * @return 存在 ? 版本号 : ""
     */
    public static String getVersionName() {
        Context context = App.getContext();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void startActivityForResult(Object object, Intent intent, Class<?> target, int requestCode) {
        boolean isActivity = isActivity(object);
        if (isActivity) {
            Activity mActivity = (Activity) object;
            if (target != null) {
                intent.setClass(mActivity, target);
            }
            mActivity.startActivityForResult(intent, requestCode);
        } else {
            Fragment mFragment = (Fragment) object;
            if (target != null) {
                intent.setClass(mFragment.getActivity(), target);
            }
            mFragment.startActivityForResult(intent, requestCode);
        }
    }

    public static void startActivityForResult(Object object, Intent intent, int requestCode) {
        startActivityForResult(object, intent, null, requestCode);
    }

    /**
     * @param object object
     * @return true -> Activity, false -> Fragment
     */
    public static boolean isActivity(Object object) {
        if (object instanceof Activity) {
            return true;
        } else if (object instanceof Fragment) {
            return false;
        } else {
            throw new IllegalArgumentException("传入的既不是Activity也不是Fragment");
        }
    }
}
