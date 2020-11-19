package com.example.mumu.warehousecheckcar;

import android.Manifest;

/***
 *created by
 * 常量类
 *on 2020/4/28
 */
public final class Constant {
    //         正式服
//        App.IP = "http://47.106.157.255";
//        App.PORT = "80";
//        测试服
//        App.IP = "http://120.79.56.119";
//        App.PORT = "8080";
//        App.IP = "http://192.168.1.243";
//        App.PORT = "80";
//    public static final String IP = "http://192.168.1.
//    93";
//    public static final String PORT = "8982";
//    public static final String IP = "http://192.168.1.222";
//    public static final String PORT = "8080";
//    public static final String IP = "http://192.168.1.105";
//    public static final String PORT = "8982";
    public static final String IP = "http://47.106.157.255";
    public static final String PORT = "80";
    //    ip: 47.107.89.97  端口:8983
//public static final String CLOUD_IP = "http://47.107.89.97";
//    public static final String CLOUD_PORT = "8983";
    public static final String CLOUD_IP = "http://yun.label1.cn";
    public static final String CLOUD_PORT = "8983";
    public static final String USERNAME = "adminjiaqian";
    public static final String PRASSWORD = "123456";

    //LogFileName
    public static final String LOG_FILENAME = "/ChenCunLog";
    /**
     * SharedPreference
     */
    public static final String APP_TABLE_NAME = "wms_app";
    public static final String APP_CAR_NAME = "car_no";
    public static final String APP_OUTP_ID = "output_id";

    public static final String[] MANIFEST = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    //code
    public static final int REQUEST_STORAGE_CODE = 5;
    public static final int BLUE_TOOTH_ENABLE_CODE = 101;

    public static final int REQUEST_CAMERA_CODE = 1001;
}
