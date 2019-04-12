package com.example.mumu.warehousecheckcar.entity;

import com.example.mumu.warehousecheckcar.R;

public enum HomeButton {
    Button1(R.id.button1,R.mipmap.click1_l),Button2(R.id.button2,R.mipmap.click2_l),Button3(R.id.button3,R.mipmap.click3_l),
    Button4(R.id.button4,R.mipmap.click4_l),Button5(R.id.button5,R.mipmap.click5_l),Button6(R.id.button6,R.mipmap.click6_l),
    Button7(R.id.button7,R.mipmap.click7_l),Button8(R.id.button8,R.mipmap.click8_l),Button9(R.id.button9,R.mipmap.click9_l),
    Button10(R.id.button10,R.mipmap.click10_l),Button11(R.id.button11,R.mipmap.click11_l),Button12(R.id.button12,R.mipmap.click12_l),
    Button13(R.id.button13,R.mipmap.click13_l),Button14(R.id.button14,R.mipmap.click14_l),Button15(R.id.button15,R.mipmap.wait_l),
    Button16(R.id.button16,R.mipmap.setting_l);
    private int id;
    private int index;
    private HomeButton(int id,int index){
        this.id=id;
        this.index=index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
