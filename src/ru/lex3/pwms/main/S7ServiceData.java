package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.PLC;
import ru.lex3.pwms.interfaces.PLCServiceData;

public class S7ServiceData extends PLCServiceData {

    /**
     *
     * @param data - [0] - S7 area number;
     *               [1] - DB number;
     *               [2] - startRead byte tor read from DB to byte[] buffer;
     *               [3] - quantity bytes tor read from DB to byte[] buffer;
     *               [4] - startRead byte tor tor write to DB from byte[] buffer;
     *               [5] - quantity bytes tor write to DB from byte[] buffer;
     */
    public S7ServiceData(int[] data, byte[] buffer) {
        super(data, buffer);
    }

    public int getDB(){
        return data[1];
    }

    public int startRead(){
        return data[2];
    }

    public int qtyRead(){
        return data[3];
    }
    public int startWrite(){
        return data[4];
    }

    public int qtyWrite(){
        return data[5];
    }
}
