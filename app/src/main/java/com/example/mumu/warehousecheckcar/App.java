package com.example.mumu.warehousecheckcar;

import android.app.Application;
import android.content.Context;

import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.entity.in.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.in.Input;
import com.example.mumu.warehousecheckcar.entity.out.OutCheckDetail;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mumu on 2018/12/8.
 */

public class App extends Application {
    private static App INSTANCE;
    public static boolean isPDA = true;

    public static App getInstance() {
        return INSTANCE;
    }

    public static Context getContext() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Sound.init(getContext());
        isPDA = new PdaController().getIsPhone();
        CrashHandler.getInstance().init(this);
        LogUtil.init();
    }

    public  static final boolean APPLOGGER=true;
    public  static final boolean APPLOGGERTXT=true;
    public static final long TIME=10000;
    public  static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    public static final String TAG_RETURN_FRAGMENT = "TitleFragment";

    //系统版本
    public static String SYSTEM_VERSION = "";
    //    系统IP
    public static String IP = "http://47.106.157.255";
    //    系统端口
    public static String PORT = "80";
    //    系统IP
    public static String CLOUD_IP = "http://yun.label1.cn";
    //    系统端口
    public static String CLOUD_PORT = "8983";
    //设备号
    public static String DEVICE_NO = "";
    //    读写声音
    public static boolean MUSIC_SWITCH = false;
    //    系统日志
    public static boolean LOGCAT_SWITCH = false;
    //    功率
    public static int PROWER = 0;

//    入库缓存详细列表
    public static List<InCheckDetail> IN_DETAIL_LIST=new ArrayList<>();

    //    出库缓存详细列表
    public static List<OutCheckDetail> OUTDETAIL_LIST=new ArrayList<>();
//    盘点库位缓存
    public static Carrier CARRIER=new Carrier();

//   上架详情缓存
    public static List<Input> INPUT_DETAIL_LIST=new ArrayList<>();
}
