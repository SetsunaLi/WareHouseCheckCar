package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.Carrier;
import com.example.mumu.warehousecheckcar.entity.Cut;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.utils.ArithUtil;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CuttingClothFragment extends Fragment implements BRecyclerAdapter.OnItemClickListener, UHFCallbackLiatener {
    private final String TAG = "CuttingClothFragment";

    private static CuttingClothFragment fragment;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;


    public static CuttingClothFragment newInstance() {
        if (fragment == null) ;
        fragment = new CuttingClothFragment();
        return fragment;
    }

    private RecycleAdapter mAdapter;
    private List<Cut> myList;
    private LinearLayoutManager ms;
    private List<String> dataKEY;
    private Sound sound;
    private List<String> epcList;
//    private List<Cut> dataList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_layout, container, false);
        ButterKnife.bind(this, view);
        initUtil();
        initArray();
        clear();
        sound = new Sound(getActivity());
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        setAdaperHeader();
        ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(ms);
        recyle.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

//        EventBus.getDefault().register(this);
        initRFID();
        return view;
    }

    /*  private Cut cut;
      @Subscribe(threadMode = ThreadMode.MAIN , sticky =  true )
      public void onEventMsg(EventBusMsg msg){
          switch (msg.getStatus()){
              case 0x03:
                  cut= (Cut) msg.getPositionObj(0);
                  break;
          }
      }*/
    private void initArray() {
        myList = new ArrayList<>();
        dataKEY = new ArrayList<>();
        epcList = new ArrayList<>();
//        dataList = new ArrayList<>();


        if (App.CARRIER != null) {
            if (App.CARRIER.getLocationNo() != null)
                text2.setText(App.CARRIER.getLocationNo() + "");
        }
    }

    private void clear() {
        if (myList != null) {
            myList.clear();
            myList.add(new Cut());
        }
        if (dataKEY != null)
            dataKEY.clear();
        if (epcList != null)
            epcList.clear();
//        if (dataList != null)
//            dataList.clear();
    }

    private void initRFID() {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        mAdapter.select(position);
        mAdapter.notifyDataSetChanged();
    }

    private void setAdaperHeader() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cut_cloth_item, null);
        mAdapter.setHeader(view);
    }

    private InputMethodManager mInputMethodManager;

    private void initUtil() {
        //初始化输入法
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
//        EventBus.getDefault().unregister(this);
        disRFID();
        clear();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                clear();
                text1.setText(epcList.size() + "");
                mAdapter.notifyDataSetChanged();
            /*    Message msg = handler.obtainMessage();
                msg.arg1 = 0x00;
                msg.obj = "3035A53700001D0009159702";
                handler.sendMessage(msg);*/
                break;
            case R.id.button2:
                if (dataKEY != null && dataKEY.size() > 0) {
                    blinkDialog();
                } else {
                    Toast.makeText(getActivity(), "请选择要上传的数据", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    long currenttime = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 0x00:
                    if (App.MUSIC_SWITCH) {
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                    }
                    String EPC = ((String) msg.obj).replaceAll(" ", "");
                    if (EPC.startsWith("3035A537") && !epcList.contains(EPC)) {
                        JSONObject epc = new JSONObject();
                        epc.put("epc", EPC);
                        final String json = epc.toJSONString();
                        try {
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/rfid/getEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
//                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/getCutEpc.sh", new OkHttpClientManager.ResultCallback<JSONArray>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "getEpc;" + e.getMessage());
                                        Toast.makeText(getActivity(), "获取库位信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onResponse(JSONArray jsonArray ) {
                                    if (jsonArray!=null&&jsonArray.size()>0){
                                        Cut cut=jsonArray.getObject(0,Cut.class);
                                            if (cut != null && !epcList.contains(cut.getEpc())) {
                                                cut.setWeight(ArithUtil.sub(cut.getWeight_in(), ArithUtil.add(cut.getBlank_add(), cut.getWeight_papertube())));
                                                epcList.add(cut.getEpc());
                                                myList.add(cut);
//                                            dataList.add(response);
                                            }
                                        text1.setText(epcList.size() + "");
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }, json);
                        } catch (IOException e) {
                            Log.i(TAG, "");
                        }catch (Exception e){

                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {

    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {
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

    private void blinkDialog() {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        text.setText("是否确认上架");
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
                //上传数据
                ArrayList<Cut> jsocList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", User.newInstance().getId());
                for (Cut obj : myList) {
                    if (obj.getEpc() != null && dataKEY.contains(obj.getEpc())) {
                        Carrier carrier=new Carrier(App.CARRIER.getTrayNo(),App.CARRIER.getLocationNo());
                        obj.setCarrier(carrier);
                        jsocList.add(obj);
//                        obj.setBlank_add(cut.getBlank_add());
//                        obj.setWeight_papertube(cut.getWeight_papertube());
                    }
                }
                jsonObject.put("data", jsocList);
                final String json = JSON.toJSONString(jsonObject);
                try {
                    OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/pushCutCloth.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onError(Request request, Exception e) {
                            if (App.LOGCAT_SWITCH) {
                                Log.i(TAG, "postInventory;" + e.getMessage());
                                Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                BaseReturn baseReturn = response.toJavaObject(BaseReturn.class);
                                if (baseReturn != null && baseReturn.getStatus() == 1) {
                                    blinkDialog2(true);
                                } else {
                                    blinkDialog2(false);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, json);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                    }
                dialog.dismiss();
            }
        });
    }

    private void blinkDialog2(boolean flag) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
        Button no = (Button) blinkView.findViewById(R.id.dialog_no);
        Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
        TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
        if (flag)
            text.setText("上传成功");
        else
            text.setText("上传失败");

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
                clear();
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<Cut> {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void setHeader(View header) {
            super.setHeader(header);
        }

        public RecycleAdapter(RecyclerView v, Collection<Cut> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        private int index = -255;

        public void select(int index) {
            if (this.index == index)
                this.index = -255;
            else
                this.index = index;
        }

        @Override
        public void convert(RecyclerHolder holder, final Cut item, final int position) {

            if (item != null) {
                final CheckBox cb = holder.getView(R.id.checkbox1);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (position == 0) {
                            if (isChecked) {
                                for (Cut i : myList) {
                                    dataKEY.add(i.getEpc());
                                }
                            } else {
                                dataKEY.clear();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isChecked) {
                                if (!dataKEY.contains(item.getEpc()))
                                    dataKEY.add(item.getEpc());
                            } else {
                                if (dataKEY.contains(item.getEpc()))
                                    dataKEY.remove(item.getEpc());
                            }
                        }
                    }
                });

                if (position != 0) {
                    if (item.getVatNo()==null||item.getColor()==null||item.getProduct_no()==null){
                        cb.setChecked(false);
                        if (cb.isEnabled())
                            cb.setEnabled(false);
                    }else {
                        if (item.getVatNo().equals("") && item.getColor().equals("") && item.getProduct_no().equals("")) {
                            cb.setChecked(false);
                            if (cb.isEnabled())
                                cb.setEnabled(false);
                        } else {
                            if (!cb.isEnabled())
                                cb.setEnabled(true);
                            if (dataKEY.contains(item.getEpc()))
                                cb.setChecked(true);
                            else
                                cb.setChecked(false);
                        }
                    }
                    LinearLayout ll = holder.getView(R.id.layout1);
                    if (item.getBlank_add() == 0 || item.getWeight_papertube() == 0)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDataNoText));
                    else if (index == position)
                        ll.setBackgroundColor(getResources().getColor(R.color.colorDialogTitleBG));
                    else
                        ll.setBackgroundColor(getResources().getColor(R.color.colorZERO));
                    holder.setText(R.id.item1, item.getProduct_no() + "");
                    holder.setText(R.id.item2, item.getColor() + "");
                    holder.setText(R.id.item3, item.getVatNo() + "");
                    holder.setText(R.id.item4, item.getFabRool() + "");
                    holder.setText(R.id.item5, item.getWeight_in() + "KG");
                    final EditText editText1 = holder.getView(R.id.edittext1);
//                    final EditText editText2 = holder.getView(R.id.edittext2);
                    editText1.setText(item.getWeight_papertube() + "");
//                    editText2.setText(item.getBlank_add() + "");
                    editText1.setEnabled(true);
//                    editText2.setEnabled(true);
                    editText1.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                String weight = charSequence.toString();
                                weight = weight.replaceAll(" ", "");
                                if (weight != null && !weight.equals("")) {
                                    double a = Double.parseDouble(weight);
                                    for (Cut cut : myList) {
                                        if (item.getEpc().equals(cut.getEpc())) {
                                            cut.setWeight(a);
//                                            cut.setWeight(ArithUtil.sub(cut.getWeight_in(), ArithUtil.add(cut.getBlank_add(), cut.getWeight_papertube())));
                                            break;

                                        }
                                    }
                                }
                            } catch (Exception e) {
                                editText1.setText("0");
                                for (Cut cut : myList) {
                                    if (item.getEpc().equals(cut.getEpc())) {
                                        cut.setWeight(0);
                                        break;

                                    }
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                 /*   editText2.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                String weight = charSequence.toString();
                                weight = weight.replaceAll(" ", "");
                                if (weight != null && !weight.equals("")) {
                                    double a = Double.parseDouble(weight);
                                    for (Cut cut : myList) {
                                        if (item.getEpc().equals(cut.getEpc())) {
                                            cut.setBlank_add(a);
                                            cut.setWeight(ArithUtil.sub(cut.getWeight_in(), ArithUtil.add(cut.getBlank_add(), cut.getWeight_papertube())));
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                editText2.setText("0");
                                for (Cut cut : myList) {
                                    if (item.getEpc().equals(cut.getEpc())) {
                                        cut.setBlank_add(0);
                                        break;

                                    }
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });*/
                }else {
                    EditText editText1 = (EditText) holder.getView(R.id.edittext1);
                    editText1.setEnabled(false);
                    EditText editText2 = (EditText) holder.getView(R.id.edittext2);
                    editText2.setEnabled(false);
                }
            }
        }
    }
}
