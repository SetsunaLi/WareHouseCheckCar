package com.example.mumu.warehousecheckcar.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/3/31.
 */

public class AboutFragment extends BaseFragment {

    @Bind(R.id.appTitle)
    TextView appTitle;
    @Bind(R.id.companyTitle)
    TextView companyTitle;
    @Bind(R.id.appVersionTitle)
    TextView appVersionTitle;
    @Bind(R.id.periodicReport)
    TextView periodicReport;
    @Bind(R.id.appVersionRow)
    TableRow appVersionRow;
    @Bind(R.id.sledTitle)
    TextView sledTitle;
    @Bind(R.id.moduleVersionTitle)
    TextView moduleVersionTitle;
    @Bind(R.id.moduleVersion)
    TextView moduleVersion;
    @Bind(R.id.moduleVersionRow)
    TableRow moduleVersionRow;
    @Bind(R.id.radioVersionTitle)
    TextView radioVersionTitle;
    @Bind(R.id.radioVersion)
    TextView radioVersion;
    @Bind(R.id.radioVersionRow)
    TableRow radioVersionRow;
    @Bind(R.id.copyRight)
    TextView copyRight;

    private static AboutFragment fragment;

    public static AboutFragment newInstance() {
        if (fragment == null) ;
        fragment = new AboutFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {
        radioVersion.setText(App.DEVICE_NO);
        moduleVersion.setText(App.IP);
    }

    @Override
    protected void addListener() {

    }
}
