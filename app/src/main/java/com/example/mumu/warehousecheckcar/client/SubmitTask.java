package com.example.mumu.warehousecheckcar.client;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/***
 *created by
 *on 2020/10/26
 */
public abstract class SubmitTask<T> extends AsyncTask<Object, Integer, List<T>> {
    @SuppressLint("StaticFieldLeak")
    private ProgressDialog progressDialog;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int size;

    public SubmitTask(Context context, int size) {
        this.context = context;
        this.size = size;
    }

    public SubmitTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        //开始前的准备工作
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传");
        progressDialog.setMessage("请稍等.......");
        progressDialog.setProgress(0);
//      拖动条不能取消
        progressDialog.setCancelable(false);
        progressDialog.setMax(size);
        progressDialog.show();
    }

    @Override
    protected List<T> doInBackground(Object... objects) {
        String url = ((String) objects[0]);
        List<T> list = (List<T>) objects[1];
        String log = (String) objects[2];
        int startSize = list.size();
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", User.newInstance().getId());
            jsonObject.put("data", t);
            final String json = jsonObject.toJSONString();
            try {
                LogUtil.i(log, json);
                Response response = OkHttpClientManager.postJsonAsyn(url, json);
               /* BaseReturn obj = null;
                try
                {
                    String j = new String(response.body().bytes());
                    Gson gson = new Gson();
                    obj = gson.fromJson(json, BaseReturn.class);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }*/
                final String string = response.body().string();
                JSONObject message = JSONObject.parseObject(string);
                BaseReturn baseReturn = message.toJavaObject(BaseReturn.class);
                LogUtil.i(log + "结果", "userId:" + User.newInstance().getId() + baseReturn.toString());
                if (baseReturn.getStatus() == 1) {
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e(log + "异常", "userId:" + User.newInstance().getId() + e);
            }
            publishProgress(startSize, startSize - list.size());
        }
        return list;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //显示进度
        if (progressDialog.getMax() != values[0])
            progressDialog.setMax(values[0]);
        progressDialog.setProgress(values[1]);
    }

    @Override
    protected void onPostExecute(List<T> result) {
        //最终结果的显示
        progressDialog.dismiss();
    }
}