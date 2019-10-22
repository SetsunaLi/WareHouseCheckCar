package com.example.mumu.warehousecheckcar.fragment.in;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.RetIn;
import com.example.mumu.warehousecheckcar.entity.RetInd;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by ${mumu}
 *on 2019/9/26
 */
public class ReturnGoodsInDetailFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {
    private final String TAG = ReturnGoodsInDetailFragment.class.getName();
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;

    public static ReturnGoodsInDetailFragment newInstance() {
        return new ReturnGoodsInDetailFragment();
    }

    private List<RetInd> myList;
    private Map<String, String> chooseEpcList;
    private ArrayList<String> scanEpcList;
    private ArrayList<String> getEpcList;
    private RetIn oldData;
    private int position;
    private RecycleAdapter mAdapter;
    private Sound sound;

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.returngoods_in_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        initRFID();
        sound = new Sound(getActivity());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return view;
    }

    private void initView() {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.returngoods_in_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.returngoods_in_detail_item, null);
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x10:
                    position = (int) msg.getPositionObj(0);
                    oldData = (RetIn) msg.getPositionObj(1);
                    chooseEpcList = (Map<String, String>) msg.getPositionObj(2);
                    mAdapter.notifyDataSetChanged();
                    try {
                        String string = URLEncoder.encode(oldData.getVat_no(), "UTF-8");
                        OkHttpClientManager.getAsyn(
                                App.IP + ":" + App.PORT + "/shYf/sh/android/inquiring/getByVatNo/" + string
                                , new OkHttpClientManager.ResultCallback<JSONArray>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getEpc;" + e.getMessage());
                                            Toast.makeText(getActivity(), "获取缸号失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            List<RetInd> arry = new ArrayList<>();
                                            Gson gson = new Gson();
                                            JsonParser jsonParser = new JsonParser();
                                            JsonArray jsonElements = jsonParser.parse(response.toJSONString()).getAsJsonArray();
                                            for (JsonElement bean : jsonElements) {
                                                arry.add(gson.fromJson(bean, RetInd.class));
                                            }
                                            if (arry != null && arry.size() > 0) {
                                                for (RetInd retInd : arry) {
                                                    if (chooseEpcList.containsKey(retInd.getWms_epc()) && chooseEpcList.get(retInd.getWms_epc()).equals(oldData.getSh_no())) {
                                                        scanEpcList.add(retInd.getWms_epc());
                                                        getEpcList.add(retInd.getWms_epc());
                                                        for (RetInd oldRetInd : oldData.getInd()) {
                                                            if (oldRetInd.getWms_epc().equals(retInd.getWms_epc())){
                                                                retInd.setWeight_in(oldRetInd.getWeight_in());
                                                            }
                                                        }
                                                    }
                                                }
                                                myList.addAll(arry);
                                                text1.setText(String.valueOf(myList.size() - 1));
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (oldData != null) {
            text2.setText(oldData.getVat_no());
            text3.setText(oldData.getSh_no());
        }
    }

    public void initData() {
        myList = new ArrayList<>();
        myList.add(new RetInd());//增加一个为头部
        chooseEpcList = new HashMap<>();
        scanEpcList = new ArrayList<>();
        getEpcList = new ArrayList<>();
    }

    private void clearData() {
        for (String epc : scanEpcList) {
            if (chooseEpcList.containsKey(epc) && chooseEpcList.get(epc).equals(oldData.getSh_no()))
                chooseEpcList.remove(epc);
        }
        scanEpcList.clear();
    }

    private void clearList(final ArrayList<String> scanList) {
        Collections.sort(myList, new Comparator<RetInd>() {
            @Override
            public int compare(RetInd obj1, RetInd obj2) {
                String epc1 = obj1.getWms_epc();
                String epc2 = obj2.getWms_epc();
                if (TextUtils.isEmpty(epc1) & !TextUtils.isEmpty(epc2))
                    return -1;
                if (TextUtils.isEmpty(epc2) & !TextUtils.isEmpty(epc1))
                    return 1;
                if (TextUtils.isEmpty(epc1) & TextUtils.isEmpty(epc2))
                    return 0;
                if (scanList.contains(epc1) & !scanList.contains(epc2))
                    return -1;
                else if (!scanList.contains(epc1) & scanList.contains(epc2))
                    return 1;
                else {
                    String aFab = obj1.getFab_roll();
                    String bFab = obj2.getFab_roll();
                    if (TextUtils.isEmpty(aFab) & !TextUtils.isEmpty(bFab))
                        return -1;
                    else if (TextUtils.isEmpty(bFab) & !TextUtils.isEmpty(aFab))
                        return 1;
                    else if (TextUtils.isEmpty(bFab) & TextUtils.isEmpty(aFab))
                        return 0;
                    else {
                        if (Integer.valueOf(aFab) > Integer.valueOf(bFab))
                            return 1;
                        else if (Integer.valueOf(aFab) < Integer.valueOf(bFab))
                            return -1;
                        else
                            return 0;

                    }
                }
            }
        });
    }

    private List<RetInd> getData() {
        List<RetInd> list = new ArrayList<>();
        for (RetInd retInd : myList) {
            if (!TextUtils.isEmpty(retInd.getWms_epc()) && chooseEpcList.containsKey(retInd.getWms_epc()) && chooseEpcList.get(retInd.getWms_epc()).equals(oldData.getSh_no()))
                list.add(retInd);
        }
        return list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(chooseEpcList);
        EventBus.getDefault().postSticky(new EventBusMsg(0x11, position, getData(), map));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        disRFID();
        initData();
    }

    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    if (App.MUSIC_SWITCH) {
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                    }
                    final String EPC = ((String) msg.obj).replaceAll(" ", "");
                    if (EPC.startsWith("3035A537") && !scanEpcList.contains(EPC)) {
                        scanEpcList.add(EPC);
                        if (getEpcList.size() < oldData.getPs() && (!chooseEpcList.containsKey(EPC) || chooseEpcList.get(EPC).equals(oldData.getSh_no()))) {
                            for (RetInd r : myList) {
                                if (!TextUtils.isEmpty(r.getWms_epc()) && r.getWms_epc().equals(EPC)) {
                                    chooseEpcList.put(EPC, oldData.getSh_no());
                                    if (!getEpcList.contains(EPC))
                                        getEpcList.add(EPC);
                                    handler.removeCallbacks(runnable);
                                    handler.postDelayed(runnable, 2000);
                                    break;
                                }
                            }
                        }

                    }
                    break;
                case 0x01:
                    clearList(scanEpcList);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0x01);
        }
    };


    @Override
    public void onItemClick(View view, Object data, int position) {

    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        Message msg = handler.obtainMessage();
        msg.what = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                break;
            case R.id.button2:
                getFragmentManager().popBackStack();
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<RetInd> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<RetInd> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        private int position;

        public void select(int i) {
            this.position = i;
        }

        @Override
        public void convert(RecyclerHolder holder, final RetInd item, final int position) {
            final CheckBox checkBox = holder.getView(R.id.checkbox1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (position == 0) {
                        if (b) {
                            for (RetInd retInd : myList) {
                                if (scanEpcList.contains(retInd.getWms_epc()) && getEpcList.size() < oldData.getPs()) {
                                    if (!chooseEpcList.containsKey(retInd.getWms_epc())) {
                                        getEpcList.add(retInd.getWms_epc());
                                        chooseEpcList.put(retInd.getWms_epc(), oldData.getSh_no());
                                    }
                                }
                            }
                        } else {
                            for (RetInd retInd : myList) {
                                if (chooseEpcList.containsKey(retInd.getWms_epc()) && chooseEpcList.get(retInd.getWms_epc()).equals(oldData.getSh_no())) {
                                    chooseEpcList.remove(retInd.getWms_epc());
                                }
                            }
                            getEpcList.clear();
                        }
                    } else {
                        if (b) {
                            if (chooseEpcList.containsKey(item.getWms_epc())) {
                                if (!chooseEpcList.get(item.getWms_epc()).equals(oldData.getSh_no())) {
                                    checkBox.setChecked(false);
                                    Toast.makeText(getActivity(), "该布匹在其他单号存在", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (getEpcList.size() < oldData.getPs()) {
                                    chooseEpcList.put(item.getWms_epc(), oldData.getSh_no());
                                    getEpcList.add(item.getWms_epc());
                                } else {
                                    Toast.makeText(getActivity(), "超出入库单条数", Toast.LENGTH_SHORT).show();
                                    checkBox.setChecked(false);
                                }
                            }

                        } else {
                            if (chooseEpcList.containsKey(item.getWms_epc()) && chooseEpcList.get(item.getWms_epc()).equals(oldData.getSh_no())) {
                                chooseEpcList.remove(item.getWms_epc());
                                getEpcList.remove(item.getWms_epc());
                            }
                        }
                    }
                }
            });
            if (position > 0 && item != null) {
                LinearLayout linearLayout = holder.getView(R.id.layout1);
                final EditText editText = holder.getView(R.id.edittext1);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        try {
                            String weight = charSequence.toString();
                            weight = weight.replaceAll(" ", "");
                            if (!TextUtils.isEmpty(weight)) {
                                double a = Double.parseDouble(weight);
                                item.setWeight_in(a);
                            } else {
                                item.setWeight_in(0d);
                            }
                        } catch (Exception e) {
                            editText.setText(String.valueOf(item.getWeight_in()));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                if (scanEpcList.contains(item.getWms_epc())) {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    checkBox.setEnabled(true);
                } else {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    checkBox.setEnabled(false);
                }
                checkBox.setChecked(getEpcList.contains(item.getWms_epc()));
                holder.setText(R.id.item1, item.getFab_roll());
                holder.setText(R.id.item2, item.getWms_epc());
                holder.setText(R.id.edittext1, item.getWeight_in() + "");
            }
        }
    }
}
