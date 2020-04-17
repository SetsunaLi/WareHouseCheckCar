package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.entity.HomeButton;
import com.example.mumu.warehousecheckcar.entity.Power;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.Imgutil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/11/19.
 */

public class HomeFragment extends BaseFragment {

    private static HomeFragment fragment;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button3)
    Button button3;
    @Bind(R.id.button4)
    Button button4;
    @Bind(R.id.button5)
    Button button5;
    @Bind(R.id.button6)
    Button button6;
    @Bind(R.id.button7)
    Button button7;
    @Bind(R.id.button8)
    Button button8;
    @Bind(R.id.button9)
    Button button9;
    @Bind(R.id.button10)
    Button button10;
    @Bind(R.id.button11)
    Button button11;
    @Bind(R.id.button12)
    Button button12;
    @Bind(R.id.button13)
    Button button13;
    @Bind(R.id.button14)
    Button button14;
    @Bind(R.id.button15)
    Button button15;
    @Bind(R.id.button16)
    Button button16;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button17)
    Button button17;


    public static HomeFragment newInstance() {
        if (fragment == null) ;
        fragment = new HomeFragment();
        return fragment;
    }

    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("主页");
        return view;
    }

    @Override
    protected void initData() {
        user = User.newInstance();
    }

    @Override
    protected void initView(View view) {
        for (HomeButton homeButton : HomeButton.values()) {
            Button button = (Button) view.findViewById(homeButton.getId());
            button.setCompoundDrawables(
                    null,
                    Imgutil.findImgAsSquare(getActivity(),
                            homeButton.getIndex(), 64),
                    null,
                    null);
        }
        if (user != null&&user.getApp_auth()!=null) {
            boolean outFlag = false;
            for (Power power : user.getApp_auth()) {
                Button button;
                switch (power.getAuth_type()) {
                    case 0:
                        button = button1;
                        break;
                    case 1:
                        button = button2;
                        break;
                    case 2:
                        button = button3;
                        break;
                    case 3:
                        button = button4;
                        break;
                    case 4:
                        button = button5;
                        break;
                    case 5:
                        button = button6;
                        break;
                    case 6:
                        button = button7;
                        if (power.getFlag() != 0)
                            outFlag = true;
                        break;
                    case 7:
                        if (outFlag)
                            button = null;
                        else
                            button = button7;
                        break;
                    case 8:
                        button = button8;
                        break;
                    case 9:
                        button = button9;
                        break;
                    case 10:
                        button = button10;
                        break;
                    case 11:
                        button = button11;
                        break;
                    case 12:
                        button = button12;
                        break;
                    case 13:
                        button = button13;
                        break;
                    case 14:
                        button = button14;
                        break;
                    case 15:
                        button = button15;
                        break;
                    case 16:
                        button = button17;
                        break;
                    default:
                        button = null;
                }
                if (button != null) {
                    if (power.getFlag() == 0)
                        button.setVisibility(View.GONE);
                    else
                        button.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void addListener() {

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
