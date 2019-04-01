package com.example.mumu.warehousecheckcar.entity;

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

    private static User user;

    public static User newInstance() {
        if (user == null)
            user = new User();
        return user;
    }

    /**
     * 成功
     */
    public void setUser(int id, String username, String msg, int code) {
        setId(id);
        setUsername(username);
        setMsg(msg);
        setCode(code);
    }

    /**
     * 失败
     */
    public void setUser(String msg, int code) {
        setMsg(msg);
        setCode(code);
        setId(-1);
        setUsername("");
    }

    public void clearUser() {
        setId(-1);
        setUsername("");
        setPassword("");
        setMsg("");
        setCode(-1);
    }

    private User() {

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
