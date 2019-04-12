package com.example.mumu.warehousecheckcar.entity;

import com.example.mumu.warehousecheckcar.R;

/**
 * Created by mumu on 2018/11/19.
 * 左边导航栏item
 */

public enum  OptionMenu {
//    id是布局里面的id，value是对应MainActivity里跳转的序号
    ITEM1(R.id.nav_item1,0),ITEM2(R.id.nav_item2,1),ITEM3(R.id.nav_item3,2),ITEM4(R.id.nav_item4,3)
    ,ITEM5(R.id.nav_item5,4),ITEM6(R.id.nav_item6,5),ITEM7(R.id.nav_item7,6),ITEM8(R.id.nav_item8,7)
    ,ITEM9(R.id.nav_item9,8),ITEM10(R.id.nav_item10,9),ITEM11(R.id.nav_item11,10),ITEM12(R.id.nav_item12,11)
    ,ITEM13(R.id.nav_item13,12),ITEM14(R.id.nav_item14,13),ITEM15(R.id.nav_item15,14),ITEM16(R.id.nav_item16,15)
    , ITEM17(R.id.nav_setting,16),ITEM18(R.id.nav_about,17);
    private int id;
    private int index;
    private OptionMenu(int id,int index){
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
