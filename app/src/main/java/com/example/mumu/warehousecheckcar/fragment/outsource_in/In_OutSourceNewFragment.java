package com.example.mumu.warehousecheckcar.fragment.outsource_in;

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
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.out.Outsource;
import com.example.mumu.warehousecheckcar.entity.out.OutsourceGroup;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;
import static com.example.mumu.warehousecheckcar.application.App.TIME;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by
 *on 2020/8/29
 */
public class In_OutSourceNewFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult, BRecyclerAdapter.OnItemClickListener {

    private static In_OutSourceNewFragment fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;

    public static In_OutSourceNewFragment newInstance() {
        if (fragment == null) ;
        fragment = new In_OutSourceNewFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private ArrayList<String> noList;
    /**
     * 数据组
     */
    private ArrayList<OutsourceGroup> myList;
    /**
     * 明细数据
     */
    private ArrayList<Outsource> dataList;
    /**
     * 扫描epc
     */
    private ArrayList<String> epcs;
    /**
     * 获取单号
     */
    private ArrayList<String> dataNos;
    /**
     * 获取epc
     */
    private ArrayList<String> dataEpcs;

    private ScanResultHandler scanResultHandler;
    private int scanCount = 0, outCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_outsource_new_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        noList = (ArrayList<String>) getArguments().getSerializable("NO");
        myList = new ArrayList<>();
        dataList = new ArrayList<>();
        epcs = new ArrayList<>();
        dataNos = new ArrayList<>();
        dataEpcs = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_outsource_new_item);
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
        initRFID();
        scanResultHandler = new ScanResultHandler(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x00:
                    int position = (int) msg.getPositionObj(0);
                    OutsourceGroup group = myList.get(position);
                    group.setAllScanWeight(0);
                    group.setScanCount(0);
                    for (Outsource outsource : dataList) {
                        if (outsource.getDeliverNo().equals(group.getDeliverNo()) && outsource.isFlag()) {
                            group.addScanCount();
                            group.setAllScanWeight(ArithUtil.add(group.getAllScanWeight(), outsource.getWeight()));
                        }
                    }
                    scanCount = 0;
                    for (OutsourceGroup og : myList) {
                        scanCount = scanCount + og.getScanCount();
                    }
                    text1.setText(String.valueOf(scanCount));
                    mAdapter.notifyDataSetChanged();
                    initRFID();
                    break;
            }
    }

    private void clearData() {
        myList.clear();
        dataList.clear();
        epcs.clear();
        dataNos.clear();
        dataEpcs.clear();
        scanCount = 0;
        outCount = 0;
        text1.setText(String.valueOf(scanCount));
        text2.setText(String.valueOf(outCount));
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
    public void rfidResult(String epc) {
        if (dataEpcs.contains(epc) && !epcs.contains(epc)) {
            epcs.add(epc);
            for (Outsource outsource : dataList) {
                if (outsource.getEpc().equals(epc)) {
                    OutsourceGroup group = null;
                    Iterator<OutsourceGroup> iterator = myList.iterator();
                    while (iterator.hasNext()) {
                        group = iterator.next();
                        if (group.getDeliverNo().equals(outsource.getDeliverNo())) {
                            group.addScanCount();
                            group.setAllScanWeight(ArithUtil.add(group.getAllScanWeight(), outsource.getWeight()));
                            outsource.setScan(true);
                            outsource.setFlag(true);
                            iterator.remove();
                            break;
                        } else
                            group = null;
                    }
                    if (group != null) {
                        myList.add(0, group);
                    }
                    break;
                }
            }
            scanCount = 0;
            for (OutsourceGroup og : myList) {
                scanCount = scanCount + og.getScanCount();
            }
            text1.setText(String.valueOf(scanCount));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        download();

    }

    private void download() {
        ArrayList<String> list = new ArrayList<>();
        for (String no : noList) {
            if (!TextUtils.isEmpty(no))
                list.add(no);
        }
        JSONObject jsonObject = new JSONObject();
        String json = JSONObject.toJSONString(list);
        try {
            OkHttpClientManager.postJsonAsyn(App.CLOUD_IP + ":" + App.CLOUD_PORT + "/a/bas/transportOutApi/outSourceStorage", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInteger("code") == 200) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            for (String key : object.keySet()) {
                                JSONArray jsonArray = object.getJSONArray(key);
                                String jsonStr = JSONObject.toJSONString(jsonArray);
                                List<Outsource> jsonArr = JSONObject.parseArray(jsonStr, Outsource.class);
                                if (jsonArr.size() > 0) {
                                    OutsourceGroup group = new OutsourceGroup(jsonArr.get(0), jsonArr.size());
                                    if (!dataNos.contains(group.getDeliverNo())) {
                                        dataNos.add(group.getDeliverNo());
                                        myList.add(group);
                                        for (Outsource outsource : jsonArr) {
                                            if (!dataEpcs.contains(outsource.getEpc())) {
                                                group.setAllWeightF(ArithUtil.add(group.getAllWeightF(), outsource.getWeight_f()));
                                                group.setAllWeight(ArithUtil.add(group.getAllWeight(), outsource.getWeight()));
                                                dataEpcs.add(outsource.getEpc());
                                                outsource.setFlag(false);
                                                outsource.setScan(false);
                                                dataList.add(outsource);
                                            }
                                        }
                                    }
                                }
                            }
                            outCount = 0;
                            for (OutsourceGroup og : myList) {
                                outCount = outCount + og.getOutCount();
                            }
                            text2.setText(String.valueOf(outCount));
                            mAdapter.notifyDataSetChanged();
                        }
                        showToast(jsonObject.getString("message"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, json);
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onDestroyView() {
        super.onDestroyView();
        disRFID();
        EventBus.getDefault().post(new EventBusMsg(0x02));
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                download();
                break;
            case R.id.button2:
              /*  rfidResult("30B5A5AF6400004000002337");
                rfidResult("30B5A5AF6400004000002345");
                rfidResult("30A5A5AF6400004000002407");
                rfidResult("30A5A5AF6400004000002371");
                rfidResult("30B5A5AF6400004000002459");*/
                showUploadDialog("是否确认入库");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submit();
                    }
                });
                break;
        }
    }

    private void submit() {
        ArrayList<List<Outsource>> list = new ArrayList<>();
        ArrayList<String> epcs = new ArrayList<>();
        boolean flag = true;
        for (OutsourceGroup group : myList) {
            if (group.isStutas()) {
                if (group.getOutCount() == group.getScanCount()) {
                    ArrayList<Outsource> outsources = new ArrayList<>();
                    for (Outsource outsource : dataList) {
                        if (outsource.isFlag() && outsource.getVat_no().equals(group.getVat_no()) && outsource.getDeliverNo().equals(group.getDeliverNo())
                                && outsource.getTransNo().equals(group.getTransNo())) {
                            outsources.add(outsource);
                            epcs.add(outsource.getEpc());
                        }
                    }
                    list.add(outsources);
                } else {
                    flag = false;
                    break;
                }

            }
        }
        if (flag && list.size() > 0) {
            uploadDialog.lockView();
            scanResultHandler.postDelayed(r, TIME);
            for (List<Outsource> outsources : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", User.newInstance().getId());
                jsonObject.put("data", outsources);
                final String json = jsonObject.toJSONString();
                try {
                    LogUtil.i(getResources().getString(R.string.log_in_outSource), json);
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cc_print_tag_line/new_inv_sum_trans", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (e instanceof ConnectException)
                                showConfirmDialog("链接超时");
                            try {
                                LogUtil.e(getResources().getString(R.string.log_in_outSource_result), e.getMessage(), e.getCause());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponse(BaseReturn response) {
                            try {
                                LogUtil.i(getResources().getString(R.string.log_in_outSource_result), "userId:" + User.newInstance().getId() + response.toString());
                                uploadDialog.openView();
                                hideUploadDialog();
                                scanResultHandler.removeCallbacks(r);
                                if (response.getStatus() == 1) {
                                    showToast("上传成功");
                                    clearData();
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    showToast("上传失败");
                                    showConfirmDialog("WMS上传失败，" + response.getMessage());
                                    Sound.faillarm();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        /*    JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("userId", Constant.USERNAME);
            jsonObject2.put("password", Constant.PRASSWORD);
            jsonObject2.put("epcs", epcs);
            final String json2 = jsonObject2.toJSONString();
            try {
                AppLog.write(getActivity(), "inventIn", "userId:" + User.newInstance().getId() + json2, AppLog.TYPE_INFO);
                OkHttpClientManager.postJsonAsyn(App.CLOUD_IP + ":" + App.CLOUD_PORT + "/a/bas/basLabelApi/inventIn", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "inventIn;" + e.getMessage());
                            Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(BaseReturn response) {
                        try {
                            AppLog.write(getActivity(), "inventIn", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                            uploadDialog.openView();
                            hideUploadDialog();
                            scanResultHandler.removeCallbacks(r);
                            if (response.getCode() == 1) {
                                showToast("上传成功");
                                clearData();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                showToast("上传失败");
                                showConfirmDialog("标签云上传失败，" + response.getMessage());
                                Sound.faillarm();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, json2);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        } else
            showConfirmDialog("至少上传一条有效单号数据，且扫描数量必须与发运数量相同");
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        disRFID();
        String no = myList.get(position).getDeliverNo();
        String vatNo = myList.get(position).getVatNo();
        String colorNo = myList.get(position).getColor_code();
        String product = myList.get(position).getProduct_name();
        String color = myList.get(position).getColor_name();
        ArrayList<Outsource> list = new ArrayList<>();
        for (Outsource outsource : dataList) {
            if (outsource.getDeliverNo().equals(no)) {
                list.add(outsource);
            }
        }
        EventBus.getDefault().postSticky(new EventBusMsg(0x01, vatNo, colorNo, product, color, position, list));
        Fragment fragment = In_OutSourceDetailFragment.newInstance();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<OutsourceGroup> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<OutsourceGroup> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final OutsourceGroup item, final int position) {
            if (item != null) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setStutas(b);
                    }
                });
                checkBox.setChecked(item.isStutas());
                holder.setText(R.id.text1, "送货单号：" + item.getDeliverNo());
                holder.setText(R.id.item1, item.getVat_no());
                holder.setText(R.id.item2, String.valueOf(item.getScanCount()));
                holder.setText(R.id.item3, String.valueOf(item.getOutCount()));
                holder.setText(R.id.item4, item.getColor_code());
                holder.setText(R.id.item5, item.getSel_color());
                holder.setText(R.id.item6, item.getColor_name());
                holder.setText(R.id.item7, item.getProduct_name());
                holder.setText(R.id.item8, item.getProduct_no());
                holder.setText(R.id.item9, item.getWidth_side());
                holder.setText(R.id.item10, String.valueOf(item.getAllScanWeight()));
                holder.setText(R.id.item11, String.valueOf(item.getAllWeight()));
                holder.setText(R.id.item12, String.valueOf(item.getAllWeightF()));
                holder.setText(R.id.item13, String.valueOf(item.getWeight_zg()));
                holder.setText(R.id.item14, String.valueOf(item.getWeight_kj()));
                holder.setText(R.id.item15, item.getCust_po());
                LinearLayout layout = holder.getView(R.id.layout1);
                layout.setBackgroundColor(item.getOutCount() == item.getScanCount() ? getResources().getColor(R.color.colorDialogTitleBG) : getResources().getColor(R.color.colorZERO));
            }
        }
    }
}