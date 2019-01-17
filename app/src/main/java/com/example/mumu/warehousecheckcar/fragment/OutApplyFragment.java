package com.example.mumu.warehousecheckcar.fragment;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.entity.OutputDetail;
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
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.DATA_KEY;
import static com.example.mumu.warehousecheckcar.application.App.KEY;
import static com.example.mumu.warehousecheckcar.application.App.OUTPUT_DETAIL_LIST;

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
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;

    private OutApplyFragment() {
    }

    public static OutApplyFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutApplyFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Output> myList;
    /**
     * 匹配机制应该是item分组字段
     * key:缸号、布号、色号
     */
    private Map<String, Integer> keyValue;
    private List<Output> dataList;
    //    private List<String> dataKey;
//    private Map<String,List<String>>dataKey;
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
        text1.setText(epcList.size() + "");
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.apply_item_layout_1, null);
        ((CheckBox) view.findViewById(R.id.checkbox1)).setVisibility(View.INVISIBLE);
        mAdapter.setHeader(view);
    }
//    主页返回执行

    public void onBackPressed() {
        RFID_2DHander.getInstance().on_2D();
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Output());
        }
        if (keyValue != null)
            keyValue.clear();
        if (DATA_KEY != null)
            DATA_KEY.clear();
        if (dataList != null)
            dataList.clear();
        if (epcList != null)
            epcList.clear();
