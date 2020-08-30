package com.example.mumu.warehousecheckcar.fragment.outsource_in;

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
import com.example.mumu.warehousecheckcar.entity.Outsource;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 *created by 
 *on 2020/8/29
 */
public class In_OutSourceDetailFragment extends BaseFragment {
    private static In_OutSourceDetailFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.checkbox1)
    CheckBox checkbox1;
    @Bind(R.id.edittext1)
    EditText edittext1;

    public static In_OutSourceDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new In_OutSourceDetailFragment();
        return fragment;
    }

    private List<Outsource> myList;
    private RecycleAdapter mAdapter;
    private String no;
    private String vatNo;
    private String colorNo;
    private String product;
    private String color;
    private int position;

    @Override
    protected void initData() {
        myList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_outsource_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        edittext1.setEnabled(false);
    }

    @Override
    protected void addListener() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (Outsource outsource : myList) {
                    if (outsource.isScan())
                        outsource.setFlag(b);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe(sticky = true)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x01:
//                    (0x01, vatNo, colorNo,product,color,position,list)
                    vatNo = (String) msg.getPositionObj(0);
                    colorNo = (String) msg.getPositionObj(1);
                    product = (String) msg.getPositionObj(2);
                    color = (String) msg.getPositionObj(3);
                    position = (int) msg.getPositionObj(4);
                    myList.addAll((List<Outsource>) msg.getPositionObj(5));
                    mAdapter.notifyDataSetChanged();
                    text1.setText(vatNo);
                    text2.setText(colorNo);
                    text3.setText(product);
                    text4.setText(color);
                    break;
            }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_outsource_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().post(new EventBusMsg(0x00, position, myList));
        EventBus.getDefault().unregister(this);
    }


    class RecycleAdapter extends BasePullUpRecyclerAdapter<Outsource> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Outsource> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final Outsource item, int position) {
            CheckBox checkBox = holder.getView(R.id.checkbox1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setFlag(b);
                }
            });
            checkBox.setEnabled(item.isScan());
            checkBox.setChecked(item.isFlag());
            LinearLayout linearLayout = holder.getView(R.id.layout1);
            linearLayout.setBackgroundColor(item.isScan() ? getResources().getColor(R.color.colorDialogTitleBG) : getResources().getColor(R.color.colorZERO));
            final EditText editText = holder.getView(R.id.edittext1);
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
            holder.setText(R.id.item1, "null");
            holder.setText(R.id.edittext1, String.valueOf(item.getWeight()));

        }
    }
}