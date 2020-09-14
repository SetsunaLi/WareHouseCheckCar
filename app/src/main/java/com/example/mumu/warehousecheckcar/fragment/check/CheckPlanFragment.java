package com.example.mumu.warehousecheckcar.fragment.check;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by 盘点计划
 *on 2019/12/6
 */
public class CheckPlanFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener {
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    private RecycleAdapter mAdapter;
    private ArrayList<CheckPlan> myList;

    public static CheckPlanFragment newInstance() {
        return new CheckPlanFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_plan_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.check_plan_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        mAdapter.setOnItemClickListener(this);
    }

    private void downList() {
        OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getDynamicLocationList", new OkHttpClientManager.ResultCallback<JSONObject>() {
            @Override
            public void onError(Request request, Exception e) {
                if (e instanceof ConnectException)
                    showConfirmDialog("链接超时");
            }

            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<CheckPlan> response = jsonArray.toJavaList(CheckPlan.class);
                        if (response != null && response.size() != 0) {
                            myList.addAll(response);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast("没有待盘点的仓位");
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.selectItem(position);
        mAdapter.notifyDataSetChanged();
        App.CARRIER.setLocationNo(((CheckPlan) data).getPallet_name());
        App.CARRIER.setLocationEPC(((CheckPlan) data).getPallet_epc());
        Fragment fragment = CheckFragment.newInstance();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                myList.clear();
                mAdapter.notifyDataSetChanged();
                downList();
                break;
            case R.id.button2:
                Fragment fragment = CheckCarrierFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<CheckPlan> {
        private Context context;
        private int id;

        public RecycleAdapter(RecyclerView v, Collection<CheckPlan> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public void convert(RecyclerHolder holder, final CheckPlan item, final int position) {
            if (item != null) {
                LinearLayout ll = holder.getView(R.id.layout1);
                if (id == position)
                    ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                else
                    ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                holder.setText(R.id.item1, item.getPallet_name());
//                holder.setText(R.id.item2, "");
                holder.setText(R.id.item3, item.getLocation_modifydate());
            }
        }

        public void selectItem(int id) {
            this.id = id;
        }
    }

    class CheckPlan extends Carrier {
        private String pallet_epc;
        private String location_modifydate;
        private String pallet_name;

        public String getPallet_epc() {
            return pallet_epc;
        }

        public void setPallet_epc(String pallet_epc) {
            this.pallet_epc = pallet_epc;
        }

        public String getLocation_modifydate() {
            return location_modifydate;
        }

        public void setLocation_modifydate(String location_modifydate) {
            this.location_modifydate = location_modifydate;
        }

        public String getPallet_name() {
            return pallet_name;
        }

        public void setPallet_name(String pallet_name) {
            this.pallet_name = pallet_name;
        }
    }
}
