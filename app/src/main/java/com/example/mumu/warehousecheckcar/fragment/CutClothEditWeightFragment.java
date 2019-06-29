package com.example.mumu.warehousecheckcar.fragment;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.User;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

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


    public static CutClothEditWeightFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothEditWeightFragment();
        return fragment;
    }

    private JSONObject json;
    private BarCode code;

    private double weightIn;


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
                try {
                    String weight = charSequence.toString();
                    weight = weight.replaceAll(" ", "");
                    if (weight != null && !weight.equals("")) {
                        weightIn = Double.parseDouble(weight);
                    }
                } catch (Exception e) {
                    edit1.setText("0.0");
                }
//                weightIn = Double.parseDouble(charSequence.toString());
//                weightIn = weightIn.replaceAll(" ", "");

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
        blinkDialog();
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认板布重量？");
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
                dialog.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = User.newInstance();
                json.put("userId", user.getId());
                json.put("cutWeight", edit1.getText());
                JSONObject jsonObject = new JSONObject(json);
                final String json = jsonObject.toJSONString();
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
                            try {
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                    getActivity().onBackPressed();
//                                    blinkDialog2(true);
                                } else {
                                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                    blinkDialog2(false);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
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

}
