package com.example.mumu.warehousecheckcar.utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.activity.LoginActivity;
import com.example.mumu.warehousecheckcar.client.HttpNet;
import com.example.mumu.warehousecheckcar.entity.UpdateBean;
import com.example.mumu.warehousecheckcar.listener.UpdateDialogOperate;
import com.example.mumu.warehousecheckcar.view.UpdateDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

import static java.lang.Thread.sleep;

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
                downFile(updateBean.getUrl(), context);
                updateDialog.dismiss();
            }
        });
        updateDialog.show();
        updateDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        updateDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private static ProgressDialog progressDialog;
    public static void downFile(final String url,final Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍等.......");
        progressDialog.setProgress(0);
//      拖动条不能拖拽
        progressDialog.setCancelable(false);
        progressDialog.show();

        HttpNet.httpDownLoadApk(url, new  okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //下载失败
                downFial(context);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //下载。。。
                InputStream is = null;//输入流
                FileOutputStream fos = null;//输出流
                try {
                    is = response.body().byteStream();//获取输入流
                    long total = response.body().contentLength();//获取文件大小
                    setMax(total, context);//为progressDialog设置大小
                    File file = null;
                    if (is != null) {
                        file = new File(Environment.getExternalStorageDirectory(), "warehouse.apk");// 设置路径
                        fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fos.write(buf, 0, ch);
                            process += ch;
                            downLoading(process, context);       //这里就是关键的实时更新进度了！
                        }
                    }
                    fos.flush();
                    // 下载完成
                    if (fos != null) {
                        fos.close();
                    }
                    sleep(1000);
                    downSuccess(context, file);
                } catch (Exception e) {
                    downFial(context);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public static void installApk(File file, Context context) {

        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT > 23) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =FileProvider.getUriForFile(context, "com.ma.update.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//      Attempt to invoke virtual   android.content.res.XmlResourceParser
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        //执行的数据类型
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public static void downFial(final Context context) {
        ((LoginActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
                Toast.makeText(context, "更新失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void setMax(final long total, Context context) {
        ((LoginActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMax((int) total);
            }
        });
    }

    /**
     * 进度条实时更新
     *
     * @param i
     */
    public static void downLoading(final int i, Context context) {
        ((LoginActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setProgress(i);
            }
        });
    }

    public static void downSuccess(final Context context, final File file) {
        //安装
        ((LoginActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    sleep(1000);
                    installApk(file, context);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
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
