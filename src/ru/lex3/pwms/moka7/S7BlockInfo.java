/*=============================================================================|
|  PROJECT moka7                                                         1.0.2 |
|==============================================================================|
|  copyright (C) 2013, 2016 Davide Nardella                                    |
|  All rights reserved.                                                        |
|==============================================================================|
|  SNAP7 is free software: you can redistribute it and/or modify               |
|  it under the terms of the Lesser GNU General Public License as published by |
|  the Free Software Foundation, either version 3 of the License, or under     |
|  EPL Eclipse Public License 1.0.                                             |
|                                                                              |
|  This means that you have to chose in advance which take before you import   |
|  the library into your project.                                              |
|                                                                              |
|  SNAP7 is distributed in the hope that it will be useful,                    |
|  but WITHOUT ANY WARRANTY; without even the implied warranty of              |
|  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you    |
|  decide to adopt.                                                            |
|                                                                              |
|=============================================================================*/
package ru.lex3.pwms.moka7;

import java.util.Date;

/**
 * @author Davide
 */
public class S7BlockInfo {

    private final int BUF_SIZE = 96;
    // MilliSeconds between 1970/1/1 (Java time base) and 1984/1/1 (Siemens base)
    private final long DELTA_MILLI_SECS = 441763200000L;
    protected byte[] buffer = new byte[BUF_SIZE];

    protected void update(byte[] src, int pos) {
        System.arraycopy(src, pos, buffer, 0, BUF_SIZE);
    }

    public int blkType() {
        return buffer[2];
    }

    public int blkNumber() {
        return S7.getWordAt(buffer, 3);
    }

    public int blkLang() {
        return buffer[1];
    }

    public int blkFlags() {
        return buffer[0];
    }

    // The real size in bytes
    public int mc7Size() {
        return S7.getWordAt(buffer, 31);
    }

    public int loadSize() {
        return S7.getDIntAt(buffer, 5);
    }

    public int localData() {
        return S7.getWordAt(buffer, 29);
    }

    public int sbbLength() {
        return S7.getWordAt(buffer, 25);
    }

    public int checksum() {
        return S7.getWordAt(buffer, 59);
    }

    public int version() {
        return buffer[57];
    }

    public Date codeDate() {
        long blockDate = ((long) S7.getWordAt(buffer, 17)) * 86400000L + DELTA_MILLI_SECS;
        return new Date(blockDate);
    }

    public Date intfDate() {
        long blockDate = ((long) S7.getWordAt(buffer, 23)) * 86400000L + DELTA_MILLI_SECS;
        return new Date(blockDate);
    }

    public String author() {
        return S7.getStringAt(buffer, 33, 8);
    }

    public String family() {
        return S7.getStringAt(buffer, 41, 8);
    }

    public String header() {
        return S7.getStringAt(buffer, 49, 8);
    }

}
