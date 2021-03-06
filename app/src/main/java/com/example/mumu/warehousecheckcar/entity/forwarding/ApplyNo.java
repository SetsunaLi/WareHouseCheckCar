package com.example.mumu.warehousecheckcar.entity.forwarding;

/**
 * Created by mumu on 2018/12/08.
 * 申请单
 */

public class ApplyNo {
    /**
     * 申请单号
     */
    private String applyNo;
    private String company;
    private boolean flag;
    private int count;

    public ApplyNo(String applyNo, boolean flag, int count, String company) {
        this.company = company;
        this.applyNo = applyNo;
        this.flag = flag;
        this.count = count;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }
}
