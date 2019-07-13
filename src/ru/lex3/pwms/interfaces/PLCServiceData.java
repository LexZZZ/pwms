package ru.lex3.pwms.interfaces;

public abstract class PLCServiceData {
    public int[] data;

    public PLCServiceData(int[] data) {
        this.data = data;
    }
}
