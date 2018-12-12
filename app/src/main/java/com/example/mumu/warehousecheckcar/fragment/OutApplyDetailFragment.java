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
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.ItemMenu;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2018/12/8.
 */

public class OutApplyDetailFragment extends Fragment implements BasePullUpRecyclerAdapter.OnItemClickListener{
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    private static OutApplyDetailFragment fragment;
    private OutApplyDetailFragment(){    }
    public static OutApplyDetailFragment newInstance(){
        if (fragment==null);
        fragment=new OutApplyDetailFragment();
        return fragment;
    }

    private CharSequence mTitle;
    private RecycleAdapter mAdapter;
    private List<InCheckDetail> myList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_apply_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("出库列表");
        myList=new ArrayList<>();
        clearData();
        mAdapter=new RecycleAdapter(recyle,myList,R.layout.apply_item_layout_1);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        return view;
    }
    private void setAdaperHeader(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.apply_item_layout_1,null);
        mAdapter.setHeader(view);
    }
    private void clearData(){
        myList.clear();
        myList.add(new InCheckDetail());
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

    //    主页返回执行
    public void onBackPressed() {
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
                break;
            case R.id.button2:
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position!=0)
        mAdapter.select(position);
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<InCheckDetail> {
        private Context context;
        public void setContext(Context context){
            this.context=context;
        }

        public void setHeader(View mHeaderView){
            super.setHeader(mHeaderView);
        }
        protected int position=-255;
        public void select(int position){
            if (this.position!=-255&&this.position!=position)
                this.position=position;
            else
                this.position=-255;
        }
        public RecycleAdapter(RecyclerView v, Collection<InCheckDetail> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, InCheckDetail item, int position) {
            if (position != 0) {
                if (item != null) {
                    for (ItemMenu im : ItemMenu.values()) {
                        if (im.getIndex() == 0)
                            holder.setText(im.getId(), "" + position);
                        else
                            holder.setText(im.getId(), "");
                    }
                }
            }
        }
    }
}
