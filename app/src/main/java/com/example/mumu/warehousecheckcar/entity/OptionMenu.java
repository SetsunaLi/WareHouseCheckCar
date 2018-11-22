package com.example.mumu.warehousecheckcar.entity;

import com.example.mumu.warehousecheckcar.R;

/**
 * Created by mumu on 2018/11/19.
 */

public enum  OptionMenu {
    ITEM1(R.id.nav_item1,0),ITEM2(R.id.nav_item2,1),ITEM3(R.id.nav_item3,2),ITEM4(R.id.nav_item4,3)
    ,ITEM5(R.id.nav_item5,4),ITEM6(R.id.nav_item6,5),ITEM7(R.id.nav_item7,6),ITEM8(R.id.nav_about,7);
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
