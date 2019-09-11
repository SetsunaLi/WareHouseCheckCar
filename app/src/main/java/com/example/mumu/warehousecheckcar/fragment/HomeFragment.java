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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/11/19.
 */

public class HomeFragment extends Fragment {

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
//    @Bind(R.id.button15)
//    Button button15;
    @Bind(R.id.button16)
    Button button16;
    @Bind(R.id.button2)
    Button button2;


    public static HomeFragment newInstance() {
        if (fragment == null) ;
        fragment = new HomeFragment();
        return fragment;
    }

    private User user = User.newInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("主页");
        initView(view);

        return view;
    }

    private void initView(View view) {
        for (HomeButton homeButton : HomeButton.values()) {
            //Parameter.IMG_SMALL是图片的大小值，setCompoundDrawables参数指定图片的位置：左、上、右、下
            Button button = (Button) view.findViewById(homeButton.getId());
            button.setCompoundDrawables(
                    null,
                    findImgAsSquare(getActivity(),
                            homeButton.getIndex(), 64),
                    null,
                    null);
            button.setVisibility(View.GONE);
        }


    }

    /**
     * 获取,寻找并裁剪图片
     *
     * @param context
     * @param id
     * @return
     */
    public static Drawable findImgAsSquare(Context context, int id, int sidedp) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        int px = dip2px(context, sidedp);
        drawable.setBounds(0, 0, px, px);
        return drawable;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            switch (user.getAuth()) {
//                目前认为GONE是最好的，INVISIBLE会占有位置
                case 5:
                    button1.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    button3.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    button2.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    button11.setVisibility(View.VISIBLE);
                    button12.setVisibility(View.VISIBLE);
                    button14.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);

                    break;
                case 9:
                    button9.setVisibility(View.VISIBLE);
                    button10.setVisibility(View.VISIBLE);
                    button13.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    button7.setVisibility(View.VISIBLE);
                    button8.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                case 11:
                    button7.setVisibility(View.VISIBLE);
                    button8.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.VISIBLE);
                    button16.setVisibility(View.VISIBLE);
                    break;
                default:
                    for (HomeButton homeButton : HomeButton.values()) {
                        Button button = (Button) getView().findViewById(homeButton.getId());
                        button.setVisibility(View.VISIBLE);
                    }
                    break;
            }

        }
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
