package com.example.mumu.warehousecheckcar.UHF;

import android.os.Message;
import android.util.Log;

import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

/**
 * Created by mumu on 2018/12/8.
 */

public class UHFResult extends RXObserver {
    private static UHFResult result;
    private UHFResult(){

    }
    public static UHFResult getInstance(){
        if (result==null)
            result=new UHFResult();
        return result;
    }

    private UHFCallbackLiatener callbackLiatener;

    public void setCallbackLiatener(UHFCallbackLiatener callbackLiatener) {
        this.callbackLiatener = callbackLiatener;
    }

    //请求读写器设置回调
    //当发送查询读写器设置指令（例如RFIDReaderHelper中的各种以get开头的查询指令函数）会回调该方法，若有返回值会存储在readerSetting相应字段中
    //具体可以参考API文档中ReaderSetting 各个字段的含义
    @Override
    protected void refreshSetting(ReaderSetting readerSetting) {
        super.refreshSetting(readerSetting);
        if (callbackLiatener!=null)
            callbackLiatener.refreshSettingCallBack(readerSetting);
    }

    //inventory 函数盘存到的标签会先缓存到RFID模块的缓存中，只有调用getInventoryBuffer 或 getAndResetInventoryBuffer 函数
    // 是才会回调该方法将数据上传，上传的标签数据无重复
    //当盘存到多张标签的时，该方法会多次回调，标签可以重复
    @Override
    protected void onInventoryTag(RXInventoryTag tag) {
        super.onInventoryTag(tag);
//            RXInventoryTag.strEPC  String类型EPC
//            RXInventoryTag.strRSSI String类型RSSI
        if (callbackLiatener!=null)
            callbackLiatener.onInventoryTagCallBack(tag);
    }

    //当一条盘存指令执行结束的时候该方法会回调
    // （fastSwitchAntInventory除外fastSwitchAntInventory结束时回调onFastSwitchAntInventoryTagEnd）
    @Override
    protected void onInventoryTagEnd(RXInventoryTag.RXInventoryTagEnd tagEnd) {
        super.onInventoryTagEnd(tagEnd);

        try {
            Thread.currentThread().sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (callbackLiatener!=null)
            callbackLiatener.onInventoryTagEndCallBack(tagEnd);
//
    }

    @Override
    protected void onOperationTag(RXOperationTag tag) {
        if (callbackLiatener!=null)
            callbackLiatener.onOperationTagCallBack(tag);
        //当执行readTag,writeTag,lockTag 或者 killTag 等操作标签指令函数时会回调该方法，当一次操作多张标签时会多次回调
        //返回数据RXOperationTag tag 参考API文档
//            RXOperationTag为操作标签（应该是操作之后，属性类似于存盘标签返回）

    }
}
