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
 * Created by mumu on 2019/1/4.
 */

public class PutawayCarrierFragment extends Fragment implements UHFCallbackLiatener {

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
    @Bind(R.id.button1)
    Button button1;

    private PutawayCarrierFragment() {
    }

    private static PutawayCarrierFragment fragment;

    public static PutawayCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new PutawayCarrierFragment();
        return fragment;
    }

    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        和盘点Check布局一样
        View view = inflater.inflate(R.layout.putaway_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());
        getActivity().setTitle("上架");
        button2.setText("确认库位");

        edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                Log.i("onTextChanged", "onTextChanged");
                String trayNo = charSequence.toString();
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
                Log.i("onTextChanged", "onTextChanged");
                String locationNo = charSequence.toString();
                App.CARRIER.setLocationNo(locationNo);
                App.CARRIER.setLocationEPC("");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (App.CARRIER == null)
            App.CARRIER = new Carrier();
        /*App.CARRIER.setLocationNo("查布区");
        App.CARRIER.setTrayNo("TP0002");*/
        if (App.CARRIER.getLocationNo() != null && !App.CARRIER.getLocationNo().equals(""))
            edittext2.setText(App.CARRIER.getLocationNo());
        if (App.CARRIER.getTrayNo() != null && !App.CARRIER.getTrayNo().equals(""))
            edittext1.setText(App.CARRIER.getTrayNo());
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

    @OnClick({R.id.button2, R.id.button1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button2:
                if (App.CARRIER != null && App.CARRIER.getLocationNo() != null && !App.CARRIER.getLocationNo().equals("")) {
                    Fragment fragment = PutawayFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                    transaction.show(fragment);
                    transaction.commit();
                } else
                    Toast.makeText(getActivity(), "请扫描库位硬标签", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button1:
                App.CARRIER.clear();
                App.CARRIER.setLocationNo("临时入库区");
                edittext2.setText("临时入库区");
                break;
        }
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
                        if (EPC.startsWith("31B5A5AF")) {
//                        final String json = EPC;
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
                        if (response != null && response.getTrayNo() != null && !response.getTrayNo().equals("")) {
                            App.CARRIER.setTrayNo(response.getTrayNo());
                            edittext1.setText(response.getTrayNo() + "");
                        }
                        if (response != null && response.getTrayEPC() != null && !response.getTrayEPC().equals(""))
                            App.CARRIER.setTrayEPC(response.getTrayEPC());

                        if (response != null && response.getLocationNo() != null && !response.getLocationNo().equals("")) {
                            App.CARRIER.setLocationNo(response.getLocationNo());
                            edittext2.setText(response.getLocationNo() + "");
                        }
                        if (response != null && response.getLocationEPC() != null && !response.getLocationEPC().equals(""))
                            App.CARRIER.setLocationEPC(response.getLocationEPC());
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
