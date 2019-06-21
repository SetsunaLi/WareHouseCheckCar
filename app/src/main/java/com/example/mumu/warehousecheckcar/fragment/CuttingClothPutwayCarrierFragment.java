package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.ArrayAdapter;
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
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CuttingClothPutwayCarrierFragment extends Fragment implements RXCallback{
     public final String TAG = "CuttingClothCarrier";

    private static CuttingClothPutwayCarrierFragment fragment;
   /* @Bind(R.id.spinner1)
    Spinner spinner;
    @Bind(R.id.spinner2)
    Spinner spinner1;*/
    @Bind(R.id.edittext2)
    EditText edittext2;
    @Bind(R.id.relativelayout)
    LinearLayout relativelayout;
    @Bind(R.id.button2)
    Button button2;


    private String[] data_list;
    private String[] data_lists;
//    private Cut cloth;
    private Sound sound;
//    private boolean flagRFID = false;
    private boolean flag2D = false;

    public static CuttingClothPutwayCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CuttingClothPutwayCarrierFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_carrier_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getResources().getString(R.string.btn_click14));
        sound = new Sound(getActivity());
//         cloth = new Cut();
//        initSpinner();
//        initSpinners();
        //监听输入框
        edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String locationNo = charSequence.toString();
                locationNo = locationNo.replaceAll(" ","");
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
        edittext2.setText(getResources().getString(R.string.cut_carroer));
        return view;
    }


    //空加下拉
    private void initSpinner() {
        //数据
        data_list = getResources().getStringArray(R.array.change_Empty_array);

        //适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(getActivity(), R.layout.adapter_mytopactionbar_spinner, data_list) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.adapter_mytopactionbar_spinner_item,parent,false);
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
    private Double getNum(String num) {
        double str = Double.parseDouble(num.replaceAll("[a-zA-Z]", ""));
        return str;
    }

    private TDScannerHelper scannerHander;
    //连接读头
    @SuppressLint("LongLogTag")
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
    //断开读头
    private void disConnect2D() {
        try {
            RFID_2DHander.getInstance().off_2D();
//            RFID_2DHander.getInstance().disConnect2D();

        } catch (Exception e) {

        }
    }


    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 0x00:
                    if (App.MUSIC_SWITCH) {
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                    }
                    String location = (String) msg.obj;
                    location = location.replaceAll(" ", "");
                    edittext2.setText(location);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

            if (flag2D) {
                disConnect2D();
                flag2D = false;
            }
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick(R.id.button2)
    public void onViewClicked(){
      if(App.CARRIER != null && App.CARRIER.getLocationNo() != null && !App.CARRIER.getLocationNo().equals("")) {
          edittext2.setFocusableInTouchMode(false);
          if (flag2D) {
              disConnect2D();
              flag2D = false;
          }
          final String json = JSON.toJSONString(App.CARRIER);
          try {
              OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/havingLocation", new OkHttpClientManager.ResultCallback<JSONObject>() {
                  @Override
                  public void onError(Request request, Exception e) {
                      if (App.LOGCAT_SWITCH) {
                          Log.i(TAG, "getInventory;" + e.getMessage());
                          Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                      }
                  }
                  @Override
                  public void onResponse(JSONObject response) {
                      try {
                          BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                          if (baseReturn != null && baseReturn.getStatus() == 1) {
                              Toast.makeText(getActivity(), "开始上架", Toast.LENGTH_LONG).show();
                              Fragment fragment = CuttingClothPutwayFragment.newInstance();
                              getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                              getActivity().getFragmentManager().beginTransaction().add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
//                              EventBus.getDefault().postSticky(new EventBusMsg(0x03,cloth));
                          } else {
                              Toast.makeText(getActivity(), "库位无效", Toast.LENGTH_LONG).show();
                          }

                      } catch (Exception e) {

                      }
                  }
              },json);
          } catch(IOException e){
              e.printStackTrace();
          }
      } else
          Toast.makeText(getActivity(), "请扫描库位硬标签", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void callback(byte[] bytes) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = new String(bytes);
        handler.sendMessage(msg);
    }
}
