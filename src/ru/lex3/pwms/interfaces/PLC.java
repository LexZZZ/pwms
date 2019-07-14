package ru.lex3.pwms.interfaces;

public interface PLC {
    /**
     * PLCDataPerformer methods
     *
     * @param plcData
     * @return
     */
    public int writeData(PLCData plcData);

    public int readData(PLCData plcData);


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
