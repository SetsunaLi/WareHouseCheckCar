package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;

/**
 * Created by mumu on 2018/12/12.
 */

public class InCheckDetialFragment extends Fragment {
    private static InCheckDetialFragment fragment;
    private InCheckDetialFragment(){    }
    public static InCheckDetialFragment newInstance(){
        if (fragment==null);
        fragment=new InCheckDetialFragment();
        return fragment;
    }
}
