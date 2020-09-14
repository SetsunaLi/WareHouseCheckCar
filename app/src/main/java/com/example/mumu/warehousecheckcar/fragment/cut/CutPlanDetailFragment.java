package com.example.mumu.warehousecheckcar.fragment.cut;

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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;


import butterknife.BindView;
import butterknife.ButterKnife;

/***
 *created by 
 *on 2020/4/2
 */
public class CutPlanDetailFragment extends BaseFragment {
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    private ArrayList<Cloth> myList;
    private RecycleAdapter mAdapter;
    private String epc;
    private String outp_id;

    public static CutPlanDetailFragment newInstance() {
        return new CutPlanDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_plan_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Cloth());
        myList.addAll((ArrayList<Cloth>) getArguments().getSerializable("detailList"));
        epc = getArguments().getString("epc");
        outp_id = getArguments().getString("outp_id");
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_plan_detail_item);
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
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().post(new EventBusMsg(0x00, epc, outp_id));
        EventBus.getDefault().unregister(this);
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cloth> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<Cloth> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final Cloth item, final int position) {
            if (item != null && position > 0) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setChecked(item.getEpc().equals(epc));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            epc = item.getEpc();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                LinearLayout layout = holder.getView(R.id.layout1);
                layout.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                holder.setText(R.id.item1, item.getFabRool());
                holder.setText(R.id.item2, item.getVatNo());
                holder.setText(R.id.item3, item.getClothNum());
                holder.setText(R.id.item4, String.valueOf(item.getWeight()));
            } else if (position == 0) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setVisibility(View.INVISIBLE);
            }
        }
    }
}
