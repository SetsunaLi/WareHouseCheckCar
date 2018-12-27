package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.OUT_APPLY_DETAIL;

/**
 * Created by mumu on 2018/12/8.
 */

public class OutApplyFragment extends Fragment implements UHFCallbackLiatener, BasePullUpRecyclerAdapter.OnItemClickListener {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    private final String TAG = "OutApplyFragment";
    private static OutApplyFragment fragment;

    private OutApplyFragment() {
    }

    public static OutApplyFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutApplyFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Inventory> myList;
    /**
     * 匹配机制应该是item分组字段
     */
    private Map<String, Integer> keyValue;
    private List<Inventory> dataList;
    private List<String> dataKey;
    private List<String> epcList;
    private LinearLayoutManager llm;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_apply_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("出库列表");

        initData();
        clearData();
        sound = new Sound(getActivity());

        mAdapter = new RecycleAdapter(recyle, myList, R.layout.apply_item_layout_1);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);

        initRFID();
        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.apply_item_layout_1, null);
        mAdapter.setHeader(view);
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Inventory());
        }
        if (keyValue != null)
            keyValue.clear();
        if (dataKey != null)
            dataKey.clear();
        if (dataList != null)
            dataList.clear();
        if (epcList != null)
            epcList.clear();
    }

    private void initData() {
        myList = new ArrayList<>();
        dataKey = new ArrayList<>();
        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
        keyValue = new HashMap<>();
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
    public void onResume() {
        super.onResume();
        if (App.APPLY_NO != null) {
            final String json = JSON.toJSONString(App.APPLY_NO);
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<List<Inventory>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(getActivity(), "无法获取申请单信息请返回重试！", Toast.LENGTH_SHORT).show();
                    }

                    /**这里应该要大改*/
                    @Override
                    public void onResponse(List<Inventory> response) {
                        /*if (response != null && response.size() != 0) {
                            for (Inventory obj : response) {
                                if (obj != null && obj.getVatNo() != null) {
                                    if (keyValue.containsKey(obj.getVatNo())) {//里面有
                                        myList.get(keyValue.get(obj.getVatNo())).addCountIn();//增加库存量
                                    } else {//里面没有
                                        obj.setCountIn(1);
                                        myList.add(obj);
                                        keyValue.put(obj.getVatNo(), myList.size() - 1);
                                    }
                                    obj.setFlag(0);//默认为0//0为盘亏
                                    dataList.add(obj);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "无法获取申请单信息请重试！", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                }, json);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        clearData();
        disRFID();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                initData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                break;
        }
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.select(position);
            mAdapter.notifyDataSetChanged();

            Inventory obj = myList.get(position);
            String key = obj.getVatNo();
            OUT_APPLY_DETAIL.clear();
            for (Inventory obj2 : dataList) {
                if (obj2.getVatNo().equals(key)) {
                    OUT_APPLY_DETAIL.add(obj2);
                }
            }
            Fragment fragment = OutApplyDetailFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }
    }

    long currenttime = 0;
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
                    String EPC = (String) msg.obj;
                    EPC.replace(" ", "");
                    EPC.replace("\"", "");
//                        可能要查看Epc格式
                    if (!epcList.contains(EPC)) {
                        final String json = JSON.toJSONString(EPC);
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<Inventory>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "getEpc;" + e.getMessage());
                                        Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onResponse(Inventory response) {
                                    if (response != null && response.getEpc() != null && !epcList.contains(response.getEpc())) {
                                        epcList.add(response.getEpc());
                                        boolean flag = false;
                                        for (Inventory data : dataList) {//判断
                                            if (data.getEpc() != null && data.getEpc().equals(response.getEpc())) {//判断成功//实盘
                                                flag = true;
                                                data.setFlag(2);
                                                myList.get(keyValue.get(response.getVatNo())).addCountReal();
                                                break;
                                            }
                                        }
                                        if (!flag) {//盘盈
                                            response.setFlag(1);
                                            dataList.add(response);
                                            if (keyValue.containsKey(response.getVatNo())) {
                                                myList.get(keyValue.get(response.getVatNo())).addCountProfit();
                                            } else {
                                                response.addCountProfit();
                                                myList.add(response);
                                                keyValue.put(response.getVatNo(), myList.size() - 1);
                                            }
                                        }

                                    }
                                }
                            }, json);
                        } catch (IOException e) {

                        } catch (Exception e) {

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
//        扫描标签数据接口
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Inventory> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        protected int position = -255;

        public void select(int position) {
            if (this.position != -255 && this.position != position)
                this.position = position;
            else
                this.position = -255;
        }

        public RecycleAdapter(RecyclerView v, Collection<Inventory> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, Inventory item, int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                if (position != 0) {
                    if (cb.isChecked()) {
                        if (!dataKey.contains(item.getVatNo()))
                            dataKey.add(item.getVatNo());
                    } else {
                        if (dataKey.contains(item.getVatNo()))
                            dataKey .remove(item.getVatNo());
                    }
                }
            }
        }
    }
}
