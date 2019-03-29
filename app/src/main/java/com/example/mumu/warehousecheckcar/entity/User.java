package com.example.mumu.warehousecheckcar.entity;

public class User {
    /**用户ID*/
    private int id=0;
    /**用户名*/
    private String username="";
    /**密码*/
    private String password="";

    private static User user;
    public static User newInstance(){
        if (user==null)
            user=new User();
        return user;
    }
    public void setUser(User user){
        setId(user.getId());
        setUsername(user.getUsername());
        setPassword(user.getPassword());
    }
    public void setUser(int id, String username,String password){
        setId(id);
        setUsername(username);
        setPassword(password);
    }
    public void clearUser(){
        setId(0);
        setUsername("");
        setPassword("");
    }
    private User(){

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
