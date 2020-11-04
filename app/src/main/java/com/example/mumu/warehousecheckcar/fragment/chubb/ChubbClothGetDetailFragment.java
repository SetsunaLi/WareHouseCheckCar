package com.example.mumu.warehousecheckcar.fragment.chubb;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.chubb.ChubbGetCloth;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 查布接收明细
 *on 2020/4/29
 */
public class ChubbClothGetDetailFragment extends BaseFragment {
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    private ArrayList<ChubbGetCloth> myList;
    private RecycleAdapter mAdapter;
    private int into;

    public static ChubbClothGetDetailFragment newInstance() {
        return new ChubbClothGetDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubb_cloth_get_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new ChubbGetCloth());
        into = getArguments().getInt("into", 0);
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubb_cloth_get_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        text1.setText(String.valueOf(into));
    }

    @Override
    protected void addListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        download();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void download() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("check_group", into);
        final String json = jsonObject.toJSONString();
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/getCheckReceiveDetail", new OkHttpClientManager.ResultCallback<BaseReturnArray<ChubbGetCloth>>() {
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
                public void onResponse(BaseReturnArray<ChubbGetCloth> response) {
                    try {
                        if (response != null && response.getStatus() == 1) {
                            myList.addAll(response.getData());
                            mAdapter.notifyDataSetChanged();
                            text2.setText(String.valueOf(myList.size() - 1));
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

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ChubbGetCloth> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ChubbGetCloth> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        @Override
        public void convert(RecyclerHolder holder, ChubbGetCloth item, int position) {
            if (position != 0) {
                if (item != null) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (!TextUtils.isEmpty(item.getLocation_name()))
                        ll.setBackgroundColor(!item.getLocation_name().contains("查布") ? getResources().getColor(R.color.colorDialogTitleBG) : getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getVat_no());
                    holder.setText(R.id.item2, item.getCloth_name());
                    holder.setText(R.id.item3, item.getInv_serial());
                    holder.setText(R.id.item4, String.valueOf(item.getWeight_inv()));
                    holder.setText(R.id.item5, item.getColor_name());
                    holder.setText(R.id.item6, item.getLocation_name());
                    holder.setText(R.id.item7, item.getPallet_name());
                }
            }
        }
    }
}
