package ru.lex3.pwms.interfaces;

public abstract class PLCServiceData {
    public PLC plc;
    public int[] data;

    public PLCServiceData(PLC plc, int[] serviceData) {
        this.plc = plc;
        this.data = serviceData;
    }
}
