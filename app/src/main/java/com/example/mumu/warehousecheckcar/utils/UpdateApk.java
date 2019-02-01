package com.example.mumu.warehousecheckcar.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;

import com.example.mumu.warehousecheckcar.activity.LoginActivity;
import com.example.mumu.warehousecheckcar.entity.UpdateBean;
import com.example.mumu.warehousecheckcar.listener.UpdateDialogOperate;
import com.example.mumu.warehousecheckcar.view.UpdateDialog;

/**
 * Created by mumu on 2019/1/29.
 */

public class UpdateApk {

    public static int UpdateVersion(final Context context, final UpdateBean updateBean) {
        String packageName = context.getPackageName();
        int nowCode = getVersionCode(context);//手机端的版本
        int newCode = updateBean.getVersionCode();
        if (nowCode < newCode) {//小于最新版本号
            checkPermission(context, updateBean);
        } else {
            Log.e("MA", "已经是最新版本");
//            ToastUtils.showMessage("已经是最新的版本");
        }
        return 0;
    }
    public static void checkPermission(final Context context, final UpdateBean updateBean) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions((LoginActivity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1000);
            return;
        } else {
            showUpdateDialog(context, updateBean);
            //
        }
    }

    public static void showUpdateDialog(final Context context, final UpdateBean updateBean) {
        final UpdateDialog updateDialog = new UpdateDialog(context);
        updateDialog.setData(updateBean, true, new UpdateDialogOperate() {
            @Override
            public void executeCancel(String text) {
                updateDialog.cancel();
            }

            @Override
            public void executeCommit(String text) {
//                downFile(updateBean.getUrl(), context);
                updateDialog.dismiss();
            }
        });
        updateDialog.show();
        updateDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        updateDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
