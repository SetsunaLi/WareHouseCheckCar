package com.example.mumu.warehousecheckcar.fragment.cut;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
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

import static com.example.mumu.warehousecheckcar.application.App.TIME;

public class CutClothOutFragment extends Fragment {
    private final String TAG = "CutClothOutFragment";

    private static CutClothOutFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static CutClothOutFragment newInstance() {
        if (fragment == null)
            fragment = new CutClothOutFragment();
        return fragment;
    }

    /*** 申请单号*/
    private ArrayList<String> fatherNoList;
    /**
     * 列表信息
     */
    private ArrayList<BarCode> myList;
    /***    主表，根据申请单号，字段组成key判断是否上传*/
    private Map<String, List<String>> dataKey;
    /***    记录查询到的申请单号，没实际用途*/
    private ArrayList<String> dateNo;
    private RecycleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cutcloth_out_layout, container, false);
        ButterKnife.bind(this, view);
        initData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cutcloth_out_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        return view;
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new BarCode());
        dataKey = new HashMap<>();
        dateNo = new ArrayList<>();
        if (!EventBus.getDefault().isRegistered(this))

            EventBus.getDefault().register(this);
    }

    private void clearData() {
        if (myList != null)
            myList.clear();
        myList.add(new BarCode());
        if (dataKey != null)
            dataKey.clear();
        if (dateNo != null)
            dateNo.clear();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cutcloth_out_head, null);
        mAdapter.setHeader(view);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null) {
            switch (msg.getStatus()) {
                case 0x04:
                    fatherNoList = (ArrayList<String>) msg.getPositionObj(0);
                    if (fatherNoList == null) {
                        fatherNoList = new ArrayList<>();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean flag = true;

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            clearData();
            downLoadData();
            text2.setText(fatherNoList.size() + "");
        }
    }

    public void downLoadData() {
        for (String no : fatherNoList) {
            JSONObject object = new JSONObject();
            object.put("applyNo", no);
            final String json = object.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/scanCutCode.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
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
                            List<BarCode> response;
                            response = json.toJavaList(BarCode.class);
                            if (response != null && response.size() != 0) {
                                if (!dateNo.contains(response.get(0).getOut_no())) {
                                    dateNo.add(response.get(0).getOut_no());
                                    response.get(0).setFlag(true);
                                    for (BarCode cutSample : response) {
                                        String key = cutSample.getOutp_id();
                                        if (!dataKey.containsKey(cutSample.getOut_no())) {
                                            dataKey.put(cutSample.getOut_no(), new ArrayList<String>());
                                        }
                                        if (!dataKey.get(cutSample.getOut_no()).contains(key)) {
                                            dataKey.get(cutSample.getOut_no()).add(key);
                                        }
                                    }
                                    myList.addAll(response);
                                    mAdapter.notifyDataSetChanged();
                                }
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
    private Handler handler=new Handler();
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
                    ArrayList<BarCode> jsocList = new ArrayList<>();
                    for (BarCode op : myList) {
                        if (dataKey.containsKey(op.getOut_no()))
                            if (dataKey.get(op.getOut_no()).contains(op.getOutp_id()))
                                jsocList.add(op);
                    }
                    if (jsocList.size() > 0) {
//                        final String json = JSON.toJSONString(jsocList);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", User.newInstance().getId());
                        jsonObject.put("data", jsocList);
                        final String json = jsonObject.toJSONString();
                        try {
                            AppLog.write(getActivity(),"ccout",json,AppLog.TYPE_INFO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCutOut.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                                            AppLog.write(getActivity(),"ccout","userId:"+User.newInstance().getId()+response.toString(),AppLog.TYPE_INFO);
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
//                                            blinkDialog2(true);
                                        } else {
                                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                                            blinkDialog2(false);
                                            Sound.faillarm();
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
                    dialog.hide();
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
    class RecycleAdapter extends BasePullUpRecyclerAdapter<BarCode> {
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

        public RecycleAdapter(RecyclerView v, Collection<BarCode> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, final BarCode item, final int position) {
            if (item != null) {
                final String key = item.getOutp_id();
                CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked){
                                for (String no:dataKey.keySet()){
                                    for (BarCode cutSample:myList){
                                        if (!dataKey.get(no).contains(cutSample.getOutp_id()))
                                            dataKey.get(no).add(cutSample.getOutp_id());
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }else {
                                for (String no:dataKey.keySet()){
                                    dataKey.get(no).clear();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (isChecked) {
                                if (dataKey.containsKey(item.getOut_no()))
                                    if (!dataKey.get(item.getOut_no()).contains(key))
                                        dataKey.get(item.getOut_no()).add(key);
                            } else {
                                if (dataKey.containsKey(item.getOut_no()))
                                    if (dataKey.get(item.getOut_no()).contains(key)) {
                                        Iterator<String> iter = dataKey.get(item.getOut_no()).iterator();
                                        while (iter.hasNext()) {
                                            String str = iter.next();
                                            if (str.equals(key))
                                                iter.remove();
                                        }
                                    }
                            }
                        }
                    }
                });
                if (position !=0) {
                    LinearLayout title = holder.getView(R.id.layout_title);
                    LinearLayout no = holder.getView(R.id.headNo);
                    View view = holder.getView(R.id.view);
                    if (item.isFlag()) {
                        title.setVisibility(View.VISIBLE);
                        no.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);
                        holder.setText(R.id.text1, "申请单号：" + item.getOut_no());
                    } else {
                        title.setVisibility(View.GONE);
                        no.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    }

                    if (((item.getVatNo() + "").equals("") && (item.getProduct_no() + "").equals("") && (item.getSelNo() + "").equals(""))) {
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                        cb.setChecked(false);
                    } else {
                        if (cb.isEnabled())
                            cb.setEnabled(true);
                        if (dataKey.containsKey(item.getOut_no())) {
                            if (dataKey.get(item.getOut_no()).contains(key))
                                cb.setChecked(true);
                            else
                                cb.setChecked(false);
                        }
                        holder.setText(R.id.item1, item.getProduct_no() + "");
                        holder.setText(R.id.item2, item.getSelColor() + "");
                        holder.setText(R.id.item3, item.getColorName() + "");
                        holder.setText(R.id.item4, item.getVatNo() + "");
                        holder.setText(R.id.item5, item.getCutWeight() + "");
                        holder.setText(R.id.item6, item.getYard_out() + "");
                    }
                }

            }
        }
    }
}
