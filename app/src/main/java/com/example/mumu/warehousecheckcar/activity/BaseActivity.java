package com.example.mumu.warehousecheckcar.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.mumu.warehousecheckcar.dialog.LoadingDialog;
import com.example.mumu.warehousecheckcar.utils.ActivityManagerUtil;
import com.example.mumu.warehousecheckcar.utils.AppUtil;

/**
 * BaseActivity
 *
 * @author Administrator
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    protected LoadingDialog loadingDialog;
    protected ActivityManagerUtil activityManagerUtil;
    protected Activity mActivity;

    protected boolean isRestart = false;

    protected void onFirstStart() {
    }

    public ActivityManagerUtil getActivityManagerUtil() {
        return activityManagerUtil;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        activityManagerUtil = ActivityManagerUtil.getInstance();
        activityManagerUtil.pushOneActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isRestart) {
            onFirstStart();
            isRestart = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRestart = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManagerUtil.popOneActivity(this);
        if (activityManagerUtil.getStackSize() == 0) {

        }
    }

    protected void showLoadingDialog() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getFragmentManager(), loadingDialog.toString());
    }

    protected void hideLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isHidden()) {
            loadingDialog.dismiss();
        }
    }


    public void showToast(String text) {
        AppUtil.showToast(text);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int) ev.getRawX();
                int rawY = (int) ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    AppUtil.hideSoftKeyboard(this);
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 判定当前是否需要隐藏
     */
    protected boolean isShouldHideKeyBord(View v, MotionEvent ev) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
