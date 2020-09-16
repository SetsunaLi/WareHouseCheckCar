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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.car.CarOutBean;
import com.example.mumu.warehousecheckcar.entity.check.Inventory;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 
 *on 2020/3/26
 */
public class CarOutStockFragment extends BaseFragment implements UHFCallbackLiatener, OnRfidResult {
    private static CarOutStockFragment fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    private ArrayList<List<CarOutBean>> myList;
    private ArrayList<String> noList;
    private ArrayList<String> epcList;
    private ArrayList<String> outNoList;
    private RecycleAdapter mAdapter;
    private ScanResultHandler scanResultHandler;

    public static CarOutStockFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarOutStockFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("叉车下架");
        View view = inflater.inflate(R.layout.car_out_stock_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        noList = new ArrayList<>();
        epcList = new ArrayList<>();
        outNoList = new ArrayList<>();
        ArrayList<String> list = (ArrayList<String>) getArguments().getSerializable("NO");
        noList.addAll(list);
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.car_stock_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        initRFID();
    }

    private void clearData() {
        myList.clear();
        epcList.clear();
        outNoList.clear();
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
    public void onStart() {
        super.onStart();
        downLoadData();
    }

    private void downLoadData() {
        for (String no : noList) {
            JSONObject object = new JSONObject();
            object.put("applyNo", no);
            final String json = object.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/static/getInfoByApplyNo", new OkHttpClientManager.ResultCallback<BaseReturnArray<CarOutBean>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(BaseReturnArray<CarOutBean> resultBean) {
                        if (resultBean.getStatus() == 1) {
                            a:
                            for (CarOutBean carOutBean : resultBean.getData()) {
                                boolean flag = false;
                                b:
                                for (List<CarOutBean> list : myList) {
                                    if (list.get(0).getOut_no().equals(carOutBean.getOut_no())) {
                                        list.add(carOutBean);
                                        flag = true;
                                        break b;
                                    }
                                }
                                if (!flag) {
                                    List<CarOutBean> newlist = new ArrayList<>();
                                    newlist.add(carOutBean);
                                    myList.add(newlist);
                                    if (!outNoList.contains(carOutBean.getOut_no()))
                                        outNoList.add(carOutBean.getOut_no());
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else
                            showToast(resultBean.getMessage());
                    }
                }, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
                downLoadData();
                break;
            case R.id.button2:
                showUploadDialog("是否确认下架？");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upLoad();
                    }
                });
                break;
        }
    }

    private void upLoad() {
        uploadDialog.lockView();
        scanResultHandler.postDelayed(r, TIME);
        a:
        for (String no : outNoList) {
            b:
            for (List<CarOutBean> arrayList : myList) {
                if (arrayList.get(0).getOut_no().equals(no)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", User.newInstance().getId());
                    jsonObject.put("apply_no", no);
                    ArrayList<CarOutBean> list = new ArrayList<>();
                    for (CarOutBean carOutBean : arrayList) {
                        if (carOutBean.isScan()) {
                            list.add(carOutBean);
                        }
                    }
                    jsonObject.put("tag", list);
                    final String json = jsonObject.toJSONString();
                    try {
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/static/forkDown", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                if (e instanceof ConnectException)
                                    showConfirmDialog("链接超时");
                                if (App.LOGCAT_SWITCH) {
                                    Log.i(TAG, "postInventory;" + e.getMessage());
                                    Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onResponse(BaseReturn response) {
                                try {
                                    try {
                                        AppLog.write(getActivity(), "carstock", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        uploadDialog.openView();
                                        hideUploadDialog();
                                        scanResultHandler.removeCallbacks(r);
//                                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                        if (response != null && response.getStatus() == 1) {
                                            showToast("上传成功");
                                            clearData();
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            showToast("上传失败");
                                            showConfirmDialog("上传失败，" + response.getMessage());
                                            Sound.faillarm();
                                        }
                                    } catch (Exception e) {

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
                    break b;
                }
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
    public void rfidResult(String epc) {
        final String EPC = epc.replaceAll(" ", "");
        if (!epcList.contains(EPC)) {
            JSONObject epcJson = new JSONObject();
            epcJson.put("epc", EPC);
            final String json = epcJson.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            List<Inventory> arry;
                            arry = jsonArray.toJavaList(Inventory.class);
                            if (arry != null && arry.size() > 0) {
                                Inventory response = arry.get(0);
                                if (!TextUtils.isEmpty(response.getVatNo())) {
                                    if (!epcList.contains(response.getEpc())) {
                                        epcList.add(response.getEpc());
                                        a:
                                        for (List<CarOutBean> list : myList) {
                                            b:
                                            for (CarOutBean carOutBean : list) {
                                                if (response.getVatNo().equals(carOutBean.getVat_no())) {
                                                    carOutBean.setScan(true);
                                                    carOutBean.setEpc(response.getEpc());
                                                }
                                            }
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, json);
            } catch (IOException e) {
                Log.i(TAG, "");
            }
        }
    }


    class RecycleAdapter extends BasePullUpRecyclerAdapter<List<CarOutBean>> {
        private Context context;
        private int id = -255;

        public RecycleAdapter(RecyclerView v, Collection<List<CarOutBean>> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void convert(RecyclerHolder holder, final List<CarOutBean> itemList, final int position) {
            if (itemList != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (!outNoList.contains(itemList.get(0).getOut_no()))
                                outNoList.add(itemList.get(0).getOut_no());
                        } else {
                            if (outNoList.contains(itemList.get(0).getOut_no()))
                                outNoList.remove(itemList.get(0).getOut_no());
                        }
                    }
//                    }
                });
                cb.setChecked(outNoList.contains(outNoList.contains(itemList.get(0).getOut_no())));
                holder.setText(R.id.text1, itemList.get(0).getOut_no());
                LinearLayout linearLayout = holder.getView(R.id.layout1);
                linearLayout.removeAllViews();
                linearLayout.addView(LayoutInflater.from(context).inflate(R.layout.car_stock_add_item, linearLayout, false));
                for (CarOutBean carOutBean : itemList) {
                    View view = LayoutInflater.from(context).inflate(R.layout.car_stock_add_item, linearLayout, false);
                    TextView textView1 = (TextView) view.findViewById(R.id.item1);
                    TextView textView2 = (TextView) view.findViewById(R.id.item2);
                    LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout1);
                    textView1.setText(carOutBean.getColor_name());
                    textView2.setText(carOutBean.getVat_no());
                    layout.setBackgroundColor(carOutBean.isScan() ? getResources().getColor(R.color.colorDialogTitleBG) : getResources().getColor(R.color.colorZERO));
                    linearLayout.addView(view);
                }
            }
        }
    }
}
