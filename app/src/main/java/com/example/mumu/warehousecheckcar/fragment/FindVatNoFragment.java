package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.mumu.warehousecheckcar.entity.FindVatNo;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2019/1/21.
 */

public class FindVatNoFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {
    private static FindVatNoFragment fragment;
    @Bind(R.id.fixeedittext1)
    FixedEditText fixeedittext1;
    /*@Bind(R.id.fixeedittext0)
    FixedEditText fixeedittext0;
    @Bind(R.id.fixeedittext2)
    FixedEditText fixeedittext2;*/
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button)
    Button button;

    private FindVatNoFragment() {
    }

    public static FindVatNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new FindVatNoFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<FindVatNo> myList;
    private List<String> dataKEY;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_vatno_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();


        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.find_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
//        点击事件可以改视图样式但不可恢复
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        initRFID();
        return view;
    }

    private void initView() {
//        fixeedittext0.setFixedText("入库单号：");
        fixeedittext1.setFixedText("缸号：");
//        fixeedittext2.setFixedText("布号：");
        fixeedittext1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    onViewClicked(button);
                    return true;
                }
                return false;
            }
        });
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.find_item, null);
        mAdapter.setHeader(view);
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new FindVatNo());
        dataKEY = new ArrayList<>();
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new FindVatNo());
        }
        if (dataKEY != null)
            dataKEY.clear();
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
        clearData();
        myList.clear();
        disRFID();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
           /* if (mAdapter.getPosition() == position) {
                dataKEY.clear();
                for (FindVatNo i : myList) {
                    if (i != null && i.getEpc() != null && !i.getEpc().equals(""))
                        dataKEY.add(i.getEpc());
                }
            } else {
                if (myList.get(position) != null && myList.get(position).getEpc() != null && !myList.get(position).getEpc().equals("")) {
                    dataKEY.clear();
                    dataKEY.add(myList.get(position).getEpc());
                }
            }*/
            mAdapter.selectItem(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private long currenttime = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String epc = (String) msg.obj;
            epc = epc.replaceAll("", "");
            if (dataKEY.contains(epc)) {
                if (System.currentTimeMillis() - currenttime > 150) {
                    sound.callAlarm();
                    currenttime = System.currentTimeMillis();
                }
            }
        }
    };

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = handler.obtainMessage();
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @OnClick(R.id.button)
    public void onViewClicked(View view) {
        goFind();
    }

    private void goFind(){
        String vatNo=fixeedittext1.getText().toString()+"";
        vatNo=vatNo.replaceAll(" ","");
        if(vatNo!=null&&!vatNo.equals("")) {
            JSONObject object = new JSONObject();
            object.put("vatNo", vatNo);
            final String json = object.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getInventoryByVatNo", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Toast.makeText(getActivity(), "缸号查询失败！" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<FindVatNo> response;
                                response = jsonArray.toJavaList(FindVatNo.class);
                                if (response != null && response.size() != 0) {
                                    clearData();
                                    myList.addAll(response);
                                    for (FindVatNo i : response) {
                                        if (i != null && i.getEpc() != null && !i.getEpc().equals(""))
                                            dataKEY.add(i.getEpc());
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getActivity(), "没查到缸号对应布匹！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "缸号查询失败！", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class RecycleAdapter extends BasePullUpRecyclerAdapter<FindVatNo> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<FindVatNo> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        private int position = -255;

        public void selectItem(int position) {
            if (this.position == position)
                this.position = -255;
            else
                this.position = position;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public void convert(RecyclerHolder holder, final FindVatNo item, final int position) {
            if (item != null) {
                if (position != 0) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.position == position)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));

                    holder.setText(R.id.item1,item.getLocation_name()+"");
                    holder.setText(R.id.item2,item.getPallet_name()+"");
                    holder.setText(R.id.item4,item.getCloth_name()+"");
                    holder.setText(R.id.item5,item.getInv_serial()+"");
                    holder.setText(R.id.item6,item.getWeight_inv()+"");
                    holder.setText(R.id.item7,item.getColor_name()+"");
                    holder.setText(R.id.item8,item.getEpc()+"");
                }

            }
        }
    }
}
