package com.example.mumu.warehousecheckcar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.FindVatNo;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
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

public class FindVatNoFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener, AdapterView.OnItemClickListener {
    private static FindVatNoFragment fragment;
    @Bind(R.id.fixeedittext1)
    FixedEditText fixeedittext1;
    /*@Bind(R.id.fixeedittext0)
    FixedEditText fixeedittext0;
    @Bind(R.id.fixeedittext2)
    FixedEditText fixeedittext2;*/

    @Bind(R.id.button)
    Button button;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.scrollView)
    HorizontalScrollView scrollView;
    @Bind(R.id.layout1)
    LinearLayout layout1;

    private FindVatNoFragment() {
    }

    public static FindVatNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new FindVatNoFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private MyAdapter textAdapter;
    private List<FindVatNo> myList;
    private List<FindVatNo> dataList;
    private List<String> dataKEY;
    private List<String> dataEpc;
    private ArrayList<String> vatList;
    private Sound sound;
    private boolean isLookFlag=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_vatno_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        initUtil();

        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.find_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
//        点击事件可以改视图样式但不可恢复
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        textAdapter = new MyAdapter(getActivity(), R.layout.fuzzy_query_item, vatList);
        listview.setAdapter(textAdapter);
//        listview.addHeaderView(getLayoutInflater().inflate(R.layout.check_item),listview,false);
        listview.setOnItemClickListener(this);
        listview.setVisibility(View.GONE);
        fixeedittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String vat = s.toString();
                vat = vat.replaceAll(" ", "");
                if (vat != null && !vat.equals("")) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/vatNo/findByVatNo/" + vat, new OkHttpClientManager.ResultCallback<JSONArray>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                List<String> arry;
                                arry = response.toJavaList(String.class);
                                if (arry != null && arry.size() > 0) {
                                    vatList.clear();
                                    vatList.addAll(arry);
                                    textAdapter.setList(vatList);
                                    textAdapter.notifyDataSetChanged();
                                    if (!vatFlag) {
                                        if (listview.getVisibility() == View.GONE&&isLookFlag)
                                            listview.setVisibility(View.VISIBLE);
                                    } else {
                                        vatFlag = false;
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fixeedittext1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isLookFlag=hasFocus;
            }
        });
        initRFID();
        return view;
    }

    private void initView() {
//        fixeedittext0.setFixedText("入库单号：");
        fixeedittext1.setFixedText("缸号：");
//        fixeedittext2.setFixedText("布号：");

        fixeedittext1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    onViewClicked(button);
                    return true;
                }
                return false;
            }
        });
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
        vatList = new ArrayList<>();
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
        if (vatList != null)
            vatList.clear();

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
           /* if (mAdapter.getPosition() == position) {
                dataKEY.clear();
                for (FindVatNo i : myList) {
                    if (i != null && i.getEpc() != null && !i.getEpc().equals(""))
                        dataKEY.add(i.getEpc());
                }
            } else {
                if (myList.get(position) != null && myList.get(position).getEpc() != null && !myList.get(position).getEpc().equals("")) {
                    dataKEY.clear();
                    dataKEY.add(myList.get(position).getEpc());
                }
            }*/
            mAdapter.selectItem(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private long currenttime = 0;
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

    @OnClick({R.id.button, R.id.button1, R.id.button2, R.id.layout1, R.id.recyle, R.id.scrollView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (listview.getVisibility() != View.GONE)
                    listview.setVisibility(View.GONE);
                fixeedittext1.clearFocus();
//                fixeedittext1.setCursorVisible(false);
//                fixeedittext1.setFocusable(false);//设置输入框不可聚集
//                fixeedittext1.setFocusable(true);//设置输入框不可聚集
                cancelKeyBoard(view);
                goFind();
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
            default:
                if (listview.getVisibility() != View.GONE)
                    listview.setVisibility(View.GONE);
                break;
           /* case R.id.layout1:

                break;
            case R.id.recyle:
                if (listview.getVisibility() != View.GONE)
                    listview.setVisibility(View.GONE);
                break;*/
        }
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

    private void goFind() {

        String vatNo = fixeedittext1.getText().toString() + "";
//        String vatNo="180524084.1";
        vatNo = vatNo.replaceAll(" ", "");
        if (vatNo != null && !vatNo.equals("")) {
            JSONObject object = new JSONObject();
            object.put("vatNo", vatNo);
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
                            if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<FindVatNo> response;
                                response = jsonArray.toJavaList(FindVatNo.class);
                                if (response != null && response.size() != 0) {
                                    clearData();
                                    dataList.addAll(response);
                                    for (FindVatNo i : response) {
                                        if (i != null && i.getEpc() != null && !i.getEpc().equals(""))
                                            dataKEY.add(i.getEpc());
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean vatFlag = false;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fixeedittext1.setText(vatList.get(position));
        if (listview.getVisibility() == View.VISIBLE)
            listview.setVisibility(View.GONE);
        vatFlag = true;
        onViewClicked(button);
    }

    class MyAdapter extends ArrayAdapter {


        private List<String> list;
        private LayoutInflater mInflater;

        public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
            super(context, resource, objects);
            this.list = objects;
            this.mInflater = LayoutInflater.from(context);
        }

        private int id = -255;

        public void selectItem(int id) {
            if (this.id != id)
                notifyDataSetChanged();
            this.id = id;
        }

        public List<String> getList() {
            return this.list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fuzzy_query_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.editText1 = (TextView) convertView.findViewById(R.id.text1);
                viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.layout1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (id == position)
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.colorZERO));
            viewHolder.editText1.setText(list.get(position));
            return convertView;
        }

        class ViewHolder {
            LinearLayout linearLayout;
            TextView editText1;

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
                    holder.setText(R.id.item4, item.getCloth_name() + "");
                    holder.setText(R.id.item5, item.getInv_serial() + "");
                    holder.setText(R.id.item6, item.getWeight_inv() + "");
                    holder.setText(R.id.item7, item.getColor_name() + "");
                    holder.setText(R.id.item8, item.getEpc() + "");
                }

            }
        }
    }
}
