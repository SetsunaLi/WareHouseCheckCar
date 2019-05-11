package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.view.FixedEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        EventBus.getDefault().register(this);
        ButterKnife.bind(this, view);
        getActivity().setTitle("发运");
        initView();

        return view;
    }
    private void initView(){
        fixeedittext1.setFixedText("车牌号：\t");
        fixeedittext2.setFixedText("姓名：\t");
        fixeedittext3.setFixedText("电话：\t");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMsg(EventBusMsg message) {

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    @OnClick(R.id.button1)
    public void onViewClicked() {
        String carNo=fixeedittext1.getText().toString()+"";
        String name=fixeedittext2.getText().toString()+"";
        String phoneNo=fixeedittext3.getText().toString()+"";
        carNo=carNo.replaceAll(" ","");
        name=name.replaceAll(" ","");
        phoneNo=phoneNo.replaceAll(" ","");
        if (carNo!=null&&!carNo.equals("")){
            EventBus.getDefault().postSticky(new EventBusMsg(0x00,new CarMsg(carNo,name,phoneNo)));
            Fragment fragment = ForwardingNoFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        }else
            Toast.makeText(getActivity(),"车牌号不能为空",Toast.LENGTH_SHORT).show();

    }
    static class CarMsg{
        /**车牌号*/
        private String carNo;
        /**s司机姓名*/
        private String carName;
        /**电话号码*/
        private String phoneNo;

        public CarMsg(String carNo, String carName, String phoneNo) {
            this.carNo = carNo;
            this.carName = carName;
            this.phoneNo = phoneNo;
        }

        public String getCarNo() {
            return carNo;
        }

        public void setCarNo(String carNo) {
            this.carNo = carNo;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }
    }
}
