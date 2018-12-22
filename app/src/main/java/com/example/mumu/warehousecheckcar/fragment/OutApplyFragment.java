package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.entity.InCheckDetail;
import com.example.mumu.warehousecheckcar.entity.ItemMenu;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.OUTDETAIL_LIST;
import static com.example.mumu.warehousecheckcar.application.App.OUT_APPLY;
import static com.example.mumu.warehousecheckcar.application.App.OUT_APPLY_DETAIL;

/**
 * Created by mumu on 2018/12/8.
 */

public class OutApplyFragment extends Fragment implements UHFCallbackLiatener,BasePullUpRecyclerAdapter.OnItemClickListener{
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    private final String TAG="OutApplyFragment";
    private static OutApplyFragment fragment;
    private OutApplyFragment(){    }
    public static OutApplyFragment newInstance(){
        if (fragment==null);
        fragment=new OutApplyFragment();
        return fragment;
    }

    private CharSequence mTitle;
    private RecycleAdapter mAdapter;
    private List<InCheckDetail> myList;
    private List<InCheckDetail> applyList;
    private List<InCheckDetail> dataList;
    private List<String> epcList;
    /**匹配机制应该是item分组字段*/
    private Map<String,Integer> keyValue;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_apply_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("出库列表");
        myList=new ArrayList<>();
        applyList=new ArrayList<>();
        dataList=new ArrayList<>();
        epcList=new ArrayList<>();
        keyValue =new HashMap<>();
        clearData();
        initData();
        mAdapter=new RecycleAdapter(recyle,myList,R.layout.apply_item_layout_1);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);

        initRFID();
        return view;
    }
    private void setAdaperHeader(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.apply_item_layout_1,null);
        mAdapter.setHeader(view);
    }
    private void clearData(){
        if (myList!=null) {
            myList.clear();
            myList.add(new InCheckDetail());
        }
        if (applyList!=null)
            applyList.clear();
        if (dataList!=null)
            dataList.clear();
        if (epcList!=null)
            epcList.clear();
        if (keyValue !=null)
            keyValue.clear();
    }
    private void initData(){
        if (OUT_APPLY!=null&&OUT_APPLY.size()>0){
            for (InCheckDetail obj:OUT_APPLY){
                if (keyValue.containsKey(obj.getVatNo())){
                    myList.add(obj);
                    keyValue.put(obj.getVatNo(),myList.size());
                }else {
                    myList.get(keyValue.get(obj.getVatNo())).addCount();
//                    申请条数+1
                }
            }
        }
    }
    private void initRFID(){
        try {
            RFID_2DHander.getInstance().on_RFID();
            UHFResult.getInstance().setCallbackLiatener(this);
        } catch (Exception e) {

        }
    }
    private void disRFID() {
        try {
            RFID_2DHander.getInstance().off_RFID();
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

    //    主页返回执行
    public void onBackPressed() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        clearData();
        OUT_APPLY.clear();
        disRFID();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clearData();
                initData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                break;
        }
    }
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position!=0) {
            mAdapter.select(position);
            mAdapter.notifyDataSetChanged();

            InCheckDetail obj=myList.get(position);
            String key=obj.getVatNo();
            OUT_APPLY_DETAIL.clear();
            for (InCheckDetail obj2:dataList){
                if (obj2.getVatNo().equals(key)){
                    OUT_APPLY_DETAIL.add(obj2);
                }
            }
            Fragment fragment = OutApplyDetailFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case 0x00:
                    String EPC=(String)msg.obj;
                    break;
            }
        }
    };
    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
//        扫描标签数据接口
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = tag.strEPC;
        handler.sendMessage(msg);
    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

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
