package com.example.mumu.warehousecheckcar.entity.check;

public class CheckWeight extends Inventory {
    /**修改重量*/
    private double weightChange;
    /**原因*/
    private String cause;

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public double getWeightChange() {
        return weightChange;
    }

    public void setWeightChange(double weightChange) {
        this.weightChange = weightChange;
    }
}
