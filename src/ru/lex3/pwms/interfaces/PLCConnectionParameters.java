package ru.lex3.pwms.interfaces;

public abstract class PLCConnectionParameters {
    /**
     * strings - parameters for establish connection
     * ints    - additional parameter for connection
     */
    public String[] strings;
    public int[] ints;
    public boolean[] bools;

    public abstract String getIpAddres();

    public abstract void setIpAddress(String ipAddress);

    public abstract boolean isAutoConnect();

    public abstract void setAutoConnect(boolean autoConnect);

    public abstract boolean isAsyncConnect();

    public abstract void setAsyncConnect(boolean asyncConnect);

    public abstract long getIdleTimeUntilConnect();

    public abstract void setIdleTimeUntilConnect(int idleTimeUntilConnect);
}
