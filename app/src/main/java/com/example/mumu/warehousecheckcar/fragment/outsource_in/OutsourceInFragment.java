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
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.Constant;
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
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.Outsource;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;
import static com.example.mumu.warehousecheckcar.application.App.TIME;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 
 *on 2020/7/22
 */
public class OutsourceInFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult, BRecyclerAdapter.OnItemClickListener {
    private static OutsourceInFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.scrollView)
    HorizontalScrollView scrollView;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;


    private ScanResultHandler scanResultHandler;
    private RecycleAdapter mAdapter;
    private ArrayList<String> titles;
    private ArrayList<VatGloud> myList;
    private ArrayList<Outsource> dates;
    private ArrayList<String> epcs;

    public static OutsourceInFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutsourceInFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.outsource_in_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        titles = new ArrayList<>();
        myList = new ArrayList<>();
        dates = new ArrayList<>();
        epcs = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.outsource_in_item);
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
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRFID();
        scanResultHandler = new ScanResultHandler(this);

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x02:
                    int position = (int) msg.getPositionObj(0);
                    myList.get(position).setEpcs((List<String>) msg.getPositionObj(1));
                    mAdapter.notifyDataSetChanged();
                    break;
            }
    }

    private void clearData() {
        titles.clear();
        myList.clear();
        dates.clear();
        epcs.clear();
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
        if (!epcs.contains(epc)) {
            List<String> list = new ArrayList<>();
            list.add(epc);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", Constant.USERNAME);
            jsonObject.put("password", Constant.PRASSWORD);
            jsonObject.put("epcs", list);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.CLOUD_IP + ":" + App.CLOUD_PORT + "/a/bas/basLabelApi/queryEpcs", new OkHttpClientManager.ResultCallback<BaseReturnArray<Outsource>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            showToast("获取信息失败");
                        }
                    }

                    @Override
                    public void onResponse(BaseReturnArray<Outsource> returnArray) {
                        if (returnArray != null) {
                            for (Outsource outsource : returnArray.getData()) {
                                if (!TextUtils.isEmpty(outsource.getCust_po()) && TextUtils.isEmpty(outsource.getDeliverNo()) && TextUtils.isEmpty(outsource.getVat_no()) && !epcs.contains(outsource.getEpc())) {

                                    if (!titles.contains(outsource.getCust_po() + outsource.getDeliverNo() + outsource.getVat_no())) {
                                        titles.add(outsource.getCust_po() + outsource.getDeliverNo() + outsource.getVat_no());
                                        myList.add(new VatGloud(outsource.getVatNo(), outsource.getProduct_no(), outsource.getColor_code(), outsource.getColor_name()
                                                , outsource.getCust_po(), outsource.getDeliverNo(), true, outsource.getEpc()));
                                    } else {
                                        for (VatGloud vatGloud : myList) {
                                            if ((vatGloud.getCust_po() + vatGloud.getDeliverNo() + vatGloud.getVatNo())
                                                    .equals(outsource.getCust_po() + outsource.getDeliverNo() + outsource.getVatNo())) {
                                                vatGloud.getEpcs().add(outsource.getEpc());
                                                break;
                                            }
                                        }
                                    }
                                    epcs.add(outsource.getEpc());
                                    dates.add(outsource);
                                    text2.setText(String.valueOf(epcs.size()));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }, json);
            } catch (IOException e) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scanResultHandler.removeMessages(ScanResultHandler.RFID);
        disRFID();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                clearData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                showUploadDialog("是否确认入库");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submit();
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });
                break;
        }
    }

    private void submit() {
        ArrayList<Outsource> list = new ArrayList<>();
        ArrayList<String> epcs = new ArrayList<>();
        for (VatGloud vatGloud : myList) {
            if (vatGloud.isFlag())
                epcs.addAll(vatGloud.getEpcs());
        }
        for (Outsource outsource : dates) {
            if (epcs.contains(outsource.getEpc()))
                list.add(outsource);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        jsonObject.put("data", list);
        final String json = jsonObject.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.CLOUD_IP + ":" + App.CLOUD_PORT + "/shYf/sh/cc_print_tag_line/new_inv_sum_trans", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        Log.i(TAG, "new_inv_sum_trans;" + e.getMessage());
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
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("userId", Constant.USERNAME);
        jsonObject2.put("password", Constant.PRASSWORD);
        jsonObject2.put("epcs", epcs);
        final String json2 = jsonObject2.toJSONString();
        try {
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
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        ArrayList<Outsource> list = new ArrayList<>();
        for (Outsource outsource : dates) {
            if (myList.get(position).getVatNo().contains(outsource.getVatNo()))
                list.add(outsource);
        }
        Fragment fragment = OutsourceInDetailFragment.newInstance();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
        EventBus.getDefault().postSticky(new EventBusMsg(0x01, list, myList.get(position).getEpcs(), position));
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<VatGloud> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<VatGloud> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        @Override
        public void convert(RecyclerHolder holder, final VatGloud item, final int position) {
            if (item != null) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setFlag(b);
                    }
                });
                checkBox.setChecked(item.isFlag());
                holder.setText(R.id.text1, "送货单号：" + item.getDeliverNo() + "  ");
                holder.setText(R.id.text4, "PO：" + item.getCust_po());
                holder.setText(R.id.item1, item.getProduct_no());
                holder.setText(R.id.item2, item.getColor_code());
                holder.setText(R.id.item3, item.getColor_name());
                holder.setText(R.id.item4, item.getVatNo());
                holder.setText(R.id.item5, String.valueOf(item.getEpcs().size()));
            }
        }
    }

    class VatGloud extends Outsource {
        private boolean flag;
        private List<String> epcs;

        public VatGloud(String vatNo, String productNo, String colorCode, String colorName, String custPo, String deliverNo, boolean flag, String epc) {
            super(vatNo, productNo, colorCode, colorName, custPo, deliverNo);
            this.flag = flag;
            epcs = new ArrayList<>();
            epcs.add(epc);
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public List<String> getEpcs() {
            return epcs;
        }

        public void setEpcs(List<String> epcs) {
            this.epcs = epcs;
        }
    }
}
