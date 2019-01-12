package com.example.mumu.warehousecheckcar.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.entity.Inventory;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/12/26.
 */

public class CheckDetailFragment extends Fragment{

    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;

    private static CheckDetailFragment fragment;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;

    private CheckDetailFragment() {
    }

    public static CheckDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new CheckDetailFragment();
        return fragment;
    }

    private final String TAG = "CheckDetailFragment";
    private List<Inventory> myList;
    private RecycleAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    public void initData() {
        myList = new ArrayList<>();
        myList.add(new Inventory());//增加一个为头部
        myList.addAll(App.CHECK_DETAIL_LIST);
        Collections.sort(myList, new Comparator<Inventory>() {
            @Override
            public int compare(Inventory obj1, Inventory obj2) {
                String aFab = obj1.getFabRool();
                if (aFab == null||aFab.equals(""))
                    return -1;
                String bFab = obj2.getFabRool();
                if (bFab == null||bFab.equals(""))
                    return 1;
                if (aFab != null && bFab != null) {
                    if (Integer.valueOf(aFab) >= Integer.valueOf(bFab)) {
                        return 1;
                    }
                    return -1;
                }
                return 0;
            }
        });
    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_check_detail_item_layout);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        if (myList.size() > 2) {
            text1.setText(myList.get(1).getVatNo() + "");
            int real=0;
            int profit=0;
            int losses=0;
            for(Inventory obj:myList){
                if (obj!=null&&obj.getVatNo()!=null)
                    switch (obj.getFlag()){
                        case 0:
                            losses++;
                            break;
                        case 1:
                            profit++;
                            break;
                        case 2:
                            real++;
                            break;
                    }
            }
            text2.setText(real+"");
            text3.setText(profit+"");
            text4.setText(losses+"");
        }
        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_detail_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.CHECK_DETAIL_LIST.clear();
        myList.clear();
    }

    //右上角列表R.menu.main2
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
    }

    //右上角列表点击监听（相当于onclickitemlistener,可用id或者title匹配）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
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
        App.CHECK_DETAIL_LIST.clear();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Inventory> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<Inventory> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, Inventory item, int position) {
            if (position != 0) {
                if (item != null) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    if (item.getFlag()==0)//亏
                        ll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    else if (item.getFlag()==1)//盈
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else if(item.getFlag()==2)//正常
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));

//                        holder.setBackground(R.id.layout1,getResources().getColor(R.color.colorAccent));
                    holder.setText(R.id.item1, item.getFabRool() + "");
                    holder.setText(R.id.item2, item.getProduct_no() + "");
                    holder.setText(R.id.item3, item.getWeight_in() + "");
                    holder.setText(R.id.item4, item.getWeight() + "");
                    holder.setText(R.id.item5, item.getColor() + "");
                    holder.setText(R.id.item6, item.getSelNo() + "");
                }
            }
        }
    }
}
