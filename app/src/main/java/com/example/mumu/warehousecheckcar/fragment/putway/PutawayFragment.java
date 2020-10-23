package com.example.mumu.warehousecheckcar.fragment.putway;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.in.Input;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.INPUT_DETAIL_LIST;
import static com.example.mumu.warehousecheckcar.application.App.TIME;

/**
 * Created by mumu on 2019/1/8.
 */

public class PutawayFragment extends BaseFragment implements UHFCallbackLiatener, BasePullUpRecyclerAdapter.OnItemClickListener, OnRfidResult {
    private static PutawayFragment fragment;
    private final String TAG = "PutawayFragment";
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;

    public static PutawayFragment newInstance() {
        if (fragment == null) ;
        fragment = new PutawayFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Input> myList;
    /**
     * 匹配机制应该是item分组字段
     */
    private Map<String, Integer> keyValue;
    private List<Input> dataList;
    private List<String> dataKey;
    private List<String> epcList;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.putaway_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.putaway_item, null);
        mAdapter.setHeader(view);
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Input());
        }
        if (keyValue != null)
            keyValue.clear();
        if (dataKey != null)
            dataKey.clear();
        if (dataList != null)
            dataList.clear();
        if (epcList != null)
            epcList.clear();
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Input());
        dataKey = new ArrayList<>();
        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
        keyValue = new HashMap<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.putaway_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        text1.setText("0");
        if (App.CARRIER != null) {
            if (!TextUtils.isEmpty(App.CARRIER.getLocationNo()))
                text2.setText(App.CARRIER.getLocationNo());
            if (!TextUtils.isEmpty(App.CARRIER.getTrayNo()))
                text3.setText(App.CARRIER.getTrayNo());
        }
    }

    @Override
    protected void addListener() {
        initRFID();
        scanResultHandler = new ScanResultHandler(this);
        mAdapter.setOnItemClickListener(this);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disRFID();
        clearData();
        myList.clear();
        INPUT_DETAIL_LIST.clear();
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

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.select(position);
            mAdapter.notifyDataSetChanged();
            Input icd = myList.get(position);
            String key = icd.getVatNo();
            INPUT_DETAIL_LIST.clear();
            for (Input obj : dataList) {
                if (obj != null && obj.getVatNo() != null && obj.getVatNo().equals(key)) {
                    INPUT_DETAIL_LIST.add(obj);
                }
            }
            Fragment fragment = PutawayDetailFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                text1.setText(String.valueOf(epcList.size()));
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否确认上架");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Input> jsocList = new ArrayList<>();
                        for (Input obj : dataList) {
                            if (!TextUtils.isEmpty(obj.getVatNo()) && dataKey.contains(obj.getVatNo())) {
                                obj.setCarrier(App.CARRIER);
                                obj.setDevice(App.DEVICE_NO);
                                jsocList.add(obj);
                            }
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", User.newInstance().getId());
                        jsonObject.put("data", jsocList);
                        final String json = JSON.toJSONString(jsonObject);
                        try {
                            LogUtil.i(getResources().getString(R.string.log_putway), json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/input/pushInput.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (e instanceof ConnectException)
                                        showConfirmDialog("链接超时");
                                    try {
                                        LogUtil.e(getResources().getString(R.string.log_putaway_result), e.getMessage(), e.getCause());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        LogUtil.i(getResources().getString(R.string.log_putaway_result), "userId:" + User.newInstance().getId() + response.toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        uploadDialog.openView();
                                        hideUploadDialog();
                                        scanResultHandler.removeCallbacks(r);
                                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                                            showToast("上传成功");
                                            clearData();
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            showToast("上传失败");
                                            showConfirmDialog("上传失败");
                                            Sound.faillarm();
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }, json);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });
                break;
        }
    }

    @Override
    public void rfidResult(String epc) {
        final String EPC = epc.replaceAll(" ", "");
        if (!epcList.contains(EPC)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", EPC);
            final String json = jsonObject.toJSONString();
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
                            List<Input> arry = jsonArray.toJavaList(Input.class);
                            if (arry != null && arry.size() > 0 && !TextUtils.isEmpty(arry.get(0).getVatNo())) {
                                Input response = arry.get(0);
                                if (response != null) {
                                    if (response.getEpc() != null && !epcList.contains(response.getEpc())) {
                                        epcList.add(EPC);
                                        dataList.add(response);
                                        String key = response.getVatNo();
                                        if (!keyValue.containsKey(key)) {//当前没有
                                            response.setCount(1);
                                            response.setWeightall(response.getWeight());
                                            myList.add(response);
                                            keyValue.put(key, myList.size() - 1);
                                            dataKey.add(response.getVatNo());
                                        } else {
                                            int index = keyValue.get(key);
                                            myList.get(index).addCount();
                                            myList.get(index).setWeightall(ArithUtil.add(myList.get(index).getWeightall(), response.getWeight()));
                                        }
                                    }
                                    text1.setText(String.valueOf(epcList.size()));
                                    mAdapter.notifyDataSetChanged();
                                }
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

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Input> {
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

        public RecycleAdapter(RecyclerView v, Collection<Input> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final Input item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Input i : myList) {
                                    if (!TextUtils.isEmpty(i.getVatNo()) && !TextUtils.isEmpty(i.getProduct_no()) && !TextUtils.isEmpty(i.getSelNo()))
                                        dataKey.add(i.getVatNo());
                                }
                            } else {
                                dataKey.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKey.contains(item.getVatNo()))
                                    dataKey.add(item.getVatNo());
                            } else {
                                if (dataKey.contains(item.getVatNo()))
                                    dataKey.remove(item.getVatNo());
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
                        if (dataKey.contains(item.getVatNo()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.position == position) {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getProduct_no());
                    holder.setText(R.id.item2, item.getSelNo());
                    holder.setText(R.id.item3, item.getColor());
                    holder.setText(R.id.item4, item.getVatNo());
                    holder.setText(R.id.item5, String.valueOf(item.getCount()));
                    holder.setText(R.id.item6, String.valueOf(item.getWeightall()));
                }
            }
        }
    }
}
