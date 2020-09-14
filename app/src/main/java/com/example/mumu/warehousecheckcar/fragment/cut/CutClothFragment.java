package com.example.mumu.warehousecheckcar.fragment.cut;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.utils.Imgutil;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CutClothFragment extends BaseFragment {

    private static CutClothFragment fragment;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button2)
    Button button2;

    public static CutClothFragment newInstance() {
        if (fragment == null) ;
        fragment = new CutClothFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.cut_os));
        View view = inflater.inflate(R.layout.cut_cloth_home_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {
        button1.setCompoundDrawables(
                Imgutil.findImgAsSquare(getActivity(),
                        R.mipmap.cut_scan_l, 64),
                null,
                null,
                null);

        button3.setCompoundDrawables(
                Imgutil.findImgAsSquare(getActivity(),
                        R.mipmap.cut_out_l, 64),
                null,
                null,
                null);
        button2.setCompoundDrawables(
                Imgutil.findImgAsSquare(getActivity(),
                        R.mipmap.expressage_l, 64),
                null,
                null,
                null);
    }

    @Override
    protected void addListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick({R.id.button1, R.id.button3, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                Fragment fragment = CutPlanFragemnt.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
            break;
            case R.id.button3: {
                Fragment fragment = BlueToothConnectFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
            break;
            case R.id.button2: {
                Fragment fragment = ExpressageNoBindingFragment.newInstance();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
            }
            break;
        }
    }
}
