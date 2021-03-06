package com.example.mumu.warehousecheckcar.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    /**
     * 用户ID
     */
    private int id = -1;
    /**
     * 用户名
     */
    private String username = "";
    /**
     * 信息
     */
    private String msg = "";
    /**
     * Code
     */
    private int code = -1;
    /**
     * 密码
     */
    private String password = "";
    /**用户权限*/
    private int auth =0;
    private List<Power> app_auth;
    private static User user;

    public static User newInstance() {
        if (user == null)
            user = new User();
        return user;
    }

    public void clearPower() {
        if (app_auth == null)
            app_auth = new ArrayList<>();
        app_auth.clear();
    }

    /**
     * 成功
     */
    public void setUser(int id, String username, String msg, int code, int auth, List<Power> app_auth) {
        setId(id);
        setUsername(username);
        setMsg(msg);
        setCode(code);
        setAuth(auth);
        setApp_auth(app_auth);
    }

    /**
     * 失败
     */
    public void setUser(String msg, int code) {
        setMsg(msg);
        setCode(code);
        setId(-1);
        setUsername("");
        clearPower();
    }

    public void clearUser() {
        setId(-1);
        setUsername("");
        setPassword("");
        setMsg("");
        setCode(-1);
        clearPower();
    }

    private User() {

    }

    public List<Power> getApp_auth() {
        return app_auth;
    }

    public void setApp_auth(List<Power> app_auth) {
        this.app_auth = app_auth;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
