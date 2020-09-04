package com.example.mumu.warehousecheckcar.fragment.in;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.in.InCheckDetail;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.squareup.okhttp.Request;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/12/12.
 */

public class InCheckDetialFragment extends BaseFragment {
    private static InCheckDetialFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    private final String TAG="InCheckDetialFragment";
    private List<InCheckDetail> myList;
    private List<InCheckDetail> dataList;
    private RecycleAdapter mAdapter;

    public static InCheckDetialFragment newInstance() {
        if (fragment == null) ;
        fragment = new InCheckDetialFragment();
        return fragment;
    }

    @Override
    protected void initData() {
        myList=new ArrayList<>();
        myList.add(new InCheckDetail());//增加一个为头部
        myList.addAll(App.IN_DETAIL_LIST);
        for(InCheckDetail old:myList){
            if (old.getFabRool()!=null)
                old.setFlag(true);
        }
        Collections.sort(myList, new Comparator<InCheckDetail>() {
            @Override
            public int compare(InCheckDetail obj1, InCheckDetail obj2) {
                String aFab = obj1.getFabRool();
                String bFab = obj2.getFabRool();
                if (TextUtils.isEmpty(aFab) & !TextUtils.isEmpty(bFab))
                    return -1;
                else if (TextUtils.isEmpty(bFab) & !TextUtils.isEmpty(aFab))
                    return 1;
                else if (TextUtils.isEmpty(bFab) & TextUtils.isEmpty(aFab))
                    return 0;
                else {
                    int a = aFab.compareTo(bFab);
                    if (a == 0) {
                        return 0;
                    } else if (a > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });
        dataList=new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_check_detail_item_layout);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.in_check_detail_item_layout, null);
        mAdapter.setHeader(view);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (myList.size() >= 2)
            if (myList.get(1) != null && myList.get(1).getVatNo() != null) {
                final String json = myList.get(1).getVatNo();
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getVatNo_in.sh", new OkHttpClientManager.ResultCallback<List<InCheckDetail>>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (e instanceof ConnectException)
                                        showConfirmDialog("链接超时");
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "getVatNo_in;" + e.getMessage());
                                        showToast("获取缸号失败");
                                    }
                                }

                                @Override
                                public void onResponse(List<InCheckDetail> response) {
                                   try{
                                        if (response != null) {
                                            List<InCheckDetail> newList = new ArrayList<InCheckDetail>();
                                            for (InCheckDetail re : response) {
                                                if (re != null && re.getFabRool() != null) {
                                                    boolean isIn = false;
                                                    for (InCheckDetail old : myList) {
                                                        if (old != null && old.getFabRool() != null)
                                                            if (old.getFabRool().equals(re.getFabRool())) {
                                                                isIn = true;
                                                                old.setFlag(true);
                                                            }
                                                    }
                                                    if (!isIn)
                                                        newList.add(re);
                                                }
                                            }
                                            myList.addAll(newList);
                                            newList.clear();
                                        }
                                        Collections.sort(myList, new Comparator<InCheckDetail>() {
                                            @Override
                                            public int compare(InCheckDetail obj1, InCheckDetail obj2) {
                                                String aFab = obj1.getFabRool();
                                                String bFab = obj2.getFabRool();
                                                if (TextUtils.isEmpty(aFab) & !TextUtils.isEmpty(bFab))
                                                    return -1;
                                                else if (TextUtils.isEmpty(bFab) & !TextUtils.isEmpty(aFab))
                                                    return 1;
                                                else if (TextUtils.isEmpty(bFab) & TextUtils.isEmpty(aFab))
                                                    return 0;
                                                else {
                                                    int a = aFab.compareTo(bFab);
                                                    if (a == 0) {
                                                        return 0;
                                                    } else if (a > 0) {
                                                        return 1;
                                                    } else {
                                                        return -1;
                                                    }
                                                }
                                            }
                                        });
                                        mAdapter.notifyDataSetChanged();
                                   }catch (Exception e){

                                   }
                                }
                            }, json);
                        } catch (Exception e) {

                        }
            }
        if (App.IN_DETAIL_LIST.size() > 1) {
            text1.setText(String.valueOf(App.IN_DETAIL_LIST.size()));
            text2.setText(App.IN_DETAIL_LIST.get(0).getVatNo());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        myList.clear();
        dataList.clear();
        App.OUTDETAIL_LIST.clear();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<InCheckDetail> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<InCheckDetail> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, InCheckDetail item, int position) {
            if (position != 0) {
                if (item != null) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (item.isFlag())
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getFabRool());
                    holder.setText(R.id.item2, item.getProduct_no());
                    holder.setText(R.id.item3, String.valueOf(item.getWeight_in()));
                    holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                    holder.setText(R.id.item5, item.getColor());
                    holder.setText(R.id.item6, item.getSelNo());
                }
            }
        }
    }
}
