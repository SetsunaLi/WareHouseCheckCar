package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;

public class CarPutawayFragment extends Fragment {
    private final String TAG = "CarPutawayFragment";

    private CarPutawayFragment() {
    }

    private static CarPutawayFragment fragment;

    public static CarPutawayFragment newInstance() {
        if (fragment == null) ;
        fragment = new CarPutawayFragment();
        return fragment;
    }
}
