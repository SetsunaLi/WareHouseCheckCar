//package com.example.mumu.warehousecheckcar.fragment.cut;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSONObject;
//import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
//import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
//import com.example.mumu.warehousecheckcar.R;
//import com.example.mumu.warehousecheckcar.application.App;
//import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
//import com.example.mumu.warehousecheckcar.entity.BarCode;
//import com.example.mumu.warehousecheckcar.entity.BaseReturn;
//import com.example.mumu.warehousecheckcar.entity.User;
//import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
//import com.example.mumu.warehousecheckcar.utils.AppLog;
//import com.squareup.okhttp.Request;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.IOException;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//import static com.example.mumu.warehousecheckcar.application.App.TIME;
//
//public class CutClothEditWeightFragment extends BaseFragment implements View.OnTouchListener {
//    private final String TAG = "CutClothEditWeightFragment";
//    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
//    private static CutClothEditWeightFragment fragment;
//    @Bind(R.id.text1)
//    TextView text1;
//    @Bind(R.id.text2)
//    TextView text2;
//    @Bind(R.id.text3)
//    TextView text3;
//    @Bind(R.id.text4)
//    TextView text4;
//    @Bind(R.id.text5)
//    TextView text5;
//    @Bind(R.id.text6)
//    TextView text6;
//    @Bind(R.id.text7)
//    TextView text7;
//    @Bind(R.id.edit1)
//    EditText edit1;
//    @Bind(R.id.button1)
//    Button button1;
//
//
//    public static CutClothEditWeightFragment newInstance() {
//        if (fragment == null) ;
//        fragment = new CutClothEditWeightFragment();
//        return fragment;
//    }
//
//    private JSONObject json;
//    private BarCode code;
//    private double weightIn;
//    private ScanResultHandler scanResultHandler;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.cut_cloth_editweight_layout, container, false);
//        view.setOnTouchListener(this);
//        ButterKnife.bind(this, view);
//
//        return view;
//    }
//
//    @Override
//    protected void initData() {
//        code = new BarCode();
//    }
//
//    @Override
//    protected void initView(View view) {
//
//    }
//
//    @Override
//    protected void addListener() {
//        scanResultHandler = new ScanResultHandler();
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
//        edit1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                try {
//                    String weight = charSequence.toString();
//                    weight = weight.replaceAll(" ", "");
//                    if (weight != null && !weight.equals("")) {
//                        weightIn = Double.parseDouble(weight);
//                    }
//                } catch (Exception e) {
//                    edit1.setText("0.0");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }
//
//    //接收事件
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onMessageEvent(JSONObject event) {
//        json = event.getJSONObject("barcode");
//        code = json.toJavaObject(BarCode.class);
//        text1.setText(code.getVatNo());
//        text2.setText(code.getProduct_no());
//        text3.setText(code.getColorName());
//        text4.setText(code.getSelColor());
//        text5.setText(code.getYard_out() + "");
//        text6.setText(code.getP_ps() + "");
//        edit1.setText("0.0");
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
//        ButterKnife.unbind(this);
//    }
//
//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        return true;
//    }
//
//    @OnClick(R.id.button1)
//    public void onViewClicked() {
//        edit1.setFocusable(false);
//        showUploadDialog("是否确认板布重量?");
//        setUploadYesClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                User user = User.newInstance();
//                json.put("userId", user.getId());
//                json.put("cutWeight", edit1.getText());
//                JSONObject jsonObject = new JSONObject(json);
//                final String json = jsonObject.toJSONString();
//                try {
//                    AppLog.write(getActivity(), "cceweight", json, AppLog.TYPE_INFO);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCutWeight.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
//                        @SuppressLint("LongLogTag")
//                        @Override
//                        public void onError(Request request, Exception e) {
//                            if (App.LOGCAT_SWITCH) {
//                                Log.i(TAG, "pushCutWeight;" + e.getMessage());
//                            }
//                        }
//
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                AppLog.write(getActivity(), "cceweight", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                uploadDialog.openView();
//                                hideUploadDialog();
//                                scanResultHandler.removeCallbacks(r);
//                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
//                                if (baseReturn != null && baseReturn.getStatus() == 1) {
//                                    showToast("上传成功");
//                                    getActivity().onBackPressed();
//                                } else {
//                                    showToast("上传失败");
//                                    showConfirmDialog("上传失败");
//                                    Sound.faillarm();
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }, json);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                uploadDialog.lockView();
//                scanResultHandler.postDelayed(r, TIME);
//            }
//        });
//    }
//}
