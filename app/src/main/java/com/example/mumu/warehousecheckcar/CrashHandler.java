package com.example.mumu.warehousecheckcar;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import com.example.mumu.warehousecheckcar.utils.DateUtil;
import com.example.mumu.warehousecheckcar.utils.FileFolder;
import com.example.mumu.warehousecheckcar.utils.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * @author wiger
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();

    private static final String FILE_NAME = "Crash ";
    private static final String FILE_NAME_SUFFIX = "Log.txt";

    private static WeakReference<CrashHandler> sInstance;
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    public static CrashHandler getInstance() {
        if (sInstance == null) {
            sInstance = new WeakReference<>(new CrashHandler());
        }
        return sInstance.get();
    }

    /**
     * 当程序中有未捕获的异常，系统将会自动调用uncaughtException
     *
     * @param t 未捕获异常的线程
     * @param e 未捕获的异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            dumpExceptionToStorage(e);
            uploadExceptionToServer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        e.printStackTrace();
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }


    /**
     * 存储异常到设备
     *
     * @param ex 异常
     */
    private void dumpExceptionToStorage(Throwable ex) throws IOException {
        File dir = FileFolder.rootDir();
        ;
        if (!dir.exists()) {
            dir.mkdir();
        }
        long current = System.currentTimeMillis();
        String time = DateUtil.formatDate(new Date(current), DateUtil.YYYY_MM_DD);
        File file = new File(dir, FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
                    file
            )));
            printWriter.println(file);
            dumpPhoneInfo(printWriter);
            printWriter.println("\n");
            printWriter.println("-----Crash Log Begin-----");
            ex.printStackTrace(printWriter);
            printWriter.println("-----Crash Log End-----");
            printWriter.close();
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "dump crash info failed");
        }
    }

    /**
     * 获得手机信息
     *
     * @param printWriter printWriter
     * @throws PackageManager.NameNotFoundException NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter printWriter) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        printWriter.print("App Version: ");
        printWriter.print(packageInfo.versionName);
        printWriter.print('_');
        printWriter.println(packageInfo.versionCode);

        //Android 版本号
        printWriter.print("OS Version: ");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print('_');
        printWriter.println(Build.VERSION.SDK_INT);

        //手机制造商
        printWriter.print("Vendor: ");
        printWriter.println(Build.MANUFACTURER);

        //手机型号
        printWriter.print("Model: ");
        printWriter.println(Build.MODEL);

        //CPU架构
        printWriter.print("CPU ABI: ");
        for (String abi : Build.SUPPORTED_ABIS) {
            printWriter.print(abi + " ");
        }
    }

    private void uploadExceptionToServer() {
        //TODO: 上传错误日志到服务器
    }
}