//        text1.setText(epcList.size()+"");
    }

    private void initData() {
        myList = new ArrayList<>();
//        dataKey = new HashMap<>();
        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
        keyValue = new HashMap<>();

        text2.setText(App.APPLY_NO + "");
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
    public void upLoad(){
        mAdapter.notifyDataSetChanged();
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
        downLoadData();

    }

    private void downLoadData() {
        try {
            if (App.APPLY_NO != null) {
                JSONObject object = new JSONObject();
                object.put("applyNo", App.APPLY_NO);
                final String json = object.toJSONString();
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/output/pullOutput.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
//                        Toast.makeText(getActivity(), "无法获取申请单信息请返回重试！", Toast.LENGTH_SHORT).show();
                    }

                    /**这里应该要大改*/
                    @Override
                    public void onResponse(JSONArray json) {
                        try {
                            List<Output> response;
                            response = json.toJavaList(Output.class);
                            if (response != null && response.size() != 0) {
                                for (Output op : response) {
                                    if (op != null && op.getVatNo() != null) {
//                                if (keyValue.containsKey(op.getVatNo())) {//里面有
//                                        目前应该不会重复
//                                        如果是重复的话在这里加载进myList的list里面
//                                } else {//里面没有
                                        myList.add(op);
                                        String key = op.getOutp_id() + op.getVatNo() + op.getProduct_no() + op.getSelNo();
                                        keyValue.put(key, myList.size() - 1);
//                                }
                                        dataList.add(op);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, json);
            } else
                Toast.makeText(getActivity(), "申请单号为空！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        clearData();
        myList.clear();
        App.APPLY_NO = null;
        App.OUTPUT_DETAIL_LIST.clear();
        App.DATA_KEY.clear();
        App.KEY = null;
        disRFID();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                text1.setText(epcList.size() + "");
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
                ArrayList<Output> jsocList = new ArrayList<>();
                for (Output obj : dataList) {
                    String key = obj.getOutp_id() + obj.getVatNo() + obj.getProduct_no() + obj.getSelNo();
                    if (key != null && DATA_KEY.containsKey(key)) {
                        obj.setDevice(App.DEVICE_NO);
                        Output obj2 = (Output) obj.clone();
                        ArrayList<OutputDetail> newList = new ArrayList<OutputDetail>();
                        for (OutputDetail od : obj.getList()) {
                            if (DATA_KEY.get(key).contains(od.getFabRool())) {
                                newList.add(od);
                            }
                        }
                        obj2.setList(newList);
                        obj2.setCount(newList.size());
                        jsocList.add(obj2);
                    }
                }
                final String json = JSON.toJSONString(jsocList);
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
                                    clearData();
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
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
                dialog.dismiss();
            }
        });
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.select(position);
            mAdapter.notifyDataSetChanged();

            Output obj = myList.get(position);
            String key = obj.getOutp_id() + obj.getVatNo() + obj.getProduct_no() + obj.getSelNo();
            KEY = key;
            OUTPUT_DETAIL_LIST.clear();
            for (Output obj2 : dataList) {
                if ((obj2.getOutp_id() + obj2.getVatNo() + obj2.getProduct_no() + obj2.getSelNo()).equals(key)) {
                    OUTPUT_DETAIL_LIST.add(obj2);
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
                    final String EPC = ((String) msg.obj).replaceAll(" ", "");
                    if (!EPC.startsWith("31") && !epcList.contains(EPC)) {
                        boolean isData = false;
                        for (Output data : dataList) {
                            List<OutputDetail> detailList = data.getList();
                            if (detailList != null && detailList.size() != 0) {
                                for (OutputDetail detail : detailList) {
                                    if (detail != null && detail.getEpc() != null && detail.getEpc().equals(EPC)) {//判断成功//实盘
                                        String key1 = data.getOutp_id() + data.getVatNo() + data.getProduct_no() + data.getSelNo();
                                        isData = true;
                                        epcList.add(EPC);
                                        myList.get(keyValue.get(key1)).addCount();
                                        if (myList.get(keyValue.get(key1)).getCount() > myList.get(keyValue.get(key1)).getCountOut()) {
                                            detail.setFlag(3);//超出配货单量
                                        } else {
                                            detail.setFlag(1);//正常配货
                                        }
                                        data.setWeightall(ArithUtil.add(data.getWeightall(), detail.getWeight()));
                                        text1.setText(epcList.size() + "");
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        if (!isData) {
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
                                                        if (!keyValue.containsKey(key2)) {
                                                            Output data = new Output();
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
                                                            dataList.add(data);
                                                            myList.add(data);
                                                            keyValue.put(key2, myList.size() - 1);
                                                        } else {
                                                            myList.get(keyValue.get(key2)).addCount();
                                                            myList.get(keyValue.get(key2)).setWeightall(ArithUtil.add(myList.get(keyValue.get(key2)).getWeightall(), response.getWeight()));
                                                            for (Output op : dataList) {
                                                                if ((op.getOutp_id() + op.getVatNo() + op.getProduct_no() + op.getSelNo()).equals(key2)) {
                                                                    boolean isIn = false;
                                                                    for (OutputDetail od : op.getList()) {
                                                                        if (od.getEpc().equals(detail.getEpc()))
                                                                            isIn = true;
                                                                    }
                                                                    if (!isIn)
                                                                        op.getList().add(detail);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    text1.setText(epcList.size() + "");
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
                if (position != 0) {
                    String key = item.getOutp_id() + item.getVatNo() + item.getProduct_no() + item.getSelNo();
                    if (item.getCountOut() == 0)
                        cb.setChecked(false);
                    if (DATA_KEY.containsKey(key))
                        cb.setChecked(true);
                    if (cb.isChecked()) {
                        if (!DATA_KEY.containsKey(key))
                            DATA_KEY.put(key, new ArrayList<String>());
                    } else {
                        if (DATA_KEY.containsKey(key))
                            DATA_KEY.remove(key);
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
                    holder.setText(R.id.item6, item.getCount() + "");
                    holder.setText(R.id.item7, item.getWeightall() + "");
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            for (int i = 1; i < myList.size(); i++) {
                                View view = llm.findViewByPosition(i);
                                CheckBox c = (CheckBox) view.findViewById(R.id.checkbox1);
                                c.setChecked(isChecked);
                            }
                        } else {
                            String key = item.getOutp_id() + item.getVatNo() + item.getProduct_no() + item.getSelNo();
                            if (isChecked) {
                                if (!DATA_KEY.containsKey(key))
                                    DATA_KEY.put(key, new ArrayList<String>());
                            } else {
                                if (DATA_KEY.containsKey(key))
                                    DATA_KEY.remove(key);
                            }
                        }
                    }
                });
            }
        }
    }
}
