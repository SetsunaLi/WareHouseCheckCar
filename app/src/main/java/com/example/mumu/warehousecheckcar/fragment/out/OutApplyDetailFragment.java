package com.example.mumu.warehousecheckcar.fragment.out;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.out.Output;
import com.example.mumu.warehousecheckcar.entity.out.OutputDetail;
import com.example.mumu.warehousecheckcar.entity.out.OutputFlag;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/12/21.
 */

public class OutApplyDetailFragment extends BaseFragment {
    private static OutApplyDetailFragment fragment;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    private final String TAG = "OutApplyDetailFragment";
    private List<OutputDetail> myList;
    private Output oldData;
    private HashMap<String, OutputFlag> dataList;
    private int id;
    private RecycleAdapter mAdapter;

    public static OutApplyDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new OutApplyDetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add(new OutputDetail());
        load();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_put_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        text1.setText(String.valueOf(myList.size() - 1));
        text2.setText(oldData.getVatNo());
    }

    @Override
    protected void addListener() {

    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.out_put_detail_item, null);
        mAdapter.setHeader(view);
    }

    public void load() {
        oldData = (Output) getArguments().getSerializable("dataList");
        dataList = (HashMap<String, OutputFlag>) getArguments().getSerializable("epcList");
        id = (int) getArguments().getSerializable("position");
        myList.addAll(oldData.getList());
        try {
            Collections.sort(myList, new Comparator<OutputDetail>() {
                @Override
                public int compare(OutputDetail obj1, OutputDetail obj2) {
                    if (TextUtils.isEmpty(obj1.getEpc()) && !TextUtils.isEmpty(obj2.getEpc()))
                        return -1;
                    else if (!TextUtils.isEmpty(obj1.getEpc()) && TextUtils.isEmpty(obj2.getEpc()))
                        return 1;
                    else if (TextUtils.isEmpty(obj1.getEpc()) && TextUtils.isEmpty(obj2.getEpc()))
                        return 0;
                    else {
                        if (dataList.containsKey(obj1.getEpc()) && dataList.containsKey(obj2.getEpc())) {
                            if (dataList.get(obj1.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)
                                    && !dataList.get(obj2.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)) {
                                return -1;
                            } else if (!dataList.get(obj1.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)
                                    && dataList.get(obj2.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)) {
                                return 1;
                            } else {
                                if (dataList.get(obj1.getEpc()).isFind() && !dataList.get(obj2.getEpc()).isFind())
                                    return -1;
                                else if (dataList.get(obj2.getEpc()).isFind() && !dataList.get(obj1.getEpc()).isFind())
                                    return 1;
                            }
                        }
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
                }
            });
        } catch (Exception e) {

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

    public HashMap getList() {
        return dataList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        myList.clear();
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<OutputDetail> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<OutputDetail> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        private int position;

        public void select(int i) {
            this.position = i;
        }

        @Override
        public void convert(RecyclerHolder holder, final OutputDetail item, final int position) {
            if (item != null) {
                if (oldData.getFlag() != 2) {
                    final CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (position == 0) {
                                if (isChecked) {
                                    for (int i = 1; i < myList.size(); i++) {
                                        if (oldData.getCountProfit() < oldData.getCountOut()) {
                                            if (!TextUtils.isEmpty(myList.get(i).getEpc()))
                                                if (dataList.get(myList.get(i).getEpc()).isFind() && dataList.get(myList.get(i).getEpc()).getApplyNo().equals("")) {
                                                    dataList.get(myList.get(i).getEpc()).setApplyNo(oldData.getApplyNo() + id);
                                                    oldData.addCountProfit();
                                                }
                                        } else
                                            break;
                                    }
                                } else {
                                    oldData.setCountProfit(0);
                                    for (int i = 1; i < myList.size(); i++) {
                                        if (!TextUtils.isEmpty(myList.get(i).getEpc()))
                                            if (dataList.get(myList.get(i).getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)) {
                                                dataList.get(myList.get(i).getEpc()).setApplyNo("");
                                            }
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if (isChecked) {
                                    if (!TextUtils.isEmpty(item.getEpc()))
                                        if (dataList.get(item.getEpc()).isFind()) {
                                            if (dataList.get(item.getEpc()).getApplyNo().equals("")) {
                                                if (oldData.getCountProfit() + 1 <= oldData.getCountOut()) {
                                                    dataList.get(item.getEpc()).setApplyNo(oldData.getApplyNo() + id);
                                                    oldData.addCountProfit();
                                                } else {
                                                    cb.setChecked(false);
                                                    showToast("已选布匹超出申请数量");
                                                }
                                            }
                                        }

                                } else {
                                    if (!TextUtils.isEmpty(item.getEpc()))
                                        if (dataList.get(item.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)) {
                                            dataList.get(item.getEpc()).setApplyNo("");
//                                            防止配货数量为负数
                                            if (oldData.getCountProfit() < 1)
                                                oldData.setCountProfit(oldData.getCountProfit() - 1);
                                            else
                                                oldData.setCountProfit(0);

                                        }
                                }

                            }
                        }
                    });
                    if (position != 0) {
                        if (TextUtils.isEmpty(item.getFabRool()) && TextUtils.isEmpty(item.getEpc())) {
                            cb.setChecked(false);
                            if (cb.isEnabled() != false)
                                cb.setEnabled(false);
                        } else {
                            if (item.getEpc() != null && !item.getEpc().equals(""))
                                if (dataList.get(item.getEpc()).isFind() &&
                                        (dataList.get(item.getEpc()).getApplyNo().equals("") || dataList.get(item.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id))) {
                                    if (cb.isEnabled() != true)
                                        cb.setEnabled(true);
                                } else {
                                    if (cb.isEnabled() != false)
                                        cb.setEnabled(false);
                                }
                            if (!TextUtils.isEmpty(item.getEpc()))
                                if (dataList.get(item.getEpc()).getApplyNo().equals(oldData.getApplyNo() + id))
                                    cb.setChecked(true);
                                else
                                    cb.setChecked(false);
                        }

                        LinearLayout ll = (LinearLayout) holder.getView(R.id.layout1);
                        if (item.getEpc() != null && !item.getEpc().equals(""))
                            if (!dataList.get(item.getEpc()).isFind()) {//亏
                                ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                                cb.setEnabled(false);
                            } else {
                                if (item.getWeight() < item.getWeight_in())
                                    ll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                else
                                    ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                                cb.setEnabled(true);
                            }
                        final EditText editText = (EditText) holder.getView(R.id.edittext1);
                        editText.setEnabled(true);
                        if (dataList.containsKey(item.getEpc()))
                            editText.setText(String.valueOf(dataList.get(item.getEpc()).getWeight()));
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    String weight = s.toString();
                                    weight = weight.replaceAll(" ", "");
                                    if (!TextUtils.isEmpty(weight)) {
                                        double a = Double.parseDouble(weight);
                                        if (dataList.containsKey(item.getEpc()))
                                            dataList.get(item.getEpc()).setWeight(a);
                                    } else {
                                        if (dataList.containsKey(item.getEpc()))
                                            dataList.get(item.getEpc()).setWeight(0);
                                    }
                                } catch (Exception e) {
                                    editText.setText(String.valueOf(item.getWeight()));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        holder.setText(R.id.item1, item.getFabRool());
                        holder.setText(R.id.item2, oldData.getProduct_no());
                        holder.setText(R.id.item3, String.valueOf(item.getWeight_in()));
                        holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                    } else {
                        EditText editText = (EditText) holder.getView(R.id.edittext1);
                        editText.setEnabled(false);
                    }

                } else {
                    if (position != 0) {
                        final CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                        cb.setEnabled(false);
                        cb.setChecked(false);
                        holder.setText(R.id.item1, item.getFabRool());
                        holder.setText(R.id.item2, oldData.getProduct_no());
                        holder.setText(R.id.item3, String.valueOf(item.getWeight_in()));
                        holder.setText(R.id.item4, String.valueOf(item.getWeight()));
                    } else {
                        EditText editText = (EditText) holder.getView(R.id.edittext1);
                        editText.setEnabled(false);
                    }
                }
            }
        }
    }
}
