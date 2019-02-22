package com.example.mumu.warehousecheckcar.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.entity.OptionMenu;
import com.example.mumu.warehousecheckcar.fragment.AboutFragment;
import com.example.mumu.warehousecheckcar.fragment.CheckCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.CheckFragment;
import com.example.mumu.warehousecheckcar.fragment.ChubbFragment;
import com.example.mumu.warehousecheckcar.fragment.ChubbUpCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.ChubbUpFragment;
import com.example.mumu.warehousecheckcar.fragment.FindVatNoFragment;
import com.example.mumu.warehousecheckcar.fragment.HomeFragment;
import com.example.mumu.warehousecheckcar.fragment.InCheckFragment;
import com.example.mumu.warehousecheckcar.fragment.OutApplyDetailFragment;
import com.example.mumu.warehousecheckcar.fragment.OutApplyFragment;
import com.example.mumu.warehousecheckcar.fragment.OutApplyNoFragment;
import com.example.mumu.warehousecheckcar.fragment.OutCheckCarFragment;
import com.example.mumu.warehousecheckcar.fragment.OutCheckFragment;
import com.example.mumu.warehousecheckcar.fragment.PutawayCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.PutawayFragment;
import com.example.mumu.warehousecheckcar.fragment.SettingFragment;
import com.example.mumu.warehousecheckcar.fragment.TextFragment;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
import com.example.mumu.warehousecheckcar.listener.FragmentCallBackListener;
import com.example.mumu.warehousecheckcar.picture.CutToBitmap;
import com.rfid.RFIDReaderHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.content_frame)
    FrameLayout mFrame;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private final String TAG = "Main2Activity";
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        //右下角按钮监听
     /*   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "欢迎使用亿锋公司配套软件", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });*/
//       左上角按钮
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mTitle = getTitle();
        initDate();
        initView();
        initRFID();
        init2D();
//      首页
        selectItem(0);
        comeBack = ComeBack.getInstance();
    }

    private ComeBack comeBack;
    //    导航数组
    private String[] mOptionTitle;
    private EditTextPreference systemVersion, systemIP, systemPort, deviceNumber;

    private void initDate() {
        mOptionTitle = getResources().getStringArray(R.array.options_array);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        App.SYSTEM_VERSION = sp.getString(getResources().getString(R.string.system_version_key), "20181210");
        App.IP="http://47.106.157.255";
        App.PORT="80";
//        App.IP="http://192.168.1.212";
//        App.PORT="8080";
//        App.IP = sp.getString(getResources().getString(R.string.system_ip_key), "http://47.106.157.255");
//        App.PORT = sp.getString(getResources().getString(R.string.system_port_key), "80");
        App.DEVICE_NO = sp.getString(getResources().getString(R.string.system_device_number_key), "YiFeng-001");
        App.MUSIC_SWITCH = sp.getBoolean(getResources().getString(R.string.system_music_key), false);
        App.PROWER = sp.getInt(getResources().getString(R.string.device_prower_key), 20);
        if (App.PROWER == 0)
            App.PROWER = 20;
        App.LOGCAT_SWITCH = sp.getBoolean(getResources().getString(R.string.logcat_ket), false);
//        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//        App.DEVICE_ID=tm.getImei();
//        App.DEVICE_ID=tm.getDeviceId();
      /*  App.SYSTEM_VERSION="20181210";
        App.IP="http://47.107.112.133";
        App.PORT="8088";
        App.DEVICE_NO="YiFeng-001";
        App.MUSIC_SWITCH=true;*/
    }

    NavigationView navigationView;

    private void initView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        导航头部
        ImageView iv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        iv.setImageBitmap(CutToBitmap.changeToBitmap(getResources(), iv, R.mipmap.user_head));
        TextView tv1 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView1);
        tv1.setText(getResources().getString(R.string.options_username));
        TextView tv2 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView2);
        tv2.setText(getResources().getString(R.string.options_welcome));
