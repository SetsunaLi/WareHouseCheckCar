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
import android.support.v4.widget.DrawerLayout;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.example.mumu.warehousecheckcar.entity.FindVatNo;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.RFIDReaderHelper;
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

/**
 * Created by mumu on 2019/1/21.
 */

public class FindVatNoFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {
    private static FindVatNoFragment fragment;
    @Bind(R.id.layout2)
    LinearLayout layout2;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.scrollView)
    HorizontalScrollView scrollView;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.button0)
    Button button0;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.autoText1)
    AutoCompleteTextView autoText1;
    @Bind(R.id.autoText2)
    AutoCompleteTextView autoText2;
    @Bind(R.id.autoText3)
    AutoCompleteTextView autoText3;
    @Bind(R.id.button10)
    Button button10;
    @Bind(R.id.button9)
    Button button9;
    @Bind(R.id.drawer_layout)
    DrawerLayout dlAssets;


    public static FindVatNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new FindVatNoFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    //    显示布匹
    private List<FindVatNo> myList;
    //    查询的布匹
    private List<FindVatNo> dataList;
    //    查询获取epc
    private List<String> dataKEY;
    //    扫描epc
    private List<String> dataEpc;
    //    查询
    private ArrayList<String> findList;

    private Sound sound;
    private FilterAdapter vatAdapter;
    private FilterAdapter colorAdapter;
    private FilterAdapter clothAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_vatno_layout, container, false);
        ButterKnife.bind(this, view);
        sound = new Sound(getActivity());
        initUtil();
        initData();
        initView();
        initLister();
        initRFID();
        return view;
    }

    private void initView() {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.find_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        点击事件可以改视图样式但不可恢复
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        vatAdapter = new FilterAdapter(getActivity());
        autoText1.setAdapter(vatAdapter);
        colorAdapter = new FilterAdapter(getActivity());
        autoText2.setAdapter(colorAdapter);
        clothAdapter = new FilterAdapter(getActivity());
        autoText3.setAdapter(clothAdapter);
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.find_item, null);
        mAdapter.setHeader(view);
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new FindVatNo());
        dataKEY = new ArrayList<>();
        dataList = new ArrayList<>();
        dataEpc = new ArrayList<>();
        findList = new ArrayList<>();
    }

    private void clearData() {
        if (myList != null) {
            myList.clear();
            myList.add(new FindVatNo());
        }
        if (dataList != null)
            dataList.clear();
        if (dataKEY != null)
            dataKEY.clear();
        if (dataEpc != null)
            dataEpc.clear();
        if (findList != null)
            findList.clear();
    }

    private void initLister() {
        mAdapter.setOnItemClickListener(this);
        autoText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 4) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/vatNo/findByVatNo/" + str, new OkHttpClientManager.ResultCallback<JSONArray>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                List<String> arry = response.toJavaList(String.class);
                                if (arry != null && arry.size() > 0) {
                                    vatAdapter.transforData(arry);
                                }
                            } catch (Exception e) {

                            }
                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        autoText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 4) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/vatNo/findByColor/" + str, new OkHttpClientManager.ResultCallback<JSONArray>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                List<String> arry = response.toJavaList(String.class);
                                if (arry != null && arry.size() > 0) {
                                    colorAdapter.transforData(arry);
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        autoText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 4) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/vatNo/findByCloth/" + str, new OkHttpClientManager.ResultCallback<JSONArray>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                List<String> arry = response.toJavaList(String.class);
                                if (arry != null && arry.size() > 0) {
                                    clothAdapter.transforData(arry);
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        clearData();
        myList.clear();
        disRFID();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.selectItem(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String epc = (String) msg.obj;
            epc = epc.replaceAll(" ", "");
            if (dataKEY.contains(epc)) {

                if (System.currentTimeMillis() - currenttime > 150) {
                    sound.callAlarm();
                    currenttime = System.currentTimeMillis();
                }
                if (!dataEpc.contains(epc)) {
                    dataEpc.add(epc);
                    for (FindVatNo findVatNo : dataList) {
                        if (findVatNo.getEpc().equals(epc)) {
                            myList.add(findVatNo);
                        }
                    }
                    Collections.sort(myList, new Comparator<FindVatNo>() {
                        @Override
                        public int compare(FindVatNo obj1, FindVatNo obj2) {
                            String aInv_serial = obj1.getInv_serial();
                            String bInv_serial = obj2.getInv_serial();

                            if ((obj1.getEpc() == null || obj1.getEpc().equals("")) && (obj1.getColor_name() == null || obj1.getColor_name().equals(""))
                                    && (obj1.getLocation_name() == null || obj1.getLocation_name().equals("")) && (obj1.getCloth_name() == null || obj1.getCloth_name().equals(""))
                                    && (obj1.getPallet_name() == null || obj1.getPallet_name().equals("")) && (obj1.getInv_serial() == null || obj1.getInv_serial().equals("")))
                                return -1;
                            if ((obj2.getEpc() == null || obj2.getEpc().equals("")) && (obj2.getColor_name() == null || obj2.getColor_name().equals(""))
                                    && (obj2.getLocation_name() == null || obj2.getLocation_name().equals("")) && (obj2.getCloth_name() == null || obj2.getCloth_name().equals(""))
                                    && (obj2.getPallet_name() == null || obj2.getPallet_name().equals("")) && (obj2.getInv_serial() == null || obj2.getInv_serial().equals("")))

                                return 1;
                            if (aInv_serial != null && bInv_serial != null) {
                                if (Integer.valueOf(aInv_serial) >= Integer.valueOf(bInv_serial)) {
                                    return 1;
                                }
                                return -1;
                            }
                            return 0;
                        }
                    });
                    text2.setText(myList.size() - 1 + "");
                    mAdapter.notifyDataSetChanged();
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

    private int erpCount = 0;

    @OnClick({R.id.button0, R.id.button1, R.id.button2, R.id.button10, R.id.button9})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button0:
                clearData();
                text2.setText(myList.size() - 1 + "");
                text3.setText(dataList.size() + "");
                layout2.removeAllViews();
                mAdapter.notifyDataSetChanged();
                vatAdapter.notifyDataSetChanged();
                colorAdapter.notifyDataSetChanged();
                clothAdapter.notifyDataSetChanged();
                break;
            case R.id.button1:
                myList.clear();
                myList.add(new FindVatNo());
                dataEpc.clear();
                text2.setText(myList.size() - 1 + "");
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                blinkDialog();
                break;
            case R.id.button10:
                clearDraw();
                break;
            case R.id.button9:
                String vat = autoText1.getText().toString().replaceAll(" ", "");
                String color = autoText2.getText().toString().replaceAll(" ", "");
                String cloth = autoText3.getText().toString().replaceAll(" ", "");
                goFind(vat, color, cloth);
                break;
        }
    }

    private void clearDraw() {
        autoText1.setText("");
        autoText2.setText("");
        autoText3.setText("");
        vatAdapter.notifyDataSetChanged();
        colorAdapter.notifyDataSetChanged();
        clothAdapter.notifyDataSetChanged();
    }

    private void addView(String vatNo) {
        vatNo = vatNo.replaceAll(" ", "");
        TextView textView = new TextView(getActivity());
        textView.setText(vatNo);
        textView.setTextColor(getResources().getColor(R.color.colorAboutText));
        textView.setTextSize(20);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setId(findList.size() - 1);
        textView.setWidth(0);
        layout2.addView(textView);
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.setprower_dialog_layout, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        SeekBar seekBar = (SeekBar) blinkView.findViewById(R.id.seekbar);
        final TextView textPrower = (TextView) blinkView.findViewById(R.id.textprower);
        textPrower.setText(App.PROWER + "dbm");
        seekBar.setProgress(App.PROWER);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textPrower.setText(progress + "dbm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("onStopTrackingTouch", "onStopTrackingTouch");
                int prower = seekBar.getProgress();
                if (rfidHander != null) {
                    int i = rfidHander.setOutputPower(RFID_2DHander.getInstance().btReadId, (byte) prower);
                    if (i == 0)
                        App.PROWER = prower;
                }
            }
        });
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
                dialog.dismiss();
            }
        });
    }

    private InputMethodManager mInputMethodManager;

    //     * 初始化必须工具
    private void initUtil() {
        //初始化输入法
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    //隐藏输入法
    public void cancelKeyBoard(View view) {
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);// 隐藏输入法
        }
    }

    private void goFind(final String vat, final String color, final String cloth) {
        JSONObject object = new JSONObject();
        if (!TextUtils.isEmpty(vat))
            object.put("vat_No", vat);
        if (!TextUtils.isEmpty(color))
            object.put("colorName", color);
        if (!TextUtils.isEmpty(cloth))
            object.put("clothName", cloth);
        final String json = object.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getInventoryByVatNo", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (App.LOGCAT_SWITCH) {
                        Toast.makeText(getActivity(), "缸号查询失败！" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String key = "缸号:" + vat + ";色号:" + color + ";布号:" + cloth;
                        if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            List<FindVatNo> response;
                            response = jsonArray.toJavaList(FindVatNo.class);
                            if (response != null && response.size() != 0 && !findList.contains(key)) {
                                findList.add(key);
                                addView(key);
                                for (FindVatNo i : response) {
                                    if (i != null && i.getEpc() != null && !i.getEpc().equals("") && !dataKEY.contains(i.getEpc())) {
                                        dataKEY.add(i.getEpc());
                                        dataList.add(i);
                                    }
                                }
                                text3.setText(dataList.size() + "");
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "成功查询缸号！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "此缸号无库存数据！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "查无此缸号！", Toast.LENGTH_SHORT).show();
//                                getActivity().onBackPressed();
                        }
                    } catch (Exception e) {

                    }
                }
            }, json);
            JSONObject object2 = new JSONObject();
            if (!TextUtils.isEmpty(vat))
                object.put("vatNo", vat);
            final String json2=object2.toJSONString();
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getErpSum", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (App.LOGCAT_SWITCH) {
                        Toast.makeText(getActivity(), "缸号查询失败m！" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                            int count = jsonObject.getJSONObject("data").getInteger("sum");
                            erpCount = count + erpCount;
                            text4.setText(erpCount + "");
                        } else {
                            Toast.makeText(getActivity(), "ERP查询失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                    }
                }
            }, json2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<FindVatNo> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<FindVatNo> datas, int itemLayoutId) {
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

        public void convert(RecyclerHolder holder, final FindVatNo item, final int position) {
            if (item != null) {
                if (position != 0) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.position == position)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));

                    holder.setText(R.id.item1, item.getLocation_name() + "");
                    holder.setText(R.id.item2, item.getPallet_name() + "");
                    holder.setText(R.id.item3, item.getVat_no() + "");
                    holder.setText(R.id.item4, item.getCloth_name() + "");
                    holder.setText(R.id.item5, item.getInv_serial() + "");
                    holder.setText(R.id.item6, item.getWeight_inv() + "");
                    holder.setText(R.id.item7, item.getColor_name() + "");
                    holder.setText(R.id.item8, item.getEpc() + "");
                }

            }
        }
    }

    class FilterAdapter extends BaseAdapter implements Filterable {
        private Context mContext;
        private List<String> mItems;
        private List<String> fData;
        private final Object mLock = new Object();
        private MyFilter mFilter;

        public FilterAdapter(Context context) {
            this.mContext = context;
            mFilter = new MyFilter();
        }

        public void transforData(List<String> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public String getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fuzzy_query_item, parent, false);
                viewHolder.content = convertView.findViewById(R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.content.setText(mItems.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        class ViewHolder {
            TextView content;
        }

        class MyFilter extends Filter {
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (fData == null) {
                    synchronized (mLock) {
                        fData = new ArrayList<>(mItems);
                    }
                }
                int count = fData.size();
                ArrayList<String> values = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    String value = fData.get(i);
                    if (null != value && null != constraint
                            && value.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        values.add(value);
                    }
                }
                results.values = values;
                results.count = values.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence arg0, FilterResults results) {
                mItems = (List<String>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }
}
