package com.example.mumu.warehousecheckcar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.dialog.ConfirmDialog;
import com.example.mumu.warehousecheckcar.dialog.LoadingDialog;
import com.example.mumu.warehousecheckcar.dialog.UploadDialog;

/***
 *created by mumu
 *on 2019/11/7
 */
public abstract class BaseFragment extends Fragment {
    public UploadDialog uploadDialog;
    public Runnable r = new Runnable() {
        @Override
        public void run() {
            uploadDialog.openView();
        }
    };
    private LoadingDialog loadingDialog;
    private ConfirmDialog confirmDialog;
    private InputMethodManager mInputMethodManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUtil();
        initData();
        initView(view);
        addListener();
    }

    /**
     * 初始化输入法
     */
    private void initUtil() {
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 显示输入法键盘
     */
    public void showKeyBoard(View view) {
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 收起输入法键盘
     */
    public void cancelKeyBoard(View view) {
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示Toast
     */
    protected void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示加载Dialog
     */
    protected void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance();
        }
        loadingDialog.show(getChildFragmentManager(), "loading");
    }

    /**
     * 隐藏加载Dialog
     */
    protected void hideLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isHidden()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 显示提示Dialog
     */
    protected void showConfirmDialog(String msg) {
        if (confirmDialog == null) {
            confirmDialog = ConfirmDialog.newInstance(msg);
        } else {
            confirmDialog.changeText(msg);
        }
        confirmDialog.show(getChildFragmentManager(), "confirm");
    }

    /**
     * 隐藏提示Dialog
     */
    protected void hideConfirmDialog() {
        if (confirmDialog != null && !confirmDialog.isHidden()) {
            confirmDialog.dismiss();
        }
    }

    /**
     * 显示上传Dialog
     */
    protected void showUploadDialog(String msg) {
        if (uploadDialog == null) {
            uploadDialog = UploadDialog.newInstance(msg);
        } else {
            uploadDialog.changeText(msg);
        }
        uploadDialog.setCancelable(false);
        uploadDialog.show(getChildFragmentManager(), "upload");
    }

    /**
     * 隐藏上传Dialog
     */
    protected void hideUploadDialog() {
        if (uploadDialog != null && !uploadDialog.isHidden()) {
            uploadDialog.dismiss();
            uploadDialog.setOnYesClickListener(null);

        }
    }

    /**
     * 上传Dialog确认按钮监听
     */
    protected void setUploadYesClickListener(View.OnClickListener onClickListener) {
        if (uploadDialog == null) {
            uploadDialog = UploadDialog.newInstance("");
        }
        uploadDialog.setOnYesClickListener(onClickListener);
    }

    /**
     * 上传Dialog取消按钮监听
     */
    protected void setUploadNoClickListener(View.OnClickListener onClickListener) {
        if (uploadDialog == null) {
            uploadDialog = UploadDialog.newInstance("");
        }
        uploadDialog.setOnNoClickListener(onClickListener);
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化布局
     *
     * @param view view
     */
    protected abstract void initView(View view);

    /**
     * 添加监听器
     */
    protected abstract void addListener();
}
