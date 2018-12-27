package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

import static com.example.mumu.warehousecheckcar.R.id.recyle;

/**
 * Created by mumu on 2018/12/21.
 */

public class OutApplyDetailFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener {
    private static OutApplyDetailFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    private final String TAG="OutApplyDetailFragment";
    private List<Inventory> myList;
    private List<Inventory> dataList;
    private RecycleAdapter mAdapter;

    private OutApplyDetailFragment() {
    }

    public static OutApplyDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutApplyDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }
    public void initData(){
        myList=new ArrayList<>();
        myList.add(new Inventory());//增加一个为头部
        myList.addAll(App.OUT_APPLY_DETAIL);

        Collections.sort(myList, new Comparator<Inventory>() {
            @Override
            public int compare(Inventory Inventory, Inventory t1) {
                String  aFab=Inventory.getFabRool();
                if (aFab==null)
                    return -1;
                String bFab=t1.getFabRool();
                if (bFab==null)
                    return 1;
                if (aFab!=null&&bFab!=null){
                    if (Integer.valueOf(aFab)>=Integer.valueOf(bFab)){
                        return 1;
                    }
                    return -1;
                }
                return 0;
            }
        });
        dataList=new ArrayList<>();
    }
    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.in_check_detail_item_layout);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        if (App.IN_DETAIL_LIST.size() > 1) {
            text1.setText(App.IN_DETAIL_LIST.size() + "");
            text2.setText(App.IN_DETAIL_LIST.get(1).getVatNo() + "");
        }
        return view;
    }
    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.in_check_detail_item_layout, null);
        mAdapter.setHeader(view);
    }
    @Override
    public void onResume() {
        super.onResume();
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
        dataList.clear();
        App.OUTDETAIL_LIST.clear();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        /*mAdapter.select(position);
        mAdapter.notifyDataSetChanged();*/
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
        /* private int index=-255;
         public void select(int index){
             if (this.index==index)
                 this.index=-255;
             else
                 this.index=index;

         }*/
        @Override
        public void convert(RecyclerHolder holder, Inventory item, int position) {
            if (position != 0) {
                if (item != null) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);

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
