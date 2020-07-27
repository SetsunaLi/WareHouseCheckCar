package com.example.mumu.warehousecheckcar.fragment.outsource_in;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.Constant;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.Outsource;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by 
 *on 2020/7/22
 */
public class OutsourceNoFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult {
    private static OutsourceNoFragment fragment;
    @Bind(R.id.fixeedittext1)
    FixedEditText fixeedittext1;
    @Bind(R.id.fixeedittext2)
    FixedEditText fixeedittext2;
    @Bind(R.id.button2)
    Button button2;
    private ScanResultHandler scanResultHandler;

    public static OutsourceNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutsourceNoFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_click20));
        View view = inflater.inflate(R.layout.outsource_no_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void addListener() {
        initRFID();
        scanResultHandler = new ScanResultHandler(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x88:
                    initRFID();
                    break;
            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        String po = fixeedittext1.getText().toString();
        String no = fixeedittext2.getText().toString();
        scanResultHandler.removeMessages(ScanResultHandler.RFID);
        disRFID();
        Bundle bundle = new Bundle();
        bundle.putString("po", po);
        bundle.putString("no", no);
        Fragment fragment = OutsourceInFragment.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, App.TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public void rfidResult(String epc) {
        List<String> list = new ArrayList<>();
        list.add(epc);
//        list.add("3035A5370001000000005258");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", Constant.USERNAME);
        jsonObject.put("password", Constant.PRASSWORD);
        jsonObject.put("epcs", list);
        final String json = jsonObject.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.CLOUD_IP + ":" + App.CLOUD_PORT + "/a/bas/basLabelApi/queryEpcs", new OkHttpClientManager.ResultCallback<BaseReturnArray<Outsource>>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (App.LOGCAT_SWITCH) {
                        showToast("获取信息失败");
                    }
                }

                @Override
                public void onResponse(BaseReturnArray<Outsource> returnArray) {
                    if (returnArray != null) {
                        for (Outsource outsource : returnArray.getData()) {
                            if (!TextUtils.isEmpty(outsource.getCust_po()) && TextUtils.isEmpty(outsource.getDeliverNo())) {
                                fixeedittext1.setText(outsource.getCust_po());
                                fixeedittext2.setText(outsource.getDeliverNo());
                            } else {
                                fixeedittext1.setText("");
                                fixeedittext2.setText("");
                            }
                        }
                    }
                }
            }, json);
        } catch (IOException e) {
        }
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
}
