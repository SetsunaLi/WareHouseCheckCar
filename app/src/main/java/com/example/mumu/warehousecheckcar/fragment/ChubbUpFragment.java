package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.ChubbUp;
import com.example.mumu.warehousecheckcar.entity.FindVatNo;
import com.example.mumu.warehousecheckcar.entity.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
import com.example.mumu.warehousecheckcar.listener.FragmentCallBackListener;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChubbUpFragment extends Fragment implements UHFCallbackLiatener, FragmentCallBackListener {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    private static ChubbUpFragment fragment;

    private ChubbUpFragment() {
    }

    public static ChubbUpFragment newInstance() {
        if (fragment == null) ;
        fragment = new ChubbUpFragment();
        return fragment;
    }

    private final String TAG = "ChubbUpFragment";
    private RecycleAdapter mAdapter;
    private List<ChubbUp> myList;
    /**
     * 匹配逻辑
     * //     * key：response.getVatNo()+response.getProduct_no()+response.getSelNo()+response.getColor()
     * key:epc
     * value：index
     */
    private Map<String, Integer> strIndex;
    /***布号+缸号+布票号*/
    private List<String> dataKEY;
    //    private List<Object> dataList;
    private List<String> dataEPC;
    private Sound sound;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubbup_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());

        return view;
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new ChubbUp());
        dataKEY = new ArrayList<>();
        strIndex = new HashMap<>();
//        dataList= new ArrayList<>();
        dataEPC = new ArrayList<>();
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new ChubbUp());
        }
        if (dataKEY != null)
            dataKEY.clear();
        if (strIndex != null)
            strIndex.clear();
