package com.example.mumu.warehousecheckcar.LDBE_UHF;

import android.os.Handler;
import android.os.Message;


/***
 *created by mumu
 *on 2019/11/5
 */
public class ScanResultHandler extends Handler {
    public static final int CODE = 1;
    public static final int RFID = 2;
    private OnCodeResult codeResult;
    private OnRfidResult rfidResult;

    public ScanResultHandler(OnCodeResult onCodeResultListener) {
        this.codeResult = onCodeResultListener;
    }

    public ScanResultHandler(OnRfidResult onRfidResultListener) {
        this.rfidResult = onRfidResultListener;
    }

    public ScanResultHandler(OnCodeResult codeResult, OnRfidResult rfidResult) {
        this.codeResult = codeResult;
        this.rfidResult = rfidResult;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        String code = (String) msg.obj;
        Sound.scanAlarm();
        switch (msg.what) {
            case CODE:
                if (codeResult != null) {
                    codeResult.codeResult(code);
                }
                break;
            case RFID:
                if (rfidResult != null) {
                    rfidResult.rfidResult(code);
                }
                break;
            default:
                break;
        }

    }
}
