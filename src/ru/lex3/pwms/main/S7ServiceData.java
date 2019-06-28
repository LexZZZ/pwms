package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.PLC;
import ru.lex3.pwms.interfaces.PLCServiceData;

public class S7ServiceData extends PLCServiceData {

    /**
     *
     * @param plc - implementation of PLC interface
     * @param serviceData - [0] - DB number;
     *                      [1] - start byte tor write from byte[] buffer
     *                      [2] - quantity bytes tor write from byte[] buffer
     */
    public S7ServiceData(PLC plc, int[] serviceData) {
        super(plc, serviceData);
    }
}
