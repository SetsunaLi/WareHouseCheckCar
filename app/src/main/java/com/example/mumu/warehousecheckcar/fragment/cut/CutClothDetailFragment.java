package com.example.mumu.warehousecheckcar.fragment.cut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class CutClothDetailFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener, OnRfidResult {

    private final String TAG = "CutClothDetailFragment";

    private static CutClothDetailFragment fragment;
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


    public static CutClothDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothDetailFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Cloth> myList;
    private JSONObject json;
    private List<String> epcList;
    private List<String> epcArray;
    private BarCode barcode;
    private String clothVat;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_matching_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Cloth());
        json = new JSONObject();
        epcList = new ArrayList<>();
        epcArray = new ArrayList<>();
        barcode = new BarCode();
        clothVat = "";
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_matching_item);
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
        mAdapter.setOnItemClickListener(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void clear() {
        if (myList != null) {
            myList.clear();
            myList.add(new Cloth());
        }
        if (epcList != null)
            epcList.clear();
        if (epcArray != null)
            epcArray.clear();
        clothVat = "";
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

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cut_cloth_matching_item, null);
        mAdapter.setHeader(view);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(JSONObject event) {
        json = event.getJSONObject("barcode");
        barcode = json.toJavaObject(BarCode.class);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clear();
                text1.setText("0");
                text2.setText("");
                mAdapter.select(-255);
                mAdapter.notifyDataSetChanged();
                scanResultHandler.removeMessages(ScanResultHandler.RFID);

                break;
            case R.id.button2:
                String codeVat = barcode.getVatNo().replaceAll(" ", "");
                if (codeVat.equals(clothVat)) {
                    showUploadDialog("是否需要进行关联");
                    setUploadYesClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JSONObject jsonObject = new JSONObject();
                            User user = User.newInstance();
                            jsonObject.put("userId", user.getId());
                            jsonObject.put("applyNo", barcode.getOut_no());
                            jsonObject.put("outp_id", barcode.getOutp_id());
                            jsonObject.put("epc", epcList.get(0));

                            final String json = jsonObject.toJSONString();
                            try {
                                AppLog.write(getActivity(), "cutclothd", json, AppLog.TYPE_INFO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCut.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getEPC;" + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            try {
                                                AppLog.write(getActivity(), "cutclothd", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            uploadDialog.lockView();
                            scanResultHandler.postDelayed(r, TIME);
                        }
                    });
                } else
                    showConfirmDialog("缸号不一致,请重新选择");
                break;
        }
    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (epc.startsWith("3035A537") && !epcArray.contains(epc)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", epc);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        if (jsonArray != null && jsonArray.size() > 0) {
                            Cloth cloth = jsonArray.getObject(0, Cloth.class);
                            if (cloth != null && !epcArray.contains(cloth.getEpc())) {
                                epcArray.add(cloth.getEpc());
                                myList.add(cloth);
                                text1.setText(String.valueOf(myList.size() - 1));
                                text2.setText(cloth.getVatNo());
                            }
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


    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cloth> {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void setHeader(View header) {
            super.setHeader(header);
        }

        public RecycleAdapter(RecyclerView v, Collection<Cloth> datas, int itemLayoutId) {
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
        public void convert(final RecyclerHolder holder, final Cloth item, final int position) {
            final LinearLayout ll = holder.getView(R.id.layout1);
            if (item != null) {
                final CheckBox cb = holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position != 0) {
                            if (isChecked) {
                                epcList.clear();
                                if (!epcList.contains(item.getEpc())) {
                                    clothVat = item.getVatNo().replaceAll("", " ");
                                    epcList.add(item.getEpc());
                                }
                                ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                            } else {
                                if (epcList.contains(item.getEpc())) {
                                    clothVat = "";
                                    epcList.remove(item.getEpc());
                                }
                                ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (index == position) {
                        epcList.clear();
                        if (!epcList.contains(item.getEpc())) {
                            epcList.add(item.getEpc());
                            clothVat = item.getVatNo().replaceAll(" ", "");
                        }
                        cb.setChecked(true);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else {
                        if (epcList.contains(item.getEpc())) {
                            epcList.remove(item.getEpc());
                            clothVat = "";
                        }
                        cb.setChecked(false);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    }
                    holder.setText(R.id.item1, item.getFabRool());
                    holder.setText(R.id.item2, item.getVatNo());
                    holder.setText(R.id.item3, item.getProduct_no());
                    holder.setText(R.id.item4, item.getColor());
                    holder.setText(R.id.item5, item.getSelNo());
                    holder.setText(R.id.item6, String.valueOf(item.getWeight()));
                } else {
                    if (cb.isEnabled())
                        cb.setEnabled(false);
                }
            }
        }
    }
}
