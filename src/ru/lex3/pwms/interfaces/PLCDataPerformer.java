package ru.lex3.pwms.interfaces;

public interface PLCDataPerformer {

    void convertByteToData(PLCData plcData);

    void convertDataToByte(PLCData plcData);
}
