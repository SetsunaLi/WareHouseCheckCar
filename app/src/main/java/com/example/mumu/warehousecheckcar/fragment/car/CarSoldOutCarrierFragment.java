package com.example.mumu.warehousecheckcar.fragment.car;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
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
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import java.io.IOException;
import java.net.ConnectException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class CarSoldOutCarrierFragment extends BaseFragment implements UHFCallbackLiatener, RXCallback, OnCodeResult, OnRfidResult {

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

    private boolean flagRFID = false;
    private boolean flag2D = false;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_car_down));
        View view = inflater.inflate(R.layout.car_soldout_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        if (App.CARRIER == null)
            App.CARRIER = new Carrier();
        else
            App.CARRIER.clear();
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this, this);

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
    }
    private void initRFID() {
        if (!PdaController.initRFID(this)) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
        }
    }

    private void disRFID() {
        if (!PdaController.disRFID()) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
        }
    }

    private void init2D() {
        if (!PdaController.init2D(this)) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        }
    }

    private void disConnect2D() {
        if (!PdaController.disConnect2D()) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        }
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = scanResultHandler.obtainMessage();
        msg.what = ScanResultHandler.RFID;
        msg.obj = tag.strEPC;
        scanResultHandler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @Override
    public void callback(byte[] bytes) {
        Message msg = scanResultHandler.obtainMessage();
        msg.what = ScanResultHandler.CODE;
        msg.obj = new String(bytes);
        scanResultHandler.sendMessage(msg);
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
        showUploadDialog("是否确认下架");
        setUploadYesClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = User.newInstance();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",user.getId());
                jsonObject.put("location",edittext2.getText());
                jsonObject.put("pallet",edittext1.getText());
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
                            if (e instanceof ConnectException)
                                showConfirmDialog("链接超时");
                            if (App.LOGCAT_SWITCH) {
                                Toast.makeText(getActivity(),"叉车下架："+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                AppLog.write(getActivity(), "carsoldout", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                uploadDialog.openView();
                                hideUploadDialog();
                                scanResultHandler.removeCallbacks(r);
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    showToast("上传成功");
                                } else {
                                    showToast("上传失败");
                                    showConfirmDialog("上传失败");
                                    Sound.faillarm();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadDialog.lockView();
                scanResultHandler.postDelayed(r, TIME);
            }
        });
    }

    @Override
    public void codeResult(String code) {
        code = code.replaceAll(" ", "");
        edittext2.setText(code);
    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (epc.startsWith("31B5A5AF")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", epc);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getCarrier.sh", new OkHttpClientManager.ResultCallback<Carrier>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getInventory;" + e.getMessage());
                            showToast("获取库位信息失败");
                        }
                    }

                    @Override
                    public void onResponse(Carrier response) {
                        try {
                            if (response != null) {
                                if (!TextUtils.isEmpty(response.getTrayNo())) {
                                    App.CARRIER.setTrayNo(response.getTrayNo());
                                    edittext1.setText(response.getTrayNo());
                                }
                                if (!TextUtils.isEmpty(response.getTrayEPC()))
                                    App.CARRIER.setTrayEPC(response.getTrayEPC());

                                if (!TextUtils.isEmpty(response.getLocationNo())) {
                                    App.CARRIER.setLocationNo(response.getLocationNo());
                                    edittext2.setText(response.getLocationNo());
                                }
                                if (!TextUtils.isEmpty(response.getLocationEPC()))
                                    App.CARRIER.setLocationEPC(response.getLocationEPC());
                            }
                        } catch (Exception e) {

                        }
                    }
                }, json);

            } catch (IOException e) {

            }
        }
    }
}
