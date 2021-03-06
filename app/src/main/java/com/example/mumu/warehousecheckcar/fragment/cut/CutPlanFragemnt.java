package com.example.mumu.warehousecheckcar.fragment.cut;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
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
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.BaseReturnObject;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.cutCloth.ClothPlan;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.App.TIME;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 剪布派工单
 *on 2020/1/9
 */
public class CutPlanFragemnt extends CodeFragment {
    private static CutPlanFragemnt fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;

    private RecycleAdapter mAdapter;
    private ArrayList<List<ClothPlan>> myList;
    private ArrayList<Cloth> cloths;
    private ArrayList<String> epcs;
    private HashMap<String, String> outId_epc;

    public static CutPlanFragemnt newInstance() {
        if (fragment == null) ;
        fragment = new CutPlanFragemnt();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.cut_pai));
        View view = inflater.inflate(R.layout.cut_plan_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        cloths = new ArrayList<>();
        epcs = new ArrayList<>();
        outId_epc = new HashMap<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_plan_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        initRFID();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mAdapter.setItemDetailOnClickListener(new ItemDetailOnClickListener() {
            @Override
            public void onItemDetailClickBack(ClothPlan clothPlan) {
                ArrayList<Cloth> detailList = new ArrayList<>();
                for (Cloth cloth : cloths) {
                    if (cloth.getVatNo().equals(clothPlan.getVat_no()))
                        detailList.add(cloth);
                }
                Bundle bundle = new Bundle();
                bundle.putString("epc", outId_epc.get(clothPlan.getOutp_id()));
                bundle.putString("outp_id", clothPlan.getOutp_id());
                bundle.putSerializable("detailList", detailList);
                Fragment fragment = CutPlanDetailFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, App.TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
        });
    }

    private void clearData() {
        myList.clear();
        cloths.clear();
        epcs.clear();
        outId_epc.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x01:
                    String changeEpc = (String) msg.getPositionObj(0);
                    String outp_id = (String) msg.getPositionObj(1);
                    outId_epc.put(outp_id, changeEpc);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        downLoadData();
    }

    private void downLoadData() {
        showLoadingDialog();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", User.newInstance().getId());
            String json = jsonObject.toJSONString();
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/getWorkInfoByUserId", new OkHttpClientManager.ResultCallback<BaseReturnObject<JSONObject>>() {
                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(BaseReturnObject<JSONObject> response) {
                    hideLoadingDialog();
                    if (response != null && response.getStatus() == 1) {
                        JSONObject jsonObject = (JSONObject) response.getData();
                        a:
                        for (String key : jsonObject.keySet()) {
                            JSONArray jsonArray = jsonObject.getJSONArray(key);
                            String jsonStr = JSONObject.toJSONString(jsonArray);
                            List<ClothPlan> jsonArr = JSONObject.parseArray(jsonStr, ClothPlan.class);
                            if (jsonArr.size() > 0) {
                                myList.add(jsonArr);
                                for (ClothPlan clothPlan : jsonArr) {
                                    outId_epc.put(clothPlan.getOutp_id(), "");
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } else if (response != null) {
                        showConfirmDialog(response.getMessage());
                    }
                }
            }, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                handler.removeMessages(ScanResultHandler.RFID);
                clearData();
                downLoadData();
                break;
            case R.id.button2:
                showUploadDialog("是否确认剪布绑定？");
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
        handler.postDelayed(r, TIME);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        HashMap<String, JSONArray> map = new HashMap<>();
        for (List<ClothPlan> list : myList) {
            if (list.get(0).isUpLoad()) {
                JSONArray jsonArray = new JSONArray();
                for (ClothPlan clothPlan : list) {
                    String epc = outId_epc.get(clothPlan.getOutp_id());
                    if (!epc.equals("")) {
                        JSONObject object = new JSONObject();
                        object.put("epc", epc);
                        object.put("product_applypid", clothPlan.getTrack_id());
                        jsonArray.add(object);
                    }
                }
                if (jsonArray.size() > 0)
                    map.put(list.get(0).getOut_no(), jsonArray);
            }
        }
        jsonObject.put("data", map);
        final String json = jsonObject.toJSONString();
        try {
            LogUtil.i(getResources().getString(R.string.log_cut_plan), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/bind", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    try {
                        LogUtil.e(getResources().getString(R.string.log_cut_plan_result), e.getMessage(), e);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onResponse(BaseReturn response) {
                    try {
                        LogUtil.i(getResources().getString(R.string.log_cut_plan_result), "userId:" + User.newInstance().getId() + response.toString());
                        uploadDialog.openView();
                        hideUploadDialog();
                        handler.removeCallbacks(r);
                        if (response.getStatus() == 1) {
                            showToast("上传成功");
                            clearData();
                            downLoadData();
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (!epcs.contains(epc)) {
            JSONObject obj = new JSONObject();
            obj.put("epc", epc);
            final String json = obj.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/hasInvSumByEpc", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "扫描查布区布匹失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInteger("status") == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<Cloth> arry = jsonArray.toJavaList(Cloth.class);
                                if (arry != null && arry.size() > 0 && !TextUtils.isEmpty(arry.get(0).getVatNo())) {
                                    Cloth value = arry.get(0);
                                    if (value != null) {
                                        if (!epcs.contains(value.getEpc())) {
                                            epcs.add(value.getEpc());
                                            cloths.add(value);
                                            Iterator<List<ClothPlan>> iterator = myList.iterator();
                                            List<ClothPlan> list = null;
                                            a:
                                            while (iterator.hasNext()) {
                                                list = iterator.next();
                                                for (ClothPlan clothPlan : list) {
                                                    if (clothPlan.getVat_no().equals(value.getVatNo())) {
                                                        String epc = outId_epc.get(clothPlan.getOutp_id());
                                                        if (epc.equals("")) {
                                                            outId_epc.put(clothPlan.getOutp_id(), value.getEpc());
                                                            iterator.remove();
                                                            break a;
                                                        }
                                                    }
                                                }
                                                list = null;
                                            }
                                            if (list != null) {
                                                myList.add(0, list);
                                            }
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            } else {
                                String msg = jsonObject.getString("message");
                                String data = jsonObject.getString("data");
                                showToast(msg + data);
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

    public interface ItemDetailOnClickListener {
        void onItemDetailClickBack(ClothPlan clothPlan);
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<List<ClothPlan>> {
        private Context context;

        public ItemDetailOnClickListener itemDetailOnClickListener;

        public void setContext(Context context) {
            this.context = context;
        }

        public RecycleAdapter(RecyclerView v, Collection<List<ClothPlan>> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setItemDetailOnClickListener(ItemDetailOnClickListener itemDetailOnClickListener) {
            this.itemDetailOnClickListener = itemDetailOnClickListener;
        }

        @Override
        public void convert(RecyclerHolder holder, final List<ClothPlan> list, final int position) {
            if (list != null) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                if (list.size() > 0) {
                    checkBox.setChecked(list.get(0).isUpLoad());
                    holder.setText(R.id.text1, "申请单号：" + list.get(0).getOut_no());
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (list.size() > 0) {
                            list.get(0).setUpLoad(b);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                LinearLayout linearLayout = holder.getView(R.id.layout1);
                linearLayout.removeAllViews();
                View view0 = View.inflate(context, R.layout.plan_add_item, null);
                linearLayout.addView(view0);
                for (final ClothPlan clothPlan : list) {
//                    View view = LayoutInflater.from(context).inflate(R.repaif_in_no_layout.plan_add_item, linearLayout, false);
                    View view = View.inflate(context, R.layout.plan_add_item, null);
                    TextView textView1 = (TextView) view.findViewById(R.id.item1);
                    TextView textView2 = (TextView) view.findViewById(R.id.item2);
                    TextView textView3 = (TextView) view.findViewById(R.id.item3);
                    TextView textView4 = (TextView) view.findViewById(R.id.item4);
                    TextView textView5 = (TextView) view.findViewById(R.id.item5);
                    textView1.setText(clothPlan.getVat_no());
                    textView2.setText(String.valueOf(clothPlan.getYard_out()));
                    textView3.setText(String.valueOf(clothPlan.getQty_kg()));
                    textView4.setText(clothPlan.getColor_name());
                    textView5.setText(clothPlan.getOutp_id());
                    linearLayout.addView(view);
                    if (!outId_epc.get(clothPlan.getOutp_id()).equals(""))
                        view.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemDetailOnClickListener != null)
                                itemDetailOnClickListener.onItemDetailClickBack(clothPlan);
                        }
                    });
                }
                linearLayout.invalidate();

            }
        }
    }
}
