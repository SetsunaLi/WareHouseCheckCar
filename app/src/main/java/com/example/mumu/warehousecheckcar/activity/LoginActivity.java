package com.example.mumu.warehousecheckcar.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.UpdateBean;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.utils.UpdateApk;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {


    @Bind(R.id.login_progress)
    ProgressBar loginProgress;
    @Bind(R.id.username)
    AutoCompleteTextView username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login_button)
    Button loginButton;
    @Bind(R.id.email_login_form)
    LinearLayout emailLoginForm;
    private UserLoginTask mAuthTask = null;
        private String unStr;
    private String pwStr;
    UpdateBean updateBean = new UpdateBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initUtil();
        // Set up the login form.
//        populateAutoComplete();
//设置回车键监听
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    onViewClicked(loginButton);
                    return true;
                }
                return false;
            }
        });
        initDate();
        checkVersion();
        /**更新版本入口*/
//        UpdateApk.UpdateVersion(this,updateBean);
    }
    private void initDate() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        App.SYSTEM_VERSION = sp.getString(getResources().getString(R.string.system_version_key), "20181210");
        App.IP="http://192.168.1.110";
        App.PORT="80";
        App.DEVICE_NO = sp.getString(getResources().getString(R.string.system_device_number_key), "YiFeng-001");
        App.MUSIC_SWITCH = sp.getBoolean(getResources().getString(R.string.system_music_key), false);
        App.PROWER = sp.getInt(getResources().getString(R.string.device_prower_key), 20);
        if (App.PROWER == 0)
            App.PROWER = 20;
        App.LOGCAT_SWITCH = sp.getBoolean(getResources().getString(R.string.logcat_ket), false);
    }

    private void checkVersion(){
        updateBean.setMessage("更新啦");
        updateBean.setTitle("立即更新");
        updateBean.setUrl("https://github.com/SetsunaLi/getNewApk/raw/master/app-debug.apk");
        updateBean.setVersionCode(1);
//        这里获取版本号
        updateBean.setVersionName("1.0.2");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        unStr=username.getText().toString();
        pwStr=password.getText().toString();


        // Reset errors。显示自定义文字
        username.setError(null);
        password.setError(null);

//        showProgress(true);
        mAuthTask = new UserLoginTask(unStr, pwStr);
        mAuthTask.execute((Void) null);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        username.setFocusable(show?false:true);
        username.setFocusableInTouchMode(show?false:true);
        password.setFocusable(show?false:true);
        password.setFocusableInTouchMode(show?false:true);
        loginButton.setEnabled(show?false:true);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            loginProgress.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private InputMethodManager mInputMethodManager;
    //     * 初始化必须工具
    private void initUtil() {
        //初始化输入法
        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }
//隐藏输入法
    public void cancelKeyBoard(View view){
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);// 隐藏输入法
        }

    }
    @OnClick(R.id.login_button)
    public void onViewClicked(View view) {
        cancelKeyBoard(view);
        attemptLogin();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;

        UserLoginTask(String userName, String password) {
            mUserName = userName;
            mPassword = password;
        }

        //后台运行，相当于Run（）方法
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

          /*  try {
                // Simulate network access.
                Thread.sleep(3000);
               *//* OkHttpClientManager.Param[] param=new OkHttpClientManager.Param[1];
                param[0]=new OkHttpClientManager.Param("username",mUserName);
                param[1]=new OkHttpClientManager.Param("password",mPassword);
                Response response=OkHttpClientManager.post(App.IP+":"+App.PORT+"/shYf/sh/android/login",param);*//*
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("username",mUserName);
                jsonObject.put("password",mPassword);
                String json=jsonObject.toString();
                Response response= OkHttpClientManager.postJsonAsyn(App.IP+":"+App.PORT+"/shYf/sh/android/login",json);
                final String string = response.body().string();
                User user=JSON.parseObject(string,User.class);
                User userSys=User.newInstance();
                userSys.setUser(user);
            } catch (InterruptedException e) {
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }*/
          /*  if (unStr==null&&pwStr==null)
                return false;*/
//            登录请求在此，相当于异步后台动作
//            登录成功返回true
//            否则返回false

            // TODO: register the new account here.
            return true;
        }

        //run（）方法返回结果，运行完调用，UI线程
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            Toast.makeText(LoginActivity.this,"登录成功\\用户名:"+unStr+"密码:"+pwStr,Toast.LENGTH_LONG).show();
            if (success) {
                Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            } else {
                password.setError(getString(R.string.error_incorrect_password));
                loginProgress.requestFocus();
            }
        }

        //run()运行前UI操作,类似于等待界面之类
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        //运行状态时更新实时进度
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

