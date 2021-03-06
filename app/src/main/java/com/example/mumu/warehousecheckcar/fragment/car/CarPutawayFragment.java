package com.example.mumu.warehousecheckcar.fragment.car;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.App.TIME;

public class CarPutawayFragment extends BaseFragment {
    private final String TAG = "CarPutawayFragment";
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.button2)
    Button button2;


    private static CarPutawayFragment fragment;

    public static CarPutawayFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarPutawayFragment();
        return fragment;
    }

    private ArrayList<Cloth> myList;
    private RecycleAdapter mAdapter;
    private ScanResultHandler scanResultHandler;
    private int assistantID = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_putaway_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getResources().getString(R.string.btn_car_up));
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Cloth());
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.car_putaway_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        if (App.CARRIER != null) {
            if (!TextUtils.isEmpty(App.CARRIER.getLocationNo()))
                text2.setText(App.CARRIER.getLocationNo());
            if (!TextUtils.isEmpty(App.CARRIER.getTrayNo()))
                text3.setText(App.CARRIER.getTrayNo());
        }
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.car_putaway_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg message) {
        switch (message.getStatus()) {
            case 0x05:
                assistantID = (int) message.getPositionObj(0);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.CARRIER != null) {
            if (!TextUtils.isEmpty(App.CARRIER.getTrayNo()))
                downLoading();
        }
    }

    private void downLoading() {
        String pallet = App.CARRIER.getTrayNo();
        JSONObject obj = new JSONObject();
        obj.put("pallet", pallet);
        final String json = obj.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/putaway/getClothFromInvSum", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                    if (App.LOGCAT_SWITCH) {
                        Log.i(TAG, "getInventory;" + e.getMessage());
                        showToast("获取托盘信息失败");
                    }
                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.get("data") != null && jsonObject.getIntValue("status") == 1) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            List<Cloth> response;
                            response = jsonArray.toJavaList(Cloth.class);
                            if (response != null && response.size() != 0) {
                                myList.addAll(response);
                                text1.setText(String.valueOf(myList.size() - 1));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }, json);
        } catch (IOException e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        if (myList.size() < 21) {
            showUploadDialog("是否确认上架");
            setUploadYesClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Cloth> jsocList = new ArrayList<>();
                    for (int i = 1; i < myList.size(); i++) {
                        jsocList.add(myList.get(i));
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("data", jsocList);
                    obj.put("carrier", new Carrier(App.CARRIER.getTrayNo(), App.CARRIER.getLocationNo()));
                    obj.put("userId", User.newInstance().getId());
                    obj.put("assistant", assistantID);
                    final String json = JSON.toJSONString(obj);
                    try {
                        LogUtil.i(getResources().getString(R.string.log_check), json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/putaway/pushClothToCcPalletTransfer", new OkHttpClientManager.ResultCallback<JSONObject>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                if (e instanceof ConnectException)
                                    showConfirmDialog("链接超时");
                                try {
                                    LogUtil.e(getResources().getString(R.string.log_carput_result), e.getMessage(), e);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    try {
                                        LogUtil.i(getResources().getString(R.string.log_check_result), "userId:" + User.newInstance().getId() + response.toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    uploadDialog.openView();
                                    hideUploadDialog();
                                    scanResultHandler.removeCallbacks(r);
                                    BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                    if (baseReturn != null && baseReturn.getStatus() == 1) {
                                        showToast("上传成功");
                                    } else {
                                        showToast("上传失败");
                                        showConfirmDialog("上传失败");
                                        Sound.faillarm();
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }, json);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    uploadDialog.lockView();
                    scanResultHandler.postDelayed(r, TIME);
                }
            });
        } else
            showToast(getResources().getString(R.string.toast_msg));
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cloth> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Cloth> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        private int index = -255;

        public void select(int index) {
            if (this.index == index)
                this.index = -255;
            else
                this.index = index;
        }

        @Override
        public void convert(RecyclerHolder holder, final Cloth item, final int position) {
            if (item != null) {
                if (position != 0) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (index == position) {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getClothNum());
                    holder.setText(R.id.item2, item.getVatNo());
                    holder.setText(R.id.item3, item.getColor());
                    holder.setText(R.id.item4, item.getFabRool());
                    holder.setText(R.id.item5, item.getWeight() + "KG");
                }
            }
        }
    }
}
