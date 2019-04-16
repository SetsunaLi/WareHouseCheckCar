package com.example.mumu.warehousecheckcar.entity;

public class Cut extends Inventory{
    /**空加*/
    private    double blank_add;

    /**纸卷重*/
    private    double weight_papertube;

    public  double getBlank_add() {
        return blank_add;
    }

    public void setBlank_add(double blank_add) {
        this.blank_add = blank_add;
    }

    public  double getWeight_papertube() {
        return weight_papertube;
    }
    public void setWeight_papertube(double weight_papertube) {
        this.weight_papertube = weight_papertube;
    }
}
