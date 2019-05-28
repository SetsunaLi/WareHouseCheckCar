package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.CutSample;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CutClothOutFragment extends Fragment {
    private final String TAG = "CutClothOutFragment";

    private static CutClothOutFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static CutClothOutFragment newInstance() {
        if (fragment == null)
            fragment = new CutClothOutFragment();
        return fragment;
    }

    /*** 申请单号*/
    private ArrayList<String> fatherNoList;
    /**
     * 列表信息
     */
    private ArrayList<CutSample> myList;
    /***    主表，根据申请单号，字段组成key判断是否上传*/
    private Map<String, List<String>> dataKey;
    /***    记录查询到的申请单号，没实际用途*/
    private ArrayList<String> dateNo;

    private RecycleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cutcloth_out_layout, container, false);
        ButterKnife.bind(this, view);

        initData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cutcloth_out_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        return view;
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new CutSample());
        dataKey = new HashMap<>();
        dateNo = new ArrayList<>();
        EventBus.getDefault().register(this);
    }

    private void clearData() {
        if (myList != null)
            myList.clear();
        myList.add(new CutSample());
        if (dataKey != null)
            dataKey.clear();
        if (dateNo != null)
            dateNo.clear();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cutcloth_out_head, null);
        mAdapter.setHeader(view);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null) {
            switch (msg.getStatus()) {
                case 0x04:
                    fatherNoList = (ArrayList<String>) msg.getPositionObj(0);
                    if (fatherNoList == null) {
                        fatherNoList = new ArrayList<>();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean flag = true;

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            clearData();
            downLoadData();
            text2.setText(fatherNoList.size() + "");
        }
    }

    public void downLoadData() {
        for (String no : fatherNoList) {
            JSONObject object = new JSONObject();
            object.put("applyNo", no);
            final String json = object.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/output/pullOutput.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONArray json) {
                        try {
                            List<CutSample> response;
                            response = json.toJavaList(CutSample.class);
                            if (response != null && response.size() != 0) {
                                if (!dateNo.contains(response.get(0).getApplyNo())) {
                                    dateNo.add(response.get(0).getApplyNo());
                                    response.get(0).setFlag(true);
                                    for (CutSample cutSample : response) {
                                        String key = cutSample.getOutp_id();
                                        if (!dataKey.containsKey(cutSample.getApplyNo())) {
                                            dataKey.put(cutSample.getApplyNo(), new ArrayList<String>());
                                        }
                                        if (!dataKey.get(cutSample.getApplyNo()).contains(key)) {
                                            dataKey.get(cutSample.getApplyNo()).add(key);
                                        }
                                    }
                                    myList.addAll(response);
                                    mAdapter.notifyDataSetChanged();
                                }
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<CutSample> {
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

        public RecycleAdapter(RecyclerView v, Collection<CutSample> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final CutSample item, final int position) {
            if (item != null) {
                final String key = item.getOutp_id();
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (String no:dataKey.keySet()){
                                    for (CutSample cutSample:myList){
                                        if (!dataKey.get(no).contains(cutSample.getOutp_id()))
                                            dataKey.get(no).add(cutSample.getOutp_id());
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }else {
                                for (String no:dataKey.keySet()){
                                    dataKey.get(no).clear();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (isChecked) {
                                if (dataKey.containsKey(item.getApplyNo()))
                                    if (!dataKey.get(item.getApplyNo()).contains(key))
                                        dataKey.get(item.getApplyNo()).add(key);
                            } else {
                                if (dataKey.containsKey(item.getApplyNo()))
                                    if (dataKey.get(item.getApplyNo()).contains(key)) {
                                        Iterator<String> iter = dataKey.get(item.getApplyNo()).iterator();
                                        while (iter.hasNext()) {
                                            String str = iter.next();
                                            if (str.equals(key))
                                                iter.remove();
                                        }
                                    }
                            }
                        }
                    }
                });
                if (position !=0) {
                    LinearLayout title = holder.getView(R.id.layout_title);
                    LinearLayout no = holder.getView(R.id.headNo);
                    View view = holder.getView(R.id.view);
                    if (item.isFlag()) {
                        title.setVisibility(View.VISIBLE);
                        no.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);
                        holder.setText(R.id.text1, "申请单号：" + item.getApplyNo());
                    } else {
                        title.setVisibility(View.GONE);
                        no.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    }

                    if (((item.getVatNo() + "").equals("") && (item.getProduct_no() + "").equals("") && (item.getSelNo() + "").equals(""))) {
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                        cb.setChecked(false);
                    } else {
                        if (cb.isEnabled())
                            cb.setEnabled(true);
                        if (dataKey.containsKey(item.getApplyNo())) {
                            if (dataKey.get(item.getApplyNo()).contains(key))
                                cb.setChecked(true);
                            else
                                cb.setChecked(false);
                        }
                        holder.setText(R.id.item1, item.getProduct_no() + "");
                        holder.setText(R.id.item2, item.getSelNo() + "");
                        holder.setText(R.id.item3, item.getColor() + "");
                        holder.setText(R.id.item4, item.getVatNo() + "");
                        holder.setText(R.id.item5, item.getWeight() + "");
                        holder.setText(R.id.item6, item.getYard_out() + "");
                    }
                }

            }
        }
    }
}
