package com.example.mumu.warehousecheckcar.entity;

public class CheckWeight extends Inventory {
    private double weightChange;
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
