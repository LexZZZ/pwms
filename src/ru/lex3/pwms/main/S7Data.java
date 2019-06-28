package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.PLCData;
import ru.lex3.pwms.interfaces.PLCServiceData;

public class S7Data extends PLCData {

    public float currentData;
    public float calibratedValue;
    public float lastMeasure;
    public int timeDelayScore;
    public float[] tollerance = {0.3f, 0.3f, 0.3f, 0.3f};
    public float scaleMin = 0;
    public float scaleMax = 20;
    public boolean errMeasure = false;
    public boolean teaching = false;

    public S7Data(PLCServiceData serviceData) {
        super(serviceData);
        bits = new boolean[32];
        ints = new int[8];
        floats = new float[12];
        init();
    }

    @Override
    public void init() {
        errMeasure = bits[0];
        teaching = bits[1];

        timeDelayScore = ints[0];

        currentData = floats[0];
        calibratedValue = floats[1];
        lastMeasure = floats[2];
        tollerance[0] = floats[3];
        tollerance[1] = floats[4];
        tollerance[2] = floats[5];
        tollerance[3] = floats[6];
        scaleMin = floats[7];
        scaleMax = floats[8];
    }

    @Override
    public void setPlcServiceData(PLCServiceData serviceData) {
        this.serviceData = serviceData;
    }
}
