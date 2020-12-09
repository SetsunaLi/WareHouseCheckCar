package com.example.mumu.warehousecheckcar.fragment.check;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.check.Inventory;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/12/26.
 */

public class CheckDetailFragment extends BaseFragment {

    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;

    private static CheckDetailFragment fragment;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.text4)
    TextView text4;


    public static CheckDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new CheckDetailFragment();
        return fragment;
    }

    private final String TAG = "CheckDetailFragment";
    private List<Inventory> myList;
    private RecycleAdapter mAdapter;

    @Override
    public void initData() {
        myList = new ArrayList<>();
        myList.add(new Inventory());
        myList.addAll((ArrayList<Inventory>) getArguments().getSerializable("list"));
        Collections.sort(myList, new Comparator<Inventory>() {
            @Override
            public int compare(Inventory obj1, Inventory obj2) {
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
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.item_check_detail);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        if (myList.size() > 2) {
            text1.setText(myList.get(1).getVatNo());
            int real = 0;
            int profit = 0;
            int losses = 0;
            for (Inventory obj : myList) {
                if (obj != null && obj.getVatNo() != null)
                    switch (obj.getFlag()) {
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
            text2.setText(String.valueOf(real));
            text3.setText(String.valueOf(profit));
            text4.setText(String.valueOf(losses));
        }
    }

    @Override
    protected void addListener() {

    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_detail_layout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_detail_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myList.clear();
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
        myList.remove(0);
        EventBus.getDefault().post(new EventBusMsg(0x30, myList));
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
        public void convert(RecyclerHolder holder, final Inventory item, int position) {
            if (item != null)
                if (position != 0) {
                    LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                    ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    if (item.getFlag() == 2)//正常
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    if (item.getWeight() == 0)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorREAD));
                    if (item.getWeight() > 0 && item.getFlag() == 1)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));

      /*              if (item.getFlag() == 0)//亏
                        ll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    else if (item.getFlag() == 1)//盈
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else if (item.getFlag() == 2)//正常
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));*/

                    holder.setText(R.id.item1, item.getFabRool());
                    holder.setText(R.id.item2, item.getProduct_no());
                    holder.setText(R.id.item3, String.valueOf(item.getWeight_in()));
                    holder.setText(R.id.item5, item.getColor());
                    holder.setText(R.id.item6, item.getSelNo());
//                    holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                    EditText editText = holder.getView(R.id.edit1);
                    editText.setEnabled(true);
                    editText.setText(String.valueOf(item.getWeight()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                String weight = charSequence.toString();
                                weight = weight.replaceAll(" ", "");
                                if (!TextUtils.isEmpty(weight)) {
                                    double a = Double.parseDouble(weight);
                                    item.setWeight(a);
                                } else {
                                    item.setWeight(0);
                                }
                            } catch (Exception e) {
                                item.setWeight(item.getWeight_in());
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }
        }
    }
}
