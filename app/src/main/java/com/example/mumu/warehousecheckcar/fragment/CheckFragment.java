package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.ItemMenu;
import com.example.mumu.warehousecheckcar.entity.MyTestEnt;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2018/11/26.
 */

public class CheckFragment extends Fragment {
    /*  @Bind(R.id.item1)
      TextView item1;
      @Bind(R.id.item2)
      TextView item2;
      @Bind(R.id.item3)
      TextView item3;
      @Bind(R.id.item4)
      TextView item4;
      @Bind(R.id.item5)
      TextView item5;
      @Bind(R.id.item6)
      TextView item6;
      @Bind(R.id.item7)
      TextView item7;
      @Bind(R.id.item8)
      TextView item8;
      @Bind(R.id.listview)
      ListView listview;
      @Bind(R.id.layout1)
      LinearLayout layout1;*/
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button3)
    Button button3;
    @Bind(R.id.button4)
    Button button4;
    @Bind(R.id.recyle)
    RecyclerView recyle;

    private static CheckFragment fragment;
    private CheckFragment(){    }
    public static CheckFragment newInstance(){
        if (fragment==null);
        fragment=new CheckFragment();
        return fragment;
    }
    //    private MyAdapter myAdapter;
    private RecycleAdapter mAdapter;
    private List<MyTestEnt> myList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_layout_upgrade, container, false);
//        View view = inflater.inflate(R.layout.check_layout, container, false) ;
        ButterKnife.bind(this, view);

        initData();
     /*   myAdapter = new MyAdapter(getActivity(), R.layout.item_layout_1, myList);
        listview.setAdapter(myAdapter);*/

        mAdapter=new RecycleAdapter(recyle,myList,R.layout.item_layout_1);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
//        点击事件可以改视图样式但不可恢复
        mAdapter.setOnItemClickListener(new BRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object data, int position) {
                if (position!=0) {
                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.layout1);
                    ll.setBackground(getResources().getDrawable(R.color.colorAccent));
                }
            }
        });
        LinearLayoutManager ms=new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        return view;
    }

    public void initData() {
        myList = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            myList.add(new MyTestEnt());
        }
    }
    private void setAdaperHeader(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_layout_1,null);
        mAdapter.setHeader(view);
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
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
            case R.id.button4:
                break;
        }
    }
    class RecycleAdapter extends BasePullUpRecyclerAdapter<MyTestEnt>{
        private  Context context;
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
                position=-255;
        }
        public RecycleAdapter(RecyclerView v, Collection<MyTestEnt> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, MyTestEnt item, int position) {
            if (position != 0) {
                if (item != null) {
                    for (ItemMenu im : ItemMenu.values()) {
                        if (im.getIndex() == 0)
                            holder.setText(im.getId(), "" + position);
                        else
                            holder.setText(im.getId(), item.str1);
                    }
                }
            }
        }
    }
    class MyAdapter extends ArrayAdapter<MyTestEnt> {
        private List<MyTestEnt> list;
        private LayoutInflater mInflater;

        public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<MyTestEnt> objects) {
            super(context, resource, objects);
            this.list = objects;
            this.mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_layout_1, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.item1 = (TextView) convertView.findViewById(R.id.item1);
                viewHolder.item2 = (TextView) convertView.findViewById(R.id.item2);
                viewHolder.item3 = (TextView) convertView.findViewById(R.id.item3);
                viewHolder.item4 = (TextView) convertView.findViewById(R.id.item4);
                viewHolder.item5 = (TextView) convertView.findViewById(R.id.item5);
                viewHolder.item6 = (TextView) convertView.findViewById(R.id.item6);
                viewHolder.item7 = (TextView) convertView.findViewById(R.id.item7);
                viewHolder.item8 = (TextView) convertView.findViewById(R.id.item8);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.item1.setText("" + position);
            viewHolder.item2.setText(list.get(position).str1);
            viewHolder.item3.setText(list.get(position).str1);
            viewHolder.item4.setText(list.get(position).str1);
            viewHolder.item5.setText(list.get(position).str1);
            viewHolder.item6.setText(list.get(position).str1);
            viewHolder.item7.setText(list.get(position).str1);
            viewHolder.item8.setText(list.get(position).str1);

            return convertView;
        }

        class ViewHolder {
            TextView item1;
            TextView item2;
            TextView item3;
            TextView item4;
            TextView item5;
            TextView item6;
            TextView item7;
            TextView item8;

        }
    }
}
