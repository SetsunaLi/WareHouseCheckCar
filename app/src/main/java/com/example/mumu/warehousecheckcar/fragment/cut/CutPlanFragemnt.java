package com.example.mumu.warehousecheckcar.fragment.cut;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.entity.OutputDetail;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by 剪布派工单
 *on 2020/1/9
 */
public class CutPlanFragemnt extends BaseFragment {
    private static CutPlanFragemnt fragment;
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
    private RecycleAdapter mAdapter;
    private ArrayList<Output> myList;

    public static CutPlanFragemnt newInstance() {
        if (fragment == null) ;
        fragment = new CutPlanFragemnt();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.cut_os));
        View view = inflater.inflate(R.layout.cut_plan_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
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
        text2.setText(String.valueOf(myList.size()));
    }

    @Override
    protected void addListener() {
        mAdapter.setOnItemClickListener(new BRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object data, int position) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        textDownLoadData();
        text2.setText(String.valueOf(myList.size()));
    }

    //    测试获取列表数据
    private void textDownLoadData() {
        JSONObject object = new JSONObject();
        object.put("applyNo", "P00104677");
        final String json = object.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/output/pullOutput.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                @Override
                public void onError(Request request, Exception e) {
                }

                @Override
                public void onResponse(JSONArray json) {
                    try {
                        List<Output> response;
                        response = json.toJavaList(Output.class);
                        if (response != null && response.size() != 0) {
                            myList.addAll(response);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                myList.clear();
                textDownLoadData();
                break;
            case R.id.button2:
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Output> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<Output> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final Output item, final int position) {
            if (item != null) {
                LinearLayout linearLayout = holder.getView(R.id.layout1);
                linearLayout.removeAllViews();
                for (OutputDetail outputDetail : item.getList()) {
                    View view = LayoutInflater.from(context).inflate(R.layout.plan_add_item, linearLayout, false);
                    TextView textView1 = (TextView) view.findViewById(R.id.item1);
                    textView1.setText(outputDetail.getFabRool());
                    linearLayout.addView(view);
                }
            }
        }
    }
}
