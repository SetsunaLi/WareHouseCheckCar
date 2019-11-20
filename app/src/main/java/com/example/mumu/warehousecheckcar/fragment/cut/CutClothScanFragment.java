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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;

import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CutClothScanFragment extends BaseFragment implements RXCallback, OnCodeResult {

    private final String TAG = "CutClothScanFragment";
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private static CutClothScanFragment fragment;
    @Bind(R.id.editNO)
    EditText editNO;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    public static CutClothScanFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothScanFragment();
        return fragment;
    }

    private ScanResultHandler scanResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_barcode_layout, container, false);
        getActivity().setTitle(getResources().getString(R.string.cut_scanner));
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        init2D();
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
                if (editNO.getText().toString() != null && !(editNO.getText().toString().equals(""))) {
                    String code = editNO.getText().toString();
                    code = code.replaceAll(" ", "");
                    final JSONObject jsonobject = new JSONObject();
                    jsonobject.put("outp_id", code);
                    final String json = jsonobject.toJSONString();
                    try {
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
                                if (response != null) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("barcode", response);
                                    Fragment fragment = CutClothDetailFragment.newInstance();
                                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                                    transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                                    transaction.show(fragment);
                                    transaction.commit();
                                    EventBus.getDefault().postSticky(jsonObject);
                                } else
                                    showConfirmDialog("单号无效");
                            }
                        }, json);
                    } catch (Exception e) {

                    }
                } else
                    showToast("请扫描条形码");
                break;
        }
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
        editNO.setText(code);
    }
}
