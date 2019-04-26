package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2018/12/21.
 */

public class CheckCarrierFragment extends Fragment implements UHFCallbackLiatener , RXCallback {

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




    private static CheckCarrierFragment fragment;

    public static CheckCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CheckCarrierFragment();
        return fragment;
    }

    private Sound sound;
    private boolean flagRFID=false;
    private boolean flag2D=false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());

        getActivity().setTitle("盘点");
        button2.setText("确认库位");
        edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                Log.i("onTextChanged","onTextChanged");
                String trayNo=charSequence.toString();
                trayNo=trayNo.replaceAll(" ","");
                App.CARRIER.setTrayNo(trayNo);
                App.CARRIER.setTrayEPC("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                Log.i("onTextChanged","onTextChanged");
                String locationNo=charSequence.toString();
                locationNo=locationNo.replaceAll(" ","");
                App.CARRIER.setLocationNo(locationNo);
                App.CARRIER.setLocationEPC("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edittext1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (!flagRFID){
                        initRFID();
                        flagRFID=true;
                    }
                }else {
                    if (flagRFID){
                        disRFID();
                        flagRFID=false;
                    }
                }
            }
        });
        edittext2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (!flag2D){
                        init2D();
                        flag2D=true;
                    }
                }else {
                    if (flag2D){
                        disConnect2D();
                        flag2D=false;
                    }
                }
            }
        });
        if (App.CARRIER == null)
            App.CARRIER =new Carrier();
        /*App.CARRIER.setLocationNo("查布区");
        App.CARRIER.setTrayNo("TP0002");*/
        if (App.CARRIER.getLocationNo()!=null&&!App.CARRIER.getLocationNo().equals(""))
            edittext2.setText(App.CARRIER.getLocationNo());
        if (App.CARRIER.getTrayNo()!=null&&!App.CARRIER.getTrayNo().equals(""))
            edittext1.setText(App.CARRIER.getTrayNo());
//        initRFID();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*Message msg=handler.obtainMessage();
        msg.obj="31B5A5AF6000004000000002";
        msg.arg1=0x00;
        handler.sendMessage(msg);*/
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
    private TDScannerHelper scannerHander;

    private void init2D() {
        try {
            boolean flag2 = RFID_2DHander.getInstance().on_2D();
//            boolean flag1=RFID_2DHander.getInstance().connect2D();
            scannerHander = RFID_2DHander.getInstance().getTDScanner();
            scannerHander.regist2DCodeData(this);
            if (!flag2)
                Toast.makeText(getActivity(), "一维读头连接失败", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.w(TAG, "2D模块异常");
            Toast.makeText(getActivity(), getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
    }

    private void disConnect2D() {
        try {
            RFID_2DHander.getInstance().off_2D();
//            RFID_2DHander.getInstance().disConnect2D();

        } catch (Exception e) {

        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
//        App.CARRIER = null;
        if (flagRFID){
            disRFID();
            flagRFID=false;
        }
        if (flag2D){
            disConnect2D();
            flag2D=false;
        }
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick(R.id.button2)
    public void onViewClicked() {
       /* if (App.CARRIER != null && (App.CARRIER.getTrayNo() != null || App.CARRIER.getLocationNo() != null) &&
                (!App.CARRIER.getTrayNo().equals("") || !App.CARRIER.getLocationNo().equals(""))) {*/
        if (App.CARRIER != null &&App.CARRIER.getLocationNo() != null&& !App.CARRIER.getLocationNo().equals("")) {
            if (flag2D){
                disConnect2D();
                flag2D=false;
            }
            Fragment fragment = CheckFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        } else
            Toast.makeText(getActivity(), "请扫描库位硬标签", Toast.LENGTH_SHORT).show();
    }

    long currenttime = 0;
    @SuppressLint("HandlerLeak")
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
                        /*if (response != null && (response.getTrayNo() != null || response.getLocationNo() != null) &&
                                (!response.getTrayNo().equals("") || !response.getLocationNo().equals(""))) {
                            App.CARRIER = response;
                            edittext2.setText(response.getLocationNo() + "");
                            edittext1.setText(response.getTrayNo() + "");
                        }*/
                        if (response!=null&&response.getTrayNo()!=null&&!response.getTrayNo().equals("")) {
                            App.CARRIER.setTrayNo(response.getTrayNo());
                            edittext1.setText(response.getTrayNo() + "");
                        }
                        if (response!=null&&response.getTrayEPC()!=null&&!response.getTrayEPC().equals(""))
                            App.CARRIER .setTrayEPC(response.getTrayEPC());

                        if (response!=null&&response.getLocationNo()!=null&&!response.getLocationNo().equals("")) {
                            App.CARRIER.setLocationNo(response.getLocationNo());
                            edittext2.setText(response.getLocationNo() + "");
                        }
                        if (response!=null&&response.getLocationEPC()!=null&&!response.getLocationEPC().equals(""))
                            App.CARRIER .setLocationEPC(response.getLocationEPC());
                        break;
                        case 0x02:
                        if (App.MUSIC_SWITCH) {
                            if (System.currentTimeMillis() - currenttime > 150) {
                                sound.callAlarm();
                                currenttime = System.currentTimeMillis();
                            }
                        }
                        String location= (String) msg.obj;
                        location=location.replaceAll(" ","");
                        edittext2.setText(location);
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

    @Override
    public void callback(byte[] bytes) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x02;
        msg.obj = new String(bytes);

        handler.sendMessage(msg);
    }
}
