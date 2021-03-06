package com.example.mumu.warehousecheckcar.LDBE_UHF;

import android.os.Handler;
import android.util.Log;

import com.example.mumu.warehousecheckcar.App;
import com.rfid.RFIDReaderHelper;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

/***
 *created by 手持模块控制器
 *on 2019/11/14
 */
public class PdaController {
    public static RFIDReaderHelper rfidHandler;
    private boolean isPDA = false;

    public static RFIDReaderHelper getRfidHandler() {
        return rfidHandler;
    }

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int prower = setPrower(App.PROWER);
                Log.i("prower", String.valueOf(prower));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static boolean initRFID(UHFCallbackLiatener callbackLiatener) {
        try {
            if (App.isPDA) {
                boolean flag = false;
                if (RFID_2DHander.getInstance().connectReader()) {
                    flag = RFID_2DHander.getInstance().on_RFID();
                    rfidHandler = RFID_2DHander.getInstance().getRFIDReader();
                    rfidHandler.registerObserver(UHFResult.getInstance());
                    UHFResult.getInstance().setCallbackLiatener(callbackLiatener);
                    new Handler().postDelayed(runnable, 200);
                }
                return flag;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disRFID() {
        try {
            if (App.isPDA) {
                boolean flag = RFID_2DHander.getInstance().off_RFID();
                if (rfidHandler != null) {
                    rfidHandler.unRegisterObserver(UHFResult.getInstance());
                }
                RFID_2DHander.getInstance().disConnectReader();
                RFID_2DHander.getInstance().releaseRFID();
                return flag;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean init2D(RXCallback callback) {
        try {
            if (App.isPDA) {
                RFID_2DHander.getInstance().connect2D();
                boolean flag = RFID_2DHander.getInstance().on_2D();
                TDScannerHelper scannerHander = RFID_2DHander.getInstance().getTDScanner();
                scannerHander.regist2DCodeData(callback);
                return flag;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disConnect2D() {
        try {
            if (App.isPDA) {
                RFID_2DHander.getInstance().off_2D();
                return RFID_2DHander.getInstance().disConnect2D();
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static int setPrower(int prower) throws Exception {
        return RFID_2DHander.getInstance().getRFIDReader().setOutputPower(RFID_2DHander.getInstance().btReadId, (byte) prower);
    }

    public static int writeTag(byte btReadId, byte[] btAryPassWord, byte btMemBank, byte btWordAdd, byte btWordCnt, byte[] btAryData) throws Exception {
        return RFID_2DHander.getInstance().getRFIDReader().writeTag(btReadId, btAryPassWord, btMemBank, btWordAdd, btWordCnt, btAryData);
    }

    /**
     * 判断设备类型
     *
     * @return 是否是手机
     */
    public boolean getIsPhone() {
        isPDA = initRFID(null);
        if (isPDA) {
            disRFID();
        }
        return isPDA;
    }


}
