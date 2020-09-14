package com.example.mumu.warehousecheckcar.fragment.forward;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.forwarding.ApplyNo;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;

/***
 *created by 
 *on 2020/8/31
 */
public class ApplyNoListFragment extends BaseFragment {
    private static ApplyNoListFragment fragment;
    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.item1)
    TextView item1;
    @BindView(R.id.item2)
    TextView item2;
    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    private ArrayList<ApplyNo> myList;
    private RecycleAdapter mAdapter;
    private int bas_transport_type;
    private ForwardingMsgFragment.CarMsg carMsg;
    private ArrayList<String> nos;

    public static ApplyNoListFragment newInstance() {
        if (fragment == null) ;
        fragment = new ApplyNoListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.apply_no_list_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        bas_transport_type = getArguments().getInt("bas_transport_type", 0);
        carMsg = (ForwardingMsgFragment.CarMsg) getArguments().getSerializable("carMsg");
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.apply_no_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (ApplyNo applyNo : myList) {
                    applyNo.setFlag(b);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dnowload();
    }

    private void dnowload() {
        myList.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        JSONObject data = new JSONObject();
        data.put("carLicense", carMsg.getCarNo());
        data.put("cc_transport_output_id", bas_transport_type);
        jsonObject.put("data", data);
        String json = jsonObject.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/getTransportApplyNoByLicense", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                }

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInteger("status") == 1) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            for (String key : object.keySet()) {
                                myList.add(new ApplyNo(key, false, object.getInteger(key)));
                            }
                            mAdapter.notifyDataSetChanged();
                        } else
                            showConfirmDialog(jsonObject.getString("message"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().post(new EventBusMsg(0x31));
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                EventBus.getDefault().postSticky(new EventBusMsg(0x00, carMsg, bas_transport_type));
                Fragment fragment = ForwardingNoFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
            case R.id.button2:
                showUploadDialog("是否移除申请单");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete();
                    }
                });
                break;
        }
    }

    private void delete() {
        uploadDialog.lockView();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        JSONArray jsonArray = new JSONArray();
        for (ApplyNo applyNo : myList) {
            if (applyNo.isFlag()) {
                JSONObject object = new JSONObject();
                object.put("out_no", applyNo.getApplyNo());
                object.put("cc_transport_output_id", bas_transport_type);
                jsonArray.add(object);
            }
            jsonObject.put("data", jsonArray);
        }
        final String json = JSON.toJSONString(jsonObject);
        try {
            AppLog.write(getActivity(), "removeTransportApplyNo", json, AppLog.TYPE_INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/removeTransportApplyNo", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        AppLog.write(getActivity(), "removeTransportApplyNo", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadDialog.openView();
                        hideUploadDialog();
                        BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                        if (baseReturn != null && baseReturn.getStatus() == 1) {
                            showToast("上传成功");
                            dnowload();
                        } else {
                            showToast("上传失败");
                            showConfirmDialog("上传失败");
                            Sound.faillarm();
                        }
                    } catch (Exception e) {

                    }
                }
            }, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ApplyNo> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ApplyNo> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final ApplyNo item, final int position) {
            CheckBox checkBox = holder.getView(R.id.checkbox1);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setFlag(b);
                }
            });
            checkBox.setChecked(item.isFlag());
            holder.setText(R.id.item1, item.getApplyNo());
            holder.setText(R.id.item2, String.valueOf(item.getCount()));
        }
    }
}