package ru.lex3.pwms.interfaces;

public interface ConfigManager{
    public void loadSettingsFrom(String ipAddress, int rack, int slot);

    public int connectToPLC(byte connectionType, String address, int rack, int slot);

    public void disconnectFromPLC();

    public boolean isConnected();

    public void getData();

    public void setData();
}
