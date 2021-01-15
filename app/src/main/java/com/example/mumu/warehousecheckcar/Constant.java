package com.example.mumu.warehousecheckcar;

import android.Manifest;

/***
 *created by
 * 常量类
 *on 2020/4/28
 */
public final class Constant {

    public static final String IP = "http://192.168.1.105";
    public static final String PORT = "9696";
    //    public static final String IP = "http://47.106.157.255";
//    public static final String PORT = "80";
    //    ip: 47.107.89.97  端口:8983
//public static final String CLOUD_IP = "http://47.107.89.97";
//    public static final String CLOUD_PORT = "8983";
    public static final String CLOUD_IP = "http://yun.label1.cn";
    public static final String CLOUD_PORT = "8983";
    public static final String USERNAME = "adminjiaqian";
    public static final String PRASSWORD = "123456";

    //LogFileName
    public static final String LOG_FILENAME = "/ChenCunLog";

    public static final String[] MANIFEST = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    //code
    public static final int REQUEST_STORAGE_CODE = 5;
    public static final int BLUE_TOOTH_ENABLE_CODE = 101;

    public static final int REQUEST_CAMERA_CODE = 1001;

    public static final int Handle_key = 134;

    public static final String APP_TABLE_NAME = "sp_chencun_wms";
    public static final String SP_PROWERS = "sp_prowers";
    public static final String SP_AUTH = "sp_auth";


}
