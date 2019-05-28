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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CutClothDetailFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {

    private final String TAG = "CutClothDetailFragment";

    private static CutClothDetailFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;


    public static CutClothDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothDetailFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private Sound sound;
    private List<Cloth> myList;
    private LinearLayoutManager ms;
    private JSONObject json;
    private List<String> epcList;
    private List<String> epcArray;
    private Cloth cloth;
    private BarCode barcode;
    private String clothVat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_matching_layout, container, false);
        ButterKnife.bind(this, view);
        initArray();
        clear();
        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_matching_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        initRFID();
        //注册中间件EventBus
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

//        Message msg = handler.obtainMessage();
//        msg.arg1 = 0x00;
//        msg.obj = "3035A53700002F0008380514";
//        handler.sendMessage(msg);
        return view;
    }

    //初始化数组
    private void initArray() {
        myList = new ArrayList<>();
        myList.add(new Cloth());
        json = new JSONObject();
        epcList = new ArrayList<>();
        epcArray = new ArrayList<>();
        cloth = new Cloth();
        barcode = new BarCode();
    }

    //清除缓存
    private void clear() {
        if (myList != null) {
            myList.clear();
            myList.add(new Cloth());
        }
        if (epcList != null)
            epcList.clear();
        if (epcArray != null)
            epcArray.clear();
        clothVat = "";
    }

    //RFID扫描
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cut_cloth_matching_item, null);
        mAdapter.setHeader(view);
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(JSONObject event) {
        json = event.getJSONObject("barcode");
        barcode = json.toJavaObject(BarCode.class);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);

    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }


    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
       /* Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);*/
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
    }

    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.arg1) {
                    case 0x00:
                        if (App.MUSIC_SWITCH) {
                            if (System.currentTimeMillis() - currenttime > 150) {
                                sound.callAlarm();
                                currenttime = System.currentTimeMillis();
                            }
                        }
                        String EPC = ((String) msg.obj).replaceAll(" ", "");
                        if (EPC.startsWith("3035A537") && !epcArray.contains(EPC)) {
                            JSONObject epc = new JSONObject();
                            epc.put("epc", EPC);
                            final String json = epc.toJSONString();
                            try{
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getEpc;" + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONArray jsonArray) {
                                        if (jsonArray != null && jsonArray.size() > 0) {
                                             cloth = jsonArray.getObject(0, Cloth.class);
                                            if (cloth != null && !epcArray.contains(cloth.getEpc())) {
                                                epcArray.add(cloth.getEpc());
                                                myList.add(cloth);
                                                System.out.println(myList);
                                                text1.setText(myList.size()-1+"");
                                                text2.setText(cloth.getVatNo());
                                            }
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }, json);
                            }catch (IOException e) {
                                Log.i(TAG, "");
                            }catch (Exception e){

                            }
                        }
                        break;
                }
            } catch (Exception e) {

            }
        }
    };

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clear();
                text1.setText("0");
                text2.setText(" ");
                mAdapter.select(-255);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                //进行数据关联
                String codeVat = barcode.getVatNo().replaceAll(""," ");
                if( codeVat.equals(clothVat))
                    blinkDialog();
                 else
                    Toast.makeText(getActivity(),"缸号不一致,请重新选择",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cloth> {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void setHeader(View header) {
            super.setHeader(header);
        }

        public RecycleAdapter(RecyclerView v, Collection<Cloth> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        private int index = -255;

        public void select(int index) {
            if (this.index == index)
                this.index = -255;
            else
                this.index = index;
        }

        @Override
        public void convert(final RecyclerHolder holder, final Cloth item, final int position) {
            final LinearLayout ll = holder.getView(R.id.layout1);
            if (item != null) {
                final CheckBox cb = holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position != 0) {
                            if (isChecked) {
                                epcList.clear();
                                if (!epcList.contains(item.getEpc())) {
                                    clothVat = item.getVatNo().replaceAll("", " ");
                                    epcList.add(item.getEpc());
                                }
                                ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                            } else {
                                if (epcList.contains(item.getEpc())) {
                                    clothVat = "";
                                    epcList.remove(item.getEpc());
                                }
                                ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                            }
                        }
                    }
                });

                if (position != 0) {
                    if (index == position) {
                        epcList.clear();
                        if (!epcList.contains(item.getEpc())){
                            epcList.add(item.getEpc());
                            clothVat = item.getVatNo().replaceAll("", " ");
                        }
                        cb.setChecked(true);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else {
                        if (epcList.contains(item.getEpc())) {
                            epcList.remove(item.getEpc());
                            clothVat = "";
                        }
                        cb.setChecked(false);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    }
                    holder.setText(R.id.item1, item.getFabRool() + "");
                    holder.setText(R.id.item2, item.getVatNo() + "");
                    holder.setText(R.id.item3, item.getProduct_no() + "");
                    holder.setText(R.id.item4, item.getColor() + "");
                    holder.setText(R.id.item5, item.getSelNo() + "");
                    holder.setText(R.id.item6,item.getWeight()+"");
                } else {
                    //隐藏表头checkBox
                    if (cb.isEnabled() != false)
                        cb.setEnabled(false);
                }
            }
        }
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否需要进行关联");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.select(0);
                mAdapter.notifyDataSetChanged();
                epcList.clear();
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                User user =  User.newInstance();
                jsonObject.put("userId",user.getId());
                jsonObject.put("applyNo",barcode.getOut_no());
                jsonObject.put("outp_id",barcode.getOutp_id());
                jsonObject.put("epc",epcList.get(0));

                final String json = jsonObject.toJSONString();
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCut.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "getEPC;" + e.getMessage());
                            }
                        }
                        @Override
                        public void onResponse(JSONObject response) {
                          Toast.makeText(getActivity(), response.getString("message"),Toast.LENGTH_SHORT).show();
                        }
                    }, json);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        });
    }


}
