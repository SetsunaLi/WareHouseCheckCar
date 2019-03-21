package com.example.mumu.warehousecheckcar.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.CHECK_DETAIL_LIST;

/**
 * Created by mumu on 2019/1/24.
 */

public class TextFragment extends Fragment {

    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    private final String TAG = "CheckFragment";

    private static CheckFragment fragment;

    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;

    private RecycleAdapter mAdapter;
    private List<Inventory> myList;
    //    缸号匹配位置
    private Map<String, Integer> keyValue;
    private List<String> dataKEY;
    private LinearLayoutManager ms;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_layout_upgrade, container, false);
        ButterKnife.bind(this, view);
        initData();
        clearData();
        loadData();
        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.check_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        点击事件可以改视图样式但不可恢复
        ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        return view;
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new Inventory());
        keyValue = new HashMap<>();
        dataKEY = new ArrayList<>();
        text1.setText(0 + "");

        if (App.CARRIER != null) {
            if (App.CARRIER.getLocationNo() != null)
                text2.setText(App.CARRIER.getLocationNo() + "");
            if (App.CARRIER.getTrayNo() != null)
                text3.setText(App.CARRIER.getTrayNo() + "");
        }

    }

    public void loadData() {
        for (int i = 0; i < 100; i++) {
            Inventory asd = new Inventory();
            asd.setVatNo("" + i);
            myList.add(asd);
            keyValue.put("" + i, i);
        }
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Inventory());
        }
        if (keyValue != null)
            keyValue.clear();
        if (dataKEY != null)
            dataKEY.clear();

//        text1.setText("0");
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_item, null);
//        ((CheckBox) view.findViewById(R.id.checkbox1)).setVisibility(View.INVISIBLE);
        mAdapter.setHeader(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                loadData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                break;
        }
    }


    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Inventory> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<Inventory> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public void convert(RecyclerHolder holder, final Inventory item, final int position) {
            if (item != null) {
                final CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (Inventory i: myList){
                                    if (!(i.getVatNo()+"").equals("")&&(i.getProduct_no()+"").equals("")&&(i.getSelNo()+"").equals(""))
                                        dataKEY.add(i.getVatNo());
                                }
                            }else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getVatNo()))
                                    dataKEY.add(item.getVatNo());
                            } else {
                                if (dataKEY.contains(item.getVatNo()))
                                    dataKEY.remove(item.getVatNo());
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (((item.getVatNo() + "").equals("") && (item.getProduct_no() + "").equals("") && (item.getSelNo() + "").equals(""))) {
                        cb.setChecked(false);
                        if (cb.getVisibility() != View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    } else {
                        if (cb.getVisibility() != View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKEY.contains(item.getVatNo()))
                            cb.setChecked(true);
                    }


                    /*if (cb.isChecked()) {
                        if (!dataKEY.contains(item.getVat_no()))
                            dataKEY.add(item.getVat_no());
                    } else {
                        if (dataKEY.contains(item.getVat_no()))
                            dataKEY.remove(item.getVat_no());
                    }*/
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (item.getFlag() == 1)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getProduct_no() + "");
                    holder.setText(R.id.item2, item.getSelNo() + "");
                    holder.setText(R.id.item3, item.getColor() + "");
                    holder.setText(R.id.item4, item.getVatNo() + "");
                    holder.setText(R.id.item5, item.getCountIn() + "");
                    holder.setText(R.id.item6, item.getCountReal() + "");
                    holder.setText(R.id.item7, item.getCountProfit() + "");
                    holder.setText(R.id.item8, item.getCountIn() - item.getCountReal() + "");
                }

            }
        }
    }
}