//      导航列表(枚举)
        for (OptionMenu om : OptionMenu.values()) {
            MenuItem mi = navigationView.getMenu().findItem(om.getId());
            mi.setTitle(mOptionTitle[om.getIndex()]);
        }
    }

    private RFIDReaderHelper rfidHander;

    //    private TDScannerHelper scannerHander;
    private void initRFID() {
        try {
            RFID_2DHander.getInstance().connectReader();
            RFID_2DHander.getInstance().on_RFID();
            rfidHander = RFID_2DHander.getInstance().getRFIDReader();
            rfidHander.registerObserver(UHFResult.getInstance());
            //返回0表示成功？
            int i = rfidHander.setOutputPower(RFID_2DHander.getInstance().btReadId, (byte) App.PROWER);
//            RFID_2DHander.getInstance().off_RFID();
        } catch (Exception e) {
            Log.w(TAG, "RFID读写器异常");
            Toast.makeText(this, getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
    }

    private void init2D() {
        try {
            boolean flag1 = RFID_2DHander.getInstance().connect2D();
        } catch (Exception e) {
            Log.w(TAG, "2D模块异常");
            Toast.makeText(this, getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
    }


    //Tag to identify the currently displayed fragment
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    protected static final String TAG_RETURN_FRAGMENT = "TitleFragment";

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = HomeFragment.newInstance();
                break;
            case 1:
                fragment = InCheckFragment.newInstance();
                break;
            case 2:
                fragment = OutCheckCarFragment.newInstance();
                break;
            case 3:
                fragment = ChubbFragment.newInstance();
                break;
            case 10:
                fragment=ChubbUpFragment.newInstance();
                break;
            case 4:
                fragment = PutawayCarrierFragment.newInstance();
                break;
            case 5:
                fragment = OutApplyNoFragment.newInstance();
                break;
            case 6:
                fragment = CheckCarrierFragment.newInstance();
                break;
            case 7://寻货待开发
                fragment = FindVatNoFragment.newInstance();
//                fragment=new TextFragment();
                break;
            case 8:
                fragment = SettingFragment.newInstance();
                break;
            case 9:
                fragment = AboutFragment.newInstance();
                break;
            default:
                fragment = HomeFragment.newInstance();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragment instanceof HomeFragment) {

            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).commit();
        } else if (fragment != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
        }

//        设置列表点击状态
        navigationView.getMenu().findItem(OptionMenu.values()[position].getId()).setChecked(true);
//        关闭抽屉
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    /*
     * 个人信息点击监听
     * */
    public void mesClick(View view) {
        selectItem(1);
    }

    /*
     * 盘点作业点击监听
     * */
    public void cheClick(View view) {
        selectItem(2);
    }

    /*
     * 设备设置点击监听
     * */
    public void setClick(View view) {
        selectItem(3);
    }

    /*
     * 个性化一点击监听
     * */
    public void inClick1(View view) {
        selectItem(4);
    }

    /*
     * 个性化二点击监听
     * */
    public void inClick2(View view) {
        selectItem(5);
    }

    /*
     * 个性化三点击监听
     * */
    public void inClick3(View view) {
        selectItem(6);
    }

    /*
     * 个性化三点击监听
     * */
    public void findClick(View view) {
        selectItem(7);
    }

    /*
     * 个性化三点击监听
     * */
    public void settingClick(View view) {
        selectItem(8);
    }

    public void chubbup(View view) {
        selectItem(10);
    }

    //返回键监听
    @Override
    public void onBackPressed() {
        Log.i("MainActivity", "onBackPressed");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment != null && (fragment instanceof AboutFragment || fragment instanceof
                    OutCheckCarFragment || fragment instanceof InCheckFragment || fragment instanceof SettingFragment
                    || fragment instanceof HomeFragment || fragment instanceof CheckCarrierFragment
                    || fragment instanceof PutawayCarrierFragment || fragment instanceof OutApplyNoFragment
            )) {

                //update the selected item in the drawer and the title
//            mDrawerList.setItemChecked(0, true);
                selectItem(0);
                setTitle(mOptionTitle[0]);
                //We are handling back pressed for saving pre-filters settings. Notify the appropriate fragment.
                //{@link BaseReceiverActivity # onBackPressed should be called by the fragment when the processing is done}
                //super.onBackPressed();

                if (fragment instanceof HomeFragment) {

                    askForOut();
                } else {

                }
            } else {
                if (fragment != null && (fragment instanceof OutCheckFragment)) {
                    askForBack();
                } else if (fragment != null && (fragment instanceof PutawayFragment)) {
                    selectItem(4);
                } else if (fragment != null && (fragment instanceof OutApplyFragment)) {
                    selectItem(5);
                } else if (fragment != null && (fragment instanceof CheckFragment)) {
                    selectItem(6);
                } else if (fragment != null && (fragment instanceof OutApplyDetailFragment)) {
                    getFragmentManager().popBackStack();
                    if (comeBack.fragmentCallBackListener != null)
                        comeBack.fragmentCallBackListener.comeBackListener();
                } else if (fragment instanceof ChubbUpCarrierFragment) {
                    getFragmentManager().popBackStack();
                    if (comeBack.fragmentCallBackListener != null)
                        comeBack.fragmentCallBackListener.ubLoad(true);

                }
                    /*else if (fragment != null && (fragment instanceof OutApplyFragment)){
                    ((OutApplyFragment)fragment).onBackPressed();
                    getFragmentManager().popBackStack();
                }*/
                else {
                    getFragmentManager().popBackStack();
                }
            }
        }
    }

    public void showProgress(final boolean show) {
/*
        loginButton.setEnabled(show?false:true);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.///////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            loginProgress.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        }*/
    }

    private void askForBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("提示" + "").
                setMessage("确认返回吗？" + "").
                setPositiveButton(getString(R.string.btn_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getFragmentManager().popBackStack();
                            }
                        }).setNegativeButton(getString(R.string.btn_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();
    }

    private void askForOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("提示" + "").
                setMessage("确认要退出吗？" + "").
                setPositiveButton(getString(R.string.btn_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //close the module
                                finish();
                            }
                        }).setNegativeButton(getString(R.string.btn_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();
    }

    //右上角列表R.menu.main2
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    //右上角列表点击监听（相当于onclickitemlistener,可用id或者title匹配）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //左边列表点击监听（相当于onclickitemlistener,可用id匹配）
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        for (OptionMenu om : OptionMenu.values()) {
            if (om.getId() == id) {
                selectItem(om.getIndex());
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        disConnectRFID();
        disConnect2D();
        clearAllData();
    }

    private void clearAllData() {
        App.APPLY_NO = "";
        App.carNo = "";
        App.SYSTEM_VERSION = "";
        App.IP = "";
        App.PORT = "";
        App.DEVICE_NO = "";
        App.CARRIER = null;
        App.DEVICE_NO = "";
        App.IN_DETAIL_LIST.clear();
        App.OUTDETAIL_LIST.clear();
        App.CHECK_DETAIL_LIST.clear();
        App.OUTPUT_DETAIL_LIST.clear();
        App.INPUT_DETAIL_LIST.clear();
        App.DATA_KEY.clear();

    }

    private void disConnectRFID() {
        try {

            RFID_2DHander.getInstance().off_RFID();
            if (rfidHander != null)
                rfidHander.unRegisterObserver(UHFResult.getInstance());
            RFID_2DHander.getInstance().disConnectReader();
            RFID_2DHander.getInstance().releaseRFID();
        } catch (Exception e) {

        }
    }

    private void disConnect2D() {
        try {
//            RFID_2DHander.getInstance().off_2D();
            RFID_2DHander.getInstance().disConnect2D();

        } catch (Exception e) {

        }
    }

}
