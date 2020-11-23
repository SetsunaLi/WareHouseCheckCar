package com.example.mumu.warehousecheckcar.fragment.cut;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
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


public class CuttingClothPutwayCarrierFragment extends CodeFragment {
    public final String TAG = "CuttingClothCarrier";

    private static CuttingClothPutwayCarrierFragment fragment;
    @BindView(R.id.edittext2)
    EditText edittext2;
    @BindView(R.id.relativelayout)
    LinearLayout relativelayout;
    @BindView(R.id.button2)
    Button button2;

    private String[] data_list;
    private String[] data_lists;

    public static CuttingClothPutwayCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CuttingClothPutwayCarrierFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.cut_cloth_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getResources().getString(R.string.btn_click14));
        return view;
    }

    @Override
    protected void initData() {
        if (App.CARRIER == null)
            App.CARRIER = new Carrier();
        else
            App.CARRIER.clear();
        App.CARRIER.setLocationNo(getResources().getString(R.string.cut_carroer));
    }

    @Override
    protected void initView(View view) {
        edittext2.setText(getResources().getString(R.string.cut_carroer));
    }

    @Override
    protected void addListener() {
        edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String locationNo = charSequence.toString();
                locationNo = locationNo.replaceAll(" ", "");
                App.CARRIER.setLocationNo(locationNo);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edittext2.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                        init2D();
                } else {
                        disConnect2D();
                }
            }

        });
    }

    //空加下拉
    private void initSpinner() {
        data_list = getResources().getStringArray(R.array.change_Empty_array);
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(getActivity(), R.layout.adapter_mytopactionbar_spinner, data_list) {
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
        //加载适配器
      /*  spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               double num = getNum(getActivity().getResources().getStringArray(R.array.change_Empty_array)[position]);
               cloth.setBlank_add(num);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    //纸卷重下拉
    private void initSpinners() {
        //数据
        data_lists = getResources().getStringArray(R.array.change_paper_array);

        //适配器
        ArrayAdapter<String> arr_adapters = new ArrayAdapter<String>(getActivity(), R.layout.adapter_mytopactionbar_spinner, data_lists) {
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
        //加载适配器
      /*  spinner1.setAdapter(arr_adapters);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double num = getNum(getActivity().getResources().getStringArray(R.array.change_paper_array)[position]);
                cloth.setWeight_papertube(num);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick(R.id.button2)
    public void onViewClicked() {
        if (App.CARRIER != null && !TextUtils.isEmpty(App.CARRIER.getLocationNo())) {
            edittext2.setFocusableInTouchMode(false);
            closeConnect();
            final String json = JSON.toJSONString(App.CARRIER);
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
                                Fragment fragment = CuttingClothPutwayFragment.newInstance();
                                getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                getActivity().getFragmentManager().beginTransaction().add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
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
    public void codeResult(String code) {
        code = code.replaceAll(" ", "");
        edittext2.setText(code);
    }
}
