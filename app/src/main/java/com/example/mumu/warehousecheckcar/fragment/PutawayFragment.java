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
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Input;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
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

import static com.example.mumu.warehousecheckcar.application.App.INPUT_DETAIL_LIST;
import static com.example.mumu.warehousecheckcar.application.App.TIME;

/**
 * Created by mumu on 2019/1/8.
 */

public class PutawayFragment extends Fragment implements UHFCallbackLiatener, BasePullUpRecyclerAdapter.OnItemClickListener {
    private static PutawayFragment fragment;
    private final String TAG = "PutawayFragment";
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;



    public static PutawayFragment newInstance() {
        if (fragment == null) ;
        fragment = new PutawayFragment();
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

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.putaway_layout, container, false);
        ButterKnife.bind(this, view);

        initData();
        clearData();
        text1.setText(epcList.size() + "");
        sound = new Sound(getActivity());

        mAdapter = new RecycleAdapter(recyle, myList, R.layout.putaway_item);
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

    @Override
    public void onResume() {
        super.onResume();
        text1.setText(0 + "");
        if (App.CARRIER != null) {
            if (App.CARRIER.getLocationNo() != null)
                text2.setText(App.CARRIER.getLocationNo() + "");
            if (App.CARRIER.getTrayNo() != null)
                text3.setText(App.CARRIER.getTrayNo() + "");
        }
        /*if (App.CARRIER != null) {
            if (App.CARRIER.getLocationNo() != null)
                text2.setText(App.CARRIER.getLocationNo() + "");
            if (App.CARRIER.getTrayNo() != null)
                text3.setText(App.CARRIER.getTrayNo() + "");
        }*/
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
        INPUT_DETAIL_LIST.clear();
//        App.CARRIER=null;

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
                    RXInventoryTag tag = (RXInventoryTag) msg.obj;
                    final String EPC = tag.strEPC.replaceAll(" ", "");
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
                                        List<Input> arry;
                                        arry = jsonArray.toJavaList(Input.class);
                                        if (arry != null && arry.size() > 0) {
                                            Input response = arry.get(0);
                                            if (response != null) {
                                                if (response.getEpc() != null && !epcList.contains(response.getEpc())) {
                                                    epcList.add(EPC);
                                                    dataList.add(response);
                                                    String key = response.getVatNo() + "";
                                                    if (!keyValue.containsKey(key)) {//当前没有
                                                        response.setCount(1);
                                                        response.setWeightall(response.getWeight());
                                                        myList.add(response);
                                                        keyValue.put(key, myList.size() - 1);
                                                        dataKey.add(response.getVatNo());
                                                    } else {
                                                        int index = keyValue.get(key);
                                                        myList.get(index).addCount();
                                                        myList.get(index).setWeightall(ArithUtil.add(myList.get(index).getWeightall(), response.getWeight()));
                                                    }
                                                }
                                                text1.setText(epcList.size() + "");
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    } catch (Exception e) {

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

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag;
        handler.sendMessage(msg);
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
        if (position != 0) {
            mAdapter.select(position);
            mAdapter.notifyDataSetChanged();
            Input icd = myList.get(position);
            String key = icd.getVatNo();
            INPUT_DETAIL_LIST.clear();
            for (Input obj : dataList) {
                if (obj != null && obj.getVatNo() != null && obj.getVatNo().equals(key)) {
                    INPUT_DETAIL_LIST.add(obj);
                }
            }
            Fragment fragment = PutawayDetailFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                text1.setText(epcList.size()+"");
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
        text.setText("是否确认上架");
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
                dialog1.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                上传数据
                ArrayList<Input> jsocList = new ArrayList<>();
                for (Input obj : dataList) {
                    if (obj.getVatNo() != null && dataKey.contains(obj.getVatNo())) {
                        obj.setCarrier(App.CARRIER);
                        obj.setDevice(App.DEVICE_NO);
                        jsocList.add(obj);
                    }
                }
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("userId", User.newInstance().getId());
                jsonObject.put("data",jsocList);
                final String json = JSON.toJSONString(jsonObject);
                try {
                    AppLog.write(getActivity(),"putaway",json,AppLog.TYPE_INFO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/input/pushInput.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "postInventory;" + e.getMessage());
                                Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                           try{
                               try {
                                   AppLog.write(getActivity(),"putaway","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
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
//                                   blinkDialog2(true);
                                   mAdapter.notifyDataSetChanged();
                               } else {
                                   Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                   blinkDialog2(false);
                               }
                           } catch (Exception e) {

                           }
                        }
                    }, json);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                dialog1.dismiss();
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
        //eventBus\RxJava
        //MAP\MVVM\MVC\MVP
        //SHARE sdk
        @Override
        public void convert(RecyclerHolder holder, final Input item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (Input i: myList){
                                    if ((i.getVatNo()!=null&&i.getProduct_no()!=null&&i.getSelNo()!=null)
                                            &&!(i.getVatNo().equals("")||i.getProduct_no().equals("")||i.getSelNo().equals("")))
                                        dataKey.add(i.getVatNo());
                                }
                            }else {
                                dataKey.clear();
                            }
                            mAdapter.notifyDataSetChanged();
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
                if (position != 0) {
                    if (((item.getVatNo()+"").equals("")&&(item.getProduct_no()+"").equals("")&&(item.getSelNo()+"").equals(""))){
                        cb.setChecked(false);
                        if (cb.getVisibility()!=View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    }else {
                        if (cb.getVisibility()!=View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKey.contains(item.getVatNo()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
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

            }
        }
    }
}
