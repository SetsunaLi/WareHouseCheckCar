package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.activity.Main2Activity;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.entity.OutputDetail;
import com.example.mumu.warehousecheckcar.listener.FragmentCallBackListener;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.APPLY_NO;
import static com.example.mumu.warehousecheckcar.application.App.DATA_KEY;
import static com.example.mumu.warehousecheckcar.application.App.KEY;

public class OutApplyNewFragment extends Fragment implements UHFCallbackLiatener, FragmentCallBackListener, BasePullUpRecyclerAdapter.OnItemClickListener {
    private final String TAG = "OutApplyNewFragment";
    private static OutApplyNewFragment fragment;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.recyle)
    RecyclerView recyle;

    private OutApplyNewFragment() {
    }

    public static OutApplyNewFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutApplyNewFragment();
        return fragment;
    }

    private ArrayList<String> fatherNoList;
    private HashMap<String, OutputFlag> epcKeyList;
    private ArrayList<Output> myList;
    private ArrayList<Output> dataList;
    private ArrayList<String> dateNo;

    private RecycleAdapter mAdapter;
    private LinearLayoutManager llm;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_apply_new_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("出库列表");

        initData();
        sound = new Sound(getActivity());

        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_apply_child_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setOnItemClickListener(this);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);

        initRFID();
        return view;
    }

    public void initData() {
        fatherNoList = new ArrayList<>();
        epcKeyList = new HashMap<>();
        myList = new ArrayList<>();
        dataList = new ArrayList<>();
        dateNo = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<String> list = (ArrayList<String>) getArguments().getSerializable("NO");
        fatherNoList.clear();
        fatherNoList.addAll(list);
        Iterator<String> iter = fatherNoList.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (str == null || str.equals(""))
                iter.remove();
        }
        downLoadData();
    }

    public void downLoadData() {
        for (String no : fatherNoList) {
            JSONObject object = new JSONObject();
            object.put("applyNo", no);
            final String json = object.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/output/pullOutput.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    /**这里应该要大改*/
                    @Override
                    public void onResponse(JSONArray json) {
                        try {
                            List<Output> response;
                            response = json.toJavaList(Output.class);
                            if (response != null && response.size() != 0) {
                                if (!dateNo.contains(response.get(0).getApplyNo())) {
                                    dateNo.add(response.get(0).getApplyNo());
                                    response.get(0).setStatus(true);
                                    for (Output output : response) {
                                        for (OutputDetail outputDetail : output.getList()) {
                                            if (!epcKeyList.containsKey(outputDetail.getEpc())) {
                                                epcKeyList.put(outputDetail.getEpc() + "", new OutputFlag(false, "", true));
                                            }
                                        }
                                        final String key = output.getOutp_id() + output.getVatNo() + output.getProduct_no() + output.getSelNo();
                                        if (!DATA_KEY.containsKey(output.getApplyNo())) {
                                            DATA_KEY.put(output.getApplyNo(), new ArrayList<String>());
                                        }
                                        if (!DATA_KEY.get(output.getApplyNo()).contains(key)) {
                                            DATA_KEY.get(output.getApplyNo()).add(key);
                                        }
                                    }
                                    myList.addAll(response);
                                }
                                mAdapter.notifyDataSetChanged();
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
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disRFID();
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
                    final String EPC = ((String) msg.obj).replaceAll(" ", "");
                    if (EPC.startsWith("3035A537") && epcKeyList.containsKey(EPC)) {
                        epcKeyList.get(EPC).setFind(true);
                        epcKeyList.get(EPC).setStatus(true);
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

    @Override
    public void comeBackListener() {
        epcKeyList.clear();
        epcKeyList.putAll(((Main2Activity)getActivity()).getOutApplyDataList());

    }

    @Override
    public void ubLoad(boolean flag) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                break;
        }
    }
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
        Output obj = myList.get(position);
        String key = obj.getOutp_id() + obj.getVatNo() + obj.getProduct_no() + obj.getSelNo();
        KEY = key;
        APPLY_NO=obj.getApplyNo();
        Fragment fragment = OutApplyDetailFragment.newInstance();
        Bundle bundle=new Bundle();
        bundle.putSerializable("dataList",obj);
        bundle.putSerializable("epcList",epcKeyList);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    public class OutputFlag {
        /**
         * 是否扫描
         */
        private boolean isFind = false;
        /**
         * No申请单号
         */
        private String applyNo = "";
        /**
         * 是否正常
         */
        private boolean status = true;

        public OutputFlag(boolean isFind, String applyNo, boolean status) {
            this.isFind = isFind;
            this.applyNo = applyNo;
            this.status = status;
        }

        public boolean isFind() {
            return isFind;
        }

        public void setFind(boolean find) {
            isFind = find;
        }

        public String getApplyNo() {
            return applyNo;
        }

        public void setApplyNo(String applyNo) {
            this.applyNo = applyNo;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Output> {
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

        public RecycleAdapter(RecyclerView v, Collection<Output> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final Output item, final int position) {
            if (item != null) {
                final String key = item.getOutp_id() + item.getVatNo() + item.getProduct_no() + item.getSelNo();
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                      /*  if (position == 0) {
                            if (isChecked) {
                                for (Output i : myList) {
                                    if (i.getOutp_id() != null && !i.getOutp_id().equals("")) {
                                        String key = i.getOutp_id() + i.getVatNo() + i.getProduct_no() + i.getSelNo();
                                        DATA_KEY.put(key, new ArrayList<String>());
                                    }
                                }
                            } else {
                                DATA_KEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {*/
                        if (isChecked) {
                            if (DATA_KEY.containsKey(item.getApplyNo()))
                                if (!DATA_KEY.get(item.getApplyNo()).contains(key))
                                    DATA_KEY.get(item.getApplyNo()).add(key);
                        } else {
                            if (DATA_KEY.containsKey(item.getApplyNo()))
                                if (!DATA_KEY.get(item.getApplyNo()).contains(key)) {
                                    Iterator<String> iter = DATA_KEY.get(item.getApplyNo()).iterator();
                                    while (iter.hasNext()) {
                                        String str = iter.next();
                                        if (str.equals(key))
                                            iter.remove();
                                    }
                                }
                        }
                    }
//                    }
                });
                LinearLayout title = holder.getView(R.id.layout_title);
                LinearLayout no = holder.getView(R.id.headNo);
                View view = holder.getView(R.id.view);
                if (item.isStatus()) {
                    title.setVisibility(View.VISIBLE);
                    no.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                } else {
                    title.setVisibility(View.GONE);
                    no.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }

                if (((item.getVatNo() + "").equals("") && (item.getProduct_no() + "").equals("") && (item.getSelNo() + "").equals(""))) {
                    cb.setChecked(false);
                    if (cb.getVisibility() != View.INVISIBLE)
                        cb.setVisibility(View.INVISIBLE);
                } else {
                    if (cb.getVisibility() != View.VISIBLE)
                        cb.setVisibility(View.VISIBLE);
                    if (DATA_KEY.containsKey(item.getApplyNo())) {
                        if (DATA_KEY.get(item.getApplyNo()).contains(key))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (item.getCount() == item.getCountOut())
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else if (item.getFlag() == 2)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    else if (item.getCount() > item.getCountOut())
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getProduct_no() + "");
                    holder.setText(R.id.item2, item.getSelNo() + "");
                    holder.setText(R.id.item3, item.getColor() + "");
                    holder.setText(R.id.item4, item.getVatNo() + "");
                    holder.setText(R.id.item5, item.getCountOut() + "");
                    holder.setText(R.id.item9, item.getCount() + "");
                    holder.setText(R.id.item7, item.getWeightall() + "");


                }

            }
        }
    }
    }
