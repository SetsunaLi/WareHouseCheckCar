package com.example.mumu.warehousecheckcar.fragment.forward;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.Forwarding;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class ForwardingFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {
    final String TAG = "ForwardingFragment";
    private static ForwardingFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;


    public static ForwardingFragment newInstance() {
        if (fragment == null) ;
        fragment = new ForwardingFragment();
        return fragment;
    }

    /*** 申请单号*/
    private ArrayList<String> fatherNoList;
    /***    epc，记录匹配是否扫描到、匹配申请单号和出库重量等信息*/
    private HashMap<String, ForwardingFlag> epcKeyList;
    /***    显示列表*/
    private ArrayList<ForwardingList> myList;
    /***    主表，根据申请单号，字段组成key判断是否上传*/
    private Map<String, List<String>> dataKey;
    /***    所有扫描的epc总集，避免多次查询*/
    private ArrayList<String> epcList;
    /***     key：字段组成，记录非单号查询到的数据，并且记录插入myList的位置*/
    private HashMap<String, Integer> getKeyValue;
//    /***    缸号，是否自动匹配缸号*/
//    private HashMap<String, Boolean> vatKey;

    /**
     * 所有的Forwarding信息，不管是申请单内还是epc扫出来的
     */
    private ArrayList<Forwarding> dataList;
    /***    记录查询到的申请单号，没实际用途*/
    private ArrayList<String> dateNo;

    private RecycleAdapter mAdapter;
    private Sound sound;

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forwarding_layout, container, false);
        ButterKnife.bind(this, view);

        initData();
        if (!EventBus.getDefault().isRegistered(this))

            EventBus.getDefault().register(this);
        sound = new Sound(getActivity());

        mAdapter = new RecycleAdapter(recyle, myList, R.layout.forwarding_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
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
        dateNo = new ArrayList<>();
        dataKey = new HashMap<>();
        epcList = new ArrayList<>();
        getKeyValue = new HashMap<>();
//        vatKey = new HashMap<>();
        dataList = new ArrayList<>();
    }

    public void clearData() {
        epcKeyList.clear();
        myList.clear();
        dateNo.clear();
        dataKey.clear();
        epcList.clear();
        getKeyValue.clear();
//        vatKey.clear();
        dataList.clear();
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

    private ForwardingMsgFragment.CarMsg carMsg;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x01:
                    carMsg = (ForwardingMsgFragment.CarMsg) msg.getPositionObj(0);
//                    fatherNoList.clear();
                    fatherNoList = (ArrayList<String>) msg.getPositionObj(1);
                    break;
                case 0xfe:
                    epcKeyList.clear();
                    epcKeyList = (HashMap<String, ForwardingFlag>) ((HashMap<String, ForwardingFlag>) msg.getPositionObj(0)).clone();
                    epcKeyList.putAll((HashMap<String, ForwardingFlag>) msg.getPositionObj(0));
                    for (ForwardingList forwardingList : myList) {
                        forwardingList.setMatchCount(0);
                        forwardingList.setMatchWeight(0.0);
                    }
                    for (Forwarding forwarding : dataList) {
                        if (epcKeyList.containsKey(forwarding.getEpc()))
                            if (epcKeyList.get(forwarding.getEpc()).isStatus()) {
                                String key = forwarding.getApplyNo() + forwarding.getVatNo();
                                myList.get(getKeyValue.get(key)).addMatchCount();
                                myList.get(getKeyValue.get(key)).addMatchWeight(forwarding.getWeight());
                            }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
            }
    }

    private boolean flag = true;

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            clearData();
            downLoadData();
            text2.setText(fatherNoList.size() + "");
            text3.setText(carMsg.getCarNo() + "");
            text4.setText(carMsg.getCarName() + "");
        }
    }

    private void downLoadData() {
        for (String no : fatherNoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("applyNo", no);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/pullDespatch.sh", new OkHttpClientManager.ResultCallback<List<Forwarding>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(List<Forwarding> response) {
                        try {
//                            List<Forwarding> response = json.toJavaList(Forwarding.class);
                            if (response != null && response.size() > 0) {
                                for (Forwarding obj1 : response) {
                                    if (!epcKeyList.containsKey(obj1.getEpc())) {
                                        dataList.add(obj1);
                                        epcKeyList.put(obj1.getEpc(), new ForwardingFlag(false, obj1.getApplyNo(), obj1.getVatNo(), false, obj1.getWeight()));
                                        String key = obj1.getApplyNo() + obj1.getVatNo();
                                        if (!getKeyValue.containsKey(key)) {
                                            ForwardingList mlis = new ForwardingList(obj1.getClothNum(), obj1.getVatNo(), obj1.getSelNo(), obj1.getColor(), obj1.getApplyNo(), true);
                                            mlis.addApplyCount();
                                            mlis.addApplyWeight(obj1.getWeight());
                                            if (dateNo.contains(obj1.getApplyNo())) {
                                                mlis.setStatus(false);
                                            } else {
                                                dateNo.add(obj1.getApplyNo());
                                                mlis.setStatus(true);
                                            }
                                            myList.add(mlis);
                                            getKeyValue.put(key, myList.size() - 1);
                                            if (!dataKey.containsKey(obj1.getApplyNo()))
                                                dataKey.put(obj1.getApplyNo(), new ArrayList<String>());
                                            dataKey.get(obj1.getApplyNo()).add(key);
                                        } else {
                                            myList.get(getKeyValue.get(key)).addApplyCount();
                                            myList.get(getKeyValue.get(key)).addApplyWeight(obj1.getWeight());
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
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (!returnWhere) {
            EventBus.getDefault().postSticky(new EventBusMsg(0x00, carMsg));
            Fragment fragment = ForwardingNoFragment.newInstance();
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
        }
//        EventBus.getDefault().post(new EventBusMsg(0xff));
        EventBus.getDefault().unregister(this);
        disRFID();
        clearData();
        fatherNoList.clear();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                downLoadData();
                mAdapter.notifyDataSetChanged();
                text1.setText(0 + "");
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
        no.setText("否");
        yes.setText("是");
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否完成装车");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoading(false);
                returnWhere = false;
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnWhere = true;
                upLoading(true);
                dialog.dismiss();
            }
        });
    }

    private void upLoading(boolean isFinish) {
        JSONObject jsonObject = new JSONObject();
        int id = User.newInstance().getId();
        jsonObject.put("userId", id);
        jsonObject.put("carMsg", carMsg);
//        false是0，true是1
        if (!isFinish)
            jsonObject.put("status", 0);
        else
            jsonObject.put("status", 1);
        jsonObject.put("applyNo", fatherNoList);
        ArrayList<Forwarding> list = new ArrayList<>();
        for (Forwarding forwarding : dataList) {
            String key = forwarding.getApplyNo() + forwarding.getVatNo();
            if (dataKey.containsKey(forwarding.getApplyNo()))
                if (dataKey.get(forwarding.getApplyNo()).contains(key)) {
                    if (epcKeyList.containsKey(forwarding.getEpc()))
                        if (epcKeyList.get(forwarding.getEpc()).isStatus())
                            list.add(forwarding);
                }
        }
        jsonObject.put("data", list);
        final String json = jsonObject.toJSONString();
        try {
            AppLog.write(getActivity(),"forwarding",json,AppLog.TYPE_INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/setTransport_out.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                        try {
                            AppLog.write(getActivity(),"forwarding","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                            Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                            clearData();
                            mAdapter.notifyDataSetChanged();
//                            blinkDialog2(true);
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

    private boolean returnWhere = false;
    private AlertDialog dialog;
    private void blinkDialog2(final boolean flag) {
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
                    onBack(flag);
                    dialog.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBack(flag);
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

    private void onBack(boolean flag) {
        if (flag)
            if (returnWhere) {
                Fragment fragment = ForwardingMsgFragment.newInstance();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
            } else
                getActivity().onBackPressed();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        ArrayList<Forwarding> list = new ArrayList<>();
        ForwardingList forwardingList = myList.get(position);
        for (Forwarding forwarding : dataList) {
            if (forwarding.getVatNo().equals(forwardingList.getVatNo()) && forwarding.getApplyNo().equals(forwardingList.getApplyNo())) {
                list.add(forwarding);
            }
        }
        EventBus.getDefault().postSticky(new EventBusMsg(0x02, list, epcKeyList));
        Fragment fragment = ForwardingDetailFragment.newInstance();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
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
                    if (!epcList.contains(EPC)) {
                        if (EPC.startsWith("3035A537") && epcKeyList.containsKey(EPC)) {
                            epcList.add(EPC);
                            if (!epcKeyList.get(EPC).isFind) {
                                epcKeyList.get(EPC).setFind(true);
                                epcKeyList.get(EPC).setStatus(true);
                                String key = epcKeyList.get(EPC).getApplyNo() + epcKeyList.get(EPC).getVatNo();
                                if (getKeyValue.containsKey(key)) {
                                    myList.get(getKeyValue.get(key)).addScannerCount();
                                    myList.get(getKeyValue.get(key)).addMatchCount();
                                    myList.get(getKeyValue.get(key)).addMatchWeight(epcKeyList.get(EPC).getWeight());
                                }
                                int count = 0;
                                for (ForwardingFlag o : epcKeyList.values()) {
                                    if (o.isFind)
                                        count++;
                                }
                                text1.setText(count + "");
                                mAdapter.notifyDataSetChanged();
                            }
                        } else if (EPC.startsWith("3035A537") && !epcKeyList.containsKey(EPC)) {
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
                                                    if (response != null) {
                                                        if (!epcList.contains(response.getEpc())) {
                                                            epcList.add(response.getEpc());
                                                            Forwarding forwarding = new Forwarding(response.getProduct_no(), response.getVatNo(), response.getSelNo()
                                                                    , response.getColor(), response.getFabRool(), response.getWeight(), response.getEpc(), "非申请单");
                                                            dataList.add(forwarding);
                                                            epcKeyList.put(forwarding.getEpc(), new ForwardingFlag(true, forwarding.getApplyNo(), forwarding.getVatNo(), true, forwarding.getWeight()));
                                                            String key = forwarding.getApplyNo() + forwarding.getVatNo();
                                                            if (!getKeyValue.containsKey(key)) {
                                                                ForwardingList mlis = new ForwardingList(forwarding.getClothNum(), forwarding.getVatNo(), forwarding.getSelNo(), forwarding.getColor(), forwarding.getApplyNo(), false);
                                                                mlis.addScannerCount();
//                                                                mlis.addApplyCount();
//                                                                mlis.addApplyWeight(forwarding.getWeight());
                                                                if (dateNo.contains(forwarding.getApplyNo())) {
                                                                    mlis.setStatus(false);
                                                                } else {
                                                                    dateNo.add(forwarding.getApplyNo());
                                                                    mlis.setStatus(true);
                                                                }
                                                                myList.add(mlis);
                                                                getKeyValue.put(key, myList.size() - 1);
                                                            } else {
                                                                myList.get(getKeyValue.get(key)).addScannerCount();
//                                                                myList.get(getKeyValue.get(key)).addApplyCount();
//                                                                myList.get(getKeyValue.get(key)).addApplyWeight(forwarding.getWeight());
                                                            }
                                                            int count = 0;
                                                            for (ForwardingFlag o : epcKeyList.values()) {
                                                                if (o.isFind)
                                                                    count++;
                                                            }
                                                            text1.setText(count + "");
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

    class ForwardingList {
        /**
         * 布号
         */
        private String clothNum = "";
        /***缸号*/
        private String vatNo = "";
        /***色号（色号指的都是销售色号）*/
        private String selNo = "";
        /***颜色*/
        private String color = "";
        /**
         * 申请单号
         */
        private String applyNo = "";
        /**
         * 申请数量
         */
        private int applyCount = 0;
        /**
         * 申请重量
         */
        private double applyWeight = 0;

        /**
         * 扫描数量
         */
        private int scannerCount = 0;
        /**
         * 匹配数量
         */
        private int matchCount = 0;
        /**
         * 匹配重量
         */
        private double matchWeight = 0;
        /**
         * 头部标志
         */
        private boolean status = false;
        /**
         * 是否订单内
         */
        private boolean flag = true;

        public ForwardingList(String clothNum, String vatNo, String selNo, String color, String applyNo, boolean flag) {
            this.clothNum = clothNum;
            this.vatNo = vatNo;
            this.selNo = selNo;
            this.color = color;
            this.applyNo = applyNo;
            this.flag = flag;
        }

        public void addApplyCount() {
            applyCount++;
        }

        public void addMatchCount() {
            matchCount++;
        }

        public void addScannerCount() {
            scannerCount++;
        }

        public void addApplyWeight(double weightNew) {
            applyWeight = ArithUtil.add(applyWeight, weightNew);
        }

        public void addMatchWeight(double weightNew) {
            matchWeight = ArithUtil.add(matchWeight, weightNew);
        }

        public int getScannerCount() {
            return scannerCount;
        }

        public void setScannerCount(int scannerCount) {
            this.scannerCount = scannerCount;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getClothNum() {
            return clothNum;
        }

        public void setClothNum(String clothNum) {
            this.clothNum = clothNum;
        }

        public String getVatNo() {
            return vatNo;
        }

        public void setVatNo(String vatNo) {
            this.vatNo = vatNo;
        }

        public String getSelNo() {
            return selNo;
        }

        public void setSelNo(String selNo) {
            this.selNo = selNo;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getApplyNo() {
            return applyNo;
        }

        public void setApplyNo(String applyNo) {
            this.applyNo = applyNo;
        }

        public int getApplyCount() {
            return applyCount;
        }

        public void setApplyCount(int applyCount) {
            this.applyCount = applyCount;
        }

        public double getApplyWeight() {
            return applyWeight;
        }

        public void setApplyWeight(double applyWeight) {
            this.applyWeight = applyWeight;
        }

        public int getMatchCount() {
            return matchCount;
        }

        public void setMatchCount(int matchCount) {
            this.matchCount = matchCount;
        }

        public double getMatchWeight() {
            return matchWeight;
        }

        public void setMatchWeight(double matchWeight) {
            this.matchWeight = matchWeight;
        }
    }

    class ForwardingFlag {
        /**
         * 是否扫描
         */
        private boolean isFind = false;
        /**
         * No申请单号
         */
        private String applyNo = "";
        /**
         * No申请单号
         */
        private String vatNo = "";
        /**
         * 是否配货
         */
        private boolean status = false;

        /**
         * 重量（指的是库存重量）
         */
        private double weight = 0.0;


        public ForwardingFlag(boolean isFind, String applyNo, String vatNo, boolean status, double weight) {
            this.isFind = isFind;
            this.applyNo = applyNo;
            this.vatNo = vatNo;
            this.status = status;
            this.weight = weight;
        }

        public String getVatNo() {
            return vatNo;
        }

        public void setVatNo(String vatNo) {
            this.vatNo = vatNo;
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

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ForwardingList> {
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

        public RecycleAdapter(RecyclerView v, Collection<ForwardingList> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final ForwardingList item, final int position) {
            if (item != null) {
                final String key = item.getApplyNo() + item.getVatNo();
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

/*
                if ((item.getVatNo() + "").equals("") && (item.getOut_no() + "").equals("")) {
                    if (cb.isEnabled())
                        cb.setEnabled(false);
                    cb.setChecked(false);
                } else {*/
                if (!cb.isEnabled())
                    cb.setEnabled(true);
                if (dataKey.containsKey(item.getApplyNo())) {
                    if (dataKey.get(item.getApplyNo()).contains(key))
                        cb.setChecked(true);
                    else
                        cb.setChecked(false);
                }
                LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                if (item.getApplyCount() == item.getMatchCount())
                    ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                else
                    ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                if (!item.isFlag()) {
                    ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                      /*  if (cb.isEnabled())
                            cb.setEnabled(false);*/
                    cb.setChecked(false);
                }
                holder.setText(R.id.item1, item.getClothNum() + "");
                holder.setText(R.id.item2, item.getSelNo() + "");
                holder.setText(R.id.item3, item.getColor() + "");
                holder.setText(R.id.item4, item.getVatNo() + "");
                holder.setText(R.id.item5, item.getApplyCount() + "");
                holder.setText(R.id.item6, item.getApplyWeight() + "");
                holder.setText(R.id.item7, item.getMatchCount() + "");
                holder.setText(R.id.item8, item.getMatchCount() + "");
                holder.setText(R.id.item9, item.getMatchWeight() + "");
//                }

            }
        }
    }
}
//package com.example.mumu.warehousecheckcar.fragment;
//
//        import android.annotation.SuppressLint;
//        import android.app.AlertDialog;
//        import android.app.Dialog;
//        import android.app.Fragment;
//        import android.app.FragmentManager;
//        import android.app.FragmentTransaction;
//        import android.content.Context;
//        import android.os.Bundle;
//        import android.os.Handler;
//        import android.os.Message;
//        import android.support.annotation.Nullable;
//        import android.support.v7.widget.LinearLayoutManager;
//        import android.support.v7.widget.RecyclerView;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.view.WindowManager;
//        import android.widget.Button;
//        import android.widget.CheckBox;
//        import android.widget.CompoundButton;
//        import android.widget.LinearLayout;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.alibaba.fastjson.JSONArray;
//        import com.alibaba.fastjson.JSONObject;
//        import com.example.mumu.warehousecheckcar.R;
//        import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
//        import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
//        import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
//        import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
//        import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
//        import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
//        import com.example.mumu.warehousecheckcar.application.App;
//        import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
//        import com.example.mumu.warehousecheckcar.entity.BaseReturn;
//        import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
//        import com.example.mumu.warehousecheckcar.entity.Forwarding;
//        import com.example.mumu.warehousecheckcar.entity.Inventory;
//        import com.example.mumu.warehousecheckcar.entity.User;
//        import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
//        import com.example.mumu.warehousecheckcar.utils.ArithUtil;
//        import com.rfid.rxobserver.ReaderSetting;
//        import com.rfid.rxobserver.bean.RXInventoryTag;
//        import com.rfid.rxobserver.bean.RXOperationTag;
//        import com.squareup.okhttp.Request;
//
//        import org.greenrobot.eventbus.EventBus;
//        import org.greenrobot.eventbus.Subscribe;
//        import org.greenrobot.eventbus.ThreadMode;
//
//        import java.io.IOException;
//        import java.util.ArrayList;
//        import java.util.Collection;
//        import java.util.HashMap;
//        import java.util.Iterator;
//        import java.util.List;
//        import java.util.Map;
//
//        import butterknife.Bind;
//        import butterknife.ButterKnife;
//        import butterknife.OnClick;
//
//public class ForwardingFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {
//    final String TAG = "ForwardingFragment";
//    private static ForwardingFragment fragment;
//    @Bind(R.id.recyle)
//    RecyclerView recyle;
//    @Bind(R.id.text1)
//    TextView text1;
//    @Bind(R.id.text2)
//    TextView text2;
//    @Bind(R.id.text3)
//    TextView text3;
//    @Bind(R.id.text4)
//    TextView text4;
//    @Bind(R.id.button1)
//    Button button1;
//    @Bind(R.id.button2)
//    Button button2;
//
//
//    public static ForwardingFragment newInstance() {
//        if (fragment == null) ;
//        fragment = new ForwardingFragment();
//        return fragment;
//    }
//
//    /*** 申请单号*/
//    private ArrayList<String> fatherNoList;
//    /***    epc，记录匹配是否扫描到、匹配申请单号和出库重量等信息*/
//    private HashMap<String, ForwardingFlag> epcKeyList;
//    /***    显示列表*/
//    private ArrayList<ForwardingList> myList;
//    /***    主表，根据申请单号，字段组成key判断是否上传*/
//    private Map<String, List<String>> dataKey;
//    /***    所有扫描的epc总集，避免多次查询*/
//    private ArrayList<String> epcList;
//    /***     key：字段组成，查询到的数据，同单号同缸号插入并记录myList的位置*/
//    private HashMap<String, Integer> getKeyValue;
////    /***    缸号，是否自动匹配缸号*/
////    private HashMap<String, Boolean> vatKey;
//
//    /**
//     * 所有的Forwarding信息，不管是申请单内还是epc扫出来的
//     */
//    private ArrayList<Forwarding> dataList;
//    /***    记录查询到的申请单号，没实际用途*/
//    private ArrayList<String> dateNo;
//
//    private RecycleAdapter mAdapter;
//    private Sound sound;
//
//    //    这里加载视图
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.forwarding_layout, container, false);
//        ButterKnife.bind(this, view);
//
//        initData();
//        EventBus.getDefault().register(this);
//        sound = new Sound(getActivity());
//
//        mAdapter = new RecycleAdapter(recyle, myList, R.layout.forwarding_item);
//        mAdapter.setContext(getActivity());
//        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
//        mAdapter.setOnItemClickListener(this);
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyle.setLayoutManager(llm);
//        recyle.setAdapter(mAdapter);
//
//        initRFID();
//
//        return view;
//    }
//
//    public void initData() {
//        fatherNoList = new ArrayList<>();
//        epcKeyList = new HashMap<>();
//        myList = new ArrayList<>();
//        dateNo = new ArrayList<>();
//        dataKey = new HashMap<>();
//        epcList = new ArrayList<>();
//        getKeyValue = new HashMap<>();
////        vatKey = new HashMap<>();
//        dataList = new ArrayList<>();
//    }
//
//    public void clearData() {
//        epcKeyList.clear();
//        myList.clear();
//        dateNo.clear();
//        dataKey.clear();
//        epcList.clear();
//        getKeyValue.clear();
////        vatKey.clear();
//        dataList.clear();
//    }
//
//    private void initRFID() {
//        try {
//            RFID_2DHander.getInstance().on_RFID();
//            UHFResult.getInstance().setCallbackLiatener(this);
//        } catch (Exception e) {
//
//        }
//    }
//
//    private void disRFID() {
//        try {
//            RFID_2DHander.getInstance().off_RFID();
//        } catch (Exception e) {
//
//        }
//    }
//
//    private ForwardingMsgFragment.CarMsg carMsg;
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void getEventMsg(EventBusMsg msg) {
//        if (msg != null)
//            switch (msg.getStatus()) {
//                case 0x01:
//                    carMsg = (ForwardingMsgFragment.CarMsg) msg.getPositionObj(0);
////                    fatherNoList.clear();
//                    fatherNoList = (ArrayList<String>) msg.getPositionObj(1);
//                    break;
//                case 0xfe:
//                    epcKeyList.clear();
//                    epcKeyList = (HashMap<String, ForwardingFlag>) ((HashMap<String, ForwardingFlag>) msg.getPositionObj(0)).clone();
//                    epcKeyList.putAll((HashMap<String, ForwardingFlag>) msg.getPositionObj(0));
//                    for (ForwardingList forwardingList : myList) {
//                        forwardingList.setMatchCount(0);
//                        forwardingList.setMatchWeight(0.0);
//                    }
//                    for (Forwarding forwarding : dataList) {
//                        if (epcKeyList.containsKey(forwarding.getEpc()))
//                            if (epcKeyList.get(forwarding.getEpc()).isStatus()) {
//                                String key = forwarding.getApplyNo() + forwarding.getVatNo();
//                                myList.get(getKeyValue.get(key)).addMatchCount();
//                                myList.get(getKeyValue.get(key)).addMatchWeight(forwarding.getWeight());
//                            }
//                    }
//                    mAdapter.notifyDataSetChanged();
//                    break;
//            }
//    }
//
//    private boolean flag = true;
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (flag) {
//            flag = false;
//            clearData();
//            downLoadData();
//            text2.setText(fatherNoList.size() + "");
//            text3.setText(carMsg.getCarNo() + "");
//            text4.setText(carMsg.getCarName() + "");
//        }
//    }
//
//    private void downLoadData() {
//        for (String no : fatherNoList) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("applyNo", no);
//            final String json = jsonObject.toJSONString();
//            try {
//                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/pullDespatch.sh", new OkHttpClientManager.ResultCallback<List<Forwarding>>() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        if (App.LOGCAT_SWITCH) {
//                            Log.i(TAG, "getEpc;" + e.getUpdate_describe());
//                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getUpdate_describe(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(List<Forwarding> response) {
//                        try {
////                            List<Forwarding> response = json.toJavaList(Forwarding.class);
//                            if (response != null && response.size() > 0) {
//                                for (Forwarding obj1 : response) {
////                                for (int i=0;i<response.size();i++){
////                                    Forwarding obj1=response.get(i);
//                                    if (!epcKeyList.containsKey(obj1.getEpc())) {
//                                        dataList.add(obj1);
//                                        epcKeyList.put(obj1.getEpc(), new ForwardingFlag(false, obj1.getApplyNo(), obj1.getVatNo(), false, obj1.getWeight()));
//                                        String key = obj1.getApplyNo() + obj1.getVatNo()+myList.size();
//                                        if (!getKeyValue.containsKey(key)) {
//                                            ForwardingList mlis = new ForwardingList(obj1.getClothNum(), obj1.getVatNo(), obj1.getSelNo(), obj1.getColor(), obj1.getApplyNo(), true);
//                                            mlis.addApplyCount();
//                                            mlis.addApplyWeight(obj1.getWeight());
//                                            if (dateNo.contains(obj1.getApplyNo())) {
//                                                mlis.setStatus(false);
//                                            } else {
//                                                dateNo.add(obj1.getApplyNo());
//                                                mlis.setStatus(true);
//                                            }
//                                            myList.add(mlis);
//                                            getKeyValue.put(key, myList.size() - 1);
//                                            if (!dataKey.containsKey(obj1.getApplyNo()))
//                                                dataKey.put(obj1.getApplyNo(), new ArrayList<String>());
//                                            dataKey.get(obj1.getApplyNo()).add(key);
//                                        } else {
//                                            myList.get(getKeyValue.get(key)).addApplyCount();
//                                            myList.get(getKeyValue.get(key)).addApplyWeight(obj1.getWeight());
//                                        }
//                                    }
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, json);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//        if (!returnWhere) {
//            EventBus.getDefault().postSticky(new EventBusMsg(0x00, carMsg));
//            Fragment fragment = ForwardingNoFragment.newInstance();
//            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
//        }
////        EventBus.getDefault().post(new EventBusMsg(0xff));
//        EventBus.getDefault().unregister(this);
//        disRFID();
//        clearData();
//        fatherNoList.clear();
//    }
//
//    @OnClick({R.id.button1, R.id.button2})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.button1:
//                clearData();
//                downLoadData();
//                mAdapter.notifyDataSetChanged();
//                text1.setText(0 + "");
//                break;
//            case R.id.button2:
//                blinkDialog();
//                break;
//        }
//    }
//
//    private void blinkDialog() {
//        final Dialog dialog;
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
//        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
//        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
//        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
//        text.setText("是否完成装车");
//        dialog = new AlertDialog.Builder(getActivity()).create();
//        dialog.show();
//        dialog.getWindow().setContentView(blinkView);
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                upLoading(false);
//                returnWhere = false;
//                dialog.dismiss();
//            }
//        });
//        yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                returnWhere = true;
//                upLoading(true);
//                dialog.dismiss();
//            }
//        });
//    }
//
//    private void upLoading(boolean isFinish) {
//        JSONObject jsonObject = new JSONObject();
//        int id = User.newInstance().getId();
//        jsonObject.put("userId", id);
//        jsonObject.put("carMsg", carMsg);
////        false是0，true是1
//        if (!isFinish)
//            jsonObject.put("status", 0);
//        else
//            jsonObject.put("status", 1);
//        jsonObject.put("applyNo", fatherNoList);
//        ArrayList<Forwarding> list = new ArrayList<>();
//
//        for (Forwarding forwarding : dataList) {
//            for (int i=0;i<myList.size();i++) {
//                String key = myList.get(i).getApplyNo() + myList.get(i).getVatNo();
//                if (dataKey.containsKey(forwarding.getApplyNo()))
//                    if (dataKey.get(forwarding.getApplyNo()).contains(key)) {
//                        if (epcKeyList.containsKey(forwarding.getEpc()))
//                            if (epcKeyList.get(forwarding.getEpc()).isStatus())
//                                list.add(forwarding);
//                    }
//            }
//        }
//        jsonObject.put("data", list);
//        final String json = jsonObject.toJSONString();
//        try {
//            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/setTransport_out.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
//                @Override
//                public void onError(Request request, Exception e) {
//                    if (App.LOGCAT_SWITCH) {
//                        Log.i(TAG, "postInventory;" + e.getUpdate_describe());
//                        Toast.makeText(getActivity(), "上传信息失败；" + e.getUpdate_describe(), Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
//                        if (baseReturn != null && baseReturn.getStatus() == 1) {
//                            Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
//                            blinkDialog2(true);
//                        } else {
//                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
//                            blinkDialog2(false);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, json);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean returnWhere = false;
//    private AlertDialog dialog;
//    private void blinkDialog2(final boolean flag) {
//        if (dialog == null) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
//            Button no = (Button) blinkView.findViewById(R.id.dialog_no);
//            Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
//            TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
//            if (flag)
//                text.setText("上传成功");
//            else
//                text.setText("上传失败");
//            dialog = new AlertDialog.Builder(getActivity()).create();
//            dialog.show();
//            dialog.getWindow().setContentView(blinkView);
//            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//
//            no.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBack(flag);
//                    dialog.dismiss();
//                }
//            });
//            yes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBack(flag);
//                    dialog.dismiss();
//                }
//            });
//        } else {
//            TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
//            if (flag)
//                text.setText("上传成功");
//            else
//                text.setText("上传失败");
//            if (!dialog.isShowing())
//                dialog.show();
//        }
//    }
//
//    private void onBack(boolean flag) {
//        if (flag)
//            if (returnWhere) {
//                Fragment fragment = ForwardingMsgFragment.newInstance();
//                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
//            } else
//                getActivity().onBackPressed();
//    }
//
//    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
//
//    @Override
//    public void onItemClick(View view, Object data, int position) {
//        ArrayList<Forwarding> list = new ArrayList<>();
//        ForwardingList forwardingList = myList.get(position);
//        for (Forwarding forwarding : dataList) {
//            if (forwarding.getVatNo().equals(forwardingList.getVatNo()) && forwarding.getApplyNo().equals(forwardingList.getApplyNo())) {
//                list.add(forwarding);
//            }
//        }
//        EventBus.getDefault().postSticky(new EventBusMsg(0x02, list, epcKeyList));
//        Fragment fragment = ForwardingDetailFragment.newInstance();
//        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
//        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
//        transaction.show(fragment);
//        transaction.commit();
//    }
//
//    long currenttime = 0;
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0x00:
//                    if (App.MUSIC_SWITCH) {
//                        if (System.currentTimeMillis() - currenttime > 150) {
//                            sound.callAlarm();
//                            currenttime = System.currentTimeMillis();
//                        }
//                    }
//                    final String EPC = ((String) msg.obj).replaceAll(" ", "");
//                    if (!epcList.contains(EPC)) {
//                        if (EPC.startsWith("3035A537") && epcKeyList.containsKey(EPC)) {
//                            epcList.add(EPC);
//                            if (!epcKeyList.get(EPC).isFind) {
//                                epcKeyList.get(EPC).setFind(true);
//                                epcKeyList.get(EPC).setStatus(true);
//                                String key = epcKeyList.get(EPC).getApplyNo() + epcKeyList.get(EPC).getVatNo();
//                                if (getKeyValue.containsKey(key)) {
//                                    myList.get(getKeyValue.get(key)).addScannerCount();
//                                    myList.get(getKeyValue.get(key)).addMatchCount();
//                                    myList.get(getKeyValue.get(key)).addMatchWeight(epcKeyList.get(EPC).getWeight());
//                                }
//                                int count = 0;
//                                for (ForwardingFlag o : epcKeyList.values()) {
//                                    if (o.isFind)
//                                        count++;
//                                }
//                                text1.setText(count + "");
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        } else if (EPC.startsWith("3035A537") && !epcKeyList.containsKey(EPC)) {
//                            JSONObject epc = new JSONObject();
//                            epc.put("epc", EPC);
//                            final String json = epc.toJSONString();
//                            if (!epcList.contains(EPC)) {
//                                try {
//                                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
//                                        @Override
//                                        public void onError(Request request, Exception e) {
//                                            if (App.LOGCAT_SWITCH) {
//                                                Log.i(TAG, "getEpc;" + e.getUpdate_describe());
//                                                Toast.makeText(getActivity(), "获取库位信息失败；" + e.getUpdate_describe(), Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onResponse(JSONArray jsonArray) {
//                                            try {
//                                                List<Inventory> arry;
//                                                arry = jsonArray.toJavaList(Inventory.class);
//                                                if (arry != null && arry.size() > 0) {
//                                                    Inventory response = arry.get(0);
//                                                    if (response != null) {
//                                                        if (!epcList.contains(response.getEpc())) {
//                                                            epcList.add(response.getEpc());
//                                                            Forwarding forwarding = new Forwarding(response.getProduct_no(), response.getVatNo(), response.getSelNo()
//                                                                    , response.getColor(), response.getFabRool(), response.getWeight(), response.getEpc(), "非申请单");
//                                                            dataList.add(forwarding);
//                                                            epcKeyList.put(forwarding.getEpc(), new ForwardingFlag(true, forwarding.getApplyNo(), forwarding.getVatNo(), true, forwarding.getWeight()));
//                                                            String key = forwarding.getApplyNo() + forwarding.getVatNo();
//                                                            if (!getKeyValue.containsKey(key)) {
//                                                                ForwardingList mlis = new ForwardingList(forwarding.getClothNum(), forwarding.getVatNo(), forwarding.getSelNo(), forwarding.getColor(), forwarding.getApplyNo(), false);
//                                                                mlis.addScannerCount();
////                                                                mlis.addApplyCount();
////                                                                mlis.addApplyWeight(forwarding.getWeight());
//                                                                if (dateNo.contains(forwarding.getApplyNo())) {
//                                                                    mlis.setStatus(false);
//                                                                } else {
//                                                                    dateNo.add(forwarding.getApplyNo());
//                                                                    mlis.setStatus(true);
//                                                                }
//                                                                myList.add(mlis);
//                                                                getKeyValue.put(key, myList.size() - 1);
//                                                            } else {
//                                                                myList.get(getKeyValue.get(key)).addScannerCount();
////                                                                myList.get(getKeyValue.get(key)).addApplyCount();
////                                                                myList.get(getKeyValue.get(key)).addApplyWeight(forwarding.getWeight());
//                                                            }
//                                                            int count = 0;
//                                                            for (ForwardingFlag o : epcKeyList.values()) {
//                                                                if (o.isFind)
//                                                                    count++;
//                                                            }
//                                                            text1.setText(count + "");
//                                                        }
//                                                    }
//                                                    mAdapter.notifyDataSetChanged();
//                                                }
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }, json);
//                                } catch (IOException e) {
//                                    Log.i(TAG, "");
//                                }
//                            }
//                        }
//                    }
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void refreshSettingCallBack(ReaderSetting readerSetting) {
//
//    }
//
//    @Override
//    public void onInventoryTagCallBack(RXInventoryTag tag) {
//        Message msg = handler.obtainMessage();
//        msg.what = 0x00;
//        msg.obj = tag.strEPC;
//        handler.sendMessage(msg);
//    }
//
//    @Override
//    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {
//
//    }
//
//    @Override
//    public void onOperationTagCallBack(RXOperationTag tag) {
//
//    }
//
//    class ForwardingList {
//        /**
//         * 布号
//         */
//        private String clothNum = "";
//        /***缸号*/
//        private String vatNo = "";
//        /***色号（色号指的都是销售色号）*/
//        private String selNo = "";
//        /***颜色*/
//        private String color = "";
//        /**
//         * 申请单号
//         */
//        private String applyNo = "";
//        /**
//         * 申请数量
//         */
//        private int applyCount = 0;
//        /**
//         * 申请重量
//         */
//        private double applyWeight = 0;
//
//        /**
//         * 扫描数量
//         */
//        private int scannerCount = 0;
//        /**
//         * 匹配数量
//         */
//        private int matchCount = 0;
//        /**
//         * 匹配重量
//         */
//        private double matchWeight = 0;
//        /**
//         * 头部标志
//         */
//        private boolean status = false;
//        /**
//         * 是否订单内
//         */
//        private boolean flag = true;
//
//        public ForwardingList(String clothNum, String vatNo, String selNo, String color, String applyNo, boolean flag) {
//            this.clothNum = clothNum;
//            this.vatNo = vatNo;
//            this.selNo = selNo;
//            this.color = color;
//            this.applyNo = applyNo;
//            this.flag = flag;
//        }
//
//        public void addApplyCount() {
//            applyCount++;
//        }
//
//        public void addMatchCount() {
//            matchCount++;
//        }
//
//        public void addScannerCount() {
//            scannerCount++;
//        }
//
//        public void addApplyWeight(double weightNew) {
//            applyWeight = ArithUtil.add(applyWeight, weightNew);
//        }
//
//        public void addMatchWeight(double weightNew) {
//            matchWeight = ArithUtil.add(matchWeight, weightNew);
//        }
//
//        public int getScannerCount() {
//            return scannerCount;
//        }
//
//        public void setScannerCount(int scannerCount) {
//            this.scannerCount = scannerCount;
//        }
//
//        public boolean isFlag() {
//            return flag;
//        }
//
//        public void setFlag(boolean flag) {
//            this.flag = flag;
//        }
//
//        public boolean isStatus() {
//            return status;
//        }
//
//        public void setStatus(boolean status) {
//            this.status = status;
//        }
//
//        public String getClothNum() {
//            return clothNum;
//        }
//
//        public void setClothNum(String clothNum) {
//            this.clothNum = clothNum;
//        }
//
//        public String getVatNo() {
//            return vatNo;
//        }
//
//        public void setVatNo(String vatNo) {
//            this.vatNo = vatNo;
//        }
//
//        public String getSelNo() {
//            return selNo;
//        }
//
//        public void setSelNo(String selNo) {
//            this.selNo = selNo;
//        }
//
//        public String getColor() {
//            return color;
//        }
//
//        public void setColor(String color) {
//            this.color = color;
//        }
//
//        public String getApplyNo() {
//            return applyNo;
//        }
//
//        public void setApplyNo(String applyNo) {
//            this.applyNo = applyNo;
//        }
//
//        public int getApplyCount() {
//            return applyCount;
//        }
//
//        public void setApplyCount(int applyCount) {
//            this.applyCount = applyCount;
//        }
//
//        public double getApplyWeight() {
//            return applyWeight;
//        }
//
//        public void setApplyWeight(double applyWeight) {
//            this.applyWeight = applyWeight;
//        }
//
//        public int getMatchCount() {
//            return matchCount;
//        }
//
//        public void setMatchCount(int matchCount) {
//            this.matchCount = matchCount;
//        }
//
//        public double getMatchWeight() {
//            return matchWeight;
//        }
//
//        public void setMatchWeight(double matchWeight) {
//            this.matchWeight = matchWeight;
//        }
//    }
//
//    class ForwardingFlag {
//        /**
//         * 是否扫描
//         */
//        private boolean isFind = false;
//        /**
//         * No申请单号
//         */
//        private String applyNo = "";
//        /**
//         * No申请单号
//         */
//        private String vatNo = "";
//        /**
//         * 是否配货
//         */
//        private boolean status = false;
//
//        /**
//         * 重量（指的是库存重量）
//         */
//        private double weight = 0.0;
//
//
//        public ForwardingFlag(boolean isFind, String applyNo, String vatNo, boolean status, double weight) {
//            this.isFind = isFind;
//            this.applyNo = applyNo;
//            this.vatNo = vatNo;
//            this.status = status;
//            this.weight = weight;
//        }
//
//        public String getVatNo() {
//            return vatNo;
//        }
//
//        public void setVatNo(String vatNo) {
//            this.vatNo = vatNo;
//        }
//
//        public double getWeight() {
//            return weight;
//        }
//
//        public void setWeight(double weight) {
//            this.weight = weight;
//        }
//
//        public boolean isFind() {
//            return isFind;
//        }
//
//        public void setFind(boolean find) {
//            isFind = find;
//        }
//
//        public String getApplyNo() {
//            return applyNo;
//        }
//
//        public void setApplyNo(String applyNo) {
//            this.applyNo = applyNo;
//        }
//
//        public boolean isStatus() {
//            return status;
//        }
//
//        public void setStatus(boolean status) {
//            this.status = status;
//        }
//    }
//
//    class RecycleAdapter extends BasePullUpRecyclerAdapter<ForwardingList> {
//        private Context context;
//
//        public void setContext(Context context) {
//            this.context = context;
//        }
//
//        public void setHeader(View mHeaderView) {
//            super.setHeader(mHeaderView);
//        }
//
//        protected int position = -255;
//
//        public void select(int position) {
//            if (this.position != -255 && this.position != position)
//                this.position = position;
//            else
//                this.position = -255;
//        }
//
//        public RecycleAdapter(RecyclerView v, Collection<ForwardingList> datas, int itemLayoutId) {
//            super(v, datas, itemLayoutId);
//
//        }
//
//        @Override
//        public void convert(RecyclerHolder holder, final ForwardingList item, final int position) {
//            if (item != null) {
//                final String key = item.getApplyNo() + item.getVatNo()+position;
//                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
//                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                        if (isChecked) {
//                            if (dataKey.containsKey(item.getApplyNo()))
//                                if (!dataKey.get(item.getApplyNo()).contains(key))
//                                    dataKey.get(item.getApplyNo()).add(key);
//                        } else {
//                            if (dataKey.containsKey(item.getApplyNo()))
//                                if (dataKey.get(item.getApplyNo()).contains(key)) {
//                                    Iterator<String> iter = dataKey.get(item.getApplyNo()).iterator();
//                                    while (iter.hasNext()) {
//                                        String str = iter.next();
//                                        if (str.equals(key))
//                                            iter.remove();
//                                    }
//                                }
//                        }
//                    }
////                    }
//                });
//                LinearLayout title = holder.getView(R.id.layout_title);
//                LinearLayout no = holder.getView(R.id.headNo);
//                View view = holder.getView(R.id.view);
//                if (item.isStatus()) {
//                    title.setVisibility(View.VISIBLE);
//                    no.setVisibility(View.VISIBLE);
//                    view.setVisibility(View.VISIBLE);
//                    holder.setText(R.id.text1, "申请单号：" + item.getApplyNo());
//                } else {
//                    title.setVisibility(View.GONE);
//                    no.setVisibility(View.GONE);
//                    view.setVisibility(View.GONE);
//                }
//
///*
//                if ((item.getVatNo() + "").equals("") && (item.getOut_no() + "").equals("")) {
//                    if (cb.isEnabled())
//                        cb.setEnabled(false);
//                    cb.setChecked(false);
//                } else {*/
//                if (!cb.isEnabled())
//                    cb.setEnabled(true);
//                if (dataKey.containsKey(item.getApplyNo())) {
//                    if (dataKey.get(item.getApplyNo()).contains(key))
//                        cb.setChecked(true);
//                    else
//                        cb.setChecked(false);
//                }
//                LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
//                if (item.getApplyCount() == item.getMatchCount())
//                    ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
//                else
//                    ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
//                if (!item.isFlag()) {
//                    ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
//                      /*  if (cb.isEnabled())
//                            cb.setEnabled(false);*/
//                    cb.setChecked(false);
//                }
//                holder.setText(R.id.item1, item.getClothNum() + "");
//                holder.setText(R.id.item2, item.getSelNo() + "");
//                holder.setText(R.id.item3, item.getColor() + "");
//                holder.setText(R.id.item4, item.getVatNo() + "");
//                holder.setText(R.id.item5, item.getApplyCount() + "");
//                holder.setText(R.id.item6, item.getApplyWeight() + "");
//                holder.setText(R.id.item7, item.getMatchCount() + "");
//                holder.setText(R.id.item8, item.getMatchCount() + "");
//                holder.setText(R.id.item9, item.getMatchWeight() + "");
////                }
//
//            }
//        }
//    }
//}
