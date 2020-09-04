package com.example.mumu.warehousecheckcar.entity.out;

/**
 * 出库单实体
 *
 * @author chenshengjin
 * @date 2019/9/23
 */
public class OutNo {
    private String out_no;

    @Override
    public String toString() {
        return "OutNo{" +
                "out_no='" + out_no + '\'' +
                '}';
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public OutNo(String out_no) {
        this.out_no = out_no;
    }

    public OutNo() {
    }
}
