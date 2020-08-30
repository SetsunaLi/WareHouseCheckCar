package com.example.mumu.warehousecheckcar.fragment.in;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.OutNo;
import com.example.mumu.warehousecheckcar.entity.RetIn;
import com.example.mumu.warehousecheckcar.entity.RetInd;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;

/***
 *created by ${mumu}
 *on 2019/9/25
 */
public class ReturnGoodsInFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener {
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
    private ScanResultHandler scanResultHandler;
    private RecycleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.return_goodsin_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("退货入库");
        return view;
    }

    @Override
    protected void initData() {
        fatherNoList = new ArrayList<>();
        myList = new ArrayList<>();
        dateNo = new ArrayList<>();
        dataKey = new ArrayList<>();
        epcNoList = new HashMap<>();
    }

    @Override
    protected void initView(View view) {
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

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            showToast("获取申请单信息失败");
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
//                                        新建列表

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
                scanResultHandler.removeMessages(ScanResultHandler.RFID);
                break;
            case R.id.button2:
                showUploadDialog("是否确认入库?");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<ArrayList<RetIn>> allList = new ArrayList<>();
                        boolean flag = true;
                        for (String no : dataKey) {
                            ArrayList<RetIn> list = new ArrayList<>();
                            for (RetIn retIn : myList) {
                                if (no.equals(retIn.getSh_no())) {
                                    if (retIn.getPs() == retIn.getInd().size()) {
                                        RetIn data = retIn.clone();
                                        data.setRecord_by(User.newInstance().getUsername());
                                        list.add(data);
                                    } else {
                                        flag = false;
                                    }
                                }
                            }
                            if (list.size() > 0)
                                allList.add(list);
                        }
                        if (flag) {
                            for (ArrayList<RetIn> retInList : allList) {
                                final String json = JSONObject.toJSONString(retInList);
                                try {
                                    AppLog.write(getActivity(), "returnIn", json, AppLog.TYPE_INFO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/android/inquiring/pushRetIn", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                        @Override
                                        public void onError(Request request, Exception e) {
                                            if (e instanceof ConnectException)
                                                showConfirmDialog("链接超时");
                                            if (App.LOGCAT_SWITCH) {
                                                Log.i(TAG, "postInventory;" + e.getMessage());
                                                showToast("上传信息失败");
                                            }
                                        }

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                AppLog.write(getActivity(), "returnIn", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                uploadDialog.openView();
                                                hideUploadDialog();
                                                scanResultHandler.removeCallbacks(r);
                                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                                    showToast("上传成功");
                                                    clearData();
                                                    mAdapter.notifyDataSetChanged();
                                                } else {
                                                    showToast("上传失败");
                                                    showConfirmDialog("上传失败");
                                                    Sound.faillarm();
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
                        } else
                            showToast("入库内容必须与退库单号一致");
                        uploadDialog.lockView();
                        scanResultHandler.postDelayed(r, TIME);
                    }
                });
                break;
        }
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
                double weight = 0;
                for (RetInd retInd : item.getInd()) {
                    weight = ArithUtil.add(weight, retInd.getWeight_in());
                }
                holder.setText(R.id.item7, String.valueOf(weight));
            }
        }
    }
}
