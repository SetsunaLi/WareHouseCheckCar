package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.module.interaction.ModuleConnector;
import com.module.interaction.RXTXListener;
import com.nativec.tools.ModuleManager;
import com.xdl2d.scanner.TDScannerConnector;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mumu on 2019/1/24.
 */

public class TextFragment extends Fragment implements RXCallback {
    private final String TAG = "TextFragment";

    private static TextFragment fragment;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_layout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    TDScannerConnector connector = new TDScannerConnector();

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1: {
//                boolean b = connector.connectCom("dev/ttyS4", 115200);
                boolean b = connector.connectCom("dev/ttyS1", 9600);

                boolean a = ModuleManager.newInstance().setScanStatus(true);
//                ModuleConnector connector = new TDScannerConnector();
                TDScannerHelper mScanner = TDScannerHelper.getDefaultHelper();
                mScanner.regist2DCodeData(callback);
            }
                break;
            case R.id.button2: {
                boolean a =ModuleManager.newInstance().setUHFStatus(false);
                TDScannerHelper mScanner = TDScannerHelper.getDefaultHelper();
                mScanner.unRegisterObservers();
                connector.disConnect();
                boolean b=ModuleManager.newInstance().release();
            }
                break;
        }
    }
    RXCallback callback = new RXCallback() {
        public void callback(byte[] bytes) {
            String str=new String(bytes);
        }
    };

    @Override
    public void callback(byte[] bytes) {
        String str=new String(bytes);
    }
}
