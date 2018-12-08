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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mumu on 2018/3/31.
 */

public class AboutFragment extends Fragment {

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

    public AboutFragment() {

    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //    这里加载视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    //这里写界面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    //右上角列表R.menu.main2
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2,menu);
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
    /**
     * method to set version details retrieved from reader after connected with reader
     */
    /*public void deviceConnected() {
        if (radioVersion != null && App.versionInfo.containsKey("NGE"))
            radioVersion.setText(App.versionInfo.get(Constants.NGE) + "");
        if (moduleVersion != null && App.versionInfo.containsKey("GENX_DEVICE"))
            moduleVersion.setText(App.versionInfo.get(Constants.GENX_DEVICE) + "");
    }*/
    /**
     * method to clear version details on disconnection of the reader
     */
    public void resetVersionDetail() {
        if (radioVersion != null)
            radioVersion.setText("");
        if (moduleVersion != null)
            moduleVersion.setText("");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
