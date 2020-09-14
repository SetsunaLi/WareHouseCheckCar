package com.example.mumu.warehousecheckcar.fragment.in;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.example.mumu.warehousecheckcar.entity.in.InAssistCloth;
import com.example.mumu.warehousecheckcar.entity.in.Input;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

/***
 *created by ${mumu}
 *on 2019/9/19
 */
public class InAssistFragment extends BaseFragment implements UHFCallbackLiatener, BRecyclerAdapter.OnItemClickListener, OnRfidResult {
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;

    public static InAssistFragment newInstance() {
        return new InAssistFragment();
    }

    private final String TAG = InAssistFragment.class.getName();
    private ArrayList<String> dataEPC;
    private ArrayList<InAssistCloth> myList;
    private ArrayList<String> dataList;
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
        dataList = new ArrayList<>();
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
        dataList.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
                                    value.setEpc(epc);
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

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearList();
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否确认上架？");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Input> jsocList = new ArrayList<>();
                        for (InAssistCloth obj : myList) {
                            if (dataList.contains(obj.getEpc()) && !TextUtils.isEmpty(obj.getSuggest_location())
                                    && !obj.getSuggest_location().equals("剪布区") && !obj.getSuggest_location().equals("备货区")) {
                                Input input = new Input();
                                Carrier carrier = new Carrier();
                                carrier.setLocationNo(obj.getSuggest_location());
                                carrier.setTrayNo(obj.getSuggest_pallet());
                                input.setCarrier(carrier);
                                input.setVatNo(obj.getBas_batch_name());
                                input.setEpc(obj.getEpc());
                                jsocList.add(input);
                            }
                        }
                        if (jsocList.size() > 0) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userId", User.newInstance().getId());
                            jsonObject.put("data", jsocList);
                            final String json = JSON.toJSONString(jsonObject);
                            try {
                                AppLog.write(getActivity(), "putaway", json, AppLog.TYPE_INFO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/input/pushInput.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                                            AppLog.write(getActivity(), "putaway", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
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
                                                onViewClicked(button1);
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
                        } else
                            showToast("请选择有效布匹信息");
                    }
                });
                break;
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
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            dataList.clear();
                            if (isChecked) {
                                for (InAssistCloth inAssistCloth : myList) {
                                    if (!TextUtils.isEmpty(inAssistCloth.getSuggest_location())) {
                                        if (!dataList.contains(inAssistCloth.getEpc()))
                                            dataList.add(inAssistCloth.getEpc());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!TextUtils.isEmpty(item.getSuggest_location())) {
                                    if (!dataList.contains(item.getEpc()))
                                        dataList.add(item.getEpc());
                                }
                            } else {
                                if (dataList.contains(item.getEpc()))
                                    dataList.remove(item.getEpc());
                            }
                        }
                    }
                });
                if (position != 0) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.position == position)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    if (dataList.contains(item.getEpc())) {
                        cb.setChecked(true);
                    } else {
                        cb.setChecked(false);
                    }
                    holder.setText(R.id.item1, item.getBas_batch_name());
                    holder.setText(R.id.item2, item.getInv_serial());
                    if (!TextUtils.isEmpty(item.getSuggest_location())) {
                        String location = item.getSuggest_location().replaceAll("剪布区", "").replaceAll("备货区", "");
                        holder.setText(R.id.item3, location);
                        cb.setEnabled(true);
                    } else {
                        cb.setEnabled(false);
                        holder.setText(R.id.item3, "Null");
                    }
                    if (!TextUtils.isEmpty(item.getSuggest_pallet())) {
                        String pallet = item.getSuggest_pallet().replaceAll("剪布区", "").replaceAll("备货区", "");
                        holder.setText(R.id.item4, pallet);
                    } else {
                        holder.setText(R.id.item4, "Null");
                    }
                    holder.setText(R.id.item5, String.valueOf(item.getQtys()));
                }
            }
        }
    }
}
