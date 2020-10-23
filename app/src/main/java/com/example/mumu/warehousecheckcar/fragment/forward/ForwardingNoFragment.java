package com.example.mumu.warehousecheckcar.fragment.forward;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnObject;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.forwarding.Forwarding;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class ForwardingNoFragment extends BaseFragment implements RXCallback, OnCodeResult {
    final String TAG = "ForwardingNoFragment";
    private static ForwardingNoFragment fragment;
    @BindView(R.id.imgbutton)
    ImageButton imgbutton;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;


    private ForwardingMsgFragment.CarMsg carMsg;
    private String company;


    public static ForwardingNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new ForwardingNoFragment();
        return fragment;
    }

    private boolean scannerFlag = true;
    private ArrayList<String> myList;
    private RecycleAdapter mAdapter;
    private ScanResultHandler scanResultHandler;
    private int transport_output_id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forwarding_no_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add("");
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_applyno_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        init2D();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
    public void onResume() {
        super.onResume();
        text1.setText(carMsg.getCarNo());
        if (carMsg.getCarName() != null)
            text2.setText(carMsg.getCarName());
        mAdapter.setId(0);
        mAdapter.select(0);
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg eventBusMsg) {
        switch (eventBusMsg.getStatus()) {
            case 0x00:
                ForwardingMsgFragment.CarMsg carMsg = (ForwardingMsgFragment.CarMsg) eventBusMsg.getPositionObj(0);
                company = (String) eventBusMsg.getPositionObj(1);
                if (carMsg != null)
                    this.carMsg = carMsg;
                else {
                    this.carMsg = new ForwardingMsgFragment.CarMsg("", "");
                    showToast("车牌号为空");
                }
                transport_output_id = (int) eventBusMsg.getPositionObj(2);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        if (scannerFlag)
            disConnect2D();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick({R.id.imgbutton, R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgbutton:
                addItem();
                break;
            case R.id.button1:
                mAdapter.select(-255);
                mAdapter.notifyDataSetChanged();
                ArrayList<String> nos = new ArrayList<>();
                for (String no : myList) {
                    if (!TextUtils.isEmpty(no) && !nos.contains(no)) {
                        nos.add(no);
                    }
                }
                if (nos.size() != 0) {
                    if (scannerFlag)
                        disConnect2D();
                    scannerFlag = false;
                    EventBus.getDefault().postSticky(new EventBusMsg(0x01, carMsg, nos, transport_output_id, company));
                    Fragment fragment = ForwardingFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    showToast(getResources().getString(R.string.forwarding_toast_msg));
                }
                break;
            case R.id.button2:
                showUploadDialog("是否剪板发运？");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submit();
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });
                break;
        }
    }

    private void addItem() {
        for (int i = 0; i < myList.size(); i++) {
            if (TextUtils.isEmpty(myList.get(i))) {
                mAdapter.select(i);
                mAdapter.setId(i);
                mAdapter.notifyDataSetChanged();
                recyle.scrollToPosition(i);
                return;
            }
        }
        myList.add("");
        mAdapter.select(myList.size() - 1);
        mAdapter.setId(myList.size() - 1);
        mAdapter.notifyDataSetChanged();
        recyle.scrollToPosition(myList.size() - 1);
    }

    private void submit() {
        ArrayList<String> nos = new ArrayList<>();
        for (String no : myList) {
            if (!TextUtils.isEmpty(no) && !nos.contains(no)) {
                nos.add(no);
            }
        }
        if (nos.size() != 0) {
            JSONObject jsonObject = new JSONObject();
            int id = User.newInstance().getId();
            jsonObject.put("userId", id);
            jsonObject.put("carMsg", carMsg);
            jsonObject.put("company", company);
            jsonObject.put("cc_transport_output_id", transport_output_id);
            jsonObject.put("status", 0);
            jsonObject.put("applyNo", nos);
            jsonObject.put("data", new ArrayList<>());
            String json = jsonObject.toJSONString();
            try {
                LogUtil.i(getResources().getString(R.string.log_forwarding_car), json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/postTransportOut", new OkHttpClientManager.ResultCallback<BaseReturnObject<JSONObject>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        try {
                            LogUtil.e(getResources().getString(R.string.log_forwarding_car_result), e.getMessage(), e.getCause());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponse(BaseReturnObject<JSONObject> response) {
                        try {
                            LogUtil.i(getResources().getString(R.string.log_forwarding_car_result), "userId:" + User.newInstance().getId() + response.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            uploadDialog.openView();
                            hideUploadDialog();
                            scanResultHandler.removeCallbacks(r);
                            if (response.getStatus() == 1) {
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
            }
        } else
            showToast(getResources().getString(R.string.forwarding_toast_msg));
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
        int id = mAdapter.getId();
        myList.set(id, code);
        addItem();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    class RecycleAdapter extends BasePullUpRecyclerAdapter<String> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        private int position = -255;

        public void select(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        private int id = -255;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public RecycleAdapter(RecyclerView v, Collection<String> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, String item, final int position) {
            final FixedEditText editNo = (FixedEditText) holder.getView(R.id.fixeedittext1);
            editNo.setTag(position);
            editNo.setText(item);
            if (position == this.position) {
                editNo.setFocusable(true);//设置输入框可聚集
                editNo.setFocusableInTouchMode(true);//设置触摸聚焦
                editNo.requestFocus();//请求焦点
                editNo.findFocus();//获取焦点
                editNo.setCursorVisible(true);
                editNo.setSelection(editNo.getText().length());
                showKeyBoard(editNo);
            }
            editNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        setId(position);
                    }
                }
            });
            editNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    Log.i("onTextChanged", "onTextChanged");
                    myList.set(position, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            ImageButton imageButton = (ImageButton) holder.getView(R.id.imagebutton1);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myList.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
