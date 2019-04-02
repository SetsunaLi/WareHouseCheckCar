package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.view.FixedEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForwardingMsgFragment extends Fragment {
    final String TAG = "ForwardingMsgFragment";
    private static ForwardingMsgFragment fragment;
    @Bind(R.id.fixeedittext1)
    FixedEditText fixeedittext1;
    @Bind(R.id.fixeedittext2)
    FixedEditText fixeedittext2;
    @Bind(R.id.fixeedittext3)
    FixedEditText fixeedittext3;
    @Bind(R.id.button1)
    Button button1;

    private ForwardingMsgFragment() {
    }

    public static ForwardingMsgFragment newInstance() {
        if (fragment == null) ;
        fragment = new ForwardingMsgFragment();
        return fragment;
    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forwarding_msg_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }
    private void initView(){
        fixeedittext1.setFixedText("车牌号:");
        fixeedittext2.setFixedText("姓名:");
        fixeedittext3.setFixedText("电话:");
        fixeedittext3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    onViewClicked();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.button1)
    public void onViewClicked() {
        String carNo=fixeedittext1.getText().toString()+"";
        String name=fixeedittext2.getText().toString()+"";
        String phoneNo=fixeedittext3.getText().toString()+"";
        carNo=carNo.replaceAll(" ","");
        name=name.replaceAll(" ","");
        phoneNo=phoneNo.replaceAll(" ","");
        if (carNo!=null&&!carNo.equals("")){

        }else
            Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT).show();

    }
}
