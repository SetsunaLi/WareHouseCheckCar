package com.example.mumu.warehousecheckcar.fragment.chubb;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
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

import static com.example.mumu.warehousecheckcar.application.App.TIME;


/**
 * Created by mumu on 2019/1/14.
 */

public class ChubbFragment extends Fragment implements UHFCallbackLiatener, BRecyclerAdapter.OnItemClickListener {
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
    private final String TAG = "ChubbFragment";


    public static ChubbFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<InCheckDetail> myList;
    /**
     * 匹配机制应该是item分组字段
     */
//    private Map<String, Integer> keyValue;
    /*private List<InCheckDetail> dataList;*/
    //    勾选框
    private List<String> dataKey;
    //    epc
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
        getActivity().setTitle("查布");
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.chubb_item, null);
        mAdapter.setHeader(view);
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new InCheckDetail());
        }
       /* if (keyValue != null)
            keyValue.clear();*/
        if (dataKey != null)
            dataKey.clear();
      /*  if (dataList != null)
            dataList.clear();*/
        if (epcList != null)
            epcList.clear();
//        text1.setText(epcList.size()+"");
    }

    private void initData() {
        myList = new ArrayList<>();
        dataKey = new ArrayList<>();
//        dataList = new ArrayList<>();
        epcList = new ArrayList<>();
//        keyValue = new HashMap<>();

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
        disRFID();
        clearData();
        myList.clear();
    }
    long currenttime = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!(imgview.getVisibility()==View.GONE))
            imgview.setVisibility(View.GONE);
            if (App.MUSIC_SWITCH) {
                if (System.currentTimeMillis() - currenttime > 150) {
                    sound.callAlarm();
                    currenttime = System.currentTimeMillis();
                }
            }
            String EPC = (String) msg.obj;
            EPC = EPC.replace(" ", "");
            if (EPC.startsWith("3035A537") && !epcList.contains(EPC)) {
                JSONObject obj = new JSONObject();
                obj.put("epc", EPC);
                String json = obj.toJSONString();
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/getCheckByEpc", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "getEpc;" + e.getMessage());
                                Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(JSONObject object) {
                            try {
                            if (object.getJSONObject("data")!=null){
                                JSONObject obj=object.getJSONObject("data");
                                InCheckDetail value=obj.toJavaObject(InCheckDetail.class);
                                if (value != null) {
                                    if (!epcList.contains(value.getEpc())) {
                                        epcList.add(value.getEpc());
                                        myList.add(value);
                                        Collections.sort(myList, new Comparator<InCheckDetail>() {
                                            @Override
                                            public int compare(InCheckDetail obj1, InCheckDetail obj2) {
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

    @Override
    public void onItemClick(View view, Object data, int position) {

    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                if (!(imgview.getVisibility()==View.VISIBLE))
                imgview.setVisibility(View.VISIBLE);
                clearData();
                text1.setText("0");
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                blinkDialog();
                break;
        }
    }
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (dialog1!=null)
                if (dialog1.isShowing()) {
                    Button no = (Button) dialog1.findViewById(R.id.dialog_no);
                    no.setEnabled(true);
                }

        }
    };
    private Dialog dialog1;

    private void blinkDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        final Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        final Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认查布？");
        dialog1 = new AlertDialog.Builder(getActivity()).create();
        dialog1.show();
        dialog1.getWindow().setContentView(blinkView);
        dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                上传数据
                ArrayList<InCheckDetail> jsocList = new ArrayList<>();
                for (InCheckDetail obj : myList) {
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
                    AppLog.write(getActivity(),"chubb",json,AppLog.TYPE_INFO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/pushCheck", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                                try {
                                    AppLog.write(getActivity(),"chubb","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (ChubbFragment.this.dialog1.isShowing())
                                    ChubbFragment.this.dialog1.dismiss();
                                no.setEnabled(true);
                                handler.removeCallbacks(r);
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                    if (!(imgview.getVisibility()==View.VISIBLE))
                                        imgview.setVisibility(View.VISIBLE);
                                    clearData();
                                    mAdapter.notifyDataSetChanged();
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
                no.setEnabled(false);
                yes.setEnabled(false);
                handler.postDelayed(r,TIME);
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
    class RecycleAdapter extends BasePullUpRecyclerAdapter<InCheckDetail> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<InCheckDetail> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final InCheckDetail item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (InCheckDetail i: myList){
                                    if ((i.getVatNo()!=null&&i.getProduct_no()!=null&&i.getSelNo()!=null)
                                            &&!(i.getVatNo().equals("")||i.getProduct_no().equals("")||i.getSelNo().equals("")))
                                        dataKey.add(i.getEpc());
                                }
                            }else {
                                dataKey.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKey.contains(item.getEpc()))
                                    dataKey.add(item.getEpc());
                            } else {
                                if (dataKey.contains(item.getEpc()))
                                    dataKey.remove(item.getEpc());
                            }
                        }
                    }
                });
                String key=item.getEpc();
                if (position != 0) {
                    if (((item.getVatNo()+"").equals("")&&(item.getProduct_no()+"").equals("")&&(item.getSelNo()+"").equals(""))){
                        cb.setChecked(false);
                        if (cb.getVisibility()!=View.INVISIBLE)
                        cb.setVisibility(View.INVISIBLE);
                    }else {
                        if (cb.getVisibility()!=View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKey.contains(item.getEpc()))
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