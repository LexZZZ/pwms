package ru.lex3.pwms.interfaces;

import ru.lex3.pwms.moka7.S7Client;

public interface WorkHProcessHandler{
    public int connectToPLC(String ipAddress, int rack, int slot);

    public int connectToPLC(byte connectionType, String address, int rack, int slot);

    public void disconnectFromPLC();

    public boolean isConnected();

    public void getData();

    public void setData();
}
