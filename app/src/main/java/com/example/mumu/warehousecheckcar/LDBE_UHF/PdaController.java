package com.example.mumu.warehousecheckcar.LDBE_UHF;

import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

/***
 *created by 手持模块控制器
 *on 2019/11/14
 */
public class PdaController {
    public static boolean initRFID(UHFCallbackLiatener callbackLiatener) {
        try {
            boolean flag = RFID_2DHander.getInstance().on_RFID();
            UHFResult.getInstance().setCallbackLiatener(callbackLiatener);
            return flag;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disRFID() {
        try {
            return RFID_2DHander.getInstance().off_RFID();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean init2D(RXCallback callback) {
        try {
            boolean flag = RFID_2DHander.getInstance().on_2D();
            TDScannerHelper scannerHander = RFID_2DHander.getInstance().getTDScanner();
            scannerHander.regist2DCodeData(callback);
            return flag;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disConnect2D() {
        try {
            return RFID_2DHander.getInstance().off_2D();
        } catch (Exception e) {
            return false;
        }
    }
}
