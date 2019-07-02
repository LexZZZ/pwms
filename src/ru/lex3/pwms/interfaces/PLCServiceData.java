package ru.lex3.pwms.interfaces;

public abstract class PLCServiceData {
    public PLC plc;
    public int[] data;

    public PLCServiceData(PLC plc, int[] data) {
        this.plc = plc;
        this.data = data;
    }
}
