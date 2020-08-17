package com.example.mumu.warehousecheckcar.fragment.outsource_in;

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
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.Outsource;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
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
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 
 *on 2020/8/10
 * 委外入库第三期，修改需求截图为证2020.8.10
 */
public class In_OutSourceFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult {
    private static In_OutSourceFragment fragment;
    @Bind(R.id.item1)
    TextView item1;
    @Bind(R.id.item2)
    TextView item2;
    @Bind(R.id.item3)
    TextView item3;
    @Bind(R.id.item4)
    TextView item4;
    @Bind(R.id.item5)
    TextView item5;
    @Bind(R.id.item6)
    TextView item6;
    @Bind(R.id.item7)
    TextView item7;
    @Bind(R.id.item8)
    TextView item8;
    @Bind(R.id.item9)
    TextView item9;
    @Bind(R.id.item10)
    TextView item10;
    @Bind(R.id.item11)
    TextView item11;
    @Bind(R.id.item12)
    TextView item12;
    @Bind(R.id.item13)
    TextView item13;
    @Bind(R.id.item14)
    TextView item14;
    @Bind(R.id.checkbox1)
    CheckBox checkbox1;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    private ScanResultHandler scanResultHandler;
    private RecycleAdapter mAdapter;
    private ArrayList<Outsource> myList;
    private ArrayList<String> epcs;
    private ArrayList<String> epcCheck;
    private String cust_po = "", sup_name = "", color_code = "", deliverNo = "", product_no = "",
            sel_color = "", vat_no = "", color_name = "", width = "", gram = "";
    private double weight_zg, weight_kj, weight;
    private int count;

    public static In_OutSourceFragment newInstance() {
        if (fragment == null) ;
        fragment = new In_OutSourceFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_click20));
        View view = inflater.inflate(R.layout.in_outsource_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        epcs = new ArrayList<>();
        epcCheck = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_outsource_item);
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        initRFID();
        scanResultHandler = new ScanResultHandler(this);
        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                epcCheck.clear();
                if (b)
                    for (Outsource outsource : myList)
                        epcCheck.add(outsource.getEpc());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void clearData() {
        myList.clear();
        epcs.clear();
        epcCheck.clear();
        cust_po = "";
        sup_name = "";
        color_code = "";
        deliverNo = "";
        product_no = "";
        sel_color = "";
        vat_no = "";
        color_name = "";
        width = "";
        gram = "";
        weight_zg = 0.0;
        weight_kj = 0.0;
        weight = 0.0;
        count = 0;
    }

    private void setData(Outsource outsource) {
        this.cust_po = outsource.getCust_po();
        this.sup_name = outsource.getSup_name();
        this.color_code = outsource.getColor_code();
        this.deliverNo = outsource.getDeliverNo();
        this.product_no = outsource.getProduct_no();
        this.sel_color = outsource.getSel_color();
        this.vat_no = outsource.getVat_no();
        this.color_name = outsource.getColor_name();
        this.width = outsource.getWidth();
        this.gram = outsource.getGram();
        this.weight_zg = outsource.getWeight_zg();
        this.weight_kj = outsource.getWeight_kj();
        this.weight = outsource.getWeight();
        this.count = myList.size();
    }

    private void clearView() {
        item1.setText(TextUtils.isEmpty(cust_po) ? "" : cust_po);
        item2.setText(TextUtils.isEmpty(sup_name) ? "" : sup_name);
        item3.setText(TextUtils.isEmpty(color_code) ? "" : color_code);
        item4.setText(TextUtils.isEmpty(deliverNo) ? "" : deliverNo);
        item5.setText(TextUtils.isEmpty(product_no) ? "" : product_no);
        item6.setText(TextUtils.isEmpty(sel_color) ? "" : sel_color);
        item7.setText(TextUtils.isEmpty(vat_no) ? "" : vat_no);
        item8.setText(TextUtils.isEmpty(color_name) ? "" : color_name);
        item9.setText(TextUtils.isEmpty(width) ? "" : width);
        item10.setText(TextUtils.isEmpty(gram) ? "" : gram);
        item11.setText(String.valueOf(weight_zg));
        item12.setText(String.valueOf(weight_kj));
        item13.setText(String.valueOf(weight));
        item14.setText(String.valueOf(count));
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
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
    public void rfidResult(final String epc) {
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
                                if (myList.size() == 0) {
                                    myList.add(outsource);
                                    setData(outsource);
                                } else {
                                    if (epcs.contains(outsource.getEpc()))
                                        return;
                                    String key = outsource.getCust_po() + outsource.getProduct_no() + outsource.getVat_no();
                                    if (key.equals(cust_po + product_no + vat_no)) {
                                        myList.add(outsource);
                                        count = myList.size();
                                        weight = ArithUtil.add(weight, outsource.getWeight());
                                    }
                                }
                                epcs.add(outsource.getEpc());
                                epcCheck.add(outsource.getEpc());
                            }
                            clearView();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, json);
            } catch (IOException e) {
            }
        }
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                clearView();
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
        for (Outsource outsource : myList) {
            if (epcCheck.contains(outsource.getEpc()))
                list.add(outsource);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        jsonObject.put("data", list);
        final String json = jsonObject.toJSONString();
        try {
            AppLog.write(getActivity(), "inventIn", "userId:" + User.newInstance().getId() + json, AppLog.TYPE_INFO);
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cc_print_tag_line/new_inv_sum_trans", new OkHttpClientManager.ResultCallback<BaseReturn>() {
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
        jsonObject2.put("epcs", epcCheck);
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
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Outsource> {
        public RecycleAdapter(RecyclerView v, Collection<Outsource> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        @Override
        public void convert(RecyclerHolder holder, final Outsource item, final int position) {
            CheckBox checkBox = holder.getView(R.id.checkbox1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (!epcCheck.contains(item.getEpc()))
                            epcCheck.add(item.getEpc());
                    } else {
                        if (epcCheck.contains(item.getEpc()))
                            epcCheck.remove(item.getEpc());
                    }

                }
            });
            final EditText editText = holder.getView(R.id.edit1);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String weight = editable.toString();
                        weight = weight.replaceAll(" ", "");
                        if (!TextUtils.isEmpty(weight)) {
                            double a = Double.parseDouble(weight);
                            item.setWeight(a);
                        } else {
                            item.setWeight(0.0);
                        }
                    } catch (Exception e) {
                        item.setWeight(0.0);
                        editText.setText("0");
                    }
                    weight = 0;
                    for (Outsource outsource : myList) {
                        weight = ArithUtil.add(weight, outsource.getWeight());
                    }
                    item13.setText(String.valueOf(weight));
                }
            });
            checkBox.setChecked(epcCheck.contains(item.getEpc()));
            holder.setText(R.id.item1, item.getFab_roll());
            holder.setText(R.id.edit1, String.valueOf(item.getWeight()));
        }
    }
}
