package com.example.mumu.warehousecheckcar.entity;

public class EventBusMsg {
    private int status;
    private Object[] obj1;

    public EventBusMsg(int status, Object... obj1) {
        this.status = status;
        this.obj1 = obj1;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getPositionObj(int position) {
        if (position<obj1.length)
        return obj1[position];
        else
            throw new ArrayIndexOutOfBoundsException();
    }


}
