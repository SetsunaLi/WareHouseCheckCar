package com.example.mumu.warehousecheckcar.fragment;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.Sound;
import com.example.mumu.warehousecheckcar.application.App;
import com.xdl2d.scanner.TDScannerHelper;
import com.xdl2d.scanner.callback.RXCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.APPLY_NO;

/**
 * Created by mumu on 2018/12/8.
 */

public class OutApplyNoFragment extends Fragment implements RXCallback {
    @Bind(R.id.editNO)
    EditText editNO;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;


    private final String TAG = "OutApplyNoFragment";

    private OutApplyNoFragment() {
    }
    private static OutApplyNoFragment fragment;
    public static OutApplyNoFragment newInstance(){
        if (fragment==null);
        fragment=new OutApplyNoFragment();
        return fragment;
    }
    private TDScannerHelper scannerHander;
    private Sound sound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_ins_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("出库");
        sound = new Sound(getActivity());

        try {
            RFID_2DHander.getInstance().on_2D();
            scannerHander = RFID_2DHander.getInstance().getTDScanner();
            scannerHander.regist2DCodeData(this);
        } catch (Exception e) {
            Log.w(TAG, "RFID读写器异常");
            Toast.makeText(getActivity(), getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onPause() {
        super.onPause();
        Log.i("onPause","onPause");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RFID_2DHander.getInstance().off_2D();
        App.APPLY_NO=null;
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    protected static final String TAG_RETURN_FRAGMENT = "TitleFragment";

    @OnClick({ R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.button2:
                String applaNO = editNO.getText().toString();
                if (applaNO != null) {
                    /*先访系统得到列表后再跳转
                    尝试不异步情况下能否访问
                    * */
                    RFID_2DHander.getInstance().off_2D();
                    APPLY_NO = applaNO;
                    Fragment fragment = OutApplyFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.hint_void), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sound.callAlarm();
            editNO.setText((String) msg.obj + "");
        }
    };

    @Override
    public void callback(byte[] bytes) {
        Message msg = handler.obtainMessage();
        msg.obj = new String(bytes);
        handler.sendMessage(msg);
    }
}
