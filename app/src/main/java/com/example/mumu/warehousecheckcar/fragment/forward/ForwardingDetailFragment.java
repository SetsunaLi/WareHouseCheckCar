package com.example.mumu.warehousecheckcar.fragment.forward;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.forwarding.Forwarding;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForwardingDetailFragment extends BaseFragment {
    final String TAG = "ForwardingDetailFragment";
    private static ForwardingDetailFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;


    public static ForwardingDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new ForwardingDetailFragment();
        return fragment;
    }

    private List<Forwarding> myList;
    private HashMap<String, ForwardingFragment.ForwardingFlag> dataList;
    private RecycleAdapter mAdapter;

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Forwarding("", "", "", "", "", 0.0, ""));//增加一个为头部
        dataList = new HashMap<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.forwarding_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x02:
                    myList.addAll((List<Forwarding>) msg.getPositionObj(0));
                    dataList.putAll((HashMap<String, ForwardingFragment.ForwardingFlag>) msg.getPositionObj(1));
                    break;
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
        mAdapter.notifyDataSetChanged();
        text1.setText(String.valueOf(myList.size() - 1));
        if (myList.size() > 1)
            text2.setText(myList.get(1).getVatNo());
    }

    private void load() {
        Collections.sort(myList, new Comparator<Forwarding>() {
            @Override
            public int compare(Forwarding obj1, Forwarding obj2) {
                String epc1 = obj1.getEpc();
                String epc2 = obj2.getEpc();
                if (TextUtils.isEmpty(epc1) & !TextUtils.isEmpty(epc2))
                    return -1;
                if (TextUtils.isEmpty(epc2) & !TextUtils.isEmpty(epc1))
                    return 1;
                if (TextUtils.isEmpty(epc1) & TextUtils.isEmpty(epc2))
                    return 0;
                if (dataList.containsKey(epc1) && dataList.containsKey(epc2)) {
                    if (dataList.get(epc1).isStatus() && !dataList.get(epc2).isStatus()) {
                        return -1;
                    } else if (!dataList.get(epc1).isStatus() && dataList.get(epc2).isStatus()) {
                        return 1;
                    }
                }
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

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.forwarding_detail_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        HashMap<String, ForwardingFragment.ForwardingFlag> msg = new HashMap<>();
        msg.putAll(dataList);
        EventBus.getDefault().post(new EventBusMsg(0xfe, msg));
        EventBus.getDefault().unregister(this);
        myList.clear();
        dataList.clear();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Forwarding> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Forwarding> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        private int position;

        public void select(int i) {
            this.position = i;
        }

        @Override
        public void convert(RecyclerHolder holder, final Forwarding item, final int position) {
            if (item != null) {
                final CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (int i = 1; i < myList.size(); i++) {
                                    if (dataList.containsKey(myList.get(i).getEpc()))
                                        dataList.get(myList.get(i).getEpc()).setStatus(true);
                                }
                            } else {
                                for (int i = 1; i < myList.size(); i++) {
                                    if (dataList.containsKey(myList.get(i).getEpc()))
                                        dataList.get(myList.get(i).getEpc()).setStatus(false);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                dataList.get(item.getEpc()).setStatus(true);
                            } else {
                                dataList.get(item.getEpc()).setStatus(false);
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (TextUtils.isEmpty(item.getFabRool())) {
                        cb.setChecked(false);
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                    } else {
                        if (dataList.containsKey(item.getEpc())) {
                            if (!cb.isEnabled())
                                cb.setEnabled(true);
                            cb.setChecked(dataList.get(item.getEpc()).isStatus());
                        } else {
                            if (cb.isEnabled())
                                cb.setEnabled(false);
                        }
                    }
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (!dataList.get(item.getEpc()).isFind()) {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                        cb.setEnabled(false);
                    } else {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                        cb.setEnabled(true);
                    }
                    holder.setText(R.id.item1, item.getFabRool());
                    holder.setText(R.id.item2, item.getClothNum());
                    holder.setText(R.id.item3, item.getVatNo());
                    holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                }
            }
        }
    }
}
