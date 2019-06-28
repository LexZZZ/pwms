package ru.lex3.pwms.interfaces;

public interface PLCDataPerformer {

    public void readDataFromPLC(PLCData plcData);

    public void writeDataToPLC(PLCData plcData);
}
