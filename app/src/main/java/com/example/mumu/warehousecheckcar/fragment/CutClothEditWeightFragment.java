package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.User;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CutClothEditWeightFragment extends Fragment implements View.OnTouchListener {
    private final String TAG = "CutClothEditWeightFragment";
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private static CutClothEditWeightFragment fragment;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.text5)
    TextView text5;
    @Bind(R.id.text6)
    TextView text6;
    @Bind(R.id.text7)
    TextView text7;
    @Bind(R.id.edit1)
    EditText edit1;
    @Bind(R.id.button1)
    Button button1;


    private BarCode code;

    public static CutClothEditWeightFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothEditWeightFragment();
        return fragment;
    }

    private JSONObject json;
    private String weightIn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_editweight_layout, container, false);
        view.setOnTouchListener(this);
        ButterKnife.bind(this, view);

        code = new BarCode();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        edit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                weightIn = charSequence.toString();
                weightIn = weightIn.replaceAll("", " ");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(JSONObject event) {
        json = event.getJSONObject("barcode");

        code = json.toJavaObject(BarCode.class);
        text1.setText(code.getVatNo());
        text2.setText(code.getProduct_no());
        text3.setText(code.getColorName());
        text4.setText(code.getSelColor());
        text5.setText(code.getYard_out() + "");
        text6.setText(code.getP_ps() + "");
        edit1.setText("0.0");
        //Toast.makeText(getActivity(), event.getString("barcode"), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @OnClick(R.id.button1)
    public void onViewClicked() {
        edit1.setFocusable(false);
        User user = User.newInstance();
        json.put("userId", user.getId());
        json.put("cutWeight", edit1.getText());

        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = json;
        handler.sendMessage(msg);

//        if(edittext1.getText() != null && !edittext1.getText().toString().equals("")) {
//              Message msg = handler.obtainMessage();
//              msg.arg1 = 0x00;
//              msg.obj = json;
//              handler.sendMessage(msg);
//        } else
//            Toast.makeText(getActivity(),"剪布重量不能为空",Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.arg1) {
                    case 0x00:

                        final String json = msg.obj.toString();
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCutWeight.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "pushCutWeight;" + e.getMessage());
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {

                                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

                                }
                            }, json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        break;
                    case 0x01:
                        break;
                }

            } catch (Exception e) {

            }
        }
    };

}
