package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChubbExceptionFragment extends Fragment implements UHFCallbackLiatener {
    private final String TAG = "ChubbExceptionFragment";
    private static ChubbExceptionFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;



    public static ChubbExceptionFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbExceptionFragment();
        return fragment;
    }
//    列表
    private List<Inventory> myList;
//    勾选框
    private List<String> dataKey;
//    epc
    private List<String> epcList;
    private Sound sound;
    private RecycleAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubb_exception_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("查布异常布匹");
        initData();
        sound = new Sound(getActivity());

        mAdapter=new RecycleAdapter(recyle,myList,R.layout.chubb_item);
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        initRFID();
        return view;
    }
    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.chubb_item, null);
        mAdapter.setHeader(view);
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
    private void initData(){
        myList = new ArrayList<>();
        myList.add(new Inventory());
        dataKey = new ArrayList<>();
        epcList = new ArrayList<>();
    }
    private void clearData(){
        if (myList != null) {
            myList.clear();
            myList.add(new Inventory());
        }
        if (dataKey != null)
            dataKey.clear();
        if (epcList != null)
            epcList.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
        clearData();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                text1.setText("0");
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
        text.setText("是否确认查布？");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
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
                for (Inventory obj : myList) {
                    String key = obj.getEpc();
                    if (key != null && dataKey.contains(key)) {
                        jsocList.add(obj);
                    }

                }
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("data",jsocList);
                jsonObject.put("userId", User.newInstance().getId());
                final String json = JSON.toJSONString(jsonObject);
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/pushErrorStateCloth", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "postInventory;" + e.getMessage());
                                Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
//                                    blinkDialog2(true);
                                } else {
                                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                    blinkDialog2(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
    private AlertDialog dialog;
    private void blinkDialog2(boolean flag) {
        if (dialog == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
            Button no = (Button) blinkView.findViewById(R.id.dialog_no);
            Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
            TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
            if (flag)
                text.setText("上传成功");
            else
                text.setText("上传失败");

            dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.show();
            dialog.getWindow().setContentView(blinkView);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
            if (flag)
                text.setText("上传成功");
            else
                text.setText("上传失败");
            if (!dialog.isShowing())
                dialog.show();
        }
    }
    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (App.MUSIC_SWITCH) {
                if (System.currentTimeMillis() - currenttime > 150) {
                    sound.callAlarm();
                    currenttime = System.currentTimeMillis();
                }
            }
            String EPC = (String) msg.obj;
            EPC = EPC.replace(" ", "");
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
                            try{
                                List<Inventory> arry;
                                arry = jsonArray.toJavaList(Inventory.class);
                                if (arry != null && arry.size() > 0) {
                                    Inventory response = arry.get(0);
                                    if (response != null) {
                                        if (response.getEpc() != null && !epcList.contains(response.getEpc())) {
                                            epcList.add(response.getEpc());
                                            myList.add(response);
                                            Collections.sort(myList, new Comparator<Inventory>() {
                                                @Override
                                                public int compare(Inventory obj1, Inventory obj2) {
                                                    String aFab = obj1.getFabRool();
                                                    String bFab = obj2.getFabRool();
                                                    if (aFab == null)
                                                        return -1;
                                                    if (bFab == null)
                                                        return 1;
                                                    if (aFab.equals(""))
                                                        return 1;
                                                    if (bFab.equals(""))
                                                        return -1;
                                                    if (aFab != null && bFab != null) {
                                                        if (Integer.valueOf(aFab) >= Integer.valueOf(bFab)) {
                                                            return 1;
                                                        }
                                                        return -1;
                                                    }
                                                    return 0;
                                                }
                                            });
                                        }
                                        text1.setText(epcList.size() + "");
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, json);
                } catch (IOException e) {
                    Log.i(TAG, "");
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
    class RecycleAdapter extends BasePullUpRecyclerAdapter<Inventory> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Inventory> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, Inventory item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
               final String key=item.getEpc();
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (Inventory i: myList){
                                    if ((i.getVatNo()!=null&&i.getProduct_no()!=null&&i.getSelNo()!=null)
                                            &&!(i.getVatNo().equals("")||i.getProduct_no().equals("")||i.getSelNo().equals(""))&&!dataKey.contains(i.getEpc()))
                                        dataKey.add(i.getEpc());
                                }
                            }else {
                                dataKey.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKey.contains(key))
                                    dataKey.add(key);
                            } else {
                                if (dataKey.contains(key))
                                    dataKey.remove(key);
                            }
                        }
                    }
                });
                if (position != 0) {
                    if (((item.getVatNo()+"").equals("")&&(item.getProduct_no()+"").equals("")&&(item.getSelNo()+"").equals(""))){
                        cb.setChecked(false);
                        if (cb.getVisibility()!=View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    }else {
                        if (cb.getVisibility()!=View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKey.contains(key))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }

//                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);

                    holder.setText(R.id.item1, item.getFabRool() + "");
                    holder.setText(R.id.item2, item.getVatNo() + "");
                    holder.setText(R.id.item3, item.getProduct_no() + "");
                    holder.setText(R.id.item4, item.getSelNo() + "");
                    holder.setText(R.id.item5, item.getColor() + "");
                    holder.setText(R.id.item6, item.getWeight() + "");
                }

            }
        }
    }
}
