package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarFragment extends Fragment {

    private static CarFragment fragment;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button3)
    Button button3;

    public static CarFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.btn_click4));
        View view = inflater.inflate(R.layout.cut_cloth_home_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        button1.setText(getResources().getString(R.string.btn_car_up));
        button1.setCompoundDrawables(
                findImgAsSquare(getActivity(),
                        R.mipmap.car_up_l, 64),
                null,
                null,
                null);
        button2.setText(getResources().getString(R.string.btn_car_down));
        button2.setCompoundDrawables(
                findImgAsSquare(getActivity(),
                        R.mipmap.car_down_l, 64),
                null,
                null,
                null);
        button3.setText(getResources().getString(R.string.btn_car_tuo));
        button3.setCompoundDrawables(
                findImgAsSquare(getActivity(),
                        R.mipmap.kong_l, 64),
                null,
                null,
                null);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick({R.id.button1, R.id.button2, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //转仓
            {
                Fragment fragment = CarPutawayCarrierFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
            break;
            case R.id.button2:
                //叉车下架
            {
                Fragment fragment = CarSoldOutCarrierFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
            break;
            case R.id.button3:
            {
                //空托整理
                Fragment fragment = EmptyShelfFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
            break;
        }
    }
}
