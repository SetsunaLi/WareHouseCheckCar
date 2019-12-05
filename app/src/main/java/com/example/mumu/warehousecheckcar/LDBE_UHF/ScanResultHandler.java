package com.example.mumu.warehousecheckcar.LDBE_UHF;

import android.os.Handler;
import android.os.Message;

import com.example.mumu.warehousecheckcar.application.App;


/***
 *created by mumu
 *on 2019/11/5
 */
public class ScanResultHandler extends Handler {
    public static final int CODE = 1;
    public static final int RFID = 2;
    public static final int NO_MUSIC_RFID = 3;
    private OnCodeResult codeResult;
    private OnRfidResult rfidResult;

    public ScanResultHandler() {
    }
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
        code = code.replaceAll(" ", "");
        switch (msg.what) {
            case CODE: {
                if (App.MUSIC_SWITCH) {
                    Sound.scanAlarm();
                }
                if (codeResult != null) {
                    codeResult.codeResult(code);
                }
            }
            break;
            case RFID: {
                if (App.MUSIC_SWITCH) {
                    Sound.scanAlarm();
                }
                if (rfidResult != null) {
                    rfidResult.rfidResult(code);
                }
            }
            case NO_MUSIC_RFID: {
                if (rfidResult != null) {
                    rfidResult.rfidResult(code);
                }
            }
            break;
            default:
                break;
        }
    }
}
