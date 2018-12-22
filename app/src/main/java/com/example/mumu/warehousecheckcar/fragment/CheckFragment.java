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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.ItemMenu;
import com.example.mumu.warehousecheckcar.entity.MyTestEnt;
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

/**
 * Created by mumu on 2018/11/26.
 */

public class CheckFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener,UHFCallbackLiatener{

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

    private CheckFragment() {
    }

    public static CheckFragment newInstance() {
        if (fragment == null) ;
        fragment = new CheckFragment();
        return fragment;
    }

    //    private MyAdapter myAdapter;
    private RecycleAdapter mAdapter;
    private List<Inventory> myList;
    private List<Inventory> dataList;
    private List<String> epcList;
    private Map<String,Integer> keyValue;
    private List<String> dataKEY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_layout_upgrade, container, false);
        ButterKnife.bind(this, view);

        initData();
        clearData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.item_layout_1);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        点击事件可以改视图样式但不可恢复
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        return view;
    }

    private void initData() {
        myList=new ArrayList<>();
        dataList=new ArrayList<>();
        epcList=new ArrayList<>();
        keyValue=new HashMap<>();
        dataKEY=new ArrayList<>();
    }
    private void clearData(){
        if (myList != null) {
            myList.clear();
            myList.add(new Inventory());
        }
        if (dataList != null)
            dataList.clear();
        if (epcList != null)
            epcList.clear();
        if (keyValue != null)
            keyValue.clear();
        if (dataKEY != null)
            dataKEY.clear();

        text1.setText("0");
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_layout_1, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.CARRIER != null) {
            final String json= JSON.toJSONString(App.CARRIER);
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<List<Inventory>>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(List<Inventory> response) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 0x01;
                        msg.obj = response;
                        handler.sendMessage(msg);


                    }
                }, json);
            } catch (IOException e) {

            }
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
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.arg1){
                    case 0x01:
                        List<Inventory> response=(List<Inventory>)msg.obj;
                        if (response!=null&&response.size()!=0){

                        }
                        break;
                }
            }catch (Exception e){

            }
        }
    };
    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {

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

        protected int position = -255;

        public void select(int position) {
            if (this.position != -255 && this.position != position)
                this.position = position;
            else
                position = -255;
        }
        public void convert(RecyclerHolder holder, Inventory item, int position) {
            if (position != 0) {

            }
        }
    }
}
