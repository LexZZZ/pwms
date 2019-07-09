package ru.lex3.pwms.interfaces;

public abstract class PLCData{
    public PLCServiceData serviceData;

    public boolean[] bits;
    public int[] ints;
    public int[] dints;
    public float[] floats;
    public abstract void initValues();
    public abstract void initBuffer();

    public PLCData(PLCServiceData serviceData) {
        setPlcServiceData(serviceData);
    }

    public abstract void setPlcServiceData(PLCServiceData serviceData);
}
