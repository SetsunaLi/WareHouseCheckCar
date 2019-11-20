package com.example.mumu.warehousecheckcar.fragment.in;

import android.annotation.SuppressLint;
import android.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.InAssistCloth;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by ${mumu}
 *on 2019/9/19
 */
public class InAssistFragment extends BaseFragment implements UHFCallbackLiatener, BRecyclerAdapter.OnItemClickListener, OnRfidResult {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;

    public static InAssistFragment newInstance() {
        return new InAssistFragment();
    }

    private final String TAG = InAssistFragment.class.getName();
    private ArrayList<String> dataEPC;
    private ArrayList<InAssistCloth> myList;
    private RecycleAdapter mAdapter;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("入库辅助");
        View view = inflater.inflate(R.layout.inassist_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        dataEPC = new ArrayList<>();
        myList = new ArrayList<>();
        myList.add(new InAssistCloth());
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.inassist_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setHeader(LayoutInflater.from(getActivity()).inflate(R.layout.inassist_item, null));
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
        mAdapter.setOnItemClickListener(this);
    }

    private void clearList() {
        dataEPC.clear();
        myList.clear();
        myList.add(new InAssistCloth());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
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

    @OnClick(R.id.button1)
    public void onViewClicked() {
        clearList();
        mAdapter.notifyDataSetChanged();
        scanResultHandler.removeMessages(ScanResultHandler.RFID);
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.selectItem(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void rfidResult(String epcCode) {
        final String epc = epcCode.replaceAll(" ", "");
        if (epc.startsWith("3035A537") && !dataEPC.contains(epc)) {
            JSONObject obj = new JSONObject();
            obj.put("epc", epc);
            final String json = obj.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/inputAssist/sumQty.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            showToast("扫描查布区布匹失败");
                        }
                    }

                    @Override
                    public void onResponse(JSONObject object) {
                        try {
                            InAssistCloth value = object.toJavaObject(InAssistCloth.class);
                            if (value != null) {
                                if (!dataEPC.contains(epc)) {
                                    dataEPC.add(epc);
                                    myList.add(value);
                                }
                            }
                            text1.setText(String.valueOf(dataEPC.size()));
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {
                Log.i(TAG, "");
            }
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<InAssistCloth> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<InAssistCloth> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        private int position = -255;

        public void selectItem(int position) {
            if (this.position == position)
                this.position = -255;
            else
                this.position = position;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public void convert(RecyclerHolder holder, final InAssistCloth item, final int position) {
            if (item != null && position != 0) {
                LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                if (this.position == position)
                    ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                else
                    ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                holder.setText(R.id.item1, item.getBas_batch_name());
                holder.setText(R.id.item2, item.getInv_serial());
                if (item.getSuggest_location() != null) {
                    String location = item.getSuggest_location().replaceAll("剪布区", "").replaceAll("备货区", "");
                    holder.setText(R.id.item3, location);
                } else
                    holder.setText(R.id.item3, "Null");
                if (item.getSuggest_location() != null) {
                    String location = item.getSuggest_location().replaceAll("剪布区", "").replaceAll("备货区", "");
                    holder.setText(R.id.item4, location);
                } else
                    holder.setText(R.id.item4, "Null");
                holder.setText(R.id.item5, String.valueOf(item.getQtys()));
            }
        }
    }
}
