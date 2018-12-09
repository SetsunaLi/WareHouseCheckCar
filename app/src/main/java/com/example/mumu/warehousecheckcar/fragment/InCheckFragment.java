package com.example.mumu.warehousecheckcar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.ApplicaFormEntity;
import com.example.mumu.warehousecheckcar.entity.ItemMenu;
import com.example.mumu.warehousecheckcar.entity.MyTestEnt;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2018/12/9.
 */

public class InCheckFragment extends Fragment implements UHFCallbackLiatener{
    private final String TAG = "InCheckFragment";
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    private InCheckFragment() {
    }

    public static InCheckFragment newInstance() {
        return new InCheckFragment();
    }

    private RecycleAdapter mAdapter;
    private List<ApplicaFormEntity> myList;
    private List<String> epcList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_check_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("入库校验");
        myList=new ArrayList<>();
        epcList=new ArrayList<>();
        clearData();
        mAdapter=new RecycleAdapter(recyle,myList,R.layout.in_check_item_layout);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        LinearLayoutManager ms=new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);

        initRFID();
        return view;
    }
    private void initRFID(){
        RFID_2DHander.getInstance().on_RFID();
        UHFResult.getInstance().setCallbackLiatener(this);
    }
    private void disRFID(){
        RFID_2DHander.getInstance().off_RFID();
    }
    private void clearData(){
        if (myList!=null){
            myList.clear();
            myList.add(new ApplicaFormEntity());
        }
        if (epcList!=null){
            epcList.clear();
        }
        text1.setText("0");
    }
    private void setAdaperHeader(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.in_check_item_layout,null);
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

    //    主页返回执行

    public void onBackPressed() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disRFID();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    protected static final String TAG_RETURN_FRAGMENT = "TitleFragment";

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                blinkDialog();
                break;
        }
    }
    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText(R.string.dialog_in_check);
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(blinkView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
        if (!epcList.contains(tag.strEPC)) {
            epcList.add(tag.strEPC);
        }
        Log.i(TAG,tag.strEPC);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<ApplicaFormEntity> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public RecycleAdapter(RecyclerView v, Collection<ApplicaFormEntity> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        @Override
        public void convert(RecyclerHolder holder, ApplicaFormEntity item, int position) {
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
