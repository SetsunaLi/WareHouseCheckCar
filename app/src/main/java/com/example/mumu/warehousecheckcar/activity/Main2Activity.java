package com.example.mumu.warehousecheckcar.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.OptionMenu;
import com.example.mumu.warehousecheckcar.entity.OutputFlag;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.AboutFragment;
import com.example.mumu.warehousecheckcar.fragment.car.CarFragment;
import com.example.mumu.warehousecheckcar.fragment.car.CarPutawayFragment;
import com.example.mumu.warehousecheckcar.fragment.check.CheckCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.check.CheckFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbExceptionFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbUpCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbUpFragment;
import com.example.mumu.warehousecheckcar.fragment.cut.CutClothFragment;
import com.example.mumu.warehousecheckcar.fragment.cut.CutClothOutFragment;
import com.example.mumu.warehousecheckcar.fragment.cut.CutClothOutNoFragment;
import com.example.mumu.warehousecheckcar.fragment.cut.CuttingClothPutwayCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.find.FindTpNoFragmentf;
import com.example.mumu.warehousecheckcar.fragment.find.FindVatNoFragment;
import com.example.mumu.warehousecheckcar.fragment.forward.ForwardingMsgFragment;
import com.example.mumu.warehousecheckcar.fragment.HomeFragment;
import com.example.mumu.warehousecheckcar.fragment.in.InAssistFragment;
import com.example.mumu.warehousecheckcar.fragment.in.InCheckCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.in.InCheckFragment;
import com.example.mumu.warehousecheckcar.fragment.in.ReturnGoodsInNoFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutApplyDetailFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutApplyNewFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutApplyNoFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutCheckCarFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutCheckFragment;
import com.example.mumu.warehousecheckcar.fragment.putway.PutawayCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.putway.PutawayFragment;
import com.example.mumu.warehousecheckcar.fragment.SettingFragment;
import com.example.mumu.warehousecheckcar.fragment.WeightChangeFragment;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
import com.example.mumu.warehousecheckcar.picture.CutToBitmap;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.rfid.RFIDReaderHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

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
    private Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        sound = new Sound(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        User user = User.newInstance();
        toolbar.setSubtitle("操作人:" + user.getUsername());
//        toolbar.bringToFront();
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
//        App.IP = "http://192.168.1.109";
//        App.PORT = "8080";
 /*       App.IP="http://47.106.157.255";
        App.PORT="80";*/
    /*    App.IP = "http://120.79.56.119";
        App.PORT = "8080";*/
    /*    App.IP = "http://192.168.1.109";
        App.PORT = "80";*/
        /*   App.IP="http://192.168.1.110";
        App.PORT="80";*/
//        App.IP = sp.getString(getResources().getString(R.string.system_ip_key), "http://47.106.157.255");
//        App.PORT = sp.getString(getResources().getString(R.string.system_port_key), "80");
        App.DEVICE_NO = sp.getString(getResources().getString(R.string.system_device_number_key), "YiFeng-001");
        App.MUSIC_SWITCH = sp.getBoolean(getResources().getString(R.string.system_music_key), true);
        App.PROWER = sp.getInt(getResources().getString(R.string.device_prower_key), 20);
        if (App.PROWER == 0)
            App.PROWER = 20;
        App.LOGCAT_SWITCH = sp.getBoolean(getResources().getString(R.string.logcat_ket), true);
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
        User user = User.newInstance();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView iv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        iv.setImageBitmap(CutToBitmap.changeToBitmap(getResources(), iv, R.mipmap.user_head));
        TextView tv1 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView1);
        tv1.setText(user.getUsername());
        TextView tv2 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView2);
        tv2.setText(getResources().getString(R.string.options_welcome));
        for (int i = 0; i < OptionMenu.values().length; i++) {
            OptionMenu om = OptionMenu.values()[i];
            MenuItem mi = navigationView.getMenu().findItem(om.getId());
            mi.setTitle(mOptionTitle[i]);
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
            rfidHander.setOutputPower(RFID_2DHander.getInstance().btReadId, (byte) App.PROWER);
        } catch (Exception e) {
            Log.w(TAG, "RFID读写器异常");
            Toast.makeText(this, getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
    }

    private void init2D() {
        try {
            RFID_2DHander.getInstance().connect2D();
        } catch (Exception e) {
            Log.w(TAG, "2D模块异常");
            Toast.makeText(this, getResources().getString(R.string.hint_rfid_mistake), Toast.LENGTH_LONG).show();
        }
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
            RFID_2DHander.getInstance().disConnect2D();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        disConnectRFID();
        disConnect2D();
        clearAllData();
//        销毁日志事务
        AppLog.clearFormat();
    }

    private void clearAllData() {
        App.APPLY_NO = "";
        App.carNo = "";
        App.SYSTEM_VERSION = "";
        App.DEVICE_NO = "";
        App.CARRIER = null;
        App.DEVICE_NO = "";
        App.IN_DETAIL_LIST.clear();
        App.OUTDETAIL_LIST.clear();
        App.CHECK_DETAIL_LIST.clear();
        App.INPUT_DETAIL_LIST.clear();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    protected static final String TAG_RETURN_FRAGMENT = "TitleFragment";

    private void selectItem(int position) {
        Fragment fragment = null;
        User user = User.newInstance();
        int auth = user.getAuth();
        boolean flag = false;
        switch (position) {
            case 0:
//                主页
                fragment = HomeFragment.newInstance();
                flag = true;
                break;
            case 1:
//                入库校验
                fragment = InCheckFragment.newInstance();
                if (auth != 6 && auth != 7 && auth != 8 && auth != 9)
                    flag = true;
                break;
            case 2:
//                盘点
                fragment = CheckCarrierFragment.newInstance();
//                出库校验
                if (auth != 5 && auth != 6 && auth != 8 && auth != 9 && auth != 10)
                    flag = true;
                break;
            case 3:
//                上架
                fragment = PutawayCarrierFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 9)
                    flag = true;
                break;
            case 4:
//                叉车操作
                fragment = CarFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 9 && auth != 10)
                    flag = true;
                break;
            case 5:
                //                寻缸
                fragment = FindVatNoFragment.newInstance();
                flag = true;
                break;
            case 6:
//                寻托盘
                fragment = FindTpNoFragmentf.newInstance();
                flag = true;
                break;
            case 7:
                //出库申请
                fragment = OutApplyNoFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 9 && auth != 6)
                    flag = true;
                break;
            case 8:
////              发运
                fragment = ForwardingMsgFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 9 && auth != 6)
                    flag = true;
                break;
            case 9:
