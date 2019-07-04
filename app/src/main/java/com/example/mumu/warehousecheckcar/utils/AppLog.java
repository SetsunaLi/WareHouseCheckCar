package com.example.mumu.warehousecheckcar.utils;

import android.content.Context;

import com.example.mumu.warehousecheckcar.application.App;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class AppLog {
    public static final int TYPE_ERROR = 0;
    public static final int TYPE_WARN = 1;
    public static final int TYPE_INFO = 2;

    /**
     * @param context
     * @param nameUrl
     * @return
     * @throws IOException
     */
    public static String readIO(Context context, String nameUrl) throws IOException {
        FileInputStream fis = context.openFileInput(nameUrl);
        byte[] buff = new byte[1024];
        int hasRead = 0;
        StringBuilder sb = new StringBuilder("");
        while ((hasRead = fis.read(buff)) > 0) {
            sb.append(new String(buff, 0, hasRead));
        }
        fis.close();
        return sb.toString();
    }


    /**
     * @param context
     * @param nameUrl 文件名称（包括文件格式）
     * @param msg     载入文字
     * @throws IOException
     */
    public static void writeIO(Context context, String nameUrl, String msg) throws IOException {
//        Context.MODE_APPEND 应用程序可以向文件追加内容
//        MODE_PRIVATE 该文件只能被当前程序读写
//        MODE_WORLD_READABLE 该文件内容可以被其他程序读取
//        MODE_WORLD_WRITEABLE 该文件可以被其他程序读写

        FileOutputStream fos = context.openFileOutput(nameUrl, Context.MODE_APPEND);
        PrintStream ps = new PrintStream(fos);
//        输入内容
        ps.print(msg);
//        关闭流
        ps.close();
    }

    static FormatStrategy formatStrategy;
    static FormatStrategy formatStrategy2;

    static void initFormat1() {
        formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("WareHouse")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
//                return super.isLoggable(priority, tag);
//                设置这个不会打印日志(控制台)
                return App.APPLOGGER;
            }
        });
    }

    static void initFormat2() {
        formatStrategy2 = CsvFormatStrategy.newBuilder()
                .tag("WareHouse")
                .build();

        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy2) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                //                设置这个不会打印日志(日志文件)
                return App.APPLOGGERTXT;
            }
        });
    }

    public static void clearFormat() {
        Logger.clearLogAdapters();
        formatStrategy = null;
        formatStrategy2 = null;
    }

    public static void writeLog(String tag, String msg, int TPYE) {
        if (formatStrategy == null)
            initFormat1();
        if (formatStrategy2 == null)
            initFormat2();

        switch (TPYE) {
            case TYPE_ERROR:
                Logger.t(tag).e(msg);
                break;
            case TYPE_WARN:
                Logger.t(tag).w(msg);
                break;
            case TYPE_INFO:
                Logger.t(tag).i(msg);
                break;
            default:
                Logger.t(tag).i(msg);
                break;
        }
    }

    public static void write(Context context, String tag, String msg, int TPYE) throws IOException {
//         IO日志
//        获取日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(System.currentTimeMillis());
        String dateStr = simpleDateFormat.format(date);
        //获取当前时间
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        String time = simpleDateFormat2.format(date);
        writeIO(context, dateStr + ".txt", time + tag + ":" + msg + "\r\n");
//Logger日志
        writeLog(tag, msg, TPYE);
    }

}
