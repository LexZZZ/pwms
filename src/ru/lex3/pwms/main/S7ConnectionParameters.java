package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.PLCConnectionParameters;

public class S7ConnectionParameters extends PLCConnectionParameters {

    public S7ConnectionParameters(String[] strings, int[] ints, boolean[] bools) {
        this.strings = strings;
        this.ints = ints;
        this.bools = bools;
    }

    public String getAddress() {
        try {
            return strings[0];
        } catch (Exception e) {
            return null;
        }
    }

    public void setIpAddress(String address) {
        try {
            strings[0] = address;
        } catch (Exception e) {
            strings = new String[]{address};
        }
    }

    public int getRack() {
        try {
            return ints[0];
        } catch (Exception e) {
            return 0;
        }
    }

    public void setRack(int rack) {
        try {
            ints[0] = rack;
        } catch (Exception e) {
            ints = new int[]{rack, 0, 0};
        }
    }

    public int getSlot() {
        try {
            return ints[1];
        } catch (Exception e) {
            return 0;
        }
    }

    public void setSlot(int slot) {
        try {
            ints[1] = slot;
        } catch (Exception e) {
            ints = new int[]{0, slot, 0};
        }
    }

    public boolean isAutoConnect() {
        try {
            return bools[0];
        } catch (Exception e) {
            return false;
        }
    }

    public void setAutoConnect(boolean autoConnect) {
        try {
            bools[0] = autoConnect;
        } catch (Exception e) {
            bools = new boolean[]{autoConnect, false};
        }
    }

    public boolean isAsyncConnect() {
        try {
            return bools[1];
        } catch (Exception e) {
            return false;
        }
    }

    public void setAsyncConnect(boolean asyncConnect) {
        try {
            bools[1] = asyncConnect;
        } catch (Exception e) {
            bools = new boolean[]{false, asyncConnect};
        }
    }

    public long getIdleTimeUntilConnect() {
        try {
            return ints[2];
        } catch (Exception e) {
            return 0;
        }
    }

    public void setIdleTimeUntilConnect(int idleTimeUntilConnect) {
        try {
            ints[2] = idleTimeUntilConnect;
        } catch (Exception e) {
            ints = new int[]{0, 0, idleTimeUntilConnect};
        }
    }

}
