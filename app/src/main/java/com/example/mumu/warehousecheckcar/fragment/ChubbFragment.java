package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.entity.Input;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.mumu.warehousecheckcar.application.App.DATA_KEY;

/**
 * Created by mumu on 2019/1/14.
 */

public class ChubbFragment extends Fragment implements UHFCallbackLiatener,BRecyclerAdapter.OnItemClickListener {
    private static ChubbFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.imgview)
    ImageView imgview;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    private ChubbFragment() {
    }

    public static ChubbFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbFragment();
        return fragment;
    }
    private RecycleAdapter mAdapter;
    private List<Input> myList;
    /**
     * 匹配机制应该是item分组字段
     */
    private Map<String, Integer> keyValue;
    private List<Input> dataList;
    private List<String> dataKey;
    private List<String> epcList;
    private LinearLayoutManager llm;
    private Sound sound;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubb_layout, container, false);
        ButterKnife.bind(this, view);

        initData();
        clearData();
        text1.setText(epcList.size() + "");
        sound = new Sound(getActivity());

        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubb_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);

        initRFID();
        return view;
    }
    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.putaway_item, null);
        ((CheckBox) view.findViewById(R.id.checkbox1)).setVisibility(View.INVISIBLE);
        mAdapter.setHeader(view);
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Input());
        }
        if (keyValue != null)
            keyValue.clear();
        if (dataKey != null)
            dataKey.clear();
        if (dataList != null)
            dataList.clear();
        if (epcList != null)
            epcList.clear();
//        text1.setText(epcList.size()+"");
    }

    private void initData() {
        myList = new ArrayList<>();
        dataKey = new ArrayList<>();
        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
        keyValue = new HashMap<>();

    }

    private void initRFID() {
        try {
            RFID_2DHander.getInstance().on_RFID();
            UHFResult.getInstance().setCallbackLiatener(this);
        } catch (Exception e) {

        }
    }

    private void disRFID() {
        try {
            RFID_2DHander.getInstance().off_RFID();
        } catch (Exception e) {

        }
    }
    //这里写界面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //右上角列表R.menu.main2
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
    }

    //右上角列表点击监听（相当于onclickitemlistener,可用id或者title匹配）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {

    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @Override
    public void onItemClick(View view, Object data, int position) {

    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Input> {
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

        public RecycleAdapter(RecyclerView v, Collection<Input> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final Input item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                if (position != 0) {
                    if (cb.isChecked()) {
                        if (!dataKey.contains(item.getVatNo()))
                            dataKey.add(item.getVatNo());
                    } else {
                        if (dataKey.contains(item.getVatNo()))
                            dataKey.remove(item.getVatNo());
                    }
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.position == position) {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));

                    holder.setText(R.id.item1, item.getProduct_no() + "");
                    holder.setText(R.id.item2, item.getSelNo() + "");
                    holder.setText(R.id.item3, item.getColor() + "");
                    holder.setText(R.id.item4, item.getVatNo() + "");
                    holder.setText(R.id.item5, item.getCount() + "");
                    holder.setText(R.id.item6, item.getWeightall() + "");
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            for (int i = 1; i < myList.size(); i++) {
                                View view = llm.findViewByPosition(i);
                                CheckBox c = (CheckBox) view.findViewById(R.id.checkbox1);
                                c.setChecked(isChecked);
                            }
                        } else {
                            if (isChecked) {
                                if (!dataKey.contains(item.getVatNo()))
                                    dataKey.add(item.getVatNo());
                            } else {
                                if (dataKey.contains(item.getVatNo()))
                                    dataKey.remove(item.getVatNo());
                            }
                        }
                    }
                });
            }
        }
    }
}
