package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2018/12/21.
 */

public class CheckCarrierFragment extends Fragment implements UHFCallbackLiatener {

    private final String TAG = "CheckCarrierFragment";
    /*  @Bind(R.id.text1)
      TextView text1;
      @Bind(R.id.text2)
      TextView text2;*/
    @Bind(R.id.relativelayout)
    LinearLayout relativelayout;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.edittext1)
    EditText edittext1;
    @Bind(R.id.edittext2)
    EditText edittext2;


    private CheckCarrierFragment() {
    }

    private static CheckCarrierFragment fragment;

    public static CheckCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CheckCarrierFragment();
        return fragment;
    }

    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());
        getActivity().setTitle("盘点");

        button2.setText("确认库位");
        initRFID();
        return view;
    }

    private void initRFID() {
        try {
            RFID_2DHander.getInstance().on_RFID();
            UHFResult.getInstance().setCallbackLiatener(this);
        } catch (Exception e) {

        }
    }

    private void disRFID() {
        try {
            RFID_2DHander.getInstance().off_RFID();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
//        App.CARRIER = null;
        disRFID();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick(R.id.button2)
    public void onViewClicked() {
        if (App.CARRIER != null && (App.CARRIER.getTrayNo() != null || App.CARRIER.getLocationNo() != null) &&
                (!App.CARRIER.getTrayNo().equals("") || !App.CARRIER.getLocationNo().equals(""))) {
            Fragment fragment = CheckFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        } else
            Toast.makeText(getActivity(), "请扫描库位硬标签", Toast.LENGTH_SHORT).show();
    }

    long currenttime = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.arg1) {
                    case 0x00:
                        if (App.MUSIC_SWITCH) {
                            if (System.currentTimeMillis() - currenttime > 150) {
                                sound.callAlarm();
                                currenttime = System.currentTimeMillis();
                            }
                        }
                        String EPC = (String) msg.obj;
                        EPC = EPC.replaceAll(" ", "");
                       /* if (EPC.startsWith("31")) {
                            EPC.replaceAll(" ", "");
                        }*/
//                        final String json = EPC;
                        if (EPC.startsWith("31B5A5AF")) {
                            JSONObject epc = new JSONObject();
                            epc.put("epc", EPC);
                            final String json = epc.toJSONString();
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getCarrier.sh", new OkHttpClientManager.ResultCallback<Carrier>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getInventory;" + e.getMessage());
                                            Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onResponse(Carrier response) {
                                        try {
                                            if (response != null && (response.getTrayNo() != null || response.getLocationNo() != null) &&
                                                    (!response.getTrayNo().equals("") || !response.getLocationNo().equals(""))) {
                                                Message msg = handler.obtainMessage();
                                                msg.arg1 = 0x01;
                                                msg.obj = response;
                                                handler.sendMessage(msg);
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                }, json);
                            } catch (IOException e) {

                            }
                        }
                        break;
                    case 0x01:
                        Carrier response = (Carrier) msg.obj;
                        if (response != null && (response.getTrayNo() != null || response.getLocationNo() != null) &&
                                (!response.getTrayNo().equals("") || !response.getLocationNo().equals(""))) {
                            App.CARRIER = response;
                            edittext2.setText(response.getLocationNo() + "");
                            edittext1.setText(response.getTrayNo() + "");
                        }
                        break;
                }
            } catch (Exception e) {

            }
        }
    };

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }
}
