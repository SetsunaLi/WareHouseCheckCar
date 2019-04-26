package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.activity.Main2Activity;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.entity.OutputDetail;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
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
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutApplyNewFragment extends Fragment implements UHFCallbackLiatener, FragmentCallBackListener, BasePullUpRecyclerAdapter.OnItemClickListener {
    private final String TAG = "OutApplyNewFragment";
    private static OutApplyNewFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;




    public static OutApplyNewFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutApplyNewFragment();
        return fragment;
    }

    /*** 申请单号*/
    private ArrayList<String> fatherNoList;
    /***    epc，记录匹配是否扫描到、匹配申请单号和出库重量等信息*/
    private HashMap<String, OutputFlag> epcKeyList;
    /***    显示列表*/
    private ArrayList<Output> myList;
    /***    主表，根据申请单号，字段组成key判断是否上传*/
    private Map<String, List<String>> dataKey;
    /***    所有扫描的epc总集，避免多次查询*/
    private ArrayList<String> epcList;
    /***     key：字段组成，记录非单号查询到的数据，并且记录插入myList的位置*/
    private HashMap<String, Integer> getEpcKey;
    /***    缸号，是否自动匹配缸号*/
    private HashMap<String, Boolean> vatKey;

//    private ArrayList<Output> dataList;
     /***    记录查询到的申请单号，没实际用途*/
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
        ComeBack.getInstance().setCallbackLiatener(this);
        initRFID();
        return view;
    }

    public void initData() {
        fatherNoList = new ArrayList<>();
        epcKeyList = new HashMap<>();
        myList = new ArrayList<>();
//        dataList = new ArrayList<>();
        dateNo = new ArrayList<>();
        dataKey = new HashMap<>();
        epcList = new ArrayList<>();
        getEpcKey = new HashMap<>();
        vatKey = new HashMap<>();
    }

    public void clearData() {
        epcKeyList.clear();
        myList.clear();
        dateNo.clear();
        dataKey.clear();
        epcList.clear();
        getEpcKey.clear();
        vatKey.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<String> list = (ArrayList<String>) getArguments().getSerializable("NO");
        fatherNoList.clear();
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            str = str.replaceAll(" ", "");
            if (str != null && !str.equals("") && !fatherNoList.contains(str))
                fatherNoList.add(str);

        }
        text1.setText(0 + "");
        text2.setText(fatherNoList.size() + "");
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
                                        if (vatKey.containsKey(output.getVatNo())){
                                            vatKey.put(output.getVatNo(),false);
                                        }else {
                                            vatKey.put(output.getVatNo(),true);
                                        }
                                        for (OutputDetail outputDetail : output.getList()) {
                                            if (!epcKeyList.containsKey(outputDetail.getEpc())) {
                                                epcKeyList.put(outputDetail.getEpc() + "", new OutputFlag(false, "", true, outputDetail.getWeight()));
                                            }
                                        }
                                        final String key = output.getOutp_id() + output.getVatNo() + output.getProduct_no() + output.getSelNo();
                                        if (!dataKey.containsKey(output.getApplyNo())) {
                                            dataKey.put(output.getApplyNo(), new ArrayList<String>());
                                        }
                                        if (!dataKey.get(output.getApplyNo()).contains(key)) {
                                            dataKey.get(output.getApplyNo()).add(key);
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
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    if (App.MUSIC_SWITCH) {
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                    }
                    final String EPC = ((String) msg.obj).replaceAll(" ", "");
                    if (EPC.startsWith("3035A537") && epcKeyList.containsKey(EPC)) {
                        if (!epcKeyList.get(EPC).isFind) {
                            epcKeyList.get(EPC).setFind(true);
                            epcKeyList.get(EPC).setStatus(true);

                            for (Output i : myList) {
                                for (OutputDetail od : i.getList()) {
                                    if (od.getEpc().equals(EPC)) {
                                        i.setCount(i.getCount() + 1);
                                        i.setWeightall(ArithUtil.add(i.getWeightall(), epcKeyList.get(EPC).getWeight()));
//                                        自动配货
                                        if (vatKey.get(i.getVatNo())&&i.getCountProfit()<i.getCountOut()) {
                                            epcKeyList.get(EPC).setApplyNo(i.getApplyNo());
                                            i.addCountProfit();
                                        }
                                        break;
                                    }
                                }
                            }
                            int count = 0;
                            for (OutputFlag o : epcKeyList.values()) {
                                if (o.isFind)
                                    count++;
                            }
                            text1.setText(count + "");
                            mAdapter.notifyDataSetChanged();
                        }
                    } else if (EPC.startsWith("3035A537") && !epcKeyList.containsKey(EPC) && !epcList.contains(EPC)) {
                        JSONObject epc = new JSONObject();
                        epc.put("epc", EPC);
                        final String json = epc.toJSONString();
                        if (!epcList.contains(EPC)) {
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getEpc;" + e.getMessage());
                                            Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONArray jsonArray) {
                                        try {
                                            List<Inventory> arry;
                                            arry = jsonArray.toJavaList(Inventory.class);
                                            if (arry != null && arry.size() > 0) {
                                                Inventory response = arry.get(0);
                                                if (!epcList.contains(response.getEpc())) {
                                                    epcList.add(response.getEpc());
                                                    if (response != null) {
                                                        epcList.add(response.getEpc());
                                                        OutputDetail detail = new OutputDetail();
                                                        detail.setEpc(response.getEpc() + "");
                                                        detail.setFabRool(response.getFabRool() + "");
                                                        detail.setWeight(response.getWeight());
                                                        detail.setWeight_in(response.getWeight_in());
                                                        detail.setOperator(response.getOperator() + "");
                                                        detail.setOperatingTime(response.getOperatingTime());
                                                        detail.setFlag(2);
                                                        String key2 = "" + response.getVatNo() + response.getProduct_no() + response.getSelNo();
                                                        if (!getEpcKey.containsKey(key2)) {
                                                            Output data = new Output();
                                                            data.setApplyNo("bug");
                                                            data.setProduct_no(response.getProduct_no() + "");
                                                            data.setVatNo(response.getVatNo() + "");
                                                            data.setSelNo(response.getSelNo() + "");
                                                            data.setColor(response.getColor() + "");
                                                            data.setCountOut(0);
                                                            data.setCount(1);
                                                            data.setCountProfit(1);
                                                            data.setCountLosses(0);
                                                            data.setFlag(2);
                                                            data.setOutp_id("");
                                                            data.setWeightall(response.getWeight());
                                                            List<OutputDetail> list = new ArrayList<OutputDetail>();
                                                            list.add(detail);
                                                            data.setList(list);
                                                            myList.add(data);
                                                            getEpcKey.put(key2, myList.size() - 1);
                                                        } else {
                                                            myList.get(getEpcKey.get(key2)).addCount();
                                                            myList.get(getEpcKey.get(key2)).setWeightall(ArithUtil.add(myList.get(getEpcKey.get(key2)).getWeightall(), response.getWeight()));
                                                            myList.get(getEpcKey.get(key2)).getList().add(detail);
                                                        }
                                                    }
                                                }
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, json);
                            } catch (IOException e) {
                                Log.i(TAG, "");
                            }
                        }
                    }
                    break;
                case 0x01:
                    epcKeyList.clear();
                    epcKeyList.putAll(((Main2Activity) getActivity()).getOutApplyDataList());
                    for (Output i : myList) {
                        i.setCountProfit(0);
                        i.setWeightPei(0);
                        for (OutputDetail od : i.getList()) {
                            if (epcKeyList.containsKey(od.getEpc())) {
                                if (epcKeyList.get(od.getEpc()).getApplyNo().equals(i.getApplyNo())) {
                                    i.setCountProfit(i.getCountProfit() + 1);
                                    i.setWeightPei(ArithUtil.add(i.getWeightPei(), epcKeyList.get(od.getEpc()).getWeight()));
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
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
        msg.what = 0x00;
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
        Message msg = handler.obtainMessage();
        msg.what = 0x01;
        handler.sendMessage(msg);
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
                clearData();
                downLoadData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                blinkDialog();
                break;
        }
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认出库");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                上传数据
                for (String applyNo : dataKey.keySet()) {
                    ArrayList<Output> jsocList = new ArrayList<>();
                    for (Output op : myList) {
                        if (op.getApplyNo().equals(applyNo)) {
                            final String key = op.getOutp_id() + op.getVatNo() + op.getProduct_no() + op.getSelNo();
                            if (dataKey.get(applyNo).contains(key)) {
                                ArrayList<OutputDetail> newList = new ArrayList<OutputDetail>();
                                for (OutputDetail od : op.getList()) {
                                    if (epcKeyList.get(od.getEpc()).getApplyNo().equals(applyNo)) {
                                        od.setFlag(1);
                                        od.setWeight_out(epcKeyList.get(od.getEpc()).getWeight());
                                        newList.add(od);
                                    }
                                }
                                if (newList.size() > 0) {
                                    Output obj = (Output) op.clone();
                                    obj.setDevice(App.DEVICE_NO);
                                    obj.setFlag(1);
                                    obj.setList(newList);
                                    jsocList.add(obj);
                                }
                            }
                        }
                    }
                    if (jsocList.size() > 0) {
//                        final String json = JSON.toJSONString(jsocList);
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("userId", User.newInstance().getId());
                        jsonObject.put("data",jsocList);
                        final String json = jsonObject.toJSONString();
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/output/pushOutput.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "postInventory;" + e.getMessage());
                                        Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                                            Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                            blinkDialog2(true);
                                        } else {
                                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                            blinkDialog2(false);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, json);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                dialog.dismiss();
            }
        });
    }

    private void blinkDialog2(boolean flag) {
        final Dialog dialog;
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
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
        Output obj = myList.get(position);
     /*   String key = obj.getOutp_id() + obj.getVat_no() + obj.getProduct_no() + obj.getSelNo();
        KEY = key;
        APPLY_NO = obj.getApplyNo();*/
        Fragment fragment = OutApplyDetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dataList", obj);
        bundle.putSerializable("epcList", epcKeyList);
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

        /**
         * 重量（指的是库存重量）
         */
        private double weight = 0.0;


        public OutputFlag(boolean isFind, String applyNo, boolean status, double weight) {
            this.isFind = isFind;
            this.applyNo = applyNo;
            this.status = status;
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
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
                        if (isChecked) {
                            if (dataKey.containsKey(item.getApplyNo()))
                                if (!dataKey.get(item.getApplyNo()).contains(key))
                                    dataKey.get(item.getApplyNo()).add(key);
                        } else {
                            if (dataKey.containsKey(item.getApplyNo()))
                                if (dataKey.get(item.getApplyNo()).contains(key)) {
                                    Iterator<String> iter = dataKey.get(item.getApplyNo()).iterator();
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
                    holder.setText(R.id.text1, "申请单号：" + item.getApplyNo());
                } else {
                    title.setVisibility(View.GONE);
                    no.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }


                if (((item.getVatNo() + "").equals("") && (item.getProduct_no() + "").equals("") && (item.getSelNo() + "").equals(""))) {
                    if (cb.isEnabled())
                        cb.setEnabled(false);
                    cb.setChecked(false);
                } else {
                    if (cb.isEnabled())
                        cb.setEnabled(true);
                    if (dataKey.containsKey(item.getApplyNo())) {
                        if (dataKey.get(item.getApplyNo()).contains(key))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (item.getCountOut() == item.getCountProfit())
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    if (item.getFlag() == 2) {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                        cb.setChecked(false);
                    }
                    holder.setText(R.id.item1, item.getProduct_no() + "");
                    holder.setText(R.id.item2, item.getSelNo() + "");
                    holder.setText(R.id.item3, item.getColor() + "");
                    holder.setText(R.id.item4, item.getVatNo() + "");
                    holder.setText(R.id.item5, item.getCountOut() + "");
                    holder.setText(R.id.item6, item.getCountProfit() + "");
                    holder.setText(R.id.item8, item.getWeightPei() + "");
                    holder.setText(R.id.item9, item.getCount() + "");
                    holder.setText(R.id.item7, item.getWeightall() + "");
                }

            }
        }
    }
}
