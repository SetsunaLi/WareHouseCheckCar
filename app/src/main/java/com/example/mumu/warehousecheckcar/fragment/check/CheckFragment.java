package com.example.mumu.warehousecheckcar.fragment.check;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.check.Inventory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

/**
 * Created by mumu on 2018/11/26.
 */

public class CheckFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener, OnRfidResult {

    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    private final String TAG = "CheckFragment";

    private static CheckFragment fragment;

    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;


    public static CheckFragment newInstance() {
        if (fragment == null) ;
        fragment = new CheckFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Inventory> myList;
    //    缸号匹配位置
    private Map<String, Integer> keyValue;
    private List<Inventory> dataList;
    private List<String> dataKEY;
    private List<String> epcList;
    private List<String> epcData;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_layout_upgrade, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Inventory());
        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
        keyValue = new HashMap<>();
        dataKEY = new ArrayList<>();
        epcData = new ArrayList<>();
        if (App.CARRIER != null) {
            if (!TextUtils.isEmpty(App.CARRIER.getLocationNo()))
                text2.setText(App.CARRIER.getLocationNo());
            if (!TextUtils.isEmpty(App.CARRIER.getTrayNo()))
                text3.setText(App.CARRIER.getTrayNo());
        }
    }

    @Override
    protected void initView(View view) {
        text1.setText("0");
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.check_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Inventory());
        }
        if (dataList != null)
            dataList.clear();
        if (epcList != null)
            epcList.clear();
        if (keyValue != null)
            keyValue.clear();
        if (dataKEY != null)
            dataKEY.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        downLoadData();
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

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_item, null);
        mAdapter.setHeader(view);
    }

    private void downLoadData() {
        if (App.CARRIER != null) {
            clearData();
            text1.setText("0");
            final String json = JSON.toJSONString(App.CARRIER);
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getInventory", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getCarrier;" + e.getMessage());
                            showToast("获取库位信息失败");
                        }
                    }

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<Inventory> response;
                                response = jsonArray.toJavaList(Inventory.class);
                                if (response != null && response.size() != 0) {
                                    for (Inventory obj : response) {
                                        if (obj != null && obj.getVatNo() != null && !epcData.contains(obj.getEpc())) {
                                            if (keyValue.containsKey(obj.getVatNo())) {//里面有
                                                myList.get(keyValue.get(obj.getVatNo())).addCountIn();//增加库存量
                                            } else {//里面没有
                                                if (!dataKEY.contains(obj.getVatNo()))
                                                    dataKEY.add(obj.getVatNo());
                                                obj.setCountIn(1);
                                                myList.add(obj);
                                                keyValue.put(obj.getVatNo(), myList.size() - 1);
                                            }
                                            obj.setFlag(0);//默认为0//0为盘亏
                                            epcData.add(obj.getEpc());
                                            dataList.add(obj);
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    showToast("该仓位没有库存");
                                }
                            } else {
                                showToast("至少需要输入一个有效库位信息");
                                getActivity().onBackPressed();
                            }
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {

            } catch (Exception e) {

            }
        }
    }

    //右上角列表R.menu.main2
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
    }

    //右上角列表点击监听（相当于onclickitemlistener,可用id或者title匹配）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disRFID();
        clearData();
        myList.clear();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                downLoadData();
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否上传盘点数据");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Inventory> jsocList = new ArrayList<>();
                        for (Inventory obj : dataList) {
                            if (obj.getVatNo() != null && dataKEY.contains(obj.getVatNo())) {
                                obj.setDevice(App.DEVICE_NO);
                                jsocList.add(obj);
                            }
                        }
                        if (jsocList.size() > 0) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("data", jsocList);
                            jsonObject.put("userId", User.newInstance().getId());
                            jsonObject.put("carrier", App.CARRIER);
                            final String json = JSON.toJSONString(jsonObject);
                            try {
                                AppLog.write(getActivity(), "check", json, AppLog.TYPE_INFO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/postInventory.sh", new OkHttpClientManager.ResultCallback<String>() {
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
                                    public void onResponse(String response) {
                                        try {
                                            AppLog.write(getActivity(), "check", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            uploadDialog.openView();
                                            hideUploadDialog();
                                            scanResultHandler.removeCallbacks(r);
                                            if (response.equals("1")) {
                                                showToast("上传成功");
                                                clearData();
                                                mAdapter.notifyDataSetChanged();
                                                getActivity().onBackPressed();
                                            } else {
                                                showToast("上传失败");
                                                showConfirmDialog("上传失败");
                                                Sound.faillarm();
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                }, json);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            uploadDialog.lockView();
                            scanResultHandler.postDelayed(r, TIME);
                        } else {
                            hideUploadDialog();
                            showConfirmDialog("请勾选上传信息");
                        }
                    }
                });
                break;
        }
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.notifyDataSetChanged();
            Inventory icd = myList.get(position);
            String key = icd.getVatNo();
            ArrayList<Inventory> list = new ArrayList<>();
            for (Inventory obj : dataList) {
                if (obj != null && !TextUtils.isEmpty(obj.getVatNo()) && obj.getVatNo().equals(key)) {
                    list.add(obj);
                }
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", list);
            Fragment fragment = CheckDetailFragment.newInstance();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
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

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (epc.startsWith("3035A537") && !epcList.contains(epc)) {
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
                            List<Inventory> arry;
                            arry = jsonArray.toJavaList(Inventory.class);
                            if (arry != null && arry.size() > 0) {
                                Inventory response = arry.get(0);
                                if (response != null && !epcList.contains(response.getEpc())) {
                                    if (!dataKEY.contains(response.getVatNo()))
                                        dataKEY.add(response.getVatNo());
                                    epcList.add(response.getEpc());
                                    boolean isData = false;
                                    for (Inventory obj : dataList) {
                                        if (obj.getEpc().equals(response.getEpc())) {//正常
                                            isData = true;
                                            obj.setFlag(2);
                                        }
                                    }
                                    if (!isData) {//盘盈
                                        response.setFlag(1);
                                        dataList.add(response);
                                    }
                                    if (keyValue.containsKey(response.getVatNo())) {
                                        if (!isData)
                                            myList.get(keyValue.get(response.getVatNo())).addCountProfit();
                                        myList.get(keyValue.get(response.getVatNo())).addCountReal();
                                    } else {
                                        response.addCountProfit();
                                        response.addCountReal();
                                        response.setFlag(1);
                                        myList.add(response);
                                        keyValue.put(response.getVatNo(), myList.size() - 1);
                                    }
                                }
                            }
                            text1.setText(String.valueOf(epcList.size()));
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {
            }
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Inventory> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<Inventory> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public void convert(RecyclerHolder holder, final Inventory item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Inventory i : myList) {
                                    if (!TextUtils.isEmpty(i.getVatNo()) || !TextUtils.isEmpty(i.getProduct_no()) || !TextUtils.isEmpty(i.getSelNo()))
                                        dataKEY.add(i.getVatNo());
                                }
                            } else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getVatNo()))
                                    dataKEY.add(item.getVatNo());
                            } else {
                                if (dataKEY.contains(item.getVatNo()))
                                    dataKEY.remove(item.getVatNo());
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (TextUtils.isEmpty(item.getVatNo()) & TextUtils.isEmpty(item.getProduct_no()) & TextUtils.isEmpty(item.getSelNo())) {
                        cb.setChecked(false);
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                    } else {
                        if (!cb.isEnabled())
                            cb.setEnabled(true);
                        if (dataKEY.contains(item.getVatNo()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (item.getFlag() == 1)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getProduct_no());
                    holder.setText(R.id.item2, item.getSelNo());
                    holder.setText(R.id.item3, item.getColor());
                    holder.setText(R.id.item4, item.getVatNo());
                    holder.setText(R.id.item5, String.valueOf(item.getCountIn()));
                    holder.setText(R.id.item6, String.valueOf(item.getCountReal()));
                    holder.setText(R.id.item7, String.valueOf(item.getCountProfit()));
                    holder.setText(R.id.item8, String.valueOf(item.getCountIn() - item.getCountReal() + item.getCountProfit()));
                }
            }
        }
    }
}
