package com.example.mumu.warehousecheckcar.fragment.car;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.RFIDReaderHelper;
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

import static com.example.mumu.warehousecheckcar.application.App.TIME;


public class EmptyShelfFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener,UHFCallbackLiatener{
    private final String TAG = "EmptyShelfFragment";
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private static EmptyShelfFragment fragment;

    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static EmptyShelfFragment newInstance() {
        if (fragment == null) ;
        fragment = new EmptyShelfFragment();
        return fragment;
    }

    private List<Carrier> myList;
    private RecycleAdapter mAdapter;
    private Sound sound;
    private List<String> dataKEY;
    private List<String> dataEpc;
    private List<String> list;
    private LinearLayoutManager ms;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_car_tuo));
        View view = inflater.inflate(R.layout.empty_shelf_layout, container, false);
        ButterKnife.bind(this, view);
        initData();
        clearData();
        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.empty_shelf_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

//        list.add("31B5A5AF6000004000000004");
//        list.add("31B5A5AF6000004000000005");
//        list.add("31B5A5AF6000004000000006");
//        list.add("31B5A5AF6000004000000007");
//        list.add("31B5A5AF6000004000000008");
//
//        for (int i = 0; i<list.size();i++) {
//            Message msg = handler.obtainMessage();
//            msg.arg1 = 0x00;
//            msg.obj = list.get(i);
//            handler.sendMessage(msg);
//        }
//
        return view;
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new Carrier());
        dataKEY = new ArrayList<>();
        dataEpc = new ArrayList<>();
        list = new ArrayList<>();


    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new Carrier());
        }
        if (dataKEY != null)
            dataKEY.clear();
        if (dataEpc != null)
            dataEpc.clear();

    }

    private void initRFID() {
        try {
            RFID_2DHander.getInstance().on_RFID();
            UHFResult.getInstance().setCallbackLiatener(this);
            rfidHander = RFID_2DHander.getInstance().getRFIDReader();
        } catch (Exception e) {

        }
    }

    private RFIDReaderHelper rfidHander;

    private void disRFID() {
        try {
            if (rfidHander != null) {
                int i = rfidHander.setOutputPower(RFID_2DHander.getInstance().btReadId, (byte) 20);
                if (i == 0)
                    App.PROWER = 20;
            }

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

    private void setAdaperHeader(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.empty_shelf_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disRFID();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //刷新数据
                clearData();
                //开启RFID扫描功能
                initRFID();
                //刷新列表
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
    private void blinkDialog(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        final Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        final Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否空托盘整理");
        dialog1 = new AlertDialog.Builder(getActivity()).create();
        dialog1.show();
        dialog1.getWindow().setContentView(blinkView);
        dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataKEY != null){
                    JSONObject jsonObject = new JSONObject();
                    User user = User.newInstance();
                    jsonObject.put("userId",user.getId());
                    jsonObject.put("pallets",dataKEY);
                    final  String json = jsonObject.toString();
                    try {
                        AppLog.write(getActivity(),"emptyshelf",json,AppLog.TYPE_INFO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/static/arrangePallets.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onError(Request request, Exception e) {
                                if (App.LOGCAT_SWITCH) {
                                    Log.i(TAG, "arrangePallets;" + e.getMessage());
                                }
                            }

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    try {
                                        AppLog.write(getActivity(),"emptyshelf","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (dialog1.isShowing())
                                        dialog1.dismiss();
                                    no.setEnabled(true);
                                    handler.removeCallbacks(r);
                                    BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                    if (baseReturn != null && baseReturn.getStatus() == 1) {
                                        Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                        clearData();
                                        mAdapter.notifyDataSetChanged();
//                                        blinkDialog2(true);
                                    } else {
                                        Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                        blinkDialog2(false);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }, json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
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

    private long currenttime = 0;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch(msg.arg1){
                    case 0x00:
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                        String epc = (String) msg.obj;
                        epc = epc.replaceAll(" ", "");
                        if (!dataEpc.contains(epc)) {
                            dataEpc.add(epc);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("epc",epc);
                            final  String json = jsonObject.toString();
                            try{
                                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getCarrier.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getCarrier;" + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if(response != null) {
                                            //转换成类添加到数组中显示在页面上
                                            Carrier value = response.toJavaObject(Carrier.class);
                                            myList.add(value);
                                        }
                                        text2.setText(myList.size()-1+"");
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }, json);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                    case 0x01:

                        break;
                }

            }catch(Exception e){

            }
        }
    }; 


    class RecycleAdapter extends BasePullUpRecyclerAdapter<Carrier> {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Carrier> datas, int itemLayoutId) {
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
        public void convert(RecyclerHolder holder, final Carrier item, final int position) {
            if (item != null) {
                final  LinearLayout ll = holder.getView(R.id.layout1);
               final CheckBox cb = holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Carrier i : myList) {
                                    if(i.getTrayNo() != null) {
                                        dataKEY.add(i.getTrayEPC());
                                    }
                                }
                            } else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getTrayEPC()))
                                    dataKEY.add(item.getTrayEPC());
                            } else {
                                if (dataKEY.contains(item.getTrayEPC()))
                                    dataKEY.remove(item.getTrayEPC());
                            }
                        }
                    }
                });

                if (position != 0) {
                    if ((item.getTrayNo() + "").equals("") && (item.getTrayEPC() + "").equals("")) {
                        cb.setChecked(false);
                        if (cb.getVisibility() != View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    } else {
                        if (cb.getVisibility() != View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKEY.contains(item.getTrayEPC())) {
                            cb.setChecked(true);
                            ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                        }else {
                            cb.setChecked(false);
                            ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                        }
                    }

                    if (index == position) {
                        System.out.println(dataKEY);
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    }else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));

                    holder.setText(R.id.item1, item.getTrayNo() + "");
                    holder.setText(R.id.item2, item.getTrayEPC() + "");
                    holder.setText(R.id.item3, item.getLocationNo() + "");
                    holder.setText(R.id.item4, item.getLocationEPC() + "");
                }
            }
        }
    }


}
