package com.example.mumu.warehousecheckcar.fragment.outsource_in;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.BaseReturnObject;
import com.example.mumu.warehousecheckcar.entity.Outsource;
import com.example.mumu.warehousecheckcar.entity.OutsourceGroup;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

/***
 *created by 
 *on 2020/8/29
 */
public class In_OutSourceNewFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult, BRecyclerAdapter.OnItemClickListener {

    private static In_OutSourceNewFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static In_OutSourceNewFragment newInstance() {
        if (fragment == null) ;
        fragment = new In_OutSourceNewFragment();
        return fragment;
    }
    private RecycleAdapter mAdapter;
    private ArrayList<String> noList;
    private ArrayList<OutsourceGroup> myList;
    private ArrayList<Outsource> dataList;
    private ArrayList<String> epcs;

    private ScanResultHandler scanResultHandler;

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
        myList=new ArrayList<>();
        dataList=new ArrayList<>();
        epcs=new ArrayList<>();
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
    }
    private void clearData(){

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

    }

    @Override
    public void onResume() {
        super.onResume();

    }
    private void download(){
        ArrayList<String> list=new ArrayList<>();
        for (String no:noList){
            if (!TextUtils.isEmpty(no))
                list.add(no);
        }
        String json= JSONObject.toJSONString(list);
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
                        if (jsonObject.getInteger("code")==200){

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
        ButterKnife.unbind(this);
        disRFID();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
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
        JSONObject jsonObject = new JSONObject();

        final String json = jsonObject.toJSONString();
        try {
            AppLog.write(getActivity(), "pushBackRepairInfoToERP", "userId:" + User.newInstance().getId() + json, AppLog.TYPE_INFO);
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/back_repair/pushBackRepairInfoToERP", new OkHttpClientManager.ResultCallback<BaseReturnObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                }

                @Override
                public void onResponse(BaseReturnObject response) {
                    uploadDialog.openView();
                    hideUploadDialog();
                    try {
                        AppLog.write(getActivity(), "pushBackRepairInfoToERP", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                        scanResultHandler.removeCallbacks(r);
                        if (response.getStatus() == 1) {
                            showToast("上传成功");
                            clearData();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast("上传失败");
                            showConfirmDialog("上传失败，" + response.getMessage());
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
    @Override
    public void onItemClick(View view, Object data, int position) {

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
                CheckBox checkBox=holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setStutas(b);
                    }
                });
                checkBox.setChecked(item.isStutas());
                holder.setText(R.id.text1,"送货单号："+item.getDeliverNo());
                holder.setText(R.id.item1,item.getCust_po());
                holder.setText(R.id.item2,item.getProduct_no());
                holder.setText(R.id.item3,item.getProduct_name());
                holder.setText(R.id.item4,item.getVat_no());
                holder.setText(R.id.item5,item.getColor_name());
                holder.setText(R.id.item6,item.getSel_color());
                holder.setText(R.id.item7,item.getColor_code());
                holder.setText(R.id.item8,item.getWidth_side());
                holder.setText(R.id.item9,String.valueOf(item.getAllWeightF()));
                holder.setText(R.id.item10,String.valueOf(item.getAllWeight()));
                holder.setText(R.id.item11,String.valueOf(item.getAllScanWeight()));
                holder.setText(R.id.item12,String.valueOf(item.getWeight_zg()));
                holder.setText(R.id.item13,String.valueOf(item.getWeight_kj()));
                holder.setText(R.id.item14,String.valueOf(item.getScanCount()));
                holder.setText(R.id.item15,String.valueOf(item.getOutCount()));
            }
        }
    }
}