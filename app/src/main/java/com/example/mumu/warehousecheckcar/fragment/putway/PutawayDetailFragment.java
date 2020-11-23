package com.example.mumu.warehousecheckcar.fragment.putway;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.in.Input;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2019/1/8.
 */

public class PutawayDetailFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener {
    private static PutawayDetailFragment fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;

    public static PutawayDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new PutawayDetailFragment();
        return fragment;
    }

    private final String TAG = "PutawayDetailFragment";
    private List<Input> myList;
    private List<Input> dataList;
    private RecycleAdapter mAdapter;

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new Input());//增加一个为头部
        if (App.INPUT_DETAIL_LIST != null && App.INPUT_DETAIL_LIST.size() > 0)
            myList.addAll(App.INPUT_DETAIL_LIST);
        Collections.sort(myList, new Comparator<Input>() {
            @Override
            public int compare(Input obj1, Input obj2) {
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
        dataList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.putaway_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        if (App.INPUT_DETAIL_LIST.size() > 1) {
            text1.setText(String.valueOf(App.INPUT_DETAIL_LIST.size()));
            text2.setText(App.INPUT_DETAIL_LIST.get(1).getVatNo());
        }
    }

    @Override
    protected void addListener() {
        mAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.in_check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.putaway_detail_item, null);
        mAdapter.setHeader(view);
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
        myList.clear();
        dataList.clear();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Input> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Input> datas, int itemLayoutId) {
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
        public void convert(RecyclerHolder holder, Input item, int position) {
            if (position != 0) {
                if (item != null) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (this.index == position) {
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    } else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getFabRool());
                    holder.setText(R.id.item2, item.getProduct_no());
                    holder.setText(R.id.item3, String.valueOf(item.getWeight_in()));
                    holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                }
            }
        }
    }
}