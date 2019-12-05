package com.example.mumu.warehousecheckcar.fragment.cut;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Input;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.view.MsgView;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

/***
 *created by 剪布出库
 *on 2019/11/21
 */
public class CutClothOperateFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult {


    public final static String LIST_KEY = "NOLIST";
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    private ArrayList<BarCode> myList;
    private ScanResultHandler scanResultHandler;
    private Map<String, CutClothFlag> dataKey;
    private ArrayList<String> noList;
    private ArrayList<String> epcList;
    private ArrayList<String> keyList;

    public static CutClothOperateFragment newInstance() {
        return new CutClothOperateFragment();
    }

    private RecycleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_operate_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList();
        noList = new ArrayList<>();
        epcList = new ArrayList<>();
        dataKey = new HashMap<>();
        keyList = new ArrayList<>();
        noList = getArguments().getStringArrayList(LIST_KEY);
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_operate_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        text2.setText(String.valueOf(noList.size()));
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
    }

    private void clearData() {
        myList.clear();
        epcList.clear();
        dataKey.clear();
        keyList.clear();
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
    public void rfidResult(final String epc) {
        if (epc.startsWith("3035A537") && !epcList.contains(epc)) {
            if (!epcList.contains(epc)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("epc", epc);
                final String json = jsonObject.toJSONString();
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                showToast("获取库位信息失败");
                            }
                        }

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            try {
                                List<Input> arry = jsonArray.toJavaList(Input.class);
                                if (arry != null && arry.size() > 0) {
                                    Input response = arry.get(0);
                                    if (response != null) {
                                        String key = response.getVatNo();
                                        for (CutClothFlag cutClothFlag : dataKey.values()) {
                                            if (!cutClothFlag.isFlag() && cutClothFlag.getVat_no().equals(key)) {
                                                epcList.add(epc);
                                                cutClothFlag.setFlag(true);
                                                cutClothFlag.setEpc(epc);
                                                cutClothFlag.setFabRool(response.getFabRool());
                                                cutClothFlag.setWeight(response.getWeight());
                                                mAdapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, json);
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        downLoadData();
    }

    private void downLoadData() {
        for (String no : noList) {
            JSONObject object = new JSONObject();
            object.put("applyNo", no);
            final String json = object.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/getInfoByApplyNo", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONArray json) {
                        try {
                            List<BarCode> response = json.toJavaList(BarCode.class);
                            if (response != null && response.size() != 0) {
                                for (BarCode barCode : response) {
                                    String key = barCode.getOut_no() + barCode.getOutp_id();
                                    if (!dataKey.containsKey(key)) {
                                        dataKey.put(key, new CutClothFlag(barCode.getOutp_id(), barCode.getVat_no()));
                                        myList.add(barCode);
                                        keyList.add(key);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, json);
            } catch (IOException e) {
                e.printStackTrace();
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
        ButterKnife.unbind(this);
        disRFID();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                clearData();
                downLoadData();
                break;
            case R.id.button2:
                showUploadDialog("是否确认出库？");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, List<CutClothFlag>> mapList = new HashMap<>();
                        Iterator<Map.Entry<String, CutClothFlag>> entries = dataKey.entrySet().iterator();
                        while (entries.hasNext()) {
                            Map.Entry<String, CutClothFlag> entry = entries.next();
                            String key = entry.getKey();
                            CutClothFlag value = entry.getValue();
                            if (keyList.contains(key)) {
                                if (mapList.containsKey(key.replaceAll(value.getProduct_applypid(), ""))) {
                                    mapList.get(key.replaceAll(value.getProduct_applypid(), "")).add(value);
                                } else {
                                    List<CutClothFlag> list = new ArrayList<>();
                                    list.add(value);
                                    mapList.put(key.replaceAll(value.getProduct_applypid(), ""), list);
                                }
                            }
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", User.newInstance().getId());
                        jsonObject.put("data", mapList);
                        final String json = jsonObject.toJSONString();
                        try {
                            AppLog.write(getActivity(), "cutOutapply", json, AppLog.TYPE_INFO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/pushCutOut", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (e instanceof ConnectException)
                                        showConfirmDialog("链接超时");
                                    if (App.LOGCAT_SWITCH) {
                                        Toast.makeText(getActivity(), "出库失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        try {
                                            AppLog.write(getActivity(), "cutOutapply", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        uploadDialog.openView();
                                        hideUploadDialog();
                                        scanResultHandler.removeCallbacks(r);
                                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                                            Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                            clearData();
                                            mAdapter.notifyDataSetChanged();
//                                            blinkDialog2(true);
                                        } else if (baseReturn != null && baseReturn.getStatus() == 0) {
                                            Toast.makeText(getActivity(), "出库失败", Toast.LENGTH_LONG).show();
                                            showConfirmDialog(baseReturn.getData() + "出库失败，请在ERP出库");
                                            Sound.faillarm();
                                        } else {
                                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                            showConfirmDialog("上传失败");
                                            Sound.faillarm();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
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

    class CutClothFlag {
        private boolean flag;
        private String vat_no;
        private double cut_weight;
        private String epc;
        private String fabRool;
        private String product_applypid;
        private double weight;
        public CutClothFlag(String product_applypid, String vat_no) {
            this.product_applypid = product_applypid;
            this.vat_no = vat_no;
            flag = false;
        }

        public void clear() {
            flag = false;
            cut_weight = 0;
            epc = "";
            fabRool = "";
            weight = 0;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public String getProduct_applypid() {
            return product_applypid;
        }

        public void setProduct_applypid(String product_applypid) {
            this.product_applypid = product_applypid;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getVat_no() {
            return vat_no;
        }

        public void setVat_no(String vat_no) {
            this.vat_no = vat_no;
        }

        public double getCut_weight() {
            return cut_weight;
        }

        public void setCut_weight(double cut_weight) {
            this.cut_weight = cut_weight;
        }

        public String getEpc() {
            return epc;
        }

        public void setEpc(String epc) {
            this.epc = epc;
        }

        public String getFabRool() {
            return fabRool;
        }

        public void setFabRool(String fabRool) {
            this.fabRool = fabRool;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<BarCode> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public RecycleAdapter(RecyclerView v, Collection<BarCode> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        @Override
        public void convert(RecyclerHolder holder, final BarCode item, final int position) {
            if (item != null) {
                final String key = item.getOut_no() + item.getOutp_id();
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            if (!keyList.contains(key))
                                keyList.add(key);
                        } else {
                            if (keyList.contains(key))
                                keyList.remove(key);
                        }
                    }
                });
                final EditText editText = holder.getView(R.id.edittext1);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (dataKey.get(key).isFlag()) {
                            try {
                                String weight = charSequence.toString();
                                weight = weight.replaceAll(" ", "");
                                if (!TextUtils.isEmpty(weight)) {
                                    double a = Double.parseDouble(weight);
                                    dataKey.get(key).setCut_weight(a);
                                } else {
                                    dataKey.get(key).setCut_weight(0);
                                }
                            } catch (Exception e) {
                                editText.setText("0");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                final Button button = holder.getView(R.id.button);
                MsgView msgView1 = holder.getView(R.id.msgText1);
                MsgView msgView2 = holder.getView(R.id.msgText2);
                MsgView msgView3 = holder.getView(R.id.msgText3);
                MsgView msgView4 = holder.getView(R.id.msgText4);
                LinearLayout linearLayout = holder.getView(R.id.layout2);
                if (dataKey.get(key).isFlag()) {
                    button.setVisibility(View.VISIBLE);
                    editText.setEnabled(true);
                    holder.setText(R.id.item1, dataKey.get(key).getFabRool());
                    holder.setText(R.id.item2, String.valueOf(dataKey.get(key).getWeight()));
                    editText.setText(String.valueOf(dataKey.get(key).getCut_weight()));
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                } else {
                    button.setVisibility(View.GONE);
                    editText.setEnabled(false);
                    holder.setText(R.id.item1, "");
                    holder.setText(R.id.item2, "");
                    editText.setText("");
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                }
                checkBox.setChecked(keyList.contains(key));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dataKey.get(key).isFlag()) {
                            dataKey.get(key).clear();
                            notifyDataSetChanged();
                        }
                    }
                });
                holder.setText(R.id.text1, "申请单号：" + item.getOut_no());
                msgView1.setMsgText(item.getOutp_id());
                msgView2.setMsgText(item.getVat_no());
                msgView3.setMsgText(String.valueOf(item.getQty_kg()));
                msgView4.setMsgText(String.valueOf(item.getYard_out()));
            }
        }
    }
}
