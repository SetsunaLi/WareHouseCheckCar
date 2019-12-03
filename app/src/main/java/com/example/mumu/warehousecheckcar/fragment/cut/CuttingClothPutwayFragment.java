package com.example.mumu.warehousecheckcar.fragment.cut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnRfidResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.Cut;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class CuttingClothPutwayFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener, OnRfidResult {
    private final String TAG = "CuttingClothPutway";

    private static CuttingClothPutwayFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;


    public static CuttingClothPutwayFragment newInstance() {
        if (fragment == null) ;
        fragment = new CuttingClothPutwayFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Cut> myList;
    private List<String> dataKEY;
    private List<String> epcList;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Cut());
        dataKEY = new ArrayList<>();
        epcList = new ArrayList<>();
        if (App.CARRIER != null) {
            if (App.CARRIER.getLocationNo() != null)
                text2.setText(App.CARRIER.getLocationNo() + "");
        }
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_item);
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

    private void clear() {
        if (myList != null) {
            myList.clear();
            myList.add(new Cut());
        }
        if (dataKEY != null)
            dataKEY.clear();
        if (epcList != null)
            epcList.clear();
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
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cut_cloth_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
        clear();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clear();
                text1.setText(String.valueOf(epcList.size()));
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                if (dataKEY != null && dataKEY.size() > 0) {
                    showUploadDialog("是否确认上架");
                    setUploadYesClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<Cut> jsocList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userId", User.newInstance().getId());
                            for (Cut obj : myList) {
                                if (obj.getEpc() != null && dataKEY.contains(obj.getEpc())) {
                                    Carrier carrier = new Carrier(App.CARRIER.getTrayNo(), App.CARRIER.getLocationNo());
                                    obj.setCarrier(carrier);
                                    jsocList.add(obj);
                                }
                            }
                            jsonObject.put("data", jsocList);
                            final String json = JSON.toJSONString(jsonObject);
                            try {
                                AppLog.write(getActivity(), "ccputway", json, AppLog.TYPE_INFO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCutCloth.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (e instanceof ConnectException)
                                            showConfirmDialog("链接超时");
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "postInventory;" + e.getMessage());
                                            showToast("上传信息失败");
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            AppLog.write(getActivity(), "ccputway", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            uploadDialog.openView();
                                            hideUploadDialog();
                                            scanResultHandler.removeCallbacks(r);
                                            BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                            if (baseReturn != null && baseReturn.getStatus() == 1) {
                                                onViewClicked(button1);
                                                showToast("上传成功");
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
                } else {
                    showToast("请选择要上传的数据");
                }
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
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (epc.startsWith("3035A537") && !epcList.contains(epc)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", epc);
            final String json = jsonObject.toJSONString();
            try {
//                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            showToast("获取库位信息失败");
                        }
                    }

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        if (jsonArray != null && jsonArray.size() > 0) {
                            Cut cut = jsonArray.getObject(0, Cut.class);
                            if (cut != null && !epcList.contains(cut.getEpc())) {
                                epcList.add(cut.getEpc());
                                myList.add(cut);
                            }
                            text1.setText(String.valueOf(epcList.size()));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, json);
            } catch (IOException e) {
                Log.i(TAG, "");
            } catch (Exception e) {

            }
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cut> {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void setHeader(View header) {
            super.setHeader(header);
        }

        public RecycleAdapter(RecyclerView v, Collection<Cut> datas, int itemLayoutId) {
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
        public void convert(RecyclerHolder holder, final Cut item, final int position) {
            if (item != null) {
                final CheckBox cb = holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Cut i : myList) {
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
                    if (TextUtils.isEmpty(item.getVatNo()) && TextUtils.isEmpty(item.getColor()) && TextUtils.isEmpty(item.getProduct_no())) {
                        cb.setChecked(false);
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                    } else {
                        if (!cb.isEnabled())
                            cb.setEnabled(true);
                        if (dataKEY.contains(item.getEpc()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    LinearLayout ll = holder.getView(R.id.layout1);
                    if (item.getBlank_add() == 0 || item.getWeight_papertube() == 0)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else if (index == position)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getProduct_no());
                    holder.setText(R.id.item2, item.getColor());
                    holder.setText(R.id.item3, item.getVatNo());
                    holder.setText(R.id.item4, item.getFabRool());
                    holder.setText(R.id.item5, item.getWeight_in() + "KG");
                    final EditText editText1 = holder.getView(R.id.edittext1);
                    editText1.setText(String.valueOf(item.getWeight()));
                    editText1.setEnabled(true);
                    editText1.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                String weight = charSequence.toString();
                                weight = weight.replaceAll(" ", "");
                                if (weight != null && !weight.equals("")) {
                                    double a = Double.parseDouble(weight);
                                    for (Cut cut : myList) {
                                        if (item.getEpc().equals(cut.getEpc())) {
                                            cut.setWeight(a);
                                            break;

                                        }
                                    }
                                }
                            } catch (Exception e) {
                                editText1.setText("0");
                                for (Cut cut : myList) {
                                    if (item.getEpc().equals(cut.getEpc())) {
                                        cut.setWeight(0);
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else {
                    EditText editText1 = (EditText) holder.getView(R.id.edittext1);
                    editText1.setEnabled(false);
                }
            }
        }
    }
}
