package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CutClothOutNoFragment extends Fragment implements RXCallback {
    public final String TAG = "CutClothOutput";
    public static CutClothOutNoFragment fragment;
    @Bind(R.id.imgbutton)
    ImageButton imgbutton;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button2)
    Button button2;

    public static CutClothOutNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothOutNoFragment();
        return fragment;
    }
    private TDScannerHelper scannerHander;
    private Sound sound;
    private ArrayList<String> myList;
    private RecycleAdapter mAdapter;
    private InputMethodManager mInputMethodManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_ins_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("剪布出库");
        sound = new Sound(getActivity());
        init2D();
        myList = new ArrayList<>();
        myList.add("");
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_applyno_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
//        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        initUtil();

        return view;
    }
    private void initUtil() {
        //初始化输入法
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    //    去空
    public String delSpacing(String str) {
        str = str.trim();
        return str;
    }
    private void init2D() {
        try {
            boolean flag2 = RFID_2DHander.getInstance().on_2D();
//            boolean flag1=RFID_2DHander.getInstance().connect2D();
            scannerHander = RFID_2DHander.getInstance().getTDScanner();
            scannerHander.regist2DCodeData(this);
            if (!flag2)
                Toast.makeText(getActivity(), "一维读头连接失败", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.w(TAG, "2D模块异常");
            Toast.makeText(getActivity(), getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
    }

    private void disConnect2D() {
        try {
            RFID_2DHander.getInstance().off_2D();
//            RFID_2DHander.getInstance().disConnect2D();

        } catch (Exception e) {

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setId(0);
        mAdapter.select(0);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disConnect2D();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sound.callAlarm();
            String No = (String) msg.obj;
            int id = mAdapter.getId();
            myList.set(id, No);
            mAdapter.notifyDataSetChanged();
        }
    };

    @OnClick({R.id.imgbutton, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgbutton:
                myList.add("");
                mAdapter.select(myList.size() - 1);
                mAdapter.setId(myList.size() - 1);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                EventBus.getDefault().postSticky(new EventBusMsg(0x04, myList));
                Fragment fragment = CutClothOutNoFragment.newInstance();
       /*         Bundle bundle = new Bundle();
                bundle.putSerializable("NO", myList);
                fragment.setArguments(bundle);*/
                getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
                break;
        }
    }

    @Override
    public void callback(byte[] bytes) {
        Message msg = handler.obtainMessage();
        msg.obj = new String(bytes);
        handler.sendMessage(msg);
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
        public void convert(RecyclerHolder holder, final String item, final int position) {
            final FixedEditText editNo = (FixedEditText) holder.getView(R.id.fixeedittext1);
            editNo.setTag(position);
//            editNo.setFixedText("申请单号：");
            editNo.setText(myList.get((Integer) editNo.getTag()));
            if (position == this.position) {
                editNo.setFocusable(true);//设置输入框可聚集
                editNo.setFocusableInTouchMode(true);//设置触摸聚焦
                editNo.requestFocus();//请求焦点
                editNo.findFocus();//获取焦点
                editNo.setCursorVisible(true);
                editNo.setSelection(editNo.getText().length());
                mInputMethodManager.showSoftInput(editNo, InputMethodManager.SHOW_FORCED);// 显示输入法
            }
            editNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        FixedEditText fe = (FixedEditText) view;
                        setId((int) fe.getTag());
                    }
                }
            });
            editNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    Log.i("onTextChanged", "onTextChanged");
                    myList.set((int) editNo.getTag(), charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
}