package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;

/**
 * Created by mumu on 2019/1/8.
 */

public class PutawayDetailFragment extends Fragment {
    private static PutawayDetailFragment fragment;
    private PutawayDetailFragment() {
    }

    public static PutawayDetailFragment newInstance() {
        if (fragment == null) ;
        fragment = new PutawayDetailFragment();
        return fragment;
    }
}
