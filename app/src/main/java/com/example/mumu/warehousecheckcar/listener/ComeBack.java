package com.example.mumu.warehousecheckcar.listener;

/**
 * Created by mumu on 2019/1/19.
 */

 public class ComeBack {
    public static ComeBack comeBack;
    public static ComeBack getInstance(){
        if (comeBack==null)
            comeBack=new ComeBack();
        return comeBack;
    }
    private   ComeBack(){}
    public static FragmentCallBackListener fragmentCallBackListener;
    public void setCallbackLiatener(FragmentCallBackListener fragmentCallBackListener) {
        this.fragmentCallBackListener = fragmentCallBackListener;
    }
}
