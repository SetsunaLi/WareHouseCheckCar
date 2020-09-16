package com.example.mumu.warehousecheckcar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.check.CheckWeight;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class WeightChangeFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult {
    private final String TAG = "WeightChangeFragment";
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.text4)
    TextView text4;
    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.edittext1)
    EditText edittext1;
    @BindView(R.id.edittext2)
    EditText edittext2;
    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.relativelayout)
    LinearLayout relativelayout;
    @BindView(R.id.button2)
    Button button2;

    private static WeightChangeFragment fragment;

    public static WeightChangeFragment newInstance() {
        if (fragment == null) ;
        fragment = new WeightChangeFragment();
        return fragment;
    }

    private CheckWeight cloth;
    private double weight;
    private int id = 2;
    private String[] array;
    private ScanResultHandler scanResultHandler;
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (dialog1 != null)
                if (dialog1.isShowing()) {
                    Button no = (Button) dialog1.findViewById(R.id.dialog_no);
                    no.setEnabled(true);
                }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weight_change_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("调整库存重量");
        return view;
    }

    @Override
    protected void initData() {
        array = getResources().getStringArray(R.array.change_cause_array);
    }

    @Override
    protected void initView(View view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.adapter_mytopactionbar_spinner, array) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.adapter_mytopactionbar_spinner_item, parent, false);
                }
                TextView spinnerText = (TextView) convertView.findViewById(R.id.spinner_textView);
                spinnerText.setText(getItem(position));
                return convertView;
            }
        };
        spinner1.setAdapter(adapter);
        edittext1.setFocusable(false);
    }

    @Override
    protected void addListener() {
        edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String str = s.toString();
                    str = str.replaceAll(" ", "");
                    if (!TextUtils.isEmpty(str)) {
                        weight = Double.parseDouble(str);
                        cloth.setWeightChange(weight);
                    }
                } catch (Exception e) {
                    edittext2.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cloth != null)
                    cloth.setCause(position + 1 + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner1.setSelection(2);
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
    }

    private void initRFID() {
        if (!PdaController.initRFID(this)) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
        }
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    private void disRFID() {
        if (!PdaController.disRFID()) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
        }
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disRFID();
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        blinkDialog();
    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = scanResultHandler.obtainMessage();
        msg.what = ScanResultHandler.RFID;
        msg.obj = tag.strEPC;
        scanResultHandler.sendMessage(msg);
    }
    private Dialog dialog1;

    private void blinkDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_weight_change, null);
        final Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        final Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text1 = (TextView) blinkView.findViewById(R.id.dialog_text1);
        TextView text2 = (TextView) blinkView.findViewById(R.id.dialog_text2);
        TextView text3 = (TextView) blinkView.findViewById(R.id.dialog_text3);
        text1.setText("修改前库存重量:" + cloth.getWeight() + "KG");
        text2.setText("修改后库存重量:" + cloth.getWeightChange() + "KG");
        text3.setText("修改的重量差异:" + ArithUtil.sub(cloth.getWeightChange(), cloth.getWeight()) + "KG");
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", cloth);
                jsonObject.put("userId", User.newInstance().getId());
                final String json = JSON.toJSONString(jsonObject);
                try {
                    AppLog.write(getActivity(), "weightc", json, AppLog.TYPE_INFO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/inv_sum/change_weight_inv", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (e instanceof ConnectException)
                                showConfirmDialog("链接超时");
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "postInventory;" + e.getMessage());
                                showToast("上传信息失败");
                            }
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                try {
                                    AppLog.write(getActivity(), "weightc", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (dialog1.isShowing())
                                    dialog1.dismiss();
                                no.setEnabled(true);
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
                                e.printStackTrace();
                            }
                        }
                    }, json);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                no.setEnabled(false);
                yes.setEnabled(false);
                scanResultHandler.postDelayed(r, TIME);
            }
        });
    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", epc);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            showToast("获取库位信息失败");
                        }
                    }

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            List<CheckWeight> arry;
                            arry = jsonArray.toJavaList(CheckWeight.class);
                            if (arry != null && arry.size() > 0 && !TextUtils.isEmpty(arry.get(0).getVatNo())) {
                                cloth = arry.get(0);
                                if (cloth != null) {
                                    cloth.setCause(String.valueOf(id + 1));
                                    text1.setText("布号:" + cloth.getProduct_no());
                                    text2.setText("销售色号:" + cloth.getSelNo());
                                    text3.setText("缸号:" + cloth.getVatNo());
                                    text4.setText("布票号:" + cloth.getFabRool());
                                    edittext1.setText(String.valueOf(cloth.getWeight()));
                                    edittext2.setText("");
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {
                Log.i(TAG, "");
            }
    }
}
