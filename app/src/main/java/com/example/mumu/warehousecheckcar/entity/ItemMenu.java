package com.example.mumu.warehousecheckcar.entity;

import com.example.mumu.warehousecheckcar.R;

/**
 * Created by mumu on 2018/11/26.
 */

public enum ItemMenu {
    TEXT1(R.id.item1,0),TEXT2(R.id.item2,1),TEXT3(R.id.item3,2),TEXT4(R.id.item4,3)
    ,TEXT5(R.id.item5,4)/*,TEXT6(R.id.item6,5),TEXT7(R.id.item7,6),TEXT8(R.id.item8,7)*/;
    private int id;
    private int index;
    private ItemMenu(int id,int index){
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
