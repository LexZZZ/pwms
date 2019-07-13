package ru.lex3.pwms.interfaces;

public abstract class PLCData{
    protected PLCServiceData serviceData;

    public boolean[] bits;

    public int[] ints;
    public float[] floats;
    public abstract void initValues();
    public abstract void initBuffer();

    public PLCData(PLCServiceData serviceData) {
        setPlcServiceData(serviceData);
    }

    public PLCServiceData getPlcServiceData() {
        return serviceData;
    }

    public abstract void setPlcServiceData(PLCServiceData serviceData);
}
