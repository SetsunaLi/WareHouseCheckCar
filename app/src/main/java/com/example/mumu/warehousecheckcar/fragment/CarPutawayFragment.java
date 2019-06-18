package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarPutawayFragment extends Fragment {
    private final String TAG = "CarPutawayFragment";
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.button2)
    Button button2;


    private static CarPutawayFragment fragment;

    public static CarPutawayFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarPutawayFragment();
        return fragment;
    }

    private ArrayList<Cloth> myList;
    private RecycleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_putaway_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getResources().getString(R.string.btn_car_up));

        initView();
        initData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.car_putaway_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
        return view;
    }

    private void initView() {
        if (App.CARRIER != null) {
            if (App.CARRIER.getLocationNo() != null && !App.CARRIER.getLocationNo().equals(""))
                text2.setText(App.CARRIER.getLocationNo());
            if (App.CARRIER.getTrayNo() != null && !App.CARRIER.getTrayNo().equals(""))
                text3.setText(App.CARRIER.getTrayNo());
        }
    }

    private void initData() {
        myList = new ArrayList<>();
        myList.add(new Cloth());
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.car_putaway_item, null);
        mAdapter.setHeader(view);
    }
    private int assistantID =0;
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg message) {
        switch (message.getStatus()){
            case 0x05:
                assistantID = (int) message.getPositionObj(0);
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (App.CARRIER != null) {
            if (App.CARRIER.getTrayNo() != null && !App.CARRIER.getTrayNo().equals(""))
                downLoading();
        }
    }

    private void downLoading() {
        String pallet = App.CARRIER.getTrayNo();
        JSONObject obj = new JSONObject();
        obj.put("pallet", pallet);
        final String json = obj.toJSONString();
//        接口
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/putaway/getClothFromInvSum", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onError(Request request, Exception e) {
                    if (App.LOGCAT_SWITCH) {
                        Log.i(TAG, "getInventory;" + e.getMessage());
                        Toast.makeText(getActivity(), "获取托盘信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
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
                                text1.setText(myList.size() - 1 + "");
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
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);

    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        if (myList.size() < 21)
            blinkDialog();
        else
            Toast.makeText(getActivity(),getResources().getString(R.string.toast_msg),Toast.LENGTH_LONG).show();
    }

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认上架");
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                上传数据
                ArrayList<Cloth> jsocList = new ArrayList<>();
                for (int i = 1; i < myList.size(); i++) {
                    jsocList.add(myList.get(i));
                }
                JSONObject obj = new JSONObject();
                obj.put("data", jsocList);
                obj.put("carrier", App.CARRIER);
                obj.put("userId", User.newInstance().getId());
                obj.put("assistant", assistantID);
                final String json = JSON.toJSONString(obj);

                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/putaway/pushClothToCcPalletTransfer", new OkHttpClientManager.ResultCallback<JSONObject>() {
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
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
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
                dialog.dismiss();
            }
        });
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
                    holder.setText(R.id.item1, item.getClothNum() + "");
                    holder.setText(R.id.item2, item.getVatNo() + "");
                    holder.setText(R.id.item3, item.getColor() + "");
                    holder.setText(R.id.item4, item.getFabRool() + "");
                    holder.setText(R.id.item5, "" + String.valueOf(item.getWeight()) + "KG");
                }
            }
        }
    }
}
