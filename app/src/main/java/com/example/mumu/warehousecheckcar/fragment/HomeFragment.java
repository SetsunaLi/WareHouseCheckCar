package com.example.mumu.warehousecheckcar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.Constant;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.entity.Power;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.Imgutil;
import com.example.mumu.warehousecheckcar.utils.SpModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/11/19.
 */

public class HomeFragment extends BaseFragment {

    private static HomeFragment fragment;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.button6)
    Button button6;
    @BindView(R.id.button7)
    Button button7;
    @BindView(R.id.button8)
    Button button8;
    @BindView(R.id.button9)
    Button button9;
    @BindView(R.id.button10)
    Button button10;
    @BindView(R.id.button11)
    Button button11;
    @BindView(R.id.button12)
    Button button12;
    @BindView(R.id.button13)
    Button button13;
    @BindView(R.id.button14)
    Button button14;
    @BindView(R.id.button15)
    Button button15;
    @BindView(R.id.button0)
    Button button0;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button17)
    Button button17;
    @BindView(R.id.button18)
    Button button18;
    @BindView(R.id.button19)
    Button button19;
    @BindView(R.id.button20)
    Button button20;
    @BindView(R.id.button21)
    Button button21;

    public static HomeFragment newInstance() {
        if (fragment == null) ;
        fragment = new HomeFragment();
        return fragment;
    }

    private User user;
    private Button[] buttons;
    private int[] imgs;
    private SpModel app;
    private List<Power> powers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("主页");
        app = SpModel.getInstance(App.getContext(), Constant.APP_TABLE_NAME);
        powers = app.getListData(Constant.SP_PROWERS, Power.class);
        return view;
    }

    @Override
    protected void initData() {
        user = User.newInstance();
        buttons = new Button[]{button0, button1, button2, button3, button4, button5, button6, button7, button8,
                button9, button10, button11, button12, button13, button14, button15, button17, button18, button19, button20, button21};
        imgs = new int[]{
                R.mipmap.setting_l, R.mipmap.click1_l, R.mipmap.click2_l, R.mipmap.click3_l, R.mipmap.click4_l, R.mipmap.click5_l
                , R.mipmap.click6_l, R.mipmap.click7_l, R.mipmap.click8_l, R.mipmap.click9_l, R.mipmap.click10_l, R.mipmap.click11_l
                , R.mipmap.click12_l, R.mipmap.click13_l, R.mipmap.click14_l, R.mipmap.click15_l, R.mipmap.backin_l, R.mipmap.chubb_get_l
                , R.mipmap.chubb_get_l, R.mipmap.outsource_in_l, R.mipmap.outsource_in_l
        };
    }

    @Override
    protected void initView(View view) {
        if (view != null) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setCompoundDrawables(
                        null,
                        Imgutil.findImgAsSquare(getActivity(),
                                imgs[i], 64),
                        null,
                        null);
            }
                boolean outFlag = false;
                boolean inSourceFlag = false;
            for (Power power : powers) {
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
                        case 17:
                            button = button18;
                            break;
                        case 18:
                            button = button19;
                            break;
                        case 19:
                            button = button20;
                            if (power.getFlag() != 0)
                                inSourceFlag = true;
                            break;
                        case 20:
                            if (inSourceFlag)
                                button = null;
                            else
                                button = button20;
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

    }
}
