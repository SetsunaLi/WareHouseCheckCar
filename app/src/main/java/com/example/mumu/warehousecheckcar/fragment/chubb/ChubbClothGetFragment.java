package com.example.mumu.warehousecheckcar.fragment.chubb;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 查布接收
 *on 2020/4/28
 */
public class ChubbClothGetFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener {
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    private RecycleAdapter mAdapter;
    private ArrayList<ClothGetGroup> myList;

    public static ChubbClothGetFragment newInstance() {
        return new ChubbClothGetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chubb_cloth_get_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.chubb_cloth_get_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        mAdapter.setOnItemClickListener(this);
    }

    private void clearData() {
        myList.clear();
    }

    private void downLoad() {
        OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/check/getCheckReceiveGroup", new OkHttpClientManager.ResultCallback<BaseReturnArray<ClothGetGroup>>() {
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
            public void onResponse(BaseReturnArray<ClothGetGroup> response) {
                try {
                    if (response != null && response.getStatus() == 1) {
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
    public void onResume() {
        super.onResume();
        downLoad();
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
                Fragment fragment = ChubbClothGetNewFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("into", myList.get(position).check_group);
        Fragment fragment = ChubbClothGetDetailFragment.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ClothGetGroup> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<ClothGetGroup> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, ClothGetGroup item, final int position) {
            holder.setText(R.id.item1, "第" + item.getCheck_group() + "组");
            LinearLayout layout = holder.getView(R.id.layout1);
            layout.setBackgroundColor(item.getCheck_status() == 1 ? getResources().getColor(R.color.colorDialogTitleBG) : getResources().getColor(R.color.colorZERO));
        }
    }

    class ClothGetGroup {
        int check_group;
        int check_status;

        public int getCheck_status() {
            return check_status;
        }

        public void setCheck_status(int check_status) {
            this.check_status = check_status;
        }

        public int getCheck_group() {
            return check_group;
        }

        public void setCheck_group(int check_group) {
            this.check_group = check_group;
        }
    }
}
