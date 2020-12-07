package com.example.mumu.warehousecheckcar.fragment.find;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.SearchAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.find.FindVatNo;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.CompareUtil;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2019/1/21.
 */

public class FindVatNoFragment extends CodeFragment implements BRecyclerAdapter.OnItemClickListener {
    private static FindVatNoFragment fragment;
    @BindView(R.id.layout2)
    LinearLayout layout2;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.scrollView)
    HorizontalScrollView scrollView;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.text4)
    TextView text4;
    @BindView(R.id.button0)
    Button button0;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.autoText1)
    AutoCompleteTextView autoText1;
    @BindView(R.id.autoText2)
    AutoCompleteTextView autoText2;
    @BindView(R.id.autoText3)
    AutoCompleteTextView autoText3;
    @BindView(R.id.button10)
    Button button10;
    @BindView(R.id.button9)
    Button button9;
    @BindView(R.id.drawer_layout)
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

    private SearchAdapter vatAdapter;
    private SearchAdapter colorAdapter;
    private SearchAdapter clothAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.find_vatno_layout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.find_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new FindVatNo());
        dataKEY = new ArrayList<>();
        dataList = new ArrayList<>();
        dataEpc = new ArrayList<>();
        findList = new ArrayList<>();

    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.find_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        vatAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText1.setAdapter(vatAdapter);
        colorAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText2.setAdapter(colorAdapter);
        clothAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText3.setAdapter(clothAdapter);

    }

    @Override
    protected void addListener() {
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
                                    vatAdapter.updataList(arry);
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
                                    colorAdapter.updataList(arry);
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
                                    clothAdapter.updataList(arry);
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
        initRFID();
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
        clearData();
        myList.clear();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.selectItem(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private int erpCount = 0;

    @OnClick({R.id.button0, R.id.button1, R.id.button2, R.id.button10, R.id.button9})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button0:
                clearData();
                text2.setText(String.valueOf(myList.size() - 1));
                text3.setText(String.valueOf(dataList.size()));
                layout2.removeAllViews();
                mAdapter.notifyDataSetChanged();
                clearDraw();
                handler.removeMessages(ScanResultHandler.NO_MUSIC_RFID);
                break;
            case R.id.button1:
                myList.clear();
                myList.add(new FindVatNo());
                dataEpc.clear();
                text2.setText(String.valueOf(myList.size() - 1));
                mAdapter.notifyDataSetChanged();
                handler.removeMessages(ScanResultHandler.NO_MUSIC_RFID);
                break;
            case R.id.button2:
                prowerDialog();
                break;
            case R.id.button10:
                clearDraw();
                break;
            case R.id.button9:
                String vat = autoText1.getText().toString().replaceAll(" ", "");
                String color = autoText2.getText().toString().replaceAll(" ", "");
                String cloth = autoText3.getText().toString().replaceAll(" ", "");
                goFind(vat, color, cloth);
                if (dlAssets.isDrawerOpen(Gravity.END))
                    dlAssets.closeDrawer(Gravity.END);
                break;
        }
    }

    private void clearDraw() {
        autoText1.setText("");
        autoText2.setText("");
        autoText3.setText("");
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

    private void prowerDialog() {
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
                int i = setPrower(seekBar.getProgress());
                if (i == 0)
                    App.PROWER = seekBar.getProgress();
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
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        showToast("缸号查询失败");
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
                                    if (i != null && !TextUtils.isEmpty(i.getEpc()) && !dataKEY.contains(i.getEpc())) {
                                        dataKEY.add(i.getEpc());
                                        dataList.add(i);
                                    }
                                }
                                text3.setText(String.valueOf(dataList.size()));
                                mAdapter.notifyDataSetChanged();
                                showToast("成功查询缸号");
                            } else {
                                showToast("此缸号无库存数据");
                            }
                        } else {
                            showToast("查无此缸号");
                        }
                    } catch (Exception e) {

                    }
                }
            }, json);
            JSONObject object2 = new JSONObject();
            if (!TextUtils.isEmpty(vat))
                object.put("vatNo", vat);
            final String json2 = object2.toJSONString();
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/count/getErpSum", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        showToast("缸号查询失败");
                    }
                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                            int count = jsonObject.getJSONObject("data").getInteger("sum");
                            erpCount = count + erpCount;
                            text4.setText(String.valueOf(erpCount));
                        } else {
                            showToast("ERP查询失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }, json2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        final String epc = tag.strEPC.replace(" ", "");
        Message message = handler.obtainMessage();
        message.what = ScanResultHandler.NO_MUSIC_RFID;
        message.obj = epc;
        handler.sendMessage(message);
    }

    @Override
    public void rfidResult(String epc) {
        epc = epc.replaceAll(" ", "");
        if (dataKEY.contains(epc)) {
            if (App.MUSIC_SWITCH) {
                Sound.scanAlarm();
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

                        if (TextUtils.isEmpty(obj1.getInv_serial()) && !TextUtils.isEmpty(obj2.getInv_serial()))
                            return -1;
                        else if (!TextUtils.isEmpty(obj1.getInv_serial()) && TextUtils.isEmpty(obj2.getInv_serial()))
                            return 1;
                        else if (TextUtils.isEmpty(obj1.getInv_serial()) && TextUtils.isEmpty(obj2.getInv_serial()))
                            return 0;
                        else if (!CompareUtil.isMoreThan(aInv_serial, bInv_serial))
                            return -1;
                        else
                            return 1;
                    }
                });
                text2.setText(String.valueOf(myList.size() - 1));
                mAdapter.notifyDataSetChanged();
            }
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

                    holder.setText(R.id.item1, item.getLocation_name());
                    holder.setText(R.id.item2, item.getPallet_name());
                    holder.setText(R.id.item3, item.getVat_no());
                    holder.setText(R.id.item4, item.getCloth_name());
                    holder.setText(R.id.item5, item.getInv_serial());
                    holder.setText(R.id.item6, item.getWeight_inv());
                    holder.setText(R.id.item7, item.getColor_name());
                    holder.setText(R.id.item8, item.getEpc());
                }

            }
        }
    }
}
