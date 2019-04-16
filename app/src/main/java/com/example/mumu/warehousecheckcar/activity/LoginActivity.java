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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.UpdateBean;
import com.example.mumu.warehousecheckcar.entity.User;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


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
    @Bind(R.id.checkbox1)
    CheckBox checkbox1;
    @Bind(R.id.checkbox2)
    CheckBox checkbox2;
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
        checkVersion();
        /**更新版本入口*/
//        UpdateApk.UpdateVersion(this,updateBean);
    }

    private void initDate() {
   /*     App.IP = "http://192.168.1.109";
        App.PORT = "80";*/
        App.IP="http://192.168.1.146";
        App.PORT="8080";
      /*  App.IP="http://192.168.1.146";
        App.PORT="8080";*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate();
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        if (sharedPreferences != null) {
            String name = sharedPreferences.getString("username", "");
            String word = sharedPreferences.getString("password", "");
            boolean rememberFlag = sharedPreferences.getBoolean("remember", true);
            boolean upFlag = sharedPreferences.getBoolean("up", false);
            username.setText(name + "");
            checkbox1.setChecked(rememberFlag);
            if (rememberFlag) {
                checkbox2.setChecked(upFlag);
                password.setText(word + "");
            }
            if(upFlag&&!name.equals("")&&!word.equals(""))
                onViewClicked(loginButton);
        }
    }

    private void checkVersion() {
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
        unStr = username.getText().toString();
        pwStr = password.getText().toString();
        unStr = unStr.replaceAll(" ", "");
        pwStr = pwStr.replaceAll(" ", "");

        // Reset errors。显示自定义文字
        username.setError(null);
        password.setError(null);

        Integer length = 20;
        if (unStr != null && !unStr.equals("") && pwStr != null && !pwStr.equals("")) {
            if (unStr.length() < length && pwStr.length() < length) {
                boolean rememberFlag = checkbox1.isChecked();
                boolean upFlag = checkbox2.isChecked();
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", unStr);
                editor.putBoolean("remember", rememberFlag);
                if (rememberFlag) {
                    editor.putString("password", pwStr);
                    editor.putBoolean("up", upFlag);

                } else {
                    editor.putString("password", "");
                    editor.putBoolean("up", false);
                }
                editor.commit();
                //        showProgress(true);
                mAuthTask = new UserLoginTask(unStr, pwStr);
                mAuthTask.execute((Void) null);
            } else
                Toast.makeText(this, "用户名密码长度不能过长", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        username.setFocusable(show ? false : true);
        username.setFocusableInTouchMode(show ? false : true);
        password.setFocusable(show ? false : true);
        password.setFocusableInTouchMode(show ? false : true);
        loginButton.setEnabled(show ? false : true);
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
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    //隐藏输入法
    public void cancelKeyBoard(View view) {
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

            try {
                // Simulate network access.
//                Thread.sleep(3000);
//                OkHttpClientManager.Param[] param=new OkHttpClientManager.Param[1];
//                param[0]=new OkHttpClientManager.Param("username",mUserName);
//                param[1]=new OkHttpClientManager.Param("password",mPassword);
//                Response response=OkHttpClientManager.post(App.IP+":"+App.PORT+"/shYf/sh/android/login",param);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", mUserName);
                jsonObject.put("password", mPassword);
                String json = jsonObject.toString();
                Response response = OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/android/login", json);
                final String string = response.body().string();
                JSONObject message = JSONObject.parseObject(string);
                int code = (int) message.get("code");
                switch (code) {
                   /* case 1001: {//用户名不存在
                        String msg = (String) message.get("msg");
                        User user = User.newInstance();
                        user.setUser(msg, code);
                        Toast.makeText(context,"用户名不存在",Toast.LENGTH_SHORT).show();

                    }
                        break;
                    case 5:{//账号已经停用
                        String msg = (String) message.get("msg");
                        User user = User.newInstance();
                        user.setUser(msg, code);
                        Toast.makeText(context,"账号已经停用",Toast.LENGTH_SHORT).show();
                    }
                        break;*/
                    case 0: {//成功
                        int id = (int) message.get("id");
                        String username = (String) message.get("username");
                        String msg = (String) message.get("msg");
                        User user = User.newInstance();
                        user.setUser(id, username, msg, code);


                        return true;
                    }
                  /*  case 1003:{//密码不正确
                        String msg = (String) message.get("msg");
                        User user = User.newInstance();
                        user.setUser(msg, code);
                        Toast.makeText(context,"密码不正确",Toast.LENGTH_SHORT).show();
                    }
                        break;*/
                    default: {//else
                        String msg = (String) message.get("msg");
                        User user = User.newInstance();
                        user.setUser(msg, code);
                        return false;
                    }
                }
//            } catch (InterruptedException e) {
//                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //run（）方法返回结果，运行完调用，UI线程
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                User user = User.newInstance();
                Toast.makeText(LoginActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
            } else {
                User user = User.newInstance();
                if (user.getMsg()==null||user.getMsg().equals(""))
                Toast.makeText(LoginActivity.this, "无法请求服务器，请检查网络！", Toast.LENGTH_SHORT).show();
                else
                Toast.makeText(LoginActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
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

