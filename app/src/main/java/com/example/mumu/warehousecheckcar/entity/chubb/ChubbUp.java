package com.example.mumu.warehousecheckcar.entity.chubb;

import com.example.mumu.warehousecheckcar.entity.Cloth;

public class ChubbUp extends Cloth {
    private int palletId = 0;
    /***原托盘号**/
    private String bas_pallet = "";
    private int locationId = 0;
    /***原库位号**/
    private String bas_location = "";

    public String getBas_pallet() {
        return bas_pallet;
    }

    public void setBas_pallet(String bas_pallet) {
        this.bas_pallet = bas_pallet;
    }

    public String getBas_location() {
        return bas_location;
    }

    public void setBas_location(String bas_location) {
        this.bas_location = bas_location;
    }
}
