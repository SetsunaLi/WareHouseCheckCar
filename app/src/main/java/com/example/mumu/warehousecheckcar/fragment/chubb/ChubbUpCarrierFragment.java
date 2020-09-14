package com.example.mumu.warehousecheckcar.fragment.chubb;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.entity.chubb.ChubbUp;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class ChubbUpCarrierFragment extends BaseFragment implements UHFCallbackLiatener, RXCallback, OnCodeResult, OnRfidResult {
    private static ChubbUpCarrierFragment fragment;
    @BindView(R.id.edittext1)
    EditText edittext1;
    @BindView(R.id.edittext2)
    EditText edittext2;
    @BindView(R.id.relativelayout)
    LinearLayout relativelayout;
    @BindView(R.id.button2)
    Button button2;


    public static ChubbUpCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbUpCarrierFragment();
        return fragment;
    }

    private final String TAG = "ChubbUpCarrierFragment";
    private boolean flagRFID = false;
    private boolean flag2D = false;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("查布上架");
        return view;
    }

    @Override
    protected void initData() {
        App.CARRIER.clear();
    }

    @Override
    protected void initView(View view) {
        button2.setText("确认库位");
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this, this);
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
                if (hasFocus) {
                    if (!flagRFID) {
                        initRFID();
                        flagRFID = true;
                    }
                } else {
                    if (flagRFID) {
                        disRFID();
                        flagRFID = false;
                    }
                }
            }
        });
        edittext2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!flag2D) {
                        init2D();
                        flag2D = true;
                    }
                } else {
                    if (flag2D) {
                        disConnect2D();
                        flag2D = false;
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
    public void onDestroyView() {
        super.onDestroyView();

        if (flagRFID) {
            disRFID();
            flagRFID = false;
        }
        if (flag2D) {
            disConnect2D();
            flag2D = false;
        }
        EventBus.getDefault().post(new EventBusMsg(0x06));
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        scanResultHandler.removeMessages(ScanResultHandler.RFID);
        final ArrayList<ChubbUp> list = (ArrayList<ChubbUp>) getArguments().getSerializable("dataList");
        if (!TextUtils.isEmpty(App.CARRIER.getLocationNo()) && (list != null && list.size() != 0)) {
            showUploadDialog("是否确认上架");
            setUploadYesClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (ChubbUp cu : list) {
                        cu.setBas_location(App.CARRIER.getLocationNo() + "");
                        cu.setBas_pallet(App.CARRIER.getTrayNo() + "");
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", list);
                    jsonObject.put("userId", User.newInstance().getId());
                    final String json = JSON.toJSONString(jsonObject);
                    final String CARRIER = JSON.toJSONString(App.CARRIER);
                    try {
                        AppLog.write(getActivity(), "ccarrier", CARRIER + json, AppLog.TYPE_INFO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/havingLocation", new OkHttpClientManager.ResultCallback<JSONObject>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                if (e instanceof ConnectException)
                                    showConfirmDialog("链接超时");
                                if (App.LOGCAT_SWITCH) {
                                    showToast("获取库位信息失败");
                                }
                            }

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    AppLog.write(getActivity(), "ccarrier", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                    if (baseReturn != null && baseReturn.getStatus() == 1) {
                                        pushing(json);
                                    } else {
                                        uploadDialog.openView();
                                        hideUploadDialog();
                                        scanResultHandler.removeCallbacks(r);
                                        Toast.makeText(getActivity(), "库位无效", Toast.LENGTH_LONG).show();
                                    }

                                } catch (Exception e) {

                                }
                            }
                        }, CARRIER);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadDialog.lockView();
                    scanResultHandler.postDelayed(r, TIME);
                }
            });
        } else {
            showToast("请输入库位号");
        }
    }

    private void pushing(String json) {
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/pushClothToCheckIn", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        Log.i(TAG, "getEpc;" + e.getMessage());
                        Toast.makeText(getActivity(), "扫描查布区布匹失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onResponse(BaseReturn baseReturn) {
                    try {
                        uploadDialog.openView();
                        hideUploadDialog();
                        scanResultHandler.removeCallbacks(r);
                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                            showToast("上传成功");
                            getActivity().onBackPressed();
                        } else {
                            showToast("上传失败");
                            showConfirmDialog("上传失败");
                            Sound.faillarm();
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
                    @Override
                    public void onError(Request request, Exception e) {
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
