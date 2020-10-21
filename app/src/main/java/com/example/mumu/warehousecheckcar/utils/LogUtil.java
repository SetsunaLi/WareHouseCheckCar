package com.example.mumu.warehousecheckcar.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by WuXiaolong
 * on 2016/4/28.
 * 微信公众号：吴小龙同学
 * 个人博客：http://wuxiaolong.me/
 *
 * @author Administrator
 */
public class LogUtil {

    private static final String FILE_NAME = "Log.txt";
    private static String tag = "Reinforce";
    private static File folder;

    private static boolean toLocal = true;
    private static boolean log = true;

    public static void init() {
        folder = FileFolder.rootDir();
    }

    public static void setTag(String tag) {
        LogUtil.tag = tag;
    }

    public static void setLog(boolean log) {
        LogUtil.log = log;
    }

    public static void i(String msg) {
        i(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (log) {
            Log.i(tag, msg);
            saveToLocal(tag, msg);
        }
    }

    public static void d(String msg) {
        d(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (log) {
            Log.d(tag, msg);
            saveToLocal(tag, msg);
        }
    }

    public static void w(String msg) {
        w(tag, msg);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable t) {
        if (log) {
            Log.w(tag, msg, t);
            saveToLocal(tag, msg);
        }
    }

    public static void v(String msg) {
        v(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (log) {
            Log.v(tag, msg);
            saveToLocal(tag, msg);
        }
    }

    public static void e(String msg) {
        e(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
        saveToLocal(tag, msg);
    }

    private static void saveToLocal(String tag, String content) {
        if (toLocal) {
            writeToLocal(tag, content);
        }
    }

    private static void writeToLocal(String tag, String content) {
        String time = DateUtil.currentTime();
        String date = DateUtil.currentDate();
        String log = "-----Log Begin-----\n" +
                "       Log Time: " + time + "\n" +
                "       Log Tag: " + tag + "\n" +
                "       Log Content: " + content + "\n" +
                "-----Log End----";
        FileUtil.writeFile(log, folder, date + FILE_NAME);
    }
}