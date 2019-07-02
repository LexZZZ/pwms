package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.PLCData;
import ru.lex3.pwms.interfaces.PLCDataPerformer;
import ru.lex3.pwms.moka7.S7;

public class S7DataPerformer implements PLCDataPerformer {

    @Override
    public void readDataFromPLC(PLCData plcData) {
        byte[] buffer = new byte[128];
        //plcData.serviceData.plc.readData(S7.S7_AREA_DB, plcData.serviceData.data[0], plcData.serviceData.data[1], 41, buffer);
        plcData.serviceData.plc.readData(plcData, buffer);
        convertByteToData(plcData, buffer);
        plcData.init();
    }

    @Override
    public void writeDataToPLC(PLCData plcData) {
        byte[] buffer = convertDataToByte(plcData);
        //plcData.serviceData.plc.writeData(S7.S7_AREA_DB, plcData.serviceData.data[0], plcData.serviceData.data[1], plcData.serviceData.data[2], buffer);
        plcData.serviceData.plc.writeData(plcData, buffer);
    }

    private void convertByteToData(PLCData plcData, byte[] buffer) {
        //  plc.readArea(S7.S7_AREA_DB, sensor.dbNr, sensor.startRead, 41, buffer);
        plcData.floats[0] = S7.getFloatAt(buffer, 0);
        plcData.floats[1] = S7.getFloatAt(buffer, 4);
        plcData.floats[2] = S7.getFloatAt(buffer, 8);
        plcData.ints[0] = S7.getDIntAt(buffer, 12);
        plcData.floats[3] = S7.getFloatAt(buffer, 16);
        plcData.floats[4] = S7.getFloatAt(buffer, 20);
        plcData.floats[5] = S7.getFloatAt(buffer, 24);
        plcData.floats[6] = S7.getFloatAt(buffer, 28);
        plcData.floats[7] = S7.getFloatAt(buffer, 32);
        plcData.floats[8] = S7.getFloatAt(buffer, 36);
        plcData.bits[0] = S7.getBitAt(buffer, 40, 0);
        plcData.bits[1] = S7.getBitAt(buffer, 40, 1);

     /*   System.out.println("Cur data: " + s7Data.currentData);
        System.out.println("CalVal: " + s7Data.calibratedValue);
        System.out.println("Last measure: " + s7Data.lastMeasure);
        System.out.println("TON: " + s7Data.timeDelayScore);
        System.out.println("Scale min: " + s7Data.scaleMin);
        System.out.println("Scale max: " + s7Data.scaleMax);*/
    }

    private byte[] convertDataToByte(PLCData plcData) {
        byte[] buffer = new byte[28];
        S7.setDIntAt(buffer, 0, plcData.dints[0]);
        S7.setFloatAt(buffer, 4, plcData.floats[3]);
        S7.setFloatAt(buffer, 8, plcData.floats[4]);
        S7.setFloatAt(buffer, 12, plcData.floats[5]);
        S7.setFloatAt(buffer, 16, plcData.floats[6]);
        S7.setFloatAt(buffer, 20, plcData.floats[7]);
        S7.setFloatAt(buffer, 24, plcData.floats[8]);

        return buffer;
    }
}
