package com.example.mumu.warehousecheckcar.fragment;

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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.activity.Main2Activity;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.OutCheckDetail;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.example.mumu.warehousecheckcar.application.App.carNo;

/**
 * Created by mumu on 2018/12/9.
 */

public class OutCheckFragment extends Fragment implements UHFCallbackLiatener {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button3)
    Button button3;
  /*  @Bind(R.id.text3)
    TextView text3;*/

    private OutCheckFragment() {
    }

    private final String TAG = "OutCheckFragment";

    public static OutCheckFragment newInstance() {
        return new OutCheckFragment();
    }

    private RecycleAdapter mAdapter;
    private List<OutCheckDetail> myList;
    /**
     * 匹配逻辑
     * key：response.getVatNo()+response.getProduct_no()+response.getSelNo()+response.getColor()
     * value：index
     */
    private Map<String, Integer> strIndex;
    private List<OutCheckDetail> dataList;
//    private List<String> epcList;
    private List<String> dataEPC;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_check_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());
        myList = new ArrayList<>();
        strIndex = new HashMap<>();
//        epcList = new ArrayList<>();
        dataEPC = new ArrayList<>();
        dataList = new ArrayList<>();
        clearData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_check_item_layout);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        initView();
        initRFID();
        return view;
    }

    public void initView() {
        text1.setText("0");
        if (carNo != null)
            text2.setText(carNo + "");
        else
            text2.setText("");
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

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new OutCheckDetail());
        }
        if (dataList!=null)
            dataList.clear();
        /*if (epcList != null)
            epcList.clear();*/
        if (dataEPC!=null)
            dataEPC.clear();
        if (strIndex!=null)
            strIndex.clear();

        text1.setText("0");
//        text3.setText("0");
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.in_check_item_layout, null);
        mAdapter.setHeader(view);
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

    //    主页返回执行

    public void onBackPressed() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    protected static final String TAG_RETURN_FRAGMENT = "TitleFragment";
    long currenttime = 0;
//    int error = 0;
    Handler handler = new Handler() {
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
                    String EPC = (String) msg.obj;
                    EPC.replace(" ", "");
                    EPC.replace("\"", "");
                    if (!dataEPC.contains(EPC)) {
//                        epcList.add(EPC);
//                        查询
                        String decice=App.DEVICE_NO;
                        final String json = JSON.toJSONString(EPC);
//                        https://192.168.43.193/shYf/sh/rfid/index.sh?shmodule_id=56
//                        http://192.168.43.193:8080/shYf/sh/rfid/outEpc.sh?shmodule_id=56
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                   /* Response response=OkHttpClientManager.postJsonAsyn(App.IP+":"+App.PORT+"/shYf/sh/rfid/outEpc.sh",json);
                                    boolean send=response.isSuccessful();*/
//                                    OkHttpClientManager.postJsonAsyn("https://"+App.IP, new OkHttpClientManager.ResultCallback<OutCheckDetail>() {
                                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<ArrayList<OutCheckDetail>>() {
                                        //                                        outDetail.sh
                                        @Override
                                        public void onError(Request request, Exception e) {
                                            Log.i("EPC", "onError");
                                        }

                                        @Override
                                        public void onResponse(ArrayList<OutCheckDetail> response) {
                                            Log.i("EPC", "onResponse");
                                            try {
                                                if (response != null && response.size() != 0) {
                                                    OutCheckDetail ocd = response.get(0);
                                                    if (ocd != null) {
                                                        ocd.setCarNo(App.carNo);
                                                        if (ocd.getEpc() != null && !dataEPC.contains(ocd.getEpc())) {
                                                            dataEPC.add(ocd.getEpc());
                                                            dataList.add(ocd);
                                                            String key = ocd.getVatNo() + ocd.getProduct_no()
                                                                    + ocd.getSelNo() + ocd.getColor() + "";
                                                            if (!strIndex.containsKey(key)) {//当前没有
                                                                ocd.setCount(1);
                                                                myList.add(ocd);
                                                                strIndex.put(key, myList.size() - 1);
                                                            } else {
                                                                int index = strIndex.get(key);
                                                                myList.get(index).addCount();
                                                            }
                                                        }
                                                    }
                                                } else {
//                                                error++;
                                               /* OutCheckDetail ocd=new OutCheckDetail();
                                                ocd.setCount();
                                                    myList.add(new OutCheckDetail);*/
//                                                显示错误
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    text1.setText("" + (dataList.size()));
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }, json);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                    break;
                case 0x02:
                    Toast.makeText(getActivity(),"上传成功",Toast.LENGTH_LONG).show();
                    clearData();
                    mAdapter.notifyDataSetChanged();
                    break;
                case 0x03:
                    Toast.makeText(getActivity(),"上传失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @OnClick({R.id.button1, R.id.button2, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                blinkDialog();
                break;
            case R.id.button3:
//                完成一车
                ((Main2Activity) getActivity()).showProgress(true);
                getFragmentManager().popBackStack();
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
        text.setText("是否确认出库？");
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
//                上传一次
                List<OutCheckDetail> list = new ArrayList<OutCheckDetail>();
                for (OutCheckDetail acd : dataList) {
                    acd.setDevice(App.DEVICE_NO+"");
                    list.add(acd);
                }
                final String json = JSON.toJSONString(list);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/outDetail.sh", new OkHttpClientManager.ResultCallback<String>() {
                                @Override
                                public void onError(Request request, Exception e) {

                                }

                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("1")){
                                        Message msg=handler.obtainMessage();
                                        msg.arg1=0x02;
                                        handler.sendMessage(msg);
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.arg1=0x03;
                                        handler.sendMessage(msg);
                                    }

                                }
                            },json);
                           /* if (response.isSuccessful()) {
//                                上传成功
                                String result = JSON.toJSONString(response);
                            } else {
//                                上传失败
                            }*/
                        } catch (IOException e) {

                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
//        if (!epcList.contains(tag.strEPC)) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);

//        }
        Log.i(TAG, tag.strEPC);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }


    class RecycleAdapter extends BasePullUpRecyclerAdapter<OutCheckDetail> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<OutCheckDetail> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, OutCheckDetail item, int position) {
            if (position != 0) {
                if (item != null) {
                    if (item.getVatNo()==null) {
                        LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }
//                        holder.setBackground(R.id.layout1,getResources().getColor(R.color.colorAccent));
                    holder.setText(R.id.item1, item.getProduct_no() + "");
                    holder.setText(R.id.item2, item.getVatNo() + "");
                    holder.setText(R.id.item3, item.getColor() + "");
                    holder.setText(R.id.item4, item.getSelNo() + "");
                    holder.setText(R.id.item5, item.getCount() + "");
                }
            }
        }
    }
}