//        if (dataList!=null)
//            dataList.clear();
        if (dataEPC != null)
            dataEPC.clear();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    //这里写界面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("查布上架");
        initData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubbup_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        ComeBack.getInstance().setCallbackLiatener(this);
        initRFID();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.chubbup_item, null);
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

    public void upLoad(boolean flag) {
        if (flag) {
            initRFID();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Iterator<ChubbUp> iter = myList.iterator();
                    while (iter.hasNext()) {
                        ChubbUp chubbUp = iter.next();
                        if (dataKEY.contains(chubbUp.getEpc()))
                            iter.remove();
//                            myList.remove(chubbUp);
                    }
                    dataKEY.clear();
//                    CHUBB_UP_LIST.clear();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        clearData();
        myList.clear();
        disRFID();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
//                if (dataKEY.size() > 0) {
                disRFID();
                ArrayList<ChubbUp> dataList = new ArrayList<>();
                for (ChubbUp data : myList) {
                    if (dataKEY.contains(data.getEpc())) {
                        dataList.add(data);
                    }
                }


//                    CHUBB_UP_LIST.clear();
//                    CHUBB_UP_LIST.addAll(dataList);
                Fragment fragment = ChubbUpCarrierFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("dataList", dataList);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
//                } else {
//                    Toast.makeText(getActivity(), "请至少选择一条布匹上架", Toast.LENGTH_SHORT).show();
//                }

//                点击上传
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
        text.setText("扫描列表未清空，是否确认退出？");
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
                getFragmentManager().popBackStack();
                dialog.dismiss();
            }
        });
    }

    /**
     * 比较两个字符串的大小，按字母的ASCII码比较
     *
     * @param pre
     * @param next
     * @return
     */
    private static boolean isMoreThan(String pre, String next) {
        if (null == pre || null == next || "".equals(pre) || "".equals(next)) {
            return false;
        }
        char[] c_pre = pre.toCharArray();
        char[] c_next = next.toCharArray();
        int minSize = Math.min(c_pre.length, c_next.length);
        for (int i = 0; i < minSize; i++) {
            if ((int) c_pre[i] > (int) c_next[i]) {
                return true;
            } else if ((int) c_pre[i] < (int) c_next[i]) {
                return false;
            }
        }
        if (c_pre.length > c_next.length) {
            return true;
        }
        return false;
    }

    long currenttime = 0;
    @SuppressLint("HandlerLeak")
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
                    String epc = (String) msg.obj;
                    epc = epc.replaceAll(" ", "");
                    if (epc.startsWith("3035A537") && !dataEPC.contains(epc)) {
                        JSONObject obj = new JSONObject();
                        obj.put("epc", epc);
                        final String json = obj.toJSONString();
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/getClothInCheckByEpc", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "getEpc;" + e.getMessage());
                                        Toast.makeText(getActivity(), "扫描查布区布匹失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject object) {

                                    try {
                                        if (object.getJSONObject("data") != null) {
                                            final JSONObject obj = object.getJSONObject("data");
                                            ChubbUp value = obj.toJavaObject(ChubbUp.class);
                                            if (value != null) {
                                                if (value.getEpc() != null && !value.getEpc().equals("") && !dataEPC.contains(value.getEpc())) {
                                                    dataEPC.add(value.getEpc());
                                                    myList.add(value);
                                                    Collections.sort(myList, new Comparator<ChubbUp>() {
                                                        @Override
                                                        public int compare(ChubbUp obj1, ChubbUp obj2) {
                                                            String aLocation = obj1.getBas_location();
                                                            String bLocation = obj2.getBas_location();
                                                            String aPallet = obj1.getBas_pallet();
                                                            String bPallet = obj2.getBas_pallet();
                                                            String aVat = obj1.getVatNo();
                                                            String bVat = obj2.getVatNo();
                                                            if ((obj1.getVatNo() == null || obj1.getVatNo().equals("")) && (obj1.getFabRool() == null || obj1.getFabRool().equals(""))
                                                                    && (obj1.getProduct_no() == null || obj1.getProduct_no().equals("")) && (obj1.getSelNo() == null || obj1.getSelNo().equals(""))
                                                                    && (obj1.getEpc() == null || obj1.getEpc().equals("")) && (obj1.getColor() == null || obj1.getColor().equals("")))
                                                                return -1;
                                                            if ((obj2.getVatNo() == null || obj2.getVatNo().equals("")) && (obj2.getFabRool() == null || obj2.getFabRool().equals(""))
                                                                    && (obj2.getProduct_no() == null || obj2.getProduct_no().equals("")) && (obj2.getSelNo() == null || obj2.getSelNo().equals(""))
                                                                    && (obj2.getEpc() == null || obj2.getEpc().equals("")) && (obj2.getColor() == null || obj2.getColor().equals("")))
                                                                return 1;

                                                            if (aLocation.equals(bLocation)) {
                                                                if (aPallet.equals(bPallet)) {
                                                                    if (aVat.equals(bVat)) {
                                                                        return 0;
                                                                    } else {
                                                                        if (isMoreThan(aVat, bVat)) {
                                                                            return -1;
                                                                        } else {
                                                                            return 1;
                                                                        }
                                                                    }
                                                                } else {
                                                                    if (isMoreThan(aPallet, bPallet)) {
                                                                        return -1;
                                                                    } else {
                                                                        return 1;
                                                                    }
                                                                }
                                                            } else {
                                                                if (isMoreThan(aLocation, bLocation)) {
                                                                    return -1;
                                                                } else {
                                                                    return 1;
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            text1.setText(dataEPC.size() + "");
                                            mAdapter.notifyDataSetChanged();
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
                case 0x01:
                    break;
            }
        }
    };

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        String epc = tag.strEPC;
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = epc;
        handler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @Override
    public void comeBackListener() {

    }

    @Override
    public void ubLoad(boolean flag) {
        if (flag) {
            upLoad(flag);
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ChubbUp> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ChubbUp> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        private int position = -255;

        public void selectItem(int position) {
            if (this.position == position)
                this.position = -255;
            else
                this.position = position;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public void convert(RecyclerHolder holder, final ChubbUp item, final int position) {
            if (item != null) {
//                final String key=item.getEpc();
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (ChubbUp i : myList) {
                                    if ((i.getVatNo() != null && i.getProduct_no() != null && i.getSelNo() != null)
                                            && !(i.getVatNo().equals("") || i.getProduct_no().equals("") || i.getSelNo().equals("")))
                                        dataKEY.add(i.getEpc());
                                }
                            } else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getEpc()))
                                    dataKEY.add(item.getEpc());
                            } else {
                                if (dataKEY.contains(item.getEpc()))
                                    dataKEY.remove(item.getEpc());
                            }
                        }
                    }
                });
                if (position != 0) {
                    if ((item.getVatNo() + "").equals("") && (item.getProduct_no() + "").equals("") && (item.getSelNo() + "").equals("")) {
                        cb.setChecked(false);
                        if (cb.getVisibility() != View.INVISIBLE)
                            cb.setVisibility(View.INVISIBLE);
                    } else {
                        if (cb.getVisibility() != View.VISIBLE)
                            cb.setVisibility(View.VISIBLE);
                        if (dataKEY.contains(item.getEpc()))
                            cb.setChecked(true);
                        else
                            cb.setChecked(false);
                    }
                  /*  LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.position == position)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));*/

                    holder.setText(R.id.item1, item.getBas_location() + "");
                    holder.setText(R.id.item2, item.getBas_pallet() + "");
                    holder.setText(R.id.item3, item.getVatNo() + "");
                    holder.setText(R.id.item4, item.getProduct_no() + "");
                    holder.setText(R.id.item5, item.getFabRool() + "");
                    holder.setText(R.id.item6, item.getSelNo() + "");
                    holder.setText(R.id.item7, item.getColor() + "");
                }

            }
        }
    }
}
