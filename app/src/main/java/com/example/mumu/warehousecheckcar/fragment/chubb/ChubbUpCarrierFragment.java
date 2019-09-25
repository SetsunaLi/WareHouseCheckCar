package com.example.mumu.warehousecheckcar.fragment.chubb;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.ChubbUp;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class ChubbUpCarrierFragment extends Fragment implements UHFCallbackLiatener , RXCallback {
    private static ChubbUpCarrierFragment fragment;
    @Bind(R.id.edittext1)
    EditText edittext1;
    @Bind(R.id.edittext2)
    EditText edittext2;
    @Bind(R.id.relativelayout)
    LinearLayout relativelayout;
    @Bind(R.id.button2)
    Button button2;


    public static ChubbUpCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbUpCarrierFragment();
        return fragment;
    }

    private Sound sound;
    private final String TAG = "ChubbUpCarrierFragment";
    private boolean flagRFID=false;
    private boolean flag2D=false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());

        getActivity().setTitle("查布上架");
        button2.setText("确认库位");
        edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                Log.i("onTextChanged", "onTextChanged");
                String trayNo = charSequence.toString();
                trayNo = trayNo.replaceAll(" ", "");
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
                locationNo = locationNo.replaceAll(" ", "");
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
//        init2D();
//        initRFID();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        App.CARRIER.clear();
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
        if (flagRFID){
            disRFID();
            flagRFID=false;
        }
        if (flag2D){
            disConnect2D();
            flag2D=false;
        }
        EventBus.getDefault().post(new EventBusMsg(0x06));
//        RFID_2DHander.getInstance().on_RFID();

//        App.CARRIER = null;
//        disConnect2D();
//        disRFID();
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        ArrayList<ChubbUp> list=( ArrayList<ChubbUp>) getArguments().getSerializable("dataList");
        if (App.CARRIER.getLocationNo()!=null&&(!App.CARRIER.getLocationNo().equals("")||App.CARRIER.getTrayNo()!=null&&!App.CARRIER.getTrayNo().equals(""))&&
        (list!=null&&list.size()!=0)){
            blinkDialog(list);

        }else {
            Toast.makeText(getActivity(),"请输入库位号",Toast.LENGTH_SHORT).show();
        }
    }
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (dialog1!=null)
                if (dialog1.isShowing()) {
                    Button no = (Button) dialog1.findViewById(R.id.dialog_no);
                    no.setEnabled(true);
                }

        }
    };
    private Dialog dialog1;

    private void blinkDialog(final ArrayList<ChubbUp> list) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        final Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        final Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认上架");
        dialog1 = new AlertDialog.Builder(getActivity()).create();
        dialog1.show();
        dialog1.getWindow().setContentView(blinkView);
        dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                上传数据
                for(ChubbUp cu:list){
                    cu.setBas_location(App.CARRIER.getLocationNo()+"");
                    cu.setBas_pallet(App.CARRIER.getTrayNo()+"");
                }
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("data",list);
                jsonObject.put("userId", User.newInstance().getId());
                final String json= JSON.toJSONString(jsonObject);
                final String CARRIER = JSON.toJSONString(App.CARRIER);
                try {
                    AppLog.write(getActivity(),"ccarrier",CARRIER+json,AppLog.TYPE_INFO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/havingLocation", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                try {
                                    AppLog.write(getActivity(),"ccarrier","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    pushing(json);
                                } else {
                                    if (dialog1.isShowing())
                                        dialog1.dismiss();
                                    no.setEnabled(true);
                                    handler.removeCallbacks(r);
                                    Toast.makeText(getActivity(), "库位无效", Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {

                            }
                        }
                    }, CARRIER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                no.setEnabled(false);
                yes.setEnabled(false);
                handler.postDelayed(r,TIME);
            }
        });
    }
    private void pushing(String json){
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/pushClothToCheckIn", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (App.LOGCAT_SWITCH) {
                        Log.i(TAG, "getEpc;" + e.getMessage());
                        Toast.makeText(getActivity(), "扫描查布区布匹失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onResponse(BaseReturn baseReturn) {
                    try {
                        if (dialog1.isShowing())
                            dialog1.dismiss();
                        Button no = (Button) dialog1.findViewById(R.id.dialog_no);
                        no.setEnabled(true);
                        handler.removeCallbacks(r);
//                            BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                            Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                            blinkDialog2(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, json);
        } catch (IOException e) {
            Log.i(TAG, "");
        }
    }
    private AlertDialog dialog;
    private void blinkDialog2(boolean flag) {
        if (dialog == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
            Button no = (Button) blinkView.findViewById(R.id.dialog_no);
            Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
            TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
            if (flag)
                text.setText("上传成功");
            else
                text.setText("上传失败");

            dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.show();
            dialog.getWindow().setContentView(blinkView);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
            if (flag)
                text.setText("上传成功");
            else
                text.setText("上传失败");
            if (!dialog.isShowing())
                dialog.show();
        }
    }
    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            try {
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
                        if (response!=null&&response.getTrayEPC()!=null&&!response.getTrayEPC().equals("")) {
                            App.CARRIER.setTrayEPC(response.getTrayEPC());
                        }

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
            /*} catch (Exception e) {

            }*/
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
