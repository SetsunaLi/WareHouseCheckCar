package com.example.mumu.warehousecheckcar.fragment.chubb;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 新建查布接收
 *on 2020/4/29
 */
public class ChubbClothGetNewFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult {
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    private RecycleAdapter mAdapter;
    private List<Cloth> myList;
    //    勾选框
    private List<String> dataKey;
    //    epc
    private List<String> epcList;
    private ScanResultHandler scanResultHandler;

    public static ChubbClothGetNewFragment newInstance() {
        return new ChubbClothGetNewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubb_cloth_get_new_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Cloth());
        dataKey = new ArrayList<>();
        epcList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubb_cloth_get_new_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        View headview = LayoutInflater.from(getActivity()).inflate(R.layout.chubb_cloth_get_new_item, null);
        mAdapter.setHeader(headview);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        text1.setText(String.valueOf(epcList.size()));
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
    }

    private void clearData() {
        myList.clear();
        myList.add(new Cloth());
        text1.setText(String.valueOf(myList.size() - 1));
        dataKey.clear();
        epcList.clear();
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

        disRFID();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                showUploadDialog("是否上传数据");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                        upload();
                    }
                });
                break;
        }
    }

    private void upload() {
        List<String> list = new ArrayList<>();
        for (Cloth cloth : myList) {
            if (dataKey.contains(cloth.getEpc())) {
                list.add(cloth.getEpc());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("epcs", list);
        jsonObject.put("userId", User.newInstance().getId());
        final String json = JSON.toJSONString(jsonObject);
        try {
            AppLog.write(getActivity(), "chubbget", json, AppLog.TYPE_INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/postCheckReceive", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        Log.i(TAG, "postInventory;" + e.getMessage());
                        Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        AppLog.write(getActivity(), "chubbget", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadDialog.openView();
                        hideUploadDialog();
                        scanResultHandler.removeCallbacks(r);
                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                            showToast("上传成功");
                            clearData();
                            mAdapter.notifyDataSetChanged();
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
    }

    @Override
    public void rfidResult(String epc) {
        final String EPC = epc.replaceAll(" ", "");
        if (!epcList.contains(EPC)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", EPC);
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
                            List<Cloth> arry = jsonArray.toJavaList(Cloth.class);
                            if (arry != null && arry.size() > 0) {
                                Cloth response = arry.get(0);
                                if (response != null && !TextUtils.isEmpty(response.getVatNo())) {
                                    if (response.getEpc() != null && !epcList.contains(response.getEpc())) {
                                        epcList.add(EPC);
                                        myList.add(response);
                                        dataKey.add(EPC);
                                        Collections.sort(myList, new Comparator<Cloth>() {
                                            @Override
                                            public int compare(Cloth obj1, Cloth obj2) {
                                                String aFab = obj1.getFabRool();
                                                String bFab = obj2.getFabRool();
                                                if (TextUtils.isEmpty(aFab) & !TextUtils.isEmpty(bFab))
                                                    return -1;
                                                else if (TextUtils.isEmpty(bFab) & !TextUtils.isEmpty(aFab))
                                                    return 1;
                                                else if (TextUtils.isEmpty(bFab) & TextUtils.isEmpty(aFab))
                                                    return 0;
                                                else {
                                                    int a = aFab.compareTo(bFab);
                                                    if (a == 0) {
                                                        return 0;
                                                    } else if (a > 0) {
                                                        return 1;
                                                    } else {
                                                        return -1;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    text1.setText(String.valueOf(myList.size() - 1));
                                    mAdapter.notifyDataSetChanged();
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

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cloth> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<Cloth> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        @Override
        public void convert(RecyclerHolder holder, final Cloth item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Cloth i : myList) {
                                    if (!TextUtils.isEmpty(i.getVatNo()) || !TextUtils.isEmpty(i.getProduct_no()) || !TextUtils.isEmpty(i.getSelNo()))
                                        dataKey.add(i.getEpc());
                                }
                            } else {
                                dataKey.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKey.contains(item.getEpc()))
                                    dataKey.add(item.getEpc());
                            } else {
                                if (dataKey.contains(item.getEpc()))
                                    dataKey.remove(item.getEpc());
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (TextUtils.isEmpty(item.getVatNo()) && TextUtils.isEmpty(item.getProduct_no()) && TextUtils.isEmpty(item.getSelNo())) {
                        cb.setChecked(false);
                        if (cb.getVisibility() != View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    } else {
                        if (cb.getVisibility() != View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKey.contains(item.getEpc()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    holder.setText(R.id.item1, item.getVatNo());
                    holder.setText(R.id.item2, item.getProduct_no());
                    holder.setText(R.id.item3, item.getFabRool());
                    holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                    holder.setText(R.id.item5, item.getSelNo());
                }
            }
        }
    }
}
