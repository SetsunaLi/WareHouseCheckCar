package com.example.mumu.warehousecheckcar.fragment.repaif_in;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.SearchAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by 
 *on 2020/8/20
 */
public class RepaifInNoFragment extends BaseFragment implements RXCallback, OnCodeResult {

    private static RepaifInNoFragment fragment;
    @BindView(R.id.fixeedittext2)
    FixedEditText fixeedittext2;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.autoText1)
    AutoCompleteTextView autoText1;
    private ScanResultHandler scanResultHandler;
    private SearchAdapter adapter;

    public static RepaifInNoFragment newInstance() {
        if (fragment == null) ;
        fragment = new RepaifInNoFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_click19));
        View view = inflater.inflate(R.layout.repaif_in_no_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {
        adapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText1.setAdapter(adapter);
    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        autoText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 1) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/back_repair/getDyeingFactory/" + str, new OkHttpClientManager.ResultCallback<JSONArray>() {
                        @Override
                        public void onError(Request request, Exception e) {
                        }

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                List<String> arry = response.toJavaList(String.class);
                                if (arry != null && arry.size() > 0) {
                                    adapter.updataList(arry);
                                }
                            } catch (Exception e) {

                            }
                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
    public void onResume() {
        super.onResume();
        init2D();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disConnect2D();
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        disConnect2D();
        String factoory = autoText1.getText().toString();
        String no = fixeedittext2.getText().toString();
        scanResultHandler.removeMessages(ScanResultHandler.RFID);
        Bundle bundle = new Bundle();
        bundle.putString("fact_name", factoory);
        bundle.putString("sh_no", no);
        Fragment fragment = RepaifInFragment.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, App.TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public void codeResult(String code) {
        code = code.replaceAll(" ", "");
        fixeedittext2.setText(code);
    }

    @Override
    public void callback(byte[] bytes) {
        Message msg = scanResultHandler.obtainMessage();
        msg.what = ScanResultHandler.CODE;
        msg.obj = new String(bytes);
        scanResultHandler.sendMessage(msg);
    }
}
