package com.example.mumu.warehousecheckcar.fragment.outsource_in;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.Outsource;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 *created by 
 *on 2020/7/22
 */
public class OutsourceInDetailFragment extends BaseFragment {

    private static OutsourceInDetailFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.checkbox1)
    CheckBox checkbox1;
    private RecycleAdapter mAdapter;
    private ArrayList<Outsource> myList;
    private ArrayList<String> epcs;
    private int position;

    public static OutsourceInDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutsourceInDetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.outsource_in_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        epcs = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.outsource_in_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (Outsource outsource : myList) {
                        if (!epcs.contains(outsource.getEpc()))
                            epcs.add(outsource.getEpc());
                    }

                } else {
                    epcs.clear();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x01:
                    myList.addAll((ArrayList<Outsource>) msg.getPositionObj(0));
                    epcs.addAll((ArrayList<String>) msg.getPositionObj(1));
                    position = (int) msg.getPositionObj(2);
                    break;
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        text1.setText(String.valueOf(myList.size()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post(new EventBusMsg(0x02, position, epcs));
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Outsource> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<Outsource> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final Outsource item, final int position) {
            if (item != null) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            if (!epcs.contains(item.getEpc()))
                                epcs.add(item.getEpc());
                        } else {
                            if (epcs.contains(item.getEpc()))
                                epcs.remove(item.getEpc());
                        }
                    }
                });
                checkBox.setChecked(epcs.contains(item.getEpc()));
                holder.setText(R.id.item1, item.getFab_roll());
                holder.setText(R.id.item2, String.valueOf(item.getWeight()));
                holder.setText(R.id.item3, item.getEpc());
            }
        }
    }
}
