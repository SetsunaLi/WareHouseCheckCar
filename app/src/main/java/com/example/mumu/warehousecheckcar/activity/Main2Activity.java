package com.example.mumu.warehousecheckcar.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.example.mumu.warehousecheckcar.fragment.OptionMenu;
import com.example.mumu.warehousecheckcar.picture.CutToBitmap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.content_frame)
    FrameLayout contentFrame;
    @Bind(R.id.nav_view)
    NavigationView navView;

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initDate();
        initView();
    }

    //    导航数组
    private String[] mOptionTitle;

    private void initDate() {
        mOptionTitle = getResources().getStringArray(R.array.options_array);
    }

    private void initView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

    //返回键监听
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        if (id == R.id.nav_item1) {
            // Handle the camera action
        } else if (id == R.id.nav_item2) {

        } else if (id == R.id.nav_item3) {

        } else if (id == R.id.nav_item4) {

        } else if (id == R.id.nav_item5) {

        } else if (id == R.id.nav_item6) {

        } else if (id == R.id.nav_item7) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
