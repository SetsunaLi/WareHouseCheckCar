package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarCarrierFragment extends Fragment {

    private static CarCarrierFragment fragment;
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button3)
    Button button3;

    public static CarCarrierFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarCarrierFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_home_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button1, R.id.button2,R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //叉车上架
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
