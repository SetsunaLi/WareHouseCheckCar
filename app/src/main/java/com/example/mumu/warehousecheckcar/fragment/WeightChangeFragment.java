package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.CheckWeight;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.entity.OutputDetail;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeightChangeFragment extends Fragment implements UHFCallbackLiatener {
    private final String TAG = "WeightChangeFragment";
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.layout1)
    LinearLayout layout1;
    @Bind(R.id.edittext1)
    EditText edittext1;
    @Bind(R.id.edittext2)
    EditText edittext2;
    @Bind(R.id.spinner1)
    Spinner spinner1;
    @Bind(R.id.relativelayout)
    LinearLayout relativelayout;
    @Bind(R.id.button2)
    Button button2;

    private WeightChangeFragment() {
    }

    private static WeightChangeFragment fragment;

    public static WeightChangeFragment newInstance() {
        if (fragment == null) ;
        fragment = new WeightChangeFragment();
        return fragment;
    }

    private Sound sound;
    private CheckWeight cloth;
    private double weight;
    private int id=2;
    private String[] array;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weight_change_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("调整库存重量");
        sound = new Sound(getActivity());
        array = getResources().getStringArray(R.array.change_cause_array);
        edittext1.setFocusable(false);
        edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String str = s.toString();
                    str = str.replaceAll(" ", "");
                    if (str != null && !str.equals("")) {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.adapter_mytopactionbar_spinner, array) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
//                    设置区域spinner展开的Item布局
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.adapter_mytopactionbar_spinner_item, parent, false);
                }
                TextView spinnerText = (TextView) convertView.findViewById(R.id.spinner_textView);
                spinnerText.setText(getItem(position));
                return convertView;
            }
        };
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cloth != null)
                    cloth.setCause(position+1+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner1.setSelection(2);
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

    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    if (App.MUSIC_SWITCH) {
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                    }
                    String EPC = (String) msg.obj;
                    EPC = EPC.replaceAll(" ", "");
                    if (EPC.startsWith("3035A537")) {
                        JSONObject epc = new JSONObject();
                        epc.put("epc", EPC);
                        final String json = epc.toJSONString();
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "getEpc;" + e.getMessage());
                                        Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onResponse(JSONArray jsonArray) {
                                    try {
                                        List<CheckWeight> arry;
                                        arry = jsonArray.toJavaList(CheckWeight.class);
                                        if (arry != null && arry.size() > 0) {
                                            cloth = arry.get(0);
                                            if (cloth != null)
                                                cloth.setCause(id+1+"");
                                            text1.setText("布号:" + cloth.getProduct_no());
                                            text2.setText("销售色号:" + cloth.getSelNo());
                                            text3.setText("缸号:" + cloth.getVatNo());
                                            text4.setText("布票号:" + cloth.getFabRool());
                                            edittext1.setText(cloth.getWeight() + "");
                                            edittext2.setText("");
                                        }

                                    } catch (Exception e) {

                                    }
                                }
                            }, json);
                        } catch (IOException e) {
                            Log.i(TAG, "");
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = handler.obtainMessage();
        msg.what = 0x00;
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        blinkDialog();
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_weight_change, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text1 = (TextView) blinkView.findViewById(R.id.dialog_text1);
        TextView text2 = (TextView) blinkView.findViewById(R.id.dialog_text2);
        TextView text3 = (TextView) blinkView.findViewById(R.id.dialog_text3);
        text1.setText("修改前库存重量:" + cloth.getWeight() + "KG");
        text2.setText("修改后库存重量:" + cloth.getWeightChange() + "KG");
        text3.setText("修改的重量差异:" + ArithUtil.sub(cloth.getWeightChange(), cloth.getWeight()) + "KG");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String json = JSON.toJSONString(cloth);
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/inv_sum/change_weight_inv", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "postInventory;" + e.getMessage());
                                Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                    blinkDialog2(true);
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
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
    }

    private void blinkDialog2(boolean flag) {
        final Dialog dialog;
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
    }
}