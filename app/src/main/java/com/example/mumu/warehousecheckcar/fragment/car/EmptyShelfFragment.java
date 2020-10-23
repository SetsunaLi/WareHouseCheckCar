package com.example.mumu.warehousecheckcar.fragment.car;

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
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;


public class EmptyShelfFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener, OnRfidResult {
    private final String TAG = "EmptyShelfFragment";
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private static EmptyShelfFragment fragment;

    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;

    public static EmptyShelfFragment newInstance() {
        if (fragment == null) ;
        fragment = new EmptyShelfFragment();
        return fragment;
    }

    private List<Carrier> myList;
    private RecycleAdapter mAdapter;
    private List<String> dataKEY;
    private List<String> dataEpc;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_car_tuo));
        View view = inflater.inflate(R.layout.empty_shelf_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Carrier());
        dataKEY = new ArrayList<>();
        dataEpc = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.empty_shelf_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Carrier());
        }
        if (dataKEY != null)
            dataKEY.clear();
        if (dataEpc != null)
            dataEpc.clear();

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

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.empty_shelf_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disRFID();

    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否空托盘整理");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject jsonObject = new JSONObject();
                        User user = User.newInstance();
                        jsonObject.put("userId", user.getId());
                        jsonObject.put("pallets", dataKEY);
                        final String json = jsonObject.toString();
                        try {
                            LogUtil.i(getResources().getString(R.string.log_emp_shelf), json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/static/arrangePallets.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (e instanceof ConnectException)
                                        showConfirmDialog("链接超时");
                                    try {
                                        LogUtil.e(getResources().getString(R.string.log_emp_shelf_result), e.getMessage(), e.getCause());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        LogUtil.i(getResources().getString(R.string.log_emp_shelf_result), "userId:" + User.newInstance().getId() + response.toString());
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
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
        if (!dataEpc.contains(epc)) {
            dataEpc.add(epc);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", epc);
            final String json = jsonObject.toString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getCarrier.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getCarrier;" + e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            Carrier value = response.toJavaObject(Carrier.class);
                            myList.add(value);
                        }
                        text2.setText(String.valueOf(myList.size() - 1));
                        mAdapter.notifyDataSetChanged();
                    }
                }, json);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    class RecycleAdapter extends BasePullUpRecyclerAdapter<Carrier> {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Carrier> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        private int index = -255;

        public void select(int index) {
            if (this.index == index)
                this.index = -255;
            else
                this.index = index;
        }

        @Override
        public void convert(RecyclerHolder holder, final Carrier item, final int position) {
            if (item != null) {
                final LinearLayout ll = holder.getView(R.id.layout1);
                final CheckBox cb = holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Carrier i : myList) {
                                    if (i.getTrayNo() != null) {
                                        dataKEY.add(i.getTrayEPC());
                                    }
                                }
                            } else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getTrayEPC()))
                                    dataKEY.add(item.getTrayEPC());
                            } else {
                                if (dataKEY.contains(item.getTrayEPC()))
                                    dataKEY.remove(item.getTrayEPC());
                            }
                        }
                    }
                });

                if (position != 0) {
                    if (TextUtils.isEmpty(item.getTrayNo()) && TextUtils.isEmpty(item.getTrayEPC())) {
                        cb.setChecked(false);
                        if (cb.getVisibility() != View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    } else {
                        if (cb.getVisibility() != View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKEY.contains(item.getTrayEPC())) {
                            cb.setChecked(true);
                            ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                        } else {
                            cb.setChecked(false);
                            ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                        }
                    }

                    if (index == position) {
                        System.out.println(dataKEY);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));

                    holder.setText(R.id.item1, item.getTrayNo());
                    holder.setText(R.id.item2, item.getTrayEPC());
                    holder.setText(R.id.item3, item.getLocationNo());
                    holder.setText(R.id.item4, item.getLocationEPC());
                }
            }
        }
    }
}
