package com.example.mumu.warehousecheckcar.fragment.putway;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2019/1/4.
 */

public class PutawayCarrierFragment extends CodeFragment {

    private final String TAG = "CheckCarrierFragment";

    @BindView(R.id.relativelayout)
    LinearLayout relativelayout;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.edittext1)
    EditText edittext1;
    @BindView(R.id.edittext2)
    EditText edittext2;


    private static PutawayCarrierFragment fragment;

    public static PutawayCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new PutawayCarrierFragment();
        return fragment;
    }

    private boolean flagRFID = false;
    private boolean flag2D = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.putaway_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("上架");
        return view;
    }

    @Override
    protected void initData() {
        if (App.CARRIER == null)
            App.CARRIER = new Carrier();
        if (!TextUtils.isEmpty(App.CARRIER.getLocationNo()))
            edittext2.setText(App.CARRIER.getLocationNo());
        if (!TextUtils.isEmpty(App.CARRIER.getTrayNo()))
            edittext1.setText(App.CARRIER.getTrayNo());
    }

    @Override
    protected void initView(View view) {
        button2.setText("确认库位");
    }

    @Override
    protected void addListener() {
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
                        initRFID();
                } else {
                        disRFID();
                }
            }
        });
        edittext2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                        init2D();
                } else {
                        disConnect2D();
                }
            }
        });
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
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick({R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button2:
                handler.removeMessages(ScanResultHandler.RFID);
                if (!TextUtils.isEmpty(App.CARRIER.getLocationNo())) {
                    edittext1.setFocusableInTouchMode(false);
                    edittext2.setFocusableInTouchMode(false);
                    closeConnect();
                    final String json = JSON.toJSONString(new Carrier(App.CARRIER.getTrayNo(), App.CARRIER.getLocationNo()));
                    try {
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/havingLocation", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                            public void onResponse(JSONObject response) {
                                try {
                                    BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                    if (baseReturn != null && baseReturn.getStatus() == 1) {
                                        showToast("开始上架");
                                        Fragment fragment = PutawayFragment.newInstance();
                                        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                                        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                                        transaction.show(fragment);
                                        transaction.commit();
                                    } else {
                                        showConfirmDialog("库位无效");
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }, json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    showToast("请扫描库位硬标签");
                break;
        }
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
