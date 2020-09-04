package com.example.mumu.warehousecheckcar.fragment.forward;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.forwarding.ApplyByPayNo;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;

/***
 *created by 
 *on 2020/8/31
 */
public class ShipmentOutNoFragment extends BaseFragment {
    private static ShipmentOutNoFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    private ArrayList<ApplyByPayNo> myList;
    private RecycleAdapter mAdapter;
    private int bas_transport_type;
    private ForwardingMsgFragment.CarMsg carMsg;
    private ArrayList<String> nos;

    public static ShipmentOutNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new ShipmentOutNoFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shipment_out_no_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        nos = new ArrayList<>();
        bas_transport_type = getArguments().getInt("bas_transport_type", 0);
        carMsg = (ForwardingMsgFragment.CarMsg) getArguments().getSerializable("carMsg");
        for (String no : getArguments().getStringArrayList("no")) {
            if (!TextUtils.isEmpty(no) && !nos.contains(no)) {
                nos.add(no);
            }
        }

    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.shipment_out_no_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        dnowload();
    }

    private void dnowload() {
        myList.clear();
        for (final String no : nos) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", User.newInstance().getId());
            JSONObject data = new JSONObject();
            data.put("payNo", no);
            jsonObject.put("data", data);
            String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/payCarry/getApplyByPayNo", new OkHttpClientManager.ResultCallback<BaseReturnArray<ApplyByPayNo>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                    }

                    @Override
                    public void onResponse(BaseReturnArray<ApplyByPayNo> returnArray) {
                        try {
                            if (returnArray != null && returnArray.getStatus() == 1 && returnArray.getData().size() > 0) {
                                for (int i = 0; i < returnArray.getData().size(); i++) {
                                    ApplyByPayNo applyByPayNo = returnArray.getData().get(i);
                                    applyByPayNo.setFlag(true);
                                    applyByPayNo.setPay_no(no);
                                    if (i == 0)
                                        applyByPayNo.setHead(true);
                                    myList.add(applyByPayNo);
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().post(new EventBusMsg(0x31));
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                dnowload();
                break;
            case R.id.button2:
                ArrayList<String> list = new ArrayList<>();
                for (ApplyByPayNo applyByPayNo : myList) {
                    if (applyByPayNo.isFlag())
                        list.add(applyByPayNo.getApply_no());
                }
                EventBus.getDefault().postSticky(new EventBusMsg(0x01, carMsg, list, bas_transport_type));
                Fragment fragment = ForwardingFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ApplyByPayNo> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ApplyByPayNo> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final ApplyByPayNo item, final int position) {
            LinearLayout head = holder.getView(R.id.headNo);
            LinearLayout title = holder.getView(R.id.layout_title);
            View view = holder.getView(R.id.view);
            if (item.isHead()) {
                head.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                holder.setText(R.id.text1, "付运表号：" + item.getPay_no());
            } else {
                head.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
            CheckBox checkBox = holder.getView(R.id.checkbox1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setFlag(b);
                }
            });
            holder.setText(R.id.item1, item.getApply_no());
        }
    }
}