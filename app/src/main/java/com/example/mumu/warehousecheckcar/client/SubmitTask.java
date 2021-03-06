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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 *created by
 *on 2020/10/26
 */
public abstract class SubmitTask<T> extends AsyncTask<Object, Integer, Map<T, String>> {
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
    protected Map<T, String> doInBackground(Object... objects) {
        HashMap<T, String> map = new HashMap<>();
        String url = ((String) objects[0]);
        List<T> list = (List<T>) objects[1];
        String log = (String) objects[2];
        HashMap<String, Object> keyValue = null;
        if (objects.length == 4)
            keyValue = (HashMap<String, Object>) objects[3];
        int startSize = list.size();
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", User.newInstance().getId());
            jsonObject.put("data", t);
            if (keyValue != null) {
                Set<String> sets = keyValue.keySet();
                for (String set : sets) {
                    jsonObject.put(set, keyValue.get(set));
                }
            }
            final String json = jsonObject.toJSONString();
            try {
                LogUtil.i(log, json);
                Response response = OkHttpClientManager.postJsonAsyn(url, json);
                BaseReturn baseReturn = null;
                String j = new String(response.body().bytes());
                Gson gson = new Gson();
                baseReturn = gson.fromJson(j, BaseReturn.class);
                LogUtil.i(log + "结果", "userId:" + User.newInstance().getId() + baseReturn.toString());
                if (baseReturn.getStatus() != 1) {
                    map.put(t, baseReturn.getMessage());
                } else {
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    LogUtil.e(log + "异常", "userId:" + User.newInstance().getId() + e.getCause().getMessage(), e.getCause());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            publishProgress(startSize, startSize - list.size());
        }
        return map;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //显示进度
        if (progressDialog.getMax() != values[0])
            progressDialog.setMax(values[0]);
        progressDialog.setProgress(values[1]);
    }

    @Override
    protected void onPostExecute(Map<T, String> result) {
        //最终结果的显示
        progressDialog.dismiss();
    }
}