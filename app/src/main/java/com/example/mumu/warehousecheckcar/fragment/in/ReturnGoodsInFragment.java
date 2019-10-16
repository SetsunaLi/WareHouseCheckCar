package com.example.mumu.warehousecheckcar.fragment.in;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.Forwarding;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.entity.OutNo;
import com.example.mumu.warehousecheckcar.entity.RetIn;
import com.example.mumu.warehousecheckcar.entity.RetInd;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.forward.ForwardingFragment;
import com.example.mumu.warehousecheckcar.fragment.forward.ForwardingMsgFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by ${mumu}
 *on 2019/9/25
 */
public class ReturnGoodsInFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener {
    private final String TAG = ReturnGoodsInFragment.class.getName();
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static ReturnGoodsInFragment newInstance() {
        return new ReturnGoodsInFragment();
    }

    /*** 申请单号*/
    private ArrayList<OutNo> fatherNoList;
    /***    显示列表*/
    private ArrayList<RetIn> myList;
    /***    主表，根据申请单号，字段组成key判断是否上传*/
    private ArrayList<String> dataKey;
    /***    所有匹配的epc总集*/
    private Map<String, String> epcNoList;
    /***    记录查询到的申请单号，没实际用途*/
    private ArrayList<String> dateNo;

    private RecycleAdapter mAdapter;
    private Handler handler;
    private Sound sound;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_apply_new_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("退货入库");
        sound=new Sound(getActivity());
        handler = new Handler();
        initData();
        initView();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return view;
    }

    public void initData() {
        fatherNoList = new ArrayList<>();
        myList = new ArrayList<>();
        dateNo = new ArrayList<>();
        dataKey = new ArrayList<>();
        epcNoList = new HashMap<>();
    }

    public void initView() {
        text3.setText("送货单数量");
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.returngoods_in_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    public void clearData() {
        myList.clear();
        dateNo.clear();
        dataKey.clear();
        epcNoList.clear();
    }

    private boolean flag = true;

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            ArrayList<String> list = (ArrayList<String>) getArguments().getSerializable("NO");
            fatherNoList.clear();
            for (String str : list) {
                str = str.replaceAll(" ", "");
                if (!TextUtils.isEmpty(str))
                    fatherNoList.add(new OutNo(str));
            }
            text1.setText(String.valueOf(0));
            text2.setText(String.valueOf(fatherNoList.size()));
            downLoadData();
        }
    }

    public void downLoadData() {
        for (final OutNo no : fatherNoList) {
            final String json = JSONObject.toJSONString(no);
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/android/inquiring/getByOutNo", new OkHttpClientManager.ResultCallback<JSONArray>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "获取申请单信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONArray json) {
                        try {
                            List<RetIn> response;
                            response = json.toJavaList(RetIn.class);
                            if (response != null && response.size() != 0) {
                                if (!dateNo.contains(no.getOut_no())) {
                                    dateNo.add(no.getOut_no());
                                    response.get(0).setStatus(true);
                                    for (int i = 0; i < response.size(); i++) {
                                        RetIn output = response.get(i);
                                        output.setSh_no(no.getOut_no());
                                        output.setInd(new ArrayList<RetInd>());
                                        if (!dataKey.contains(output.getSh_no())) {
                                            dataKey.add(output.getSh_no());
                                        }
                                    }
                                    myList.addAll(response);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                downLoadData();
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
            if (dialog1 != null)
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
        text.setText("是否确认出库");
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
                ArrayList<RetIn> allList = new ArrayList<>();
                boolean flag = true;
                for (RetIn retIn : myList) {
                    if (dataKey.contains(retIn.getSh_no())) {
                        if (retIn.getPs() == retIn.getInd().size()) {
                            RetIn data = retIn.clone();
                            allList.add(data);
                        } else {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    for (RetIn retIn : allList) {
                        retIn.setRecord_by(User.newInstance().getUsername());
                        final String json = JSONObject.toJSONString(retIn);
                        try {
                            AppLog.write(getActivity(), "returnIn", json, AppLog.TYPE_INFO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/android/inquiring/pushRetIn", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                                            AppLog.write(getActivity(), "returnIn", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
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
                                        } else {
                                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                            showDialog("上传失败");
                                            sound.uploadFail();
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
                    }
                } else
                    Toast.makeText(getActivity(), "入库内容必须与退库单号一致", Toast.LENGTH_SHORT).show();
                no.setEnabled(false);
                yes.setEnabled(false);
                handler.postDelayed(r, App.TIME);
            }
        });
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @Override
    public void onItemClick(View view, Object data, int position) {
        Fragment fragment = ReturnGoodsInDetailFragment.newInstance();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
        EventBus.getDefault().postSticky(new EventBusMsg(0x10, position, myList.get(position), epcNoList));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x11:
                    int position = (int) msg.getPositionObj(0);
                    myList.get(position).getInd().clear();
                    myList.get(position).getInd().addAll((List<RetInd>) msg.getPositionObj(1));
                    epcNoList.clear();
                    epcNoList.putAll((Map<String, String>) msg.getPositionObj(2));
                    mAdapter.notifyDataSetChanged();
                    break;
            }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<RetIn> {
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

        public RecycleAdapter(RecyclerView v, Collection<RetIn> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final RetIn item, final int position) {
            if (item != null) {
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (!dataKey.contains(item.getSh_no()))
                                dataKey.add(item.getSh_no());
                        } else {
                            if (dataKey.contains(item.getSh_no()))
                                dataKey.remove(item.getSh_no());
                        }
                    }
//                    }
                });
                LinearLayout title = holder.getView(R.id.layout_title);
                LinearLayout no = holder.getView(R.id.headNo);
                View view = holder.getView(R.id.view);
                LinearLayout layout = holder.getView(R.id.layout1);
                if (item.isStatus()) {
                    title.setVisibility(View.VISIBLE);
                    no.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    holder.setText(R.id.text1, "退货单号：" + item.getSh_no());
                } else {
                    title.setVisibility(View.GONE);
                    no.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
                if (item.getPs() == item.getInd().size())
                    layout.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                else
                    layout.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                cb.setChecked(dataKey.contains(item.getSh_no()));
                holder.setText(R.id.item1, item.getProduct_no());
                holder.setText(R.id.item2, item.getSel_color());
                holder.setText(R.id.item3, item.getColor_name());
                holder.setText(R.id.item4, item.getVat_no());
                holder.setText(R.id.item5, String.valueOf(item.getPs()));
                holder.setText(R.id.item6, String.valueOf(item.getInd().size()));
            }
        }
    }
}
