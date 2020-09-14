package com.example.mumu.warehousecheckcar.fragment.repaif_in;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.in.RepaifIn;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 *created by 
 *on 2020/8/20
 */
public class RepaifInDetailFragment extends BaseFragment {

    private static RepaifInDetailFragment fragment;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.item1)
    TextView item1;
    @BindView(R.id.edit1)
    EditText edit1;
    @BindView(R.id.item3)
    TextView item3;
    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    private RecycleAdapter mAdapter;
    private ArrayList<RepaifIn> myList;
    private ArrayList<RepaifIn> dataList;
    private String sh_no;
    private String vat_no;

    public static RepaifInDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new RepaifInDetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repaif_in_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        edit1.setEnabled(false);
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
                for (RepaifIn repaif : myList)
                    repaif.setFlag(b);
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
                    sh_no = (String) msg.getPositionObj(0);
                    vat_no = (String) msg.getPositionObj(1);
                    dataList = (ArrayList<RepaifIn>) msg.getPositionObj(2);
                    text2.setText("送货单号：" + sh_no);
                    myList.clear();
                    for (RepaifIn repaif : dataList) {
                        if (vat_no.equals(repaif.getVat_no()))
                            myList.add(repaif);
                    }
                    text1.setText(String.valueOf(myList.size()));
                    mAdapter.notifyDataSetChanged();
                    break;
            }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post(new EventBusMsg(0x02, dataList));
        EventBus.getDefault().unregister(this);

    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<RepaifIn> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<RepaifIn> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final RepaifIn item, final int position) {
            if (item != null) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setFlag(b);

                    }
                });
                final EditText editText = holder.getView(R.id.edit1);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            String weight = editable.toString();
                            weight = weight.replaceAll(" ", "");
                            if (!TextUtils.isEmpty(weight)) {
                                double a = Double.parseDouble(weight);
                                item.setWeight(a);
                            } else {
                                item.setWeight(0.0);
                            }
                        } catch (Exception e) {
                            item.setWeight(0.0);
                            editText.setText("0");
                        }
                    }
                });
                checkBox.setChecked(item.isFlag());
                holder.setText(R.id.item1, item.getFab_roll());
                holder.setText(R.id.edit1, String.valueOf(item.getWeight()));
                holder.setText(R.id.item3, item.getEpc());
            }
        }
    }
}
