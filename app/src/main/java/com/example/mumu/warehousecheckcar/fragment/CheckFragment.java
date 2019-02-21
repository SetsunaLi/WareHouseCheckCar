package com.example.mumu.warehousecheckcar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
 * Created by mumu on 2018/11/26.
 */

public class CheckFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {

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
    //    缸号匹配位置
    private Map<String, Integer> keyValue;
    private List<Inventory> dataList;
    private List<String> dataKEY;
    private List<String> epcList;
    private LinearLayoutManager ms;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_layout_upgrade, container, false);
        ButterKnife.bind(this, view);
        initData();
        clearData();
        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.check_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        点击事件可以改视图样式但不可恢复
        mAdapter.setOnItemClickListener(this);
        ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        initRFID();
        return view;
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new Inventory());
        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
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

    private void clearData() {
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

//        text1.setText("0");
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

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_item, null);
//        ((CheckBox) view.findViewById(R.id.checkbox1)).setVisibility(View.INVISIBLE);
        mAdapter.setHeader(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        downLoadData();
    }

    private void downLoadData() {
        if (App.CARRIER != null) {
            clearData();
            text1.setText(0 + "");
            final String json = JSON.toJSONString(App.CARRIER);
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getInventory", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getCarrier;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(getActivity(), "获取库位信息失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try{
                            if (jsonObject.get("data")!=null && jsonObject.getIntValue("status")==1){
                            JSONArray jsonArray =jsonObject.getJSONArray("data");
                            List<Inventory> response;
                            response = jsonArray.toJavaList(Inventory.class);
                            if (response != null && response.size() != 0) {
                                clearData();
                                for (Inventory obj : response) {
                                    if (obj != null && obj.getVatNo() != null) {
                                        if (keyValue.containsKey(obj.getVatNo())) {//里面有
                                            myList.get(keyValue.get(obj.getVatNo())).addCountIn();//增加库存量
                                        } else {//里面没有
                                            obj.setCountIn(1);
                                            myList.add(obj);
                                            keyValue.put(obj.getVatNo(), myList.size() - 1);
                                            dataKEY.add(obj.getVatNo());
                                        }
                                        obj.setFlag(0);//默认为0//0为盘亏
                                        dataList.add(obj);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "该仓位没有库存！", Toast.LENGTH_SHORT).show();
                            }
                            }else {
                                Toast.makeText(getActivity(), "至少需要输入一个有效库位信息！", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        }catch (Exception e){

                       }
                    }
                }, json);
            } catch (IOException e) {

            } catch (Exception e) {

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
        disRFID();
        clearData();
        myList.clear();
        CHECK_DETAIL_LIST.clear();
//        App.CARRIER = null;
    }

    long currenttime = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                switch (msg.arg1) {
                    case 0x00:
                        if (App.MUSIC_SWITCH) {
                            if (System.currentTimeMillis() - currenttime > 150) {
                                sound.callAlarm();
                                currenttime = System.currentTimeMillis();
                            }
                        }
                        String EPC = ((String) msg.obj).replaceAll(" ", "");
                        if (EPC.startsWith("3035A537")&&!epcList.contains(EPC)) {
                            JSONObject epc = new JSONObject();
                            epc.put("epc", EPC);
                            final String json = epc.toJSONString();
                            try {
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getEpc;" + e.getMessage());
                                            Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONArray jsonArray) {
                                       try {
                                            List<Inventory> arry;
                                            arry = jsonArray.toJavaList(Inventory.class);
                                            if (arry != null && arry.size() > 0) {
                                                Inventory response = arry.get(0);
                                                if (response != null && !epcList.contains(response.getEpc())) {
                                                    epcList.add(response.getEpc());
                                                    boolean isData = false;
                                                    for (Inventory obj : dataList) {
                                                        if (obj.getEpc().equals(response.getEpc())) {//正常
                                                            isData = true;
                                                            obj.setFlag(2);
                                                        }
                                                    }
                                                    if (!isData) {//盘盈
                                                        response.setFlag(1);
                                                        dataList.add(response);
                                                    }
                                                    if (keyValue.containsKey(response.getVatNo())) {
                                                        if (isData)
                                                            myList.get(keyValue.get(response.getVatNo())).addCountReal();
                                                        else
                                                            myList.get(keyValue.get(response.getVatNo())).addCountProfit();
                                                    } else {
                                                        response.addCountProfit();
                                                        response.setFlag(1);
                                                        myList.add(response);
                                                        keyValue.put(response.getVatNo(), myList.size() - 1);
                                                        dataKEY.add(response.getVatNo());
                                                    }
                                                }
                                            }
                                            text1.setText(epcList.size() + "");
                                            mAdapter.notifyDataSetChanged();
                                        }catch (Exception e){

                                       }
                                    }
                                }, json);
                            } catch (IOException e) {
                                Log.i(TAG, "");
                            }
                        }
                        break;
                }
        }
    };

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                downLoadData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                blinkDialog();
                break;
        }
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否上传盘点数据");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                上传数据
                ArrayList<Inventory> jsocList = new ArrayList<>();
                for (Inventory obj : dataList) {
                    if (obj.getVatNo() != null && dataKEY.contains(obj.getVatNo())) {
                        Carrier c=new Carrier();
                        c.setTrayNo(App.CARRIER.getTrayNo());
                        c.setLocationNo(App.CARRIER.getLocationNo());
                        c.setLocationEPC(App.CARRIER.getLocationEPC());
                        c.setTrayEPC(App.CARRIER.getTrayEPC());
                        c.setId(App.CARRIER.getId());
                        obj.setDevice(App.DEVICE_NO);
                        obj.setCarrier(c);
                        jsocList.add(obj);
                    }
                }
                final String json = JSON.toJSONString(jsocList);
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/postInventory.sh", new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "postInventory;" + e.getMessage());
                                Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(String response) {
                            try{
                                if (response.equals("1")) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                    clearData();
                                    mAdapter.notifyDataSetChanged();
                                    //调用Activity中onBackPressed()方法
                                    getActivity().onBackPressed();
                                } else {
                                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){

                            }
                        }
                    }, json);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.notifyDataSetChanged();
            Inventory icd = myList.get(position);
            String key = icd.getVatNo();
            CHECK_DETAIL_LIST.clear();
//            IN_DETAIL_LIST.add(new InCheckDetail());//增加一个为头部
            for (Inventory obj : dataList) {
                if (obj != null && obj.getVatNo() != null && obj.getVatNo().equals(key)) {
                    CHECK_DETAIL_LIST.add(obj);
                }
            }
            Fragment fragment = CheckDetailFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);
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

        public void convert(RecyclerHolder holder, final Inventory item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (Inventory i: myList){
                                    if ((i.getVatNo()!=null&&i.getProduct_no()!=null&&i.getSelNo()!=null)
                                            &&!(i.getVatNo().equals("")||i.getProduct_no().equals("")||i.getSelNo().equals("")))
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
                    if ((item.getVatNo()+"").equals("")&&(item.getProduct_no()+"").equals("")&&(item.getSelNo()+"").equals("")){
                        cb.setChecked(false);
                        if (cb.getVisibility()!=View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    }else {
                        if (cb.getVisibility()!=View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKEY.contains(item.getEpc()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }

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
