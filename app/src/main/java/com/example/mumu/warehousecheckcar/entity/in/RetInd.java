package com.example.mumu.warehousecheckcar.entity.in;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 退布明细
 *
 * @author chenshengjin
 * @date 2019/9/23
 */
public class RetInd implements Serializable {
    @SerializedName(value = "fab_roll", alternate = "fabRool")
    private String fab_roll;
    @SerializedName(value = "wms_epc", alternate = "epc")
    private String wms_epc;
    @SerializedName(value = "weight", alternate = "weight_inv")
    private Double weight;

    @Override
    public String toString() {
        return "RetInd{" +
                "fab_roll='" + fab_roll + '\'' +
                ", wms_epc='" + wms_epc + '\'' +
                ", weight_in=" + weight +
                '}';
    }

    public String getFab_roll() {
        return fab_roll;
    }

    public void setFab_roll(String fab_roll) {
        this.fab_roll = fab_roll;
    }

    public String getWms_epc() {
        return wms_epc;
    }

    public void setWms_epc(String wms_epc) {
        this.wms_epc = wms_epc;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public RetInd() {
    }
}
