package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.PLC;
import ru.lex3.pwms.interfaces.PLCData;
import ru.lex3.pwms.interfaces.PLCDataPerformer;
import ru.lex3.pwms.moka7.S7;

public class S7DataPerformer implements PLCDataPerformer {

    public void convertByteToData(PLCData plcData) {
        byte[] buffer = plcData.getPlcServiceData().getBuffer();
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
    }

    public void convertDataToByte(PLCData plcData) {
        byte[] buffer = plcData.getPlcServiceData().getBuffer();
        S7.setDIntAt(buffer, 0, plcData.ints[0]);
        S7.setFloatAt(buffer, 4, plcData.floats[3]);
        S7.setFloatAt(buffer, 8, plcData.floats[4]);
        S7.setFloatAt(buffer, 12, plcData.floats[5]);
        S7.setFloatAt(buffer, 16, plcData.floats[6]);
        S7.setFloatAt(buffer, 20, plcData.floats[7]);
        S7.setFloatAt(buffer, 24, plcData.floats[8]);
    }
}
