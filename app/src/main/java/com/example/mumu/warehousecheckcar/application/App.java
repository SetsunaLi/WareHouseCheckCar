package com.example.mumu.warehousecheckcar.application;

import android.app.Application;

import com.example.mumu.warehousecheckcar.entity.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.OutCheckDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mumu on 2018/12/8.
 */

public class App extends Application {
//    申请单号缓存
    public static String applyNo="";
//    车牌号缓存
    public static String carNo="";
//系统版本
    public static String SYSTEM_VERSION="";
//    系统IP
    public static String IP="";
//    系统端口
    public static String PORT="";
//设备号
    public static String DEVICE_NO="";
    public static String DEVICE_ID="";
//    读写声音
    public static boolean MUSIC_SWITCH=false;

//    入库缓存详细列表
    public static List<InCheckDetail> IN_DETAIL_LIST=new ArrayList<>();

    //    出库缓存详细列表
    public static List<OutCheckDetail> OUTDETAIL_LIST=new ArrayList<>();
}
