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
            button.setVisibility(View.GONE);
        }
        if (user != null) {
            switch (user.getAuth()) {
//                目前认为GONE是最好的，INVISIBLE会占有位置
                case 5:
                    button1.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    button3.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    button2.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    button11.setVisibility(View.VISIBLE);
                    button12.setVisibility(View.VISIBLE);
                    button14.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);

                    break;
                case 9:
                    button9.setVisibility(View.VISIBLE);
                    button10.setVisibility(View.VISIBLE);
                    button13.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    button1.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                    button7.setVisibility(View.VISIBLE);
                    button8.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button11.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 11:
                    button1.setVisibility(View.VISIBLE);
                    button3.setVisibility(View.VISIBLE);
                    button7.setVisibility(View.VISIBLE);
                    button8.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button15.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
             /*   case 12:
                    for (HomeButton homeButton : HomeButton.values()) {
                        Button button = (Button) getView().findViewById(homeButton.getId());
                        button.setVisibility(View.VISIBLE);
                    }
                    break;*/
                default:
                    for (HomeButton homeButton : HomeButton.values()) {
                        Button button = (Button) getView().findViewById(homeButton.getId());
                        button.setVisibility(View.VISIBLE);
                    }
                    break;
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
