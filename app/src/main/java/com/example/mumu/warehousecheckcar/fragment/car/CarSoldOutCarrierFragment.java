package com.example.mumu.warehousecheckcar.fragment.car;

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

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.AppLog;
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

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class CarSoldOutCarrierFragment extends Fragment implements UHFCallbackLiatener, RXCallback {

    private final String TAG = "CarSoldOutCarrierFragment";

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    private static CarSoldOutCarrierFragment fragment;
    @Bind(R.id.edittext1)
    EditText edittext1;
    @Bind(R.id.edittext2)
    EditText edittext2;
    @Bind(R.id.relativelayout)
    LinearLayout relativelayout;
    @Bind(R.id.button2)
    Button button2;

    public static CarSoldOutCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarSoldOutCarrierFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_car_down));
        View view = inflater.inflate(R.layout.car_soldout_carrier_layout, container, false);

        ButterKnife.bind(this, view);
        //监听输入框信息
        edittext1.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
        else
            App.CARRIER.clear();
        return view;
    }

    private boolean flagRFID = false;
    private boolean flag2D = false;

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
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        blinkDialog();
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
    private void blinkDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        final Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        final Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认下架");
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
                dialog1.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = User.newInstance();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",user.getId());
//        jsonObject.put("location","8A01");
//        jsonObject.put("pallet","TP0001");
                jsonObject.put("location",edittext2.getText());
                jsonObject.put("pallet",edittext1.getText());


//        if (App.CARRIER != null &&App.CARRIER.getLocationNo() != null&& !App.CARRIER.getLocationNo().equals("")) {
//            final String json = JSON.toJSONString(App.CARRIER);
                final  String json = jsonObject.toJSONString();
                try {
                    AppLog.write(getActivity(),"carsoldout",json,AppLog.TYPE_INFO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/static/forkDown.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Toast.makeText(getActivity(),"叉车下架："+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                try {
                                    AppLog.write(getActivity(),"carsoldout","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (CarSoldOutCarrierFragment.this.dialog1.isShowing())
                                    CarSoldOutCarrierFragment.this.dialog1.dismiss();
                                no.setEnabled(true);
                                handler.removeCallbacks(r);
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();

//                                    blinkDialog2(true);
                                } else {
                                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                    blinkDialog2(false);
                                    Sound.faillarm();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//        } else
//            Toast.makeText(getActivity(), "请扫描库位硬标签", Toast.LENGTH_SHORT).show();
                no.setEnabled(false);
                yes.setEnabled(false);
                handler.postDelayed(r,TIME);
            }
        });
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
   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           try{
               switch (msg.arg1) {
                   case 0x00:
                       if (App.MUSIC_SWITCH) {
                           if (System.currentTimeMillis() - currenttime > 150) {
                               Sound.scanAlarm();
                               currenttime = System.currentTimeMillis();
                           }
                       }
                       String EPC = (String) msg.obj;
                       EPC = EPC.replaceAll(" ", "");
                       if (EPC.startsWith("31B5A5AF")) {
                           JSONObject epc = new JSONObject();
                           epc.put("epc", EPC);
                           final String json = epc.toJSONString();
                           try{
                               OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getCarrier.sh", new OkHttpClientManager.ResultCallback<Carrier>() {
                                   @SuppressLint("LongLogTag")
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

                           }catch(IOException e){

                           }
                       }
                       break;
                   case 0x01:
                       Carrier response = (Carrier) msg.obj;
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
                               Sound.scanAlarm();
                               currenttime = System.currentTimeMillis();
                           }
                       }
                       String location= (String) msg.obj;
                       location=location.replaceAll(" ","");
                       edittext2.setText(location);
                       break;
               }

           }catch(Exception e){

           }
       }
   };
}
