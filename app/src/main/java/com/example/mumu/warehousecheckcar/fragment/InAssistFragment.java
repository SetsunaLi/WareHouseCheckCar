package com.example.mumu.warehousecheckcar.fragment;

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
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.InAssistCloth;
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
public class InAssistFragment extends Fragment implements UHFCallbackLiatener, BRecyclerAdapter.OnItemClickListener {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;

    public static InAssistFragment newInstance() {
        return new InAssistFragment();
    }

    private final String TAG = InAssistFragment.class.getName();
    private ArrayList<String> dataEPC = new ArrayList<>();
    private ArrayList<InAssistCloth> myList = new ArrayList<InAssistCloth>();
    private RecycleAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRFID();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        clearList();
        View view = inflater.inflate(R.layout.inassist_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void clearList() {
        dataEPC.clear();
        myList.clear();
        myList.add(new InAssistCloth());
    }

    private void initView() {
        getActivity().setTitle("入库辅助");
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.inassist_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setHeader(LayoutInflater.from(getActivity()).inflate(R.layout.inassist_item, null));
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String epc = ((String) msg.obj).replaceAll(" ", "");
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
                                Toast.makeText(getActivity(), "扫描查布区布匹失败" + e.getMessage(), Toast.LENGTH_LONG).show();
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
                                text1.setText(dataEPC.size() + "");
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
    };

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        String epc = tag.strEPC;
        Message msg = handler.obtainMessage();
        msg.obj = epc;
        handler.sendMessage(msg);
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
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.selectItem(position);
        mAdapter.notifyDataSetChanged();
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

                holder.setText(R.id.item1, item.getBas_batch_name() + "");
                holder.setText(R.id.item2, item.getInv_serial() + "");
                if (item.getSuggest_location() != null) {
                    String location = item.getSuggest_location().replaceAll("剪布区", "").replaceAll("备货区", "");
                    holder.setText(R.id.item3, location + "");
                } else
                    holder.setText(R.id.item3, "Null");
                if (item.getSuggest_location() != null) {
                    String location = item.getSuggest_location().replaceAll("剪布区", "").replaceAll("备货区", "");
                    holder.setText(R.id.item4, location + "");
                } else
                    holder.setText(R.id.item4, "Null");
                holder.setText(R.id.item5, item.getQtys() + "");
            }
        }
    }
}
