package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;

import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BarCode;
import com.example.mumu.warehousecheckcar.entity.Cloth;
import com.example.mumu.warehousecheckcar.entity.Cut;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;
import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CutClothBarCodeFragment extends Fragment implements RXCallback{

    private final String TAG = "CutClothBarCodeFragment";
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private static CutClothBarCodeFragment fragment;
    @Bind(R.id.editNO)
    EditText editNO;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static CutClothBarCodeFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothBarCodeFragment();
        return fragment;
    }

    //音频
    private Sound sound;
    private TDScannerHelper scannerHander;
    private JSONObject json;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_barcode_layout, container, false);
        getActivity().setTitle("剪板扫描");
        sound = new Sound(getActivity());
        json = new JSONObject();
        ButterKnife.bind(this, view);
        return view;
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

    //开启条形码
    private void init2D() {
        try {
            boolean flag2 = RFID_2DHander.getInstance().on_2D();
            scannerHander = RFID_2DHander.getInstance().getTDScanner();
            scannerHander.regist2DCodeData(this);
            if (!flag2)
                Toast.makeText(getActivity(), "一维读头连接失败", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.w(TAG, "2D模块异常");
            Toast.makeText(getActivity(), getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
    }

    //关闭条形码
    private void disConnect2D() {
        try {
            RFID_2DHander.getInstance().off_2D();
//            RFID_2DHander.getInstance().disConnect2D();

        } catch (Exception e) {

        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
       disConnect2D();
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //扫描条形码按钮
                init2D();
                break;
            case R.id.button2:
                //确定按钮
                Message msg = handler.obtainMessage();
                msg.arg1 = 0x01;
                msg.obj = "P0001253383";
                handler.sendMessage(msg);

                /* if(editNO.getText().toString() != null && !(editNO.getText().toString().equals(""))){
                       Message msg = handler.obtainMessage();
                       msg.arg1 = 0x01;
                       msg.obj = "P0001253383";
                       handler.sendMessage(msg);
                   } else
                       Toast.makeText(getActivity(),"请扫描条形码",Toast.LENGTH_SHORT).show();*/



                break;
        }
    }

    long currenttime = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.arg1){
                    case 0x00:
                        //callback
                        if (App.MUSIC_SWITCH) {
                            if (System.currentTimeMillis() - currenttime > 150) {
                                sound.callAlarm();
                                currenttime = System.currentTimeMillis();
                            }
                        }
                        String barcode= (String) msg.obj;
                        barcode = barcode.replaceAll(""," ");
                        editNO.setText(barcode);
                        disConnect2D();
                        break;
                    case 0x01:
                        String code = (String) msg.obj;
                        final JSONObject jsonobject = new JSONObject();
                        jsonobject.put("outp_id",code);
                        final String json = jsonobject.toJSONString();
                        try{
                            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/getApplyByOutpId.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onError(Request request, Exception e) {
                                    if (App.LOGCAT_SWITCH) {
                                        Log.i(TAG, "postInventory;" + e.getMessage());
                                    }
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    //转换成实体类
                                    JSONObject jsonObject = new JSONObject();

                                    jsonObject.put("barcode",response);

                                    Fragment fragment = CutClothDetailFragment.newInstance();

                                    getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                    getActivity().getFragmentManager().beginTransaction().add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();

                                    EventBus.getDefault().postSticky(jsonObject);
                                }
                            }, json);
                        }catch(Exception e ){

                        }
                        break;
                }
            }catch (Exception e){

            }
        }
    };


    @Override
    public void callback(byte[] bytes) {
        Log.i("bytes",bytes + "");
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = new String(bytes);
        handler.sendMessage(msg);
    }
}
