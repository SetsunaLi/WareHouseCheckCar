package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mumu.warehousecheckcar.R;

import butterknife.ButterKnife;

public class ForwardingNoFragment extends Fragment {
    final String TAG="ForwardingNoFragment";
    private static ForwardingNoFragment fragment;
    private ForwardingNoFragment(){    }
    public static ForwardingNoFragment newInstance(){
        if (fragment==null);
        fragment=new ForwardingNoFragment();
        return fragment;
    }
    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
