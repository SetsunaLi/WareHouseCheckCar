package com.example.mumu.warehousecheckcar.entity;

/***
 *created by 
 *on 2020/8/29
 */
public class OutsourceGroup extends Outsource {
    private double allWeightF;
    private double allWeight;
    private double allScanWeight;
    private int scanCount;
    private int outCount;
    private boolean stutas=false;

    public OutsourceGroup(Outsource outsource, int outCount) {
        super(outsource.isFlag(), outsource.getTransNo(), outsource.getVat_no(), outsource.getProduct_no(), outsource.getProduct_name(), outsource.getColor_code(), outsource.getSel_color()
                , outsource.getColor_name(), outsource.getCust_po(), outsource.getWidth_side(), outsource.getWeight_kj(), outsource.getWeight_zg(), outsource.getDeliverNo());
        this.allWeightF = 0;
        this.allWeight = 0;
        this.allScanWeight = 0;
        this.scanCount = 0;
        this.outCount = outCount;
        this.stutas = false;
    }

    public void addScanCount() {
        scanCount++;
    }

    public double getAllWeightF() {
        return allWeightF;
    }

    public void setAllWeightF(double allWeightF) {
        this.allWeightF = allWeightF;
    }

    public double getAllWeight() {
        return allWeight;
    }

    public void setAllWeight(double allWeight) {
        this.allWeight = allWeight;
    }

    public double getAllScanWeight() {
        return allScanWeight;
    }

    public void setAllScanWeight(double allScanWeight) {
        this.allScanWeight = allScanWeight;
    }

    public int getScanCount() {
        return scanCount;
    }

    public void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    public int getOutCount() {
        return outCount;
    }

    public void setOutCount(int outCount) {
        this.outCount = outCount;
    }

    public boolean isStutas() {
        return stutas;
    }

    public void setStutas(boolean stutas) {
        this.stutas = stutas;
    }
}