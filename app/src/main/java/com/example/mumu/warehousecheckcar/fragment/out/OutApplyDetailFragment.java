package com.example.mumu.warehousecheckcar.fragment.out;

import android.app.Fragment;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.Output;
import com.example.mumu.warehousecheckcar.entity.OutputDetail;
import com.example.mumu.warehousecheckcar.entity.OutputFlag;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    int COUNT;

    public void initData() {
        myList = new ArrayList<>();
        myList.add(new OutputDetail());//增加一个为头部
    }

    private LinearLayoutManager llm;

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_check_detail_layout, container, false);
        ButterKnife.bind(this, view);
        initUtil();
        initData();
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_put_detail_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

      /*  if (App.OUTPUT_DETAIL_LIST.size() > 0) {
            text2.setText(App.OUTPUT_DETAIL_LIST.get(0).getVat_no() + "");
            int i = 0;
            for (OutputDetail od : App.OUTPUT_DETAIL_LIST.get(0).getList())
                if (od.getFlag() != 0)
                    i++;
            text1.setText(i + "");
        }*/
        return view;
    }

    private InputMethodManager mInputMethodManager;


    //    *
//     * 初始化必须工具
//
    private void initUtil() {
        //初始化输入法
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.out_put_detail_item, null);
        mAdapter.setHeader(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
        text1.setText(myList.size() - 1 + "");
        text2.setText(oldData.getVatNo() + "");
        mAdapter.notifyDataSetChanged();
    }

    public void load() {
        oldData = (Output) getArguments().getSerializable("dataList");
        dataList = (HashMap<String, OutputFlag>) getArguments().getSerializable("epcList");
        id = (int) getArguments().getSerializable("position");
     /*   int pei = 0;
        for (OutputDetail i : oldData.getList()) {
            if (dataList.get(i.getEpc()).getOut_no().equals(oldData.getOut_no())) {
                pei++;
            }
        }*/
//        oldData.setCountProfit(0);
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
                            }else {
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

    public HashMap getList() {
        return dataList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
//        ((Main2Activity)getActivity()).setOutApplyDataList(dataList);
        myList.clear();
//        App.OUTPUT_DETAIL_LIST.clear();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
//        mAdapter.select(id);
//        mAdapter.notifyDataSetChanged();
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
                                            if (myList.get(i).getEpc() != null && !myList.get(i).getEpc().equals(""))
                                                if (dataList.get(myList.get(i).getEpc()).isFind() && dataList.get(myList.get(i).getEpc()).getApplyNo().equals("")) {
                                                    dataList.get(myList.get(i).getEpc()).setApplyNo(oldData.getApplyNo() + id);
//                                                    oldData.setCountProfit(oldData.getCountProfit() + 1);
                                                    oldData.addCountProfit();
                                                }
                                        } else
                                            break;
                                    }
                                } else {
                                    oldData.setCountProfit(0);
                                    for (int i = 1; i < myList.size(); i++) {
                                        if (myList.get(i).getEpc() != null && !myList.get(i).getEpc().equals(""))
                                            if (dataList.get(myList.get(i).getEpc()).getApplyNo().equals(oldData.getApplyNo() + id)) {
                                                dataList.get(myList.get(i).getEpc()).setApplyNo("");
                                            }
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if (isChecked) {
                                    if (item.getEpc() != null && !item.getEpc().equals(""))
                                        if (dataList.get(item.getEpc()).isFind()) {
                                            if (dataList.get(item.getEpc()).getApplyNo().equals("")) {
                                                if (oldData.getCountProfit() + 1 <= oldData.getCountOut()) {
                                                    dataList.get(item.getEpc()).setApplyNo(oldData.getApplyNo() + id);
                                                    oldData.addCountProfit();
                                                } else {
                                                    cb.setChecked(false);
                                                    Toast.makeText(getActivity(), "已选布匹超出申请数量！", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                } else {
                                    if (item.getEpc() != null && !item.getEpc().equals(""))
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
                        if (((item.getFabRool() + "").equals("") && (item.getEpc() + "").equals("") && (item.getWeight() + "").equals(""))) {
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
                            if (item.getEpc() != null && !item.getEpc().equals(""))

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
                            editText.setText(dataList.get(item.getEpc()).getWeight() + "");
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    String weight = s.toString();
                                    weight = weight.replaceAll(" ", "");
                                    if (weight != null && !weight.equals("")) {
                                        double a = Double.parseDouble(weight);
                                        if (dataList.containsKey(item.getEpc()))
                                            dataList.get(item.getEpc()).setWeight(a);
                                    } else {
                                        if (dataList.containsKey(item.getEpc()))
                                            dataList.get(item.getEpc()).setWeight(0);
                                    }
                                } catch (Exception e) {
                                    editText.setText(item.getWeight() + "");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                       /* editText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setFocusable(true);//设置输入框可聚集
                                v.setFocusableInTouchMode(true);//设置触摸聚焦
                                v.requestFocus();//请求焦点
                                v.findFocus();//获取焦点
                                ((EditText)v).setCursorVisible(true);
                                ((EditText)v).setSelection( ((EditText)v).getText().length());
                                mInputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);// 显示输入法
                            }
                        });*/

                        holder.setText(R.id.item1, item.getFabRool() + "");
                        holder.setText(R.id.item2, oldData.getProduct_no() + "");
                        holder.setText(R.id.item3, item.getWeight_in() + "");
                        holder.setText(R.id.item4, item.getWeight() + "");
                    } else {
                        EditText editText = (EditText) holder.getView(R.id.edittext1);
                        editText.setEnabled(false);
                    }

                } else {
                    if (position != 0) {
                        final CheckBox cb = (CheckBox) holder.getView(R.id.checkbox1);
                        cb.setEnabled(false);
                        cb.setChecked(false);
                        holder.setText(R.id.item1, item.getFabRool() + "");
                        holder.setText(R.id.item2, oldData.getProduct_no() + "");
                        holder.setText(R.id.item3, item.getWeight_in() + "");
                        holder.setText(R.id.item4, item.getWeight() + "");
                    } else {
                        EditText editText = (EditText) holder.getView(R.id.edittext1);
                        editText.setEnabled(false);
                    }
                }
            }
        }
    }
}
