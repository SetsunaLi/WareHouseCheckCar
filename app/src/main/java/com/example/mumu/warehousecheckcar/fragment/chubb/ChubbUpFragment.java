package com.example.mumu.warehousecheckcar.fragment.chubb;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.chubb.ChubbUp;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
import com.example.mumu.warehousecheckcar.listener.FragmentCallBackListener;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.CompareUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChubbUpFragment extends BaseFragment implements UHFCallbackLiatener, FragmentCallBackListener, OnRfidResult {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    private static ChubbUpFragment fragment;

    public static ChubbUpFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbUpFragment();
        return fragment;
    }

    private final String TAG = "ChubbUpFragment";
    private RecycleAdapter mAdapter;
    private List<ChubbUp> myList;
    /**
     * 匹配逻辑
     * //     * key：response.getVat_no()+response.getProduct_no()+response.getSelNo()+response.getColor()
     * key:epc
     * value：index
     */
    private Map<String, Integer> strIndex;
    /***布号+缸号+布票号*/
    private List<String> dataKEY;
    private List<String> dataEPC;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubbup_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("查布上架");
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new ChubbUp());
        dataKEY = new ArrayList<>();
        strIndex = new HashMap<>();
        dataEPC = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubbup_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
        ComeBack.getInstance().setCallbackLiatener(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new ChubbUp());
        }
        if (dataKEY != null)
            dataKEY.clear();
        if (strIndex != null)
            strIndex.clear();
        if (dataEPC != null)
            dataEPC.clear();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.chubbup_item, null);
        mAdapter.setHeader(view);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void upLoad(boolean flag) {
        if (flag) {
            initRFID();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Iterator<ChubbUp> iter = myList.iterator();
                    while (iter.hasNext()) {
                        ChubbUp chubbUp = iter.next();
                        if (dataKEY.contains(chubbUp.getEpc()))
                            iter.remove();
                    }
                    dataKEY.clear();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x06:
                    initRFID();
                    break;
            }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        clearData();
        myList.clear();
        disRFID();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                disRFID();
                ArrayList<ChubbUp> dataList = new ArrayList<>();
                for (ChubbUp data : myList) {
                    if (dataKEY.contains(data.getEpc())) {
                        dataList.add(data);
                    }
                }
                Fragment fragment = ChubbUpCarrierFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("dataList", dataList);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
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
    public void comeBackListener() {

    }

    @Override
    public void ubLoad(boolean flag) {
        if (flag) {
            upLoad(flag);
        }
    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (epc.startsWith("3035A537") && !dataEPC.contains(epc)) {
            JSONObject obj = new JSONObject();
            obj.put("epc", epc);
            final String json = obj.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/getClothInCheckByEpc", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                            if (object.getJSONObject("data") != null) {
                                final JSONObject obj = object.getJSONObject("data");
                                ChubbUp value = obj.toJavaObject(ChubbUp.class);
                                if (value != null) {
                                    if (value.getEpc() != null && !value.getEpc().equals("") && !dataEPC.contains(value.getEpc())) {
                                        dataEPC.add(value.getEpc());
                                        myList.add(value);
                                        Collections.sort(myList, new Comparator<ChubbUp>() {
                                            @Override
                                            public int compare(ChubbUp obj1, ChubbUp obj2) {
                                                String aLocation = obj1.getBas_location();
                                                String bLocation = obj2.getBas_location();
                                                String aPallet = obj1.getBas_pallet();
                                                String bPallet = obj2.getBas_pallet();
                                                String aVat = obj1.getVatNo();
                                                String bVat = obj2.getVatNo();
                                                if ((obj1.getVatNo() == null || obj1.getVatNo().equals("")) && (obj1.getFabRool() == null || obj1.getFabRool().equals(""))
                                                        && (obj1.getProduct_no() == null || obj1.getProduct_no().equals("")) && (obj1.getSelNo() == null || obj1.getSelNo().equals(""))
                                                        && (obj1.getEpc() == null || obj1.getEpc().equals("")) && (obj1.getColor() == null || obj1.getColor().equals("")))
                                                    return -1;
                                                if ((obj2.getVatNo() == null || obj2.getVatNo().equals("")) && (obj2.getFabRool() == null || obj2.getFabRool().equals(""))
                                                        && (obj2.getProduct_no() == null || obj2.getProduct_no().equals("")) && (obj2.getSelNo() == null || obj2.getSelNo().equals(""))
                                                        && (obj2.getEpc() == null || obj2.getEpc().equals("")) && (obj2.getColor() == null || obj2.getColor().equals("")))
                                                    return 1;

                                                if (aLocation.equals(bLocation)) {
                                                    if (aPallet.equals(bPallet)) {
                                                        if (aVat.equals(bVat)) {
                                                            return 0;
                                                        } else {
                                                            if (CompareUtil.isMoreThan(aVat, bVat)) {
                                                                return -1;
                                                            } else {
                                                                return 1;
                                                            }
                                                        }
                                                    } else {
                                                        if (CompareUtil.isMoreThan(aPallet, bPallet)) {
                                                            return -1;
                                                        } else {
                                                            return 1;
                                                        }
                                                    }
                                                } else {
                                                    if (CompareUtil.isMoreThan(aLocation, bLocation)) {
                                                        return -1;
                                                    } else {
                                                        return 1;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                                text1.setText(String.valueOf(dataEPC.size()));
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {
                Log.i(TAG, "");
            }
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ChubbUp> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ChubbUp> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public void convert(RecyclerHolder holder, final ChubbUp item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (ChubbUp i : myList) {
                                    if (!TextUtils.isEmpty(i.getVatNo()) || !TextUtils.isEmpty(i.getProduct_no()) || !TextUtils.isEmpty(i.getSelNo())
                                            || !TextUtils.isEmpty(i.getEpc()))
                                        dataKEY.add(i.getEpc());
                                }
                            } else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getEpc()))
                                    dataKEY.add(item.getEpc());
                            } else {
                                if (dataKEY.contains(item.getEpc()))
                                    dataKEY.remove(item.getEpc());
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (TextUtils.isEmpty(item.getVatNo()) && TextUtils.isEmpty(item.getProduct_no()) && TextUtils.isEmpty(item.getSelNo())) {
                        cb.setChecked(false);
                        if (cb.getVisibility() != View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    } else {
                        if (cb.getVisibility() != View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKEY.contains(item.getEpc()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    if (item.getBas_location() != null) {
                        String location = item.getBas_location().replaceAll("临时入库区", "").replaceAll("备货区", "");
                        holder.setText(R.id.item1, location);
                    } else
                        holder.setText(R.id.item1, "");
                    holder.setText(R.id.item2, item.getBas_pallet());
                    holder.setText(R.id.item3, item.getVatNo());
                    holder.setText(R.id.item4, item.getProduct_no());
                    holder.setText(R.id.item5, item.getFabRool());
                    holder.setText(R.id.item6, item.getSelNo());
                    holder.setText(R.id.item7, item.getColor());
                }

            }
        }
    }
}
