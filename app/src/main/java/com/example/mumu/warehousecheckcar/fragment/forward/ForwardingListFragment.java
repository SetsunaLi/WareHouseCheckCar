package com.example.mumu.warehousecheckcar.fragment.forward;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.ForwardingListBean;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;
import static com.example.mumu.warehousecheckcar.application.App.TIME;

/***
 *created by 
 *on 2020/5/19
 */
public class ForwardingListFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener {

    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button1)
    Button button1;
    private RecycleAdapter mAdapter;
    private ArrayList<ForwardingListBean> myList;
    private Handler handler;

    public static ForwardingListFragment newInstance() {
        return new ForwardingListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forwarding_list_fragment, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("主页");
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new ForwardingListBean());
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.forwarding_list_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        mAdapter.setHeader(LayoutInflater.from(getActivity()).inflate(R.layout.forwarding_list_head, null));
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        handler = new Handler();
        mAdapter.setOnItemClickListener(this);
    }

    private void clearData() {
        myList.clear();
        myList.add(new ForwardingListBean());
    }

    @Override
    public void onResume() {
        super.onResume();
        downLoad();
    }

    private void downLoad() {
        showLoadingDialog();
        OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/getTransportOutList", new OkHttpClientManager.ResultCallback<BaseReturnArray<ForwardingListBean>>() {
            @Override
            public void onError(Request request, Exception e) {
                if (e instanceof ConnectException)
                    showConfirmDialog("链接超时");
                if (App.LOGCAT_SWITCH) {
                    showToast("获取申请单信息失败");

                }
            }

            @Override
            public void onResponse(BaseReturnArray<ForwardingListBean> response) {
                try {
                    hideLoadingDialog();
                    if (response != null && response.getStatus() == 1) {
                        clearData();
                        myList.addAll(response.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position > 0) {
            String carNo = myList.get(position).getLicense_plate();
            String name = myList.get(position).getDriver();
            carNo = carNo.replaceAll(" ", "");
            name = name.replaceAll(" ", "");
            EventBus.getDefault().postSticky(new EventBusMsg(0x00, new ForwardingMsgFragment.CarMsg(carNo, name), myList.get(position).getId()));
            Fragment fragment = ForwardingNoFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                downLoad();
                break;
            case R.id.button2:
                Fragment fragment = ForwardingMsgFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ForwardingListBean> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ForwardingListBean> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public void setContext(Context context) {
            this.context = context;
        }


        @Override
        public void convert(RecyclerHolder holder, final ForwardingListBean item, final int position) {
            if (position != 0) {
                holder.setText(R.id.item1, item.getLicense_plate());
                holder.setText(R.id.item2, String.valueOf(item.getQty()));
                holder.setText(R.id.item3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getCreatedatetime()));
                Button button = holder.getView(R.id.button);
                if (item.getOutput_status() == 0)
                    button.setEnabled(true);
                else
                    button.setEnabled(false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUploadDialog("是否结束装车？");
                        setUploadYesClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                uploadDialog.lockView();
                                handler.postDelayed(r, TIME);
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("userId", User.newInstance().getId());
                                    jsonObject.put("status", 1);
                                    jsonObject.put("cc_transport_output_id", item.getId());
                                    final String json = jsonObject.toJSONString();
                                    try {
                                        AppLog.write(context, "finishF", "userId:" + User.newInstance().getId() + json, AppLog.TYPE_INFO);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/postTransportOut", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                        @Override
                                        public void onError(Request request, Exception e) {
                                            if (e instanceof ConnectException)
                                                showConfirmDialog("链接超时");
                                            if (App.LOGCAT_SWITCH) {
                                                showToast("上传信息失败");
                                            }
                                        }

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                AppLog.write(context, "finishF", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                uploadDialog.openView();
                                                hideUploadDialog();
                                                handler.removeCallbacks(r);
                                                if (response != null && response.getInteger("status") == 1) {
                                                    showToast("上传成功");
                                                    onClick(button2);
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
                                }
                            }
                        });

                    }
                });
            }
        }
    }

}
