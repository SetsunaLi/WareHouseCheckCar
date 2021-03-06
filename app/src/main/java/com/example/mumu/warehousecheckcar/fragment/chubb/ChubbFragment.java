package com.example.mumu.warehousecheckcar.fragment.chubb;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.in.InCheckDetail;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.App.TIME;


/**
 * Created by mumu on 2019/1/14.
 */

public class ChubbFragment extends CodeFragment {
    private static ChubbFragment fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.imgview)
    ImageView imgview;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    private final String TAG = "ChubbFragment";

    public static ChubbFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<InCheckDetail> myList;

    //    勾选框
    private List<String> dataKey;
    //    epc
    private List<String> epcList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.chubb_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("查布");
        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.chubb_item, null);
        mAdapter.setHeader(view);
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new InCheckDetail());
        }
        if (dataKey != null)
            dataKey.clear();
        if (epcList != null)
            epcList.clear();
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new InCheckDetail());
        dataKey = new ArrayList<>();
        epcList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubb_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        text1.setText(String.valueOf(epcList.size()));
    }

    @Override
    protected void addListener() {
        initRFID();
    }

    //这里写界面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        clearData();
        myList.clear();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                if (!(imgview.getVisibility() == View.VISIBLE))
                    imgview.setVisibility(View.VISIBLE);
                clearData();
                text1.setText("0");
                mAdapter.notifyDataSetChanged();
                handler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否确认查布？");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<InCheckDetail> jsocList = new ArrayList<>();
                        for (InCheckDetail obj : myList) {
                            String key = obj.getEpc();
                            if (key != null && dataKey.contains(key)) {
                                jsocList.add(obj);
                            }
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("data", jsocList);
                        jsonObject.put("userId", User.newInstance().getId());
                        final String json = JSON.toJSONString(jsonObject);
                        try {
                            LogUtil.i(getResources().getString(R.string.log_chubb_cloth), json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/pushCheck", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (e instanceof ConnectException)
                                        showConfirmDialog("链接超时");
                                    try {
                                        LogUtil.e(getResources().getString(R.string.log_chubb_cloth_result), e.getMessage(), e);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        LogUtil.i(getResources().getString(R.string.log_chubb_cloth_result), "userId:" + User.newInstance().getId() + response.toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        uploadDialog.openView();
                                        hideUploadDialog();
                                        handler.removeCallbacks(r);
                                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                                            showToast("上传成功");
                                            if (!(imgview.getVisibility() == View.VISIBLE))
                                                imgview.setVisibility(View.VISIBLE);
                                            clearData();
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            showToast("上传失败");
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
                        handler.postDelayed(r, TIME);
                    }
                });
                break;
        }
    }

    @Override
    public void rfidResult(String epc) {
        if ((imgview.getVisibility() == View.VISIBLE))
            imgview.setVisibility(View.GONE);
        epc = epc.replace(" ", "");
        if (!epcList.contains(epc)) {
            JSONObject obj = new JSONObject();
            obj.put("epc", epc);
            String json = obj.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/getCheckByEpc", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            showToast("获取库位信息失败");
                        }
                    }

                    @Override
                    public void onResponse(JSONObject object) {
                        try {
                            if (object.getJSONObject("data") != null) {
                                JSONObject obj = object.getJSONObject("data");
                                InCheckDetail value = obj.toJavaObject(InCheckDetail.class);
                                if (value != null && !TextUtils.isEmpty(value.getVatNo())) {
                                    if (!epcList.contains(value.getEpc())) {
                                        epcList.add(value.getEpc());
                                        myList.add(value);
                                        Collections.sort(myList, new Comparator<InCheckDetail>() {
                                            @Override
                                            public int compare(InCheckDetail obj1, InCheckDetail obj2) {
                                                String aFab = obj1.getFabRool();
                                                String bFab = obj2.getFabRool();
                                                if (TextUtils.isEmpty(aFab) & !TextUtils.isEmpty(bFab))
                                                    return -1;
                                                else if (TextUtils.isEmpty(bFab) & !TextUtils.isEmpty(aFab))
                                                    return 1;
                                                else if (TextUtils.isEmpty(bFab) & TextUtils.isEmpty(aFab))
                                                    return 0;
                                                else {
                                                    int a = aFab.compareTo(bFab);
                                                    if (a == 0) {
                                                        return 0;
                                                    } else if (a > 0) {
                                                        return 1;
                                                    } else {
                                                        return -1;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    text1.setText(String.valueOf(epcList.size()));
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

    class RecycleAdapter extends BasePullUpRecyclerAdapter<InCheckDetail> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<InCheckDetail> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final InCheckDetail item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (InCheckDetail i : myList) {
                                    if (!TextUtils.isEmpty(i.getVatNo()) || !TextUtils.isEmpty(i.getProduct_no()) || !TextUtils.isEmpty(i.getSelNo()))
                                        dataKey.add(i.getEpc());
                                }
                            } else {
                                dataKey.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKey.contains(item.getEpc()))
                                    dataKey.add(item.getEpc());
                            } else {
                                if (dataKey.contains(item.getEpc()))
                                    dataKey.remove(item.getEpc());
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
                        if (dataKey.contains(item.getEpc()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                    holder.setText(R.id.item1, item.getFabRool());
                    holder.setText(R.id.item2, item.getVatNo());
                    holder.setText(R.id.item3, item.getProduct_no());
                    holder.setText(R.id.item4, item.getSelNo());
                    holder.setText(R.id.item5, item.getColor());
                    holder.setText(R.id.item6, String.valueOf(item.getWeight()));
                }
            }
        }
    }
}