//                剪布操作
                fragment = CutClothFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 10 && auth != 6)
                    flag = true;
                break;
            case 10:
                //                剪布上架
                fragment = CuttingClothPutwayCarrierFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 10 && auth != 6)
                    flag = true;
                break;
            case 11:
                //                查布
                fragment = ChubbFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 9 && auth != 10 && auth != 6)
                    flag = true;
                break;
            case 12:
                //                查布上架
                fragment = ChubbUpFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 9 && auth != 10 && auth != 6)
                    flag = true;
                break;
            case 13:
//                重量修改
                fragment = WeightChangeFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 8 && auth != 10 && auth != 6)
                    flag = true;
                break;
            case 14:
//                查布异常
                fragment = ChubbExceptionFragment.newInstance();
                if (auth != 5 && auth != 7 && auth != 9 && auth != 10 && auth != 6)
                    flag = true;
                break;
            case 15:
//                待开发
                fragment = InAssistFragment.newInstance();
                flag = true;
                break;
            case 16:
                fragment = SettingFragment.newInstance();
                flag = true;
                break;
            case 17:
                fragment = ReturnGoodsInNoFragment.newInstance();
                flag = true;
                break;
            default:
                fragment = AboutFragment.newInstance();
                flag = true;
                break;
        }
        if (flag) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragment instanceof HomeFragment) {

                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).commit();
            } else if (fragment != null) {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
            }
        } else {
            Toast.makeText(this, "你的账号权限无法使用该功能", Toast.LENGTH_LONG).show();
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    /*
     * 入库校验
     * */
    public void click1(View view) {
        selectItem(1);
    }

    /*
     * 盘点
     * */
    public void click2(View view) {
        selectItem(2);
    }

    /*
     * 上架
     * */
    public void click3(View view) {
        selectItem(3);
    }

    /*
     * 叉车操作
     * */
    public void click4(View view) {
        selectItem(4);
    }

    /*
     * 寻缸
     * */
    public void click5(View view) {
        selectItem(5);
    }

    /*
     * 寻托
     * */
    public void click6(View view) {
        selectItem(6);
    }

    /*
     * 出库
     * */
    public void click7(View view) {
        selectItem(7);
    }

    /*
     * 发运
     * */
    public void click8(View view) {
        selectItem(8);
    }

    /**
     * 剪布
     */
    public void click9(View view) {
        selectItem(9);
    }

    /**
     * 剪布上架
     */
    public void click10(View view) {
        selectItem(10);
    }

    /**
     * 查布
     */
    public void click11(View view) {
        selectItem(11);
    }

    /**
     * 查布上架
     */
    public void click12(View view) {
        selectItem(12);
    }

    /**
     * 调整库存重量
     */
    public void click13(View view) {
        selectItem(13);
    }

    /**
     * 查布异常
     */
    public void click14(View view) {
        selectItem(14);
    }

    /**
     * 待开发
     */
    public void click15(View view) {
        selectItem(15);
    }

    /**
     * 系统
     */
    public void click16(View view) {
        selectItem(16);
    }

    /**
     * 退库入库
     */
    public void click17(View view) {
        selectItem(17);
    }

    //返回键监听
    @Override
    public void onBackPressed() {
        Log.i("MainActivity", "onBackPressed");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment != null && (fragment instanceof AboutFragment || fragment instanceof SettingFragment || fragment instanceof HomeFragment
                    || fragment instanceof InCheckCarrierFragment || fragment instanceof OutCheckCarFragment
                    || fragment instanceof PutawayCarrierFragment || fragment instanceof CarFragment
                    || fragment instanceof ChubbFragment || fragment instanceof ChubbUpFragment
                    || fragment instanceof OutApplyNoFragment || fragment instanceof CheckCarrierFragment
                    || fragment instanceof FindVatNoFragment || fragment instanceof FindTpNoFragmentf
                    || fragment instanceof WeightChangeFragment || fragment instanceof ChubbExceptionFragment
                    || fragment instanceof ForwardingMsgFragment || fragment instanceof CuttingClothPutwayCarrierFragment
                    || fragment instanceof CutClothFragment || fragment instanceof InAssistFragment)) {
                if (fragment instanceof HomeFragment) {
                    askForOut();
                }
                selectItem(0);
                setTitle(mOptionTitle[0]);
            } else {
                if (fragment != null && (fragment instanceof OutCheckFragment)) {
                    askForBack();
                } else if (fragment != null && (fragment instanceof PutawayFragment)) {
                    selectItem(3);
                } else if (fragment != null && (fragment instanceof CarPutawayFragment)) {
                    selectItem(4);
                } else if (fragment != null && (fragment instanceof OutApplyNewFragment)) {
                    selectItem(7);
                } else if (fragment != null && (fragment instanceof CheckFragment)) {
                    selectItem(2);
                } else if (fragment != null && (fragment instanceof CutClothOutFragment || fragment instanceof CutClothOutNoFragment)) {
                    selectItem(9);
                } else if (fragment != null && (fragment instanceof OutApplyDetailFragment)) {
                    setOutApplyDataList(((OutApplyDetailFragment) fragment).getList());
                    getFragmentManager().popBackStack();
                    if (comeBack.fragmentCallBackListener != null)
                        comeBack.fragmentCallBackListener.comeBackListener();
                } else if (fragment instanceof ChubbUpCarrierFragment) {
                    getFragmentManager().popBackStack();
                    if (comeBack.fragmentCallBackListener != null)
                        comeBack.fragmentCallBackListener.ubLoad(true);
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        }
    }

    private HashMap<String, OutputFlag> dataList = new HashMap<>();

    public HashMap getOutApplyDataList() {
        if (dataList != null)
            return dataList;
        return new HashMap();
    }

    public void setOutApplyDataList(HashMap<String, OutputFlag> dataList) {
        this.dataList.clear();
        this.dataList.putAll(dataList);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            EventBus.getDefault().postSticky(new EventBusMsg(0xfe));
            Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
}
