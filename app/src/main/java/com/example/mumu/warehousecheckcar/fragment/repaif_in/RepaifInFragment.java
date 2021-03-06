package com.example.mumu.warehousecheckcar.fragment.repaif_in;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnObject;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.in.RepaifIn;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.App.TAG_CONTENT_FRAGMENT;
import static com.example.mumu.warehousecheckcar.App.TIME;

/***
 *created by 
 *on 2020/8/20
 */
public class RepaifInFragment extends CodeFragment implements BRecyclerAdapter.OnItemClickListener {
    private static RepaifInFragment fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.scrollView)
    HorizontalScrollView scrollView;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.headNo)
    LinearLayout headNo;
    @BindView(R.id.layout_title)
    LinearLayout layoutTitle;
    @BindView(R.id.checkbox1)
    CheckBox checkBox;
    String fact_name;
    String sh_no;
    private RecycleAdapter mAdapter;
    private ArrayList<RepaifIn> myList;
    private ArrayList<RepaifIn> dates;
    private ArrayList<String> epcs;

    public static RepaifInFragment newInstance() {
        if (fragment == null) ;
        fragment = new RepaifInFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.repaif_in_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        fact_name = getArguments().getString("fact_name");
        sh_no = getArguments().getString("sh_no");
        myList = new ArrayList<>();
        dates = new ArrayList<>();
        epcs = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.repaif_in_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        text1.setText("送货单号：" + sh_no);
        text2.setText("0");
    }

    @Override
    protected void addListener() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRFID();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (RepaifIn r : myList) {
                    r.setCheck(b);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void clearData() {
        myList.clear();
        dates.clear();
        epcs.clear();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg msg) {
        if (msg != null)
            switch (msg.getStatus()) {
                case 0x02:
                    dates = (ArrayList<RepaifIn>) msg.getPositionObj(0);
                    myList.clear();
                    for (RepaifIn r : dates) {
                        boolean flag = true;
                        for (RepaifIn repaif : myList) {
                            if (repaif.getVat_no().equals(r.getVat_no()) && r.isFlag()) {
                                repaif.addCount();
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            r.setCount(1);
                            myList.add(r);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
            }
    }

    @Override
    public void rfidResult(String epc) {
        if (!epcs.contains(epc)) {
            JSONObject object = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("epc", epc);
            jsonObject.put("sh_no", sh_no);
            jsonObject.put("fact_name", fact_name);
            jsonObject.put("fact_code", "");
            object.put("userId", User.newInstance().getId());
            object.put("data", jsonObject);
            final String json = object.toJSONString();
            OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/back_repair/getClothInfoByEpc/" + json, new OkHttpClientManager.ResultCallback<BaseReturnObject<RepaifIn>>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (App.LOGCAT_SWITCH) {
                        showToast("获取信息失败");
                    }
                }

                @Override
                public void onResponse(BaseReturnObject<RepaifIn> returnArray) {
                    if (returnArray != null && !TextUtils.isEmpty(returnArray.getData().getVat_no())) {
                        if (!epcs.contains(returnArray.getData().getEpc())) {
                            epcs.add(returnArray.getData().getEpc());
                            dates.add(returnArray.getData());
                            boolean flag = true;
                            for (RepaifIn repaif : myList) {
                                if (repaif.getVat_no().equals(returnArray.getData().getVat_no())) {
                                    repaif.addCount();
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                returnArray.getData().setCount(1);
                                myList.add(returnArray.getData());
                            }
                        }
                        text2.setText(String.valueOf(epcs.size()));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                handler.removeMessages(ScanResultHandler.RFID);
                clearData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                showUploadDialog("是否确认入库");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submit();
                        uploadDialog.lockView();
                        handler.postDelayed(r, TIME);
                    }
                });
                break;
        }
    }

    private void submit() {
        ArrayList<RepaifIn> list = new ArrayList<>();
        for (RepaifIn repaif : dates) {
            for (RepaifIn r : myList) {
                if (r.getVat_no().equals(repaif.getVat_no()) && r.isCheck() && repaif.isFlag()) {
                    list.add(repaif);
                    break;
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        jsonObject.put("data", list);
        final String json = jsonObject.toJSONString();
        try {
            LogUtil.i(getResources().getString(R.string.log_repaif_in), json);
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/back_repair/pushBackRepairInfoToERP", new OkHttpClientManager.ResultCallback<BaseReturnObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    try {
                        LogUtil.e(getResources().getString(R.string.log_repaif_in_result), e.getMessage(), e);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onResponse(BaseReturnObject response) {
                    uploadDialog.openView();
                    hideUploadDialog();
                    try {
                        LogUtil.i(getResources().getString(R.string.log_repaif_in_result), "userId:" + User.newInstance().getId() + response.toString());
                        handler.removeCallbacks(r);
                        if (response.getStatus() == 1) {
                            showToast("上传成功");
                            clearData();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast("上传失败");
                            showConfirmDialog("上传失败，" + response.getMessage());
                            Sound.faillarm();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        Fragment fragment = RepaifInDetailFragment.newInstance();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
        EventBus.getDefault().postSticky(new EventBusMsg(0x01, sh_no, myList.get(position).getVat_no(), dates));
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<RepaifIn> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<RepaifIn> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        @Override
        public void convert(RecyclerHolder holder, final RepaifIn item, final int position) {
            if (item != null) {
                holder.setText(R.id.item1, item.getProduct_no());
                holder.setText(R.id.item2, item.getSel_color());
                holder.setText(R.id.item3, item.getVat_no());
                holder.setText(R.id.item4, String.valueOf(item.getCount()));
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setChecked(item.isCheck());
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setCheck(b);
                    }
                });
            }
        }
    }
}
