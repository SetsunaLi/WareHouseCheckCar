package com.example.mumu.warehousecheckcar.entity;

import java.util.List;

/***
 *created by 
 *on 2020/3/27
 */
public class ResultBean<T> {

    /**
     * status : 1
     * message : 查找成功
     * data : []
     */

    private int status;
    private String message;
    private List<T> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
