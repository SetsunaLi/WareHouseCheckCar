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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.mumu.warehousecheckcar.entity.BaseReturnObject;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.Forwarding;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.example.mumu.warehousecheckcar.utils.SpModel;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.Constant.APP_OUTP_ID;
import static com.example.mumu.warehousecheckcar.Constant.APP_TABLE_NAME;
import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class ForwardingFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener, OnRfidResult {
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
    /**
     * 所有的Forwarding信息，不管是申请单内还是epc扫出来的
     */
    private ArrayList<Forwarding> dataList;
    /***    记录查询到的申请单号，没实际用途*/
    private ArrayList<String> dateNo;
    private RecycleAdapter mAdapter;
    private ScanResultHandler scanResultHandler;
    private int transport_output_id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forwarding_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        fatherNoList = new ArrayList<>();
        epcKeyList = new HashMap<>();
        myList = new ArrayList<>();
        dateNo = new ArrayList<>();
        dataKey = new HashMap<>();
        epcList = new ArrayList<>();
        getKeyValue = new HashMap<>();
        dataList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.forwarding_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);

    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    public void clearData() {
        epcKeyList.clear();
        myList.clear();
        dateNo.clear();
        dataKey.clear();
        epcList.clear();
        getKeyValue.clear();
        dataList.clear();
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

    private ForwardingMsgFragment.CarMsg carMsg;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x01:
                    carMsg = (ForwardingMsgFragment.CarMsg) msg.getPositionObj(0);
                    fatherNoList = (ArrayList<String>) msg.getPositionObj(1);
                    transport_output_id = (int) msg.getPositionObj(2);
                    break;
                case 0xfe:
                    epcKeyList.clear();
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

    @Override
    public void onResume() {
        super.onResume();
        downLoadData();
        text2.setText(String.valueOf(fatherNoList.size()));
        text3.setText(String.valueOf(carMsg.getCarNo()));
        text4.setText(String.valueOf(carMsg.getCarName()));
    }

    private void downLoadData() {
        for (String no : fatherNoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("applyNo", no);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/pullDespatch", new OkHttpClientManager.ResultCallback<List<Forwarding>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            showToast("获取申请单信息失败");

                        }
                    }

                    @Override
                    public void onResponse(List<Forwarding> response) {
                        try {
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
                text1.setText("0");
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否上传装车");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upLoading();
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });
         /*       setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        returnWhere = true;
                        upLoading(true);
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });*/
                break;
        }
    }

    private void upLoading() {
        JSONObject jsonObject = new JSONObject();
        int id = User.newInstance().getId();
        jsonObject.put("userId", id);
        jsonObject.put("carMsg", carMsg);
        jsonObject.put("cc_transport_output_id", transport_output_id);
//        false是0，true是1
        jsonObject.put("status", 0);
        jsonObject.put("applyNo", fatherNoList);
        ArrayList<Forwarding> list = new ArrayList<>();
        boolean flag = true;
        for (Forwarding forwarding : dataList) {
            String key = forwarding.getApplyNo() + forwarding.getVatNo();
            if (dataKey.containsKey(forwarding.getApplyNo()))
                if (dataKey.get(forwarding.getApplyNo()).contains(key)) {
                    if (getKeyValue.containsKey(key)) {
                        ForwardingList forwardingList = myList.get(getKeyValue.get(key));
                        if (forwardingList.isFlag() && forwardingList.getApplyCount() != forwardingList.getMatchCount()) {
                            flag = false;
                            break;
                        }
                    }
                    if (epcKeyList.containsKey(forwarding.getEpc()))
                        if (epcKeyList.get(forwarding.getEpc()).isStatus())
                            list.add(forwarding);
                }
        }
        if (flag) {
            jsonObject.put("data", list);
            final String json = jsonObject.toJSONString();
            try {
                AppLog.write(getActivity(), "forwarding", json, AppLog.TYPE_INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/postTransportOut", new OkHttpClientManager.ResultCallback<BaseReturnObject<JSONObject>>() {
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
                    public void onResponse(BaseReturnObject<JSONObject> response) {
                        try {
                            AppLog.write(getActivity(), "forwarding", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            uploadDialog.openView();
                            hideUploadDialog();
                            scanResultHandler.removeCallbacks(r);
                            if (response.getStatus() == 1) {
                                showToast("上传成功");
                                clearData();
                                mAdapter.notifyDataSetChanged();
                                Fragment fragment = ForwardingListFragment.newInstance();
                                getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                getActivity().getFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
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
        } else {
            Sound.faillarm();
            showConfirmDialog("上传失败，申请单内扫描条数必须与申请条数一致！");
            uploadDialog.openView();
            hideUploadDialog();
            scanResultHandler.removeCallbacks(r);
        }

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
        if (!epcList.contains(epc)) {
            if (epc.startsWith("3035A537") && epcKeyList.containsKey(epc)) {
                epcList.add(epc);
                if (!epcKeyList.get(epc).isFind) {
                    epcKeyList.get(epc).setFind(true);
                    epcKeyList.get(epc).setStatus(true);
                    String key = epcKeyList.get(epc).getApplyNo() + epcKeyList.get(epc).getVatNo();
                    if (getKeyValue.containsKey(key)) {
                        myList.get(getKeyValue.get(key)).addScannerCount();
                        myList.get(getKeyValue.get(key)).addMatchCount();
                        myList.get(getKeyValue.get(key)).addMatchWeight(epcKeyList.get(epc).getWeight());
                    }
                    int count = 0;
                    for (ForwardingFlag o : epcKeyList.values()) {
                        if (o.isFind && !o.getApplyNo().equals("非申请单"))
                            count++;
                    }
                    text1.setText(String.valueOf(count));
                    mAdapter.notifyDataSetChanged();
                }
            } else if (epc.startsWith("3035A537") && !epcKeyList.containsKey(epc)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("epc", epc);
                final String json = jsonObject.toJSONString();
                if (!epcList.contains(epc)) {
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
        }
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
                    cb.setChecked(false);
                }
                holder.setText(R.id.item1, item.getClothNum());
                holder.setText(R.id.item2, item.getSelNo());
                holder.setText(R.id.item3, item.getColor());
                holder.setText(R.id.item4, item.getVatNo());
                holder.setText(R.id.item5, String.valueOf(item.getApplyCount()));
                holder.setText(R.id.item6, String.valueOf(item.getApplyWeight()));
                holder.setText(R.id.item7, String.valueOf(item.getMatchCount()));
                holder.setText(R.id.item8, String.valueOf(item.getMatchCount()));
                holder.setText(R.id.item9, String.valueOf(item.getMatchWeight()));
            }
        }
    }
}