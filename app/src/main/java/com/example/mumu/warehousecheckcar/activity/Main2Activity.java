package com.example.mumu.warehousecheckcar.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.entity.OptionMenu;
import com.example.mumu.warehousecheckcar.fragment.AboutFragment;
import com.example.mumu.warehousecheckcar.fragment.HomeFragment;
import com.example.mumu.warehousecheckcar.picture.CutToBitmap;

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

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        //右下角按钮监听
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "欢迎使用亿锋公司配套软件", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
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
//      首页
        selectItem(0);
    }

    //    导航数组
    private String[] mOptionTitle;

    private void initDate() {
        mOptionTitle = getResources().getStringArray(R.array.options_array);
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
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                fragment = AboutFragment.newInstance();
                break;
            default:
                fragment = HomeFragment.newInstance();
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment instanceof HomeFragment) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).commit();
        } else if (fragment instanceof AboutFragment) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(TAG_RETURN_FRAGMENT).commit();
        }
        setTitle(mOptionTitle[position]);
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

    //返回键监听
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    }
}
