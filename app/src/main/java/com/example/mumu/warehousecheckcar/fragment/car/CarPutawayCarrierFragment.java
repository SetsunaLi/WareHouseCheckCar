package com.example.mumu.warehousecheckcar.fragment.car;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.entity.car.Coadjutant;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarPutawayCarrierFragment extends BaseFragment implements UHFCallbackLiatener, RXCallback, OnCodeResult, OnRfidResult {
    private final String TAG = "CarPutawayCarrierFragment";

    @BindView(R.id.relativelayout)
    LinearLayout relativelayout;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.edittext1)
    EditText edittext1;
    @BindView(R.id.edittext2)
    EditText edittext2;
    @BindView(R.id.line1)
    LinearLayout line1;
    @BindView(R.id.spinner1)
    Spinner spinner1;

    private static CarPutawayCarrierFragment fragment;

    public static CarPutawayCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarPutawayCarrierFragment();
        return fragment;
    }

    private boolean flagRFID = false;
    private boolean flag2D = false;
    private int assistantID = 0;
    private ArrayList<Coadjutant> data_list;
    private ArrayList<String> spinner_list;
    private ArrayAdapter<String> arr_adapter;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_putaway_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getResources().getString(R.string.btn_car_up));
        return view;
    }

    @Override
    protected void initData() {
        data_list = new ArrayList<>();
        data_list.add(new Coadjutant("", "", "无", -1));
        spinner_list = new ArrayList<>();
        spinner_list.add("无");
        if (App.CARRIER == null)
            App.CARRIER = new Carrier();
        else
            App.CARRIER.clear();
    }

    @Override
    protected void initView(View view) {
        button2.setText("确认库位");
        arr_adapter = new ArrayAdapter<String>(getActivity(), R.layout.adapter_mytopactionbar_spinner, spinner_list) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.adapter_mytopactionbar_spinner_item, parent, false);
                }
                TextView spinnerText = (TextView) convertView.findViewById(R.id.spinner_textView);
                spinnerText.setText(getItem(position).toString());
                return convertView;
            }
        };
        spinner1.setAdapter(arr_adapter);
        spinner1.setSelection(0);
        line1.setVisibility(View.VISIBLE);
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
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assistantID = data_list.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/putaway/getAccountList", new OkHttpClientManager.ResultCallback<JSONArray>() {
            @Override
            public void onError(Request request, Exception e) {
                if (e instanceof ConnectException)
                    showConfirmDialog("链接超时");
                if (App.LOGCAT_SWITCH) {
                    showToast("获取托盘信息失败");
                }
            }

            @Override
            public void onResponse(JSONArray response) {
                List<Coadjutant> list = response.toJavaList(Coadjutant.class);
                data_list.addAll(list);
                spinner_list.clear();
                for (Coadjutant c : data_list)
                    spinner_list.add(c.getUsername());
                arr_adapter.notifyDataSetChanged();
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
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick(R.id.button2)
    public void onViewClicked() {
        if (App.CARRIER != null && App.CARRIER.getLocationNo() != null && !App.CARRIER.getLocationNo().equals("")) {
            edittext1.setFocusableInTouchMode(false);
            edittext2.setFocusableInTouchMode(false);
            if (flagRFID) {
                disRFID();
                flagRFID = false;
            }
            if (flag2D) {
                disConnect2D();
                flag2D = false;
            }
            final String json = JSON.toJSONString(App.CARRIER);
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
                            BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                            if (baseReturn != null && baseReturn.getStatus() == 1) {
                                Toast.makeText(getActivity(), "开始上架", Toast.LENGTH_LONG).show();
                                EventBus.getDefault().postSticky(new EventBusMsg(0x05, assistantID));
                                Fragment fragment = CarPutawayFragment.newInstance();
                                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                                transaction.show(fragment);
                                transaction.commit();
                            } else {
                                showToast("库位无效");
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
        String location = code.replaceAll(" ", "");
        edittext2.setText(location);
    }

    @Override
    public void rfidResult(String epc) {
        String EPC = epc.replaceAll(" ", "");
        if (EPC.startsWith("31B5A5AF")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", EPC);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getCarrier.sh", new OkHttpClientManager.ResultCallback<Carrier>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            showToast("获取库位信息失败");
                        }
                    }

                    @Override
                    public void onResponse(Carrier response) {
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
                    }
                }, json);
            } catch (IOException e) {

            }
        }
    }
}
