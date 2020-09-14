package com.example.mumu.warehousecheckcar.fragment.out;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.activity.Main2Activity;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.entity.check.Inventory;
import com.example.mumu.warehousecheckcar.entity.out.Output;
import com.example.mumu.warehousecheckcar.entity.out.OutputDetail;
import com.example.mumu.warehousecheckcar.entity.out.OutputFlag;
import com.example.mumu.warehousecheckcar.entity.Power;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
import com.example.mumu.warehousecheckcar.listener.FragmentCallBackListener;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutApplyNewFragment extends BaseFragment implements UHFCallbackLiatener, FragmentCallBackListener, BasePullUpRecyclerAdapter.OnItemClickListener
        , RXCallback, OnCodeResult, OnRfidResult {
    private final String TAG = "OutApplyNewFragment";
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
    @BindView(R.id.edit1)
    EditText edit1;

    private static OutApplyNewFragment fragment;
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
    private ArrayList<String> dataKey;
    /***    所有扫描的epc总集，避免多次查询*/
    private ArrayList<String> epcList;
    /***     key：字段组成，记录非单号查询到的数据，并且记录插入myList的位置*/
    private HashMap<String, Integer> getEpcKey;
    /***    缸号，是否自动匹配缸号*/
    private HashMap<String, Boolean> vatKey;
    /***    记录查询到的申请单号，没实际用途*/
    private ArrayList<String> dateNo;
    private ScanResultHandler scanResultHandler;
    private RecycleAdapter mAdapter;
    private boolean is2D = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_apply_new_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("出库列表");
        return view;
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            uploadDialog.openView();
        }
    };

    @Override
    protected void initData() {
        fatherNoList = new ArrayList<>();
        epcKeyList = new HashMap<>();
        myList = new ArrayList<>();
        dateNo = new ArrayList<>();
        dataKey = new ArrayList<>();
        epcList = new ArrayList<>();
        getEpcKey = new HashMap<>();
        vatKey = new HashMap<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_apply_child_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this, this);
        initRFID();
        mAdapter.setOnItemClickListener(this);
        ComeBack.getInstance().setCallbackLiatener(this);
        edit1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    is2D = true;
                    init2D();
                } else {
                    is2D = false;
                    disConnect2D();
                }
            }
        });
        edit1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    edit1.clearFocus();
                    cancelKeyBoard(textView);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean flag = true;

    private void clearData() {
        epcKeyList.clear();
        myList.clear();
        dateNo.clear();
        dataKey.clear();
        epcList.clear();
        getEpcKey.clear();
        vatKey.clear();
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
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONArray json) {
                        try {
                            List<Output> response;
                            response = json.toJavaList(Output.class);
                            if (response != null && response.size() != 0) {
                                if (!dateNo.contains(response.get(0).getApplyNo())) {
                                    dateNo.add(response.get(0).getApplyNo());
                                    response.get(0).setStatus(true);
                                    for (int i = 0; i < response.size(); i++) {
                                        Output output = response.get(i);
                                        if (vatKey.containsKey(output.getVatNo())) {
                                            vatKey.put(output.getVatNo(), false);
                                        } else {
                                            vatKey.put(output.getVatNo(), true);
                                        }
                                        for (OutputDetail outputDetail : output.getList()) {
                                            if (!epcKeyList.containsKey(outputDetail.getEpc())) {
                                                epcKeyList.put(outputDetail.getEpc() + "", new OutputFlag(false, "", true, outputDetail.getWeight()));
                                            }
                                        }
                                        if (!dataKey.contains(output.getApplyNo()))
                                            dataKey.add(output.getApplyNo());
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
        if (is2D)
            disConnect2D();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            ArrayList<String> list = (ArrayList<String>) getArguments().getSerializable("NO");
            fatherNoList.clear();
            Iterator<String> iter = list.iterator();
            while (iter.hasNext()) {
                String str = iter.next();
                str = str.replaceAll(" ", "");
                if (str != null && !str.equals("") && !fatherNoList.contains(str))
                    fatherNoList.add(str);
            }
            text1.setText(String.valueOf(0));
            text2.setText(String.valueOf(fatherNoList.size()));
            downLoadData();
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
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    //标签操作
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
    public void ubLoad(boolean flag) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void comeBackListener() {
        epcKeyList.clear();
        epcKeyList.putAll(((Main2Activity) getActivity()).getOutApplyDataList());
        Output i = myList.get(position);
        i.setCountProfit(0);
        i.setWeightPei(0);
        for (OutputDetail od : i.getList()) {
            if (epcKeyList.containsKey(od.getEpc())) {
                if (epcKeyList.get(od.getEpc()).getApplyNo().equals(i.getApplyNo() + position)) {
                    i.setCountProfit(i.getCountProfit() + 1);
                    i.setWeightPei(ArithUtil.add(i.getWeightPei(), epcKeyList.get(od.getEpc()).getWeight()));
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        if (is2D)
            init2D();
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
                showUploadDialog("是否确认出库");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String location = edit1.getText().toString();
                        if (TextUtils.isEmpty(location)) {
                            upLoad(location);
                        } else {
                            judge(location);
                        }
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, App.TIME);
                    }
                });
                break;
        }
    }

    private void judge(final String location) {
        Carrier carrier = new Carrier();
        carrier.setLocationNo(location);
        final String CARRIER = JSON.toJSONString(carrier);
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/havingLocation", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        try {
                            AppLog.write(getActivity(), "ccarrier", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                            upLoad(location);
                        } else {
                            uploadDialog.openView();
                            hideUploadDialog();
                            scanResultHandler.removeCallbacks(r);
                            showConfirmDialog("库位无效");
                        }
                    } catch (Exception e) {

                    }
                }
            }, CARRIER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upLoad(final String location) {
        ArrayList<ArrayList<Output>> allList = new ArrayList<>();
        boolean isPush = true;
        for (String applyNo : dataKey) {
            ArrayList<Output> oneNoList = new ArrayList<>();
            for (int i = 0; i < myList.size(); i++) {
                Output op = myList.get(i);
                if (applyNo.equals(op.getApplyNo()) && op.getCountOut() > 0) {
                    ArrayList<OutputDetail> detailsList = new ArrayList<OutputDetail>();
                    for (OutputDetail od : op.getList()) {
                        if (epcKeyList.get(od.getEpc()).getApplyNo().equals(applyNo + i)) {
                            od.setFlag(1);
                            od.setWeight_out(epcKeyList.get(od.getEpc()).getWeight());
                            detailsList.add(od);
                        }
                    }
                    if (detailsList.size() > 0) {
                        Output obj = (Output) op.clone();
                        obj.setDevice(App.DEVICE_NO);
                        obj.setFlag(1);
                        obj.setList(detailsList);
                        if (isPush && obj.getCountOut() != detailsList.size()) {
                            isPush = false;
                        }
                        oneNoList.add(obj);
                    } else {
                        isPush = false;
                    }
                }
            }
            if (oneNoList.size() > 0) {
                allList.add(oneNoList);
            }
        }
        int outAuth6 = -1;
        int outAuth7 = -1;
        for (Power power : User.newInstance().getApp_auth()) {
            if (power.getAuth_type() == 6)
                outAuth6 = power.getFlag();
            if (power.getAuth_type() == 7)
                outAuth7 = power.getFlag();
        }
        if ((outAuth7 != 0 || (outAuth6 != 0 && isPush)) && allList.size() > 0) {
            for (ArrayList<Output> jsocList : allList) {
                if (jsocList.size() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", User.newInstance().getId());
                    jsonObject.put("data", jsocList);
                    if (!TextUtils.isEmpty(location))
                        jsonObject.put("tempLocation", location);
                    else
                        jsonObject.put("tempLocation", "");

                    final String json = jsonObject.toJSONString();
                    try {
                        AppLog.write(getActivity(), "outapply", json, AppLog.TYPE_INFO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/output/pushOutput.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                                    try {
                                        AppLog.write(getActivity(), "outapply", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    uploadDialog.openView();
                                    hideUploadDialog();
                                    scanResultHandler.removeCallbacks(r);
                                    BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                    if (baseReturn != null && baseReturn.getStatus() == 1) {
                                        Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                        clearData();
                                        mAdapter.notifyDataSetChanged();
//                                            blinkDialog2(true);
                                    } else {
                                        Toast.makeText(getActivity(), baseReturn.getMessage(), Toast.LENGTH_LONG).show();
                                        showConfirmDialog(baseReturn.getMessage());
                                        Sound.faillarm();
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
        } else {
            uploadDialog.openView();
            hideUploadDialog();
            scanResultHandler.removeCallbacks(r);
            showConfirmDialog("配货条数与申请条数不一致！请联系收发人员或出库文员。");
        }
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    private int position;

    @Override
    public void onItemClick(View view, Object data, int position) {
        this.position = position;
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
        if (is2D)
            disConnect2D();
        Output obj = myList.get(position);
        Fragment fragment = OutApplyDetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dataList", obj);
        bundle.putSerializable("epcList", epcKeyList);
        bundle.putSerializable("position", position);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
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
        edit1.setText(code);
    }

    @Override
    public void rfidResult(String epc) {
        final String EPC = epc.replaceAll(" ", "");
        if (EPC.startsWith("3035A537") && epcKeyList.containsKey(EPC)) {
            if (!epcKeyList.get(EPC).isFind()) {
                epcKeyList.get(EPC).setFind(true);
                epcKeyList.get(EPC).setStatus(true);
                for (int i = 0; i < myList.size(); i++) {
                    Output output = myList.get(i);
                    for (OutputDetail od : output.getList()) {
                        if (od.getEpc().equals(EPC)) {
                            output.setCount(output.getCount() + 1);
                            output.setWeightall(ArithUtil.add(output.getWeightall(), epcKeyList.get(EPC).getWeight()));

//                                        自动配货
                            if (vatKey.get(output.getVatNo()) && output.getCountProfit() < output.getCountOut()) {
                                epcKeyList.get(EPC).setApplyNo(output.getApplyNo() + i);
                                output.setWeightPei(ArithUtil.add(output.getWeightPei(), epcKeyList.get(EPC).getWeight()));
                                output.addCountProfit();
                            }
                        }
                    }
                }
                int count = 0;
                for (OutputFlag o : epcKeyList.values()) {
                    if (o.isFind())
                        count++;
                }
                text1.setText(count + "");
                mAdapter.notifyDataSetChanged();
            }
        } else if (EPC.startsWith("3035A537") && !epcKeyList.containsKey(EPC) && !epcList.contains(EPC)) {
            JSONObject epcJson = new JSONObject();
            epcJson.put("epc", EPC);
            final String json = epcJson.toJSONString();
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
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (!dataKey.contains(item.getApplyNo()))
                                dataKey.add(item.getApplyNo());
                        } else {
                            if (dataKey.contains(item.getApplyNo()))
                                dataKey.remove(item.getApplyNo());
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
                    if (!cb.isEnabled())
                        cb.setEnabled(true);
                    if (dataKey.contains(item.getApplyNo()))
                        cb.setChecked(true);
                    else
                        cb.setChecked(false);

                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
//                    扫描>配货红色
                    if (item.getCount() > item.getCountOut())
                        ll.setBackgroundColor(getResources().getColor(R.color.colorREAD));
                    else if (item.getCountOut() == item.getCountProfit())
                        //                    扫描=配货蓝色
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    if (item.getFlag() == 2) {
                        //            非单号扫描黄色
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
