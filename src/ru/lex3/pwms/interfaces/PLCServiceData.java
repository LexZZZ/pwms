package ru.lex3.pwms.interfaces;

public abstract class PLCServiceData {
    private byte[] buffer;

    public int[] data;

    public PLCServiceData(int[] data, byte[] buffer) {
        this.data = data;
        this.buffer = buffer;
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
