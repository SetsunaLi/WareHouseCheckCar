package com.example.mumu.warehousecheckcar.fragment.in;

import android.annotation.SuppressLint;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.in.RetIn;
import com.example.mumu.warehousecheckcar.entity.in.RetInd;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by ${mumu}
 *on 2019/9/26
 */
public class ReturnGoodsInDetailFragment extends CodeFragment {
    private final String TAG = ReturnGoodsInDetailFragment.class.getName();
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.returngoods_in_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new RetInd());//增加一个为头部
        chooseEpcList = new HashMap<>();
        scanEpcList = new ArrayList<>();
        getEpcList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.returngoods_in_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        initRFID();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.returngoods_in_detail_item, null);
        mAdapter.setHeader(view);
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
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("vatNo", oldData.getVat_no());
                        String json = jsonObject.toJSONString();
                        OkHttpClientManager.postJsonAsyn(
                                App.IP + ":" + App.PORT + "/shYf/sh/android/inquiring/getByVatNo"
                                , new OkHttpClientManager.ResultCallback<JSONArray>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        if (e instanceof ConnectException)
                                            showConfirmDialog("链接超时");
                                        if (App.LOGCAT_SWITCH) {
                                            Log.i(TAG, "getEpc;" + e.getMessage());
                                            showToast("获取缸号失败");
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
                                            if (arry.size() > 0) {
                                                for (RetInd retInd : arry) {
                                                    if (chooseEpcList.containsKey(retInd.getWms_epc()) && chooseEpcList.get(retInd.getWms_epc()).equals(oldData.getSh_no())) {
                                                        scanEpcList.add(retInd.getWms_epc());
                                                        getEpcList.add(retInd.getWms_epc());
                                                        for (RetInd oldRetInd : oldData.getInd()) {
                                                            if (oldRetInd.getWms_epc().equals(retInd.getWms_epc())) {
                                                                retInd.setWeight_in(oldRetInd.getWeight_in());
                                                            }
                                                        }
                                                    }
                                                }
                                                myList.addAll(arry);
                                                text1.setText(String.valueOf(myList.size() - 1));
                                                clearList(myList, scanEpcList);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                }, json);
                    } catch (IOException e) {
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

    private void clearData() {
        for (String epc : scanEpcList) {
            if (chooseEpcList.containsKey(epc) && chooseEpcList.get(epc).equals(oldData.getSh_no()))
                chooseEpcList.remove(epc);
        }
        scanEpcList.clear();
    }

    private void clearList(final List<RetInd> myList, final List<String> scanList) {
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
                        int a = aFab.compareTo(bFab);
                        if (a == 0) {
                            return 0;
                        } else if (a > 0) {
                            return 1;
                        } else {
                            return -1;
                        }
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
        EventBus.getDefault().unregister(this);
        clearData();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    clearList(myList, scanEpcList);
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

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                super.handler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                getFragmentManager().popBackStack();
                super.handler.removeMessages(ScanResultHandler.RFID);
                break;
        }
    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (!scanEpcList.contains(epc)) {
            scanEpcList.add(epc);
            if (getEpcList.size() < oldData.getPs() && (!chooseEpcList.containsKey(epc) || chooseEpcList.get(epc).equals(oldData.getSh_no()))) {
                for (RetInd r : myList) {
                    if (!TextUtils.isEmpty(r.getWms_epc()) && r.getWms_epc().equals(epc)) {
                        chooseEpcList.put(epc, oldData.getSh_no());
                        if (!getEpcList.contains(epc))
                            getEpcList.add(epc);
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 2000);
                        break;
                    }
                }
            }

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
                                    showToast("该布匹在其他单号存在");
                                }
                            } else {
                                if (getEpcList.size() < oldData.getPs()) {
                                    chooseEpcList.put(item.getWms_epc(), oldData.getSh_no());
                                    getEpcList.add(item.getWms_epc());
                                } else {
                                    showToast("超出入库单条数");
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
                holder.setText(R.id.edittext1, String.valueOf(item.getWeight_in()));
            }
        }
    }
}
