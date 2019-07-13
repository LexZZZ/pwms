package ru.lex3.pwms.interfaces;

public interface PLC {
    /**
     * PLCDataPerformer methods
     *
     * @param plcData
     * @param data
     * @return
     */
    public int writeData(PLCData plcData, byte[] data);

    public int readData(PLCData plcData, byte[] data);


    /**
     * PLCConnector methods
     *
     * @return int - connection result
     */
    int connectTo();

    public void disconnect();

    public boolean isConnected();

    public void setConnectionParameters(PLCConnectionParameters connectionParameters);

    public PLCConnectionParameters getConnectionParameters();
}
