package com.example.mumu.warehousecheckcar.fragment.cut;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CutClothWeightFragment extends Fragment implements RXCallback {

    private final String TAG = "CutClothWeightFragment";

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    private static CutClothWeightFragment fragment;
    @Bind(R.id.editNO)
    EditText editNO;
    @Bind(R.id.relativelayout)
    RelativeLayout relativelayout;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static CutClothWeightFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothWeightFragment();
        return fragment;
    }

    private Sound sound;
    private TDScannerHelper scannerHander;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_scan_layout, container, false);
        getActivity().setTitle(getResources().getString(R.string.cut_out));
        sound = new Sound(getActivity());
        ButterKnife.bind(this, view);
        init2D();
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
                break;
            case R.id.button2:
                String str = editNO.getText().toString();
                str = str.replaceAll(" ", "");
                if (str != null && !(str.equals(""))) {
                    final JSONObject jsonobject = new JSONObject();
                    jsonobject.put("outp_id", str);
                    final String json = jsonobject.toJSONString();
                    try {
                        OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cut/getApplyByOutpId.sh", new OkHttpClientManager.ResultCallback<JSONObject>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onError(Request request, Exception e) {
                                if (App.LOGCAT_SWITCH) {
                                    Log.i(TAG, "getApplyByOutpId;" + e.getMessage());
                                }
                            }

                            @Override
                            public void onResponse(JSONObject response) {
                                //转换成实体类
                                if (response != null) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("barcode", response);
                                    Fragment fragment = CutClothEditWeightFragment.newInstance();
                                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                                    transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                                    transaction.show(fragment);
                                    transaction.commit();
                                    EventBus.getDefault().postSticky(jsonObject);
                                } else
                                    blinkDialog2(false);
                            }
                        }, json);
                    } catch (Exception e) {

                    }

                } else
                    Toast.makeText(getActivity(), "请扫描条形码", Toast.LENGTH_SHORT).show();


                break;
        }
    }

    private AlertDialog dialog;

    private void blinkDialog2(boolean flag) {
        if (dialog == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View blinkView = inflater.inflate(R.layout.dialog_in_check, null);
            Button no = (Button) blinkView.findViewById(R.id.dialog_no);
            Button yes = (Button) blinkView.findViewById(R.id.dialog_yes);
            TextView text = (TextView) blinkView.findViewById(R.id.dialog_text);
            if (flag)
                text.setText("单号有效");
            else
                text.setText("单号无效");

            dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.show();
            dialog.getWindow().setContentView(blinkView);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
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
        } else {
            TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
            if (flag)
                text.setText("单号有效");
            else
                text.setText("单号无效");
            if (!dialog.isShowing())
                dialog.show();
        }
    }

    long currenttime = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 0x00:
                    //callback
                    if (App.MUSIC_SWITCH) {
                        if (System.currentTimeMillis() - currenttime > 150) {
                            sound.callAlarm();
                            currenttime = System.currentTimeMillis();
                        }
                    }
                    String barcode = (String) msg.obj;
                    barcode = barcode.replaceAll(" ", "");
                    editNO.setText(barcode);
                    break;
            }
        }
    };

    @Override
    public void callback(byte[] bytes) {
        Message msg = handler.obtainMessage();
        msg.arg1 = 0x00;
        msg.obj = new String(bytes);
        handler.sendMessage(msg);
    }
}
