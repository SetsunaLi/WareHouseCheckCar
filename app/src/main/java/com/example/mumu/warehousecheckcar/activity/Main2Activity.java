package com.example.mumu.warehousecheckcar.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.Constant;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.entity.OptionMenu;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.entity.out.OutputFlag;
import com.example.mumu.warehousecheckcar.fragment.AboutFragment;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.fragment.HomeFragment;
import com.example.mumu.warehousecheckcar.fragment.SettingFragment;
import com.example.mumu.warehousecheckcar.fragment.WeightChangeFragment;
import com.example.mumu.warehousecheckcar.fragment.car.CarFragment;
import com.example.mumu.warehousecheckcar.fragment.car.CarPutawayFragment;
import com.example.mumu.warehousecheckcar.fragment.check.CheckCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.check.CheckFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbClothGetFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbExceptionFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbUpCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.chubb.ChubbUpFragment;
import com.example.mumu.warehousecheckcar.fragment.cut.CutClothFragment;
import com.example.mumu.warehousecheckcar.fragment.cut.CuttingClothPutwayCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.find.FindTpNoFragmentf;
import com.example.mumu.warehousecheckcar.fragment.find.FindVatNoFragment;
import com.example.mumu.warehousecheckcar.fragment.forward.ForwardingListFragment;
import com.example.mumu.warehousecheckcar.fragment.in.InAssistFragment;
import com.example.mumu.warehousecheckcar.fragment.in.InCheckCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.in.InCheckFragment;
import com.example.mumu.warehousecheckcar.fragment.in.ReturnGoodsInNoFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutApplyDetailFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutApplyNewFragment;
import com.example.mumu.warehousecheckcar.fragment.out.OutApplyNoFragment;
import com.example.mumu.warehousecheckcar.fragment.outsource_in.In_OutSourceFragment;
import com.example.mumu.warehousecheckcar.fragment.outsource_in.In_OutSourceNoFragment;
import com.example.mumu.warehousecheckcar.fragment.putway.PutawayCarrierFragment;
import com.example.mumu.warehousecheckcar.fragment.putway.PutawayFragment;
import com.example.mumu.warehousecheckcar.fragment.repaif_in.RepaifInNoFragment;
import com.example.mumu.warehousecheckcar.listener.ComeBack;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.example.mumu.warehousecheckcar.utils.CutToBitmapUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.content_frame)
    FrameLayout mFrame;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private final String TAG = "Main2Activity";
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        User user = User.newInstance();
        toolbar.setSubtitle("操作人:" + user.getUsername());
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mTitle = getTitle();
        initDate();
        initView();
        selectItem(0);
        comeBack = ComeBack.getInstance();
        initPermission();
    }

    private ComeBack comeBack;
    //    导航数组
    private String[] mOptionTitle;
    private EditTextPreference systemVersion, systemIP, systemPort, deviceNumber;

    private void initDate() {
        mOptionTitle = getResources().getStringArray(R.array.options_array);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        App.SYSTEM_VERSION = sp.getString(getResources().getString(R.string.system_version_key), "20181210");
        App.DEVICE_NO = sp.getString(getResources().getString(R.string.system_device_number_key), "YiFeng-001");
        App.MUSIC_SWITCH = sp.getBoolean(getResources().getString(R.string.system_music_key), true);
        App.PROWER = sp.getInt(getResources().getString(R.string.device_prower_key), 20);
        if (App.PROWER == 0)
            App.PROWER = 20;
        App.LOGCAT_SWITCH = sp.getBoolean(getResources().getString(R.string.logcat_ket), true);
    }

    NavigationView navigationView;

    private void initView() {
        User user = User.newInstance();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView iv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        iv.setImageBitmap(CutToBitmapUtil.changeToBitmap(getResources(), iv, R.mipmap.user_head));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == Constant.Handle_key) {
            Fragment fragment = getFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment instanceof CodeFragment) {
                ((CodeFragment) fragment).on134CallBack(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == Constant.Handle_key) {
            Fragment fragment = getFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment instanceof CodeFragment) {
                ((CodeFragment) fragment).on134CallBack(false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAllData();
        AppLog.clearFormat();
    }

    private void clearAllData() {
        App.SYSTEM_VERSION = "";
        App.DEVICE_NO = "";
        App.CARRIER = null;
        App.DEVICE_NO = "";
        App.IN_DETAIL_LIST.clear();
        App.OUTDETAIL_LIST.clear();
        App.INPUT_DETAIL_LIST.clear();
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
//                主页
                fragment = HomeFragment.newInstance();
                break;
            case 1:
//                入库校验
                fragment = InCheckFragment.newInstance();
                break;
            case 2:
//                盘点
                fragment = CheckCarrierFragment.newInstance();
                break;
            case 3:
//                上架
                fragment = PutawayCarrierFragment.newInstance();
                break;
            case 4:
//                叉车操作
                fragment = CarFragment.newInstance();
                break;
            case 5:
                //                寻缸
                fragment = FindVatNoFragment.newInstance();
                break;
            case 6:
//                寻托盘
                fragment = FindTpNoFragmentf.newInstance();
                break;
            case 7:
                //出库申请
                fragment = OutApplyNoFragment.newInstance();
                break;
            case 8:
////              发运
                fragment = ForwardingListFragment.newInstance();
                break;
            case 9:
//                剪布操作
                fragment = CutClothFragment.newInstance();
                break;
            case 10:
                //                剪布上架
                fragment = CuttingClothPutwayCarrierFragment.newInstance();
                break;
            case 11:
                //                查布
                fragment = ChubbFragment.newInstance();
                break;
            case 12:
                //                查布上架
                fragment = ChubbUpFragment.newInstance();
                break;
            case 13:
//                重量修改
                fragment = WeightChangeFragment.newInstance();
                break;
            case 14:
//                查布异常
                fragment = ChubbExceptionFragment.newInstance();
                break;
            case 15:
                fragment = InAssistFragment.newInstance();
                break;
            case 16:
                fragment = SettingFragment.newInstance();
                break;
            case 17:
                fragment = ReturnGoodsInNoFragment.newInstance();
                break;
            case 18:
                fragment = ChubbClothGetFragment.newInstance();
                break;
            case 19:
                fragment = RepaifInNoFragment.newInstance();
                break;
            case 20:
                fragment = In_OutSourceNoFragment.newInstance();
                break;
            case 21:
                fragment = In_OutSourceFragment.newInstance();
                break;
            default:
                fragment = AboutFragment.newInstance();
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

    /**
     * 查布接收
     */
    public void click18(View view) {
        selectItem(18);
    }

    /**
     * 无
     */
    public void click19(View view) {
        selectItem(19);
    }

    /**
     * 委外入库
     */
    public void click20(View view) {
        selectItem(20);
    }

    /**
     * 特殊委外入库
     */
    public void click21(View view) {
        selectItem(21);
    }

    //返回键监听
    @Override
    public void onBackPressed() {
        Log.i("MainActivity", "onBackPressed");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment instanceof AboutFragment || fragment instanceof SettingFragment || fragment instanceof HomeFragment
                    || fragment instanceof InCheckCarrierFragment
                    || fragment instanceof PutawayCarrierFragment || fragment instanceof CarFragment
                    || fragment instanceof ChubbFragment || fragment instanceof ChubbUpFragment
                    || fragment instanceof OutApplyNoFragment || fragment instanceof CheckCarrierFragment
                    || fragment instanceof FindVatNoFragment || fragment instanceof FindTpNoFragmentf
                    || fragment instanceof WeightChangeFragment || fragment instanceof ChubbExceptionFragment
                    || fragment instanceof ForwardingListFragment || fragment instanceof CuttingClothPutwayCarrierFragment
                    || fragment instanceof CutClothFragment || fragment instanceof InAssistFragment) {
                if (fragment instanceof HomeFragment) {
                    askForOut();
                }
                selectItem(0);
                setTitle(mOptionTitle[0]);
            } else {
                if (fragment instanceof PutawayFragment) {
                    selectItem(3);
                } else if (fragment instanceof CarPutawayFragment) {
                    selectItem(4);
                } else if (fragment instanceof OutApplyNewFragment) {
                    selectItem(7);
                } else if (fragment instanceof CheckFragment) {
                    selectItem(2);
                } else if (fragment instanceof OutApplyDetailFragment) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void initPermission() {

        if (EasyPermissions.hasPermissions(this, Constant.MANIFEST)) {

        } else {
            EasyPermissions.requestPermissions(this, "日志需要设备的存储权限", Constant.REQUEST_STORAGE_CODE, Constant.MANIFEST);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    Log.i("onPermissionsGranted", String.valueOf(requestCode));
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("onPermissionsDenied", String.valueOf(requestCode));
    }
}
