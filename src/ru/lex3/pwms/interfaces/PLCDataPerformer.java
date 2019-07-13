package ru.lex3.pwms.interfaces;

public interface PLCDataPerformer {

    void readDataFromPLC(PLCData plcData);

    void writeDataToPLC(PLCData plcData);

    void setPLC(PLC plc);

    PLC getPLC();
}
