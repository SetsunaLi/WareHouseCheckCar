package com.example.mumu.warehousecheckcar.fragment.forward;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TAG_CONTENT_FRAGMENT;

/***
 *created by 
 *on 2020/8/30
 */
public class ShipmentNoFragment extends BaseFragment implements RXCallback, OnCodeResult {
    private static ShipmentNoFragment fragment;
    @BindView(R.id.imgbutton)
    ImageButton imgbutton;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.button2)
    Button button2;

    private int bas_transport_type;
    private ForwardingMsgFragment.CarMsg carMsg;
    private String company;

    public static ShipmentNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new ShipmentNoFragment();
        return fragment;
    }

    private ArrayList<String> myList;
    private RecycleAdapter mAdapter;
    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shipment_no_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add("");
        bas_transport_type = getArguments().getInt("bas_transport_type", 0);
        carMsg = (ForwardingMsgFragment.CarMsg) getArguments().getSerializable("carMsg");
        company = getArguments().getString("company");
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_applyno_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        init2D();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg eventBusMsg) {
        switch (eventBusMsg.getStatus()) {
            case 0x31:
                init2D();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setId(0);
        mAdapter.select(0);
        mAdapter.notifyDataSetChanged();
    }

    private void init2D() {
        if (!PdaController.init2D(this)) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        }
    }

    private void disConnect2D() {
        if (!PdaController.disConnect2D()) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        disConnect2D();
    }

    @OnClick({R.id.imgbutton, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgbutton:
                addItem();
                break;
            case R.id.button2:
                mAdapter.select(-255);
                mAdapter.notifyDataSetChanged();
                boolean flag = false;
                for (String str : myList) {
                    if (!TextUtils.isEmpty(str))
                        flag = true;
                }
                if (flag) {
                    disConnect2D();
                    Bundle bundle = new Bundle();
                    bundle.putInt("bas_transport_type", bas_transport_type);
                    bundle.putSerializable("carMsg", carMsg);
                    bundle.putStringArrayList("no", myList);
                    bundle.putString("company", company);
                    Fragment fragment = ShipmentOutNoFragment.newInstance();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    showToast(getResources().getString(R.string.forwarding_toast_msg));
                }
                break;
        }
    }

    private void addItem() {
        for (int i = 0; i < myList.size(); i++) {
            if (TextUtils.isEmpty(myList.get(i))) {
                mAdapter.select(i);
                mAdapter.setId(i);
                mAdapter.notifyDataSetChanged();
                recyle.scrollToPosition(i);
                return;
            }
        }
        myList.add("");
        mAdapter.select(myList.size() - 1);
        mAdapter.setId(myList.size() - 1);
        mAdapter.notifyDataSetChanged();
        recyle.scrollToPosition(myList.size() - 1);
    }

    @Override
    public void callback(byte[] bytes) {
        Message msg = scanResultHandler.obtainMessage();
        msg.what = ScanResultHandler.CODE;
        msg.obj = new String(bytes);
        scanResultHandler.sendMessage(msg);
    }

    @Override
    public void codeResult(String code) {
        code = code.replaceAll(" ", "");
        int id = mAdapter.getId();
        myList.set(id, code);
        addItem();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<String> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        private int position = -255;

        public void select(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        private int id = -255;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public RecycleAdapter(RecyclerView v, Collection<String> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, String item, final int position) {
            final FixedEditText editNo = (FixedEditText) holder.getView(R.id.fixeedittext1);
            editNo.setHint("请输入发运单号");
            editNo.setTag(position);
            editNo.setText(item);
            if (position == this.position) {
                editNo.setFocusable(true);//设置输入框可聚集
                editNo.setFocusableInTouchMode(true);//设置触摸聚焦
                editNo.requestFocus();//请求焦点
                editNo.findFocus();//获取焦点
                editNo.setCursorVisible(true);
                editNo.setSelection(editNo.getText().length());
                showKeyBoard(editNo);
            }
            editNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        setId(position);
                    }
                }
            });
            editNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    myList.set(position, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            ImageButton imageButton = (ImageButton) holder.getView(R.id.imagebutton1);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myList.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}