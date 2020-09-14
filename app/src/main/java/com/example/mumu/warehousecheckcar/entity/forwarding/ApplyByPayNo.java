package com.example.mumu.warehousecheckcar.entity.forwarding;

/***
 *created by 
 *on 2020/8/31
 */
public class ApplyByPayNo {

    /**
     * out_no : BB00801261
     * apply_no : B00813456
     */

    private String out_no;
    private String apply_no;
    private String pay_no;
    private boolean flag = true;
    private boolean isHead = false;


    public String getPay_no() {
        return pay_no;
    }

    public void setPay_no(String pay_no) {
        this.pay_no = pay_no;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public String getApply_no() {
        return apply_no;
    }

    public void setApply_no(String apply_no) {
        this.apply_no = apply_no;
    }
}