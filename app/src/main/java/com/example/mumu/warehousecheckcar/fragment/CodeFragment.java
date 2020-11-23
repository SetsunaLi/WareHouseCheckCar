package com.example.mumu.warehousecheckcar.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.util.StringTool;
import com.xdl2d.scanner.callback.RXCallback;

/***
 *created by 
 *on 2020/11/20
 */
public abstract class CodeFragment extends BaseFragment implements UHFCallbackLiatener, RXCallback, OnRfidResult, OnCodeResult {
    protected static boolean isRfidConnect = false, isBarcodeConnect = false;
    protected ScanResultHandler handler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new ScanResultHandler(this, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeConnect();
    }

    protected void initRFID() {
        if (!PdaController.initRFID(this)) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
        } else
            isRfidConnect = true;
    }

    protected void init2D() {
        if (!PdaController.init2D(this)) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        } else
            isBarcodeConnect = true;

    }

    protected void disRFID() {
        if (isRfidConnect && App.isPDA) {
            PdaController.disRFID();
            isRfidConnect = false;
        }
    }

    protected void disConnect2D() {
        if (isBarcodeConnect && App.isPDA) {
            PdaController.disConnect2D();
            isBarcodeConnect = false;
        }
    }

    protected void closeConnect() {
        disRFID();
        disConnect2D();
    }

    public void on134CallBack(boolean start) {
        if (handler != null && isRfidConnect)
            handler.startOrStopInventory(start);

/*        if (isBarcodeConnect)
            bcr.startDecode(); // start decode (callback gets results)*/

    }

    /**
     * 写功率
     */
    protected int setPrower(int prower) {
        try {
            return PdaController.setPrower(prower);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.param_pda_error));
            return -1;
        }
    }

    /**
     * 写码操作
     */
    protected int writeTag(String epc) {
        {
            byte btMemBank = 0x01;
            byte btWordAdd = 0x00;
            byte btWordCnt = 0x00;
            byte[] btAryPassWord = null;
            try {
                btWordAdd = (byte) 2;
            } catch (Exception e) {
                showToast(getResources().getString(R.string.param_start_addr_error));
                return -1;
            }

            try {
                String[] reslut = StringTool.stringToStringArray("00000000", 2);
                btAryPassWord = StringTool.stringArrayToByteArray(reslut, 4);
            } catch (Exception e) {
                showToast(getResources().getString(R.string.param_password_error));
                return -1;
            }
            byte[] btAryData = null;
            String[] result = null;
            try {
                result = StringTool.stringToStringArray(epc.toUpperCase(), 2);
                btAryData = StringTool.stringArrayToByteArray(result, result.length);
                btWordCnt = (byte) ((result.length / 2 + result.length % 2) & 0xFF);
            } catch (Exception e) {
                showToast(getResources().getString(R.string.param_data_error));
                return -1;
            }
            if (btAryData == null || btAryData.length <= 0) {
                showToast(getResources().getString(R.string.param_data_error));
                return -1;
            }

            if (btAryPassWord == null || btAryPassWord.length < 4) {
                showToast(getResources().getString(R.string.param_password_error));
                return -1;
            }
            try {
                return PdaController.writeTag((byte) -1, btAryPassWord, btMemBank, btWordAdd, btWordCnt, btAryData);
            } catch (Exception e) {
                e.printStackTrace();
                showToast(getResources().getString(R.string.param_pda_error));
                return -1;
            }
        }
    }

    @Override
    public void callback(byte[] bytes) {
        final String result = new String(bytes);
        Message message = handler.obtainMessage();
        message.what = ScanResultHandler.CODE;
        message.obj = result;
        handler.sendMessage(message);
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        final String epc = tag.strEPC.replace(" ", "");
        Message message = handler.obtainMessage();
        message.what = ScanResultHandler.RFID;
        message.obj = epc;
        handler.sendMessage(message);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @Override
    public void codeResult(String code) {
        Log.i("Code", code);
    }

    @Override
    public void rfidResult(String epc) {
        Log.i("EPC", epc);
    }
}