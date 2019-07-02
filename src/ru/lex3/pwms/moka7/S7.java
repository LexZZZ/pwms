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

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Calendar;

/**
 * @author Davide
 */


// Step 7 Constants and Conversion helper class
public class S7 {
    // S7 ID Area (Area that we want to read/write)
    public static final int S7_AREA_PE = 0x81;
    public static final int S7_AREA_PA = 0x82;
    public static final int S7_AREA_MK = 0x83;
    public static final int S7_AREA_DB = 0x84;
    public static final int S7_AREA_CT = 0x1C;
    public static final int S7_AREA_TM = 0x1D;
    // Connection types
    public static final byte PG = 0x01;
    public static final byte OP = 0x02;
    public static final byte S7_BASIC = 0x03;
    // Block type
    public static final int BLOCK_OB = 0x38;
    public static final int BLOCK_DB = 0x41;
    public static final int BLOCK_SDB = 0x42;
    public static final int BLOCK_FC = 0x43;
    public static final int BLOCK_SFC = 0x44;
    public static final int BLOCK_FB = 0x45;
    public static final int BLOCK_SFB = 0x46;
    // Sub Block Type
    public static final int SUBBLK_OB = 0x08;
    public static final int SUBBLK_DB = 0x0A;
    public static final int SUBBLK_SDB = 0x0B;
    public static final int SUBBLK_FC = 0x0C;
    public static final int SUBBLK_SFC = 0x0D;
    public static final int SUBBLK_FB = 0x0E;
    public static final int SUBBLK_SFB = 0x0F;
    // Block languages
    public static final int BLOCK_LANG_AWL = 0x01;
    public static final int BLOCK_LANG_KOP = 0x02;
    public static final int BLOCK_LANG_FUP = 0x03;
    public static final int BLOCK_LANG_SCL = 0x04;
    public static final int BLOCK_LANG_DB = 0x05;
    public static final int BLOCK_LANG_GRAPH = 0x06;
    // PLC Status
    public static final int S7_CPU_STATUS_UNKNOWN = 0x00;
    public static final int S7_CPU_STATUS_RUN = 0x08;
    public static final int S7_CPU_STATUS_STOP = 0x04;
    // Type Var
    public static final int S7_TYPE_BOOL = 1;
    public static final int S7_TYPE_INT = 1;

    // Returns the bit at Pos.Bit 
    public static boolean getBitAt(byte[] buffer, int pos, int bit) {
        int value = buffer[pos] & 0x0FF;
        byte[] mask = {
                (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08,
                (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80
        };
        if (bit < 0) bit = 0;
        if (bit > 7) bit = 7;

        return (value & mask[bit]) != 0;
    }

    /**
     * Returns a 16 bit unsigned value : from 0 to 65535 (2^16-1)
     *
     * @param buffer
     * @param pos    startRead position
     * @return
     */
    public static int getWordAt(byte[] buffer, int pos) {
        int hi = (buffer[pos] & 0x00FF);
        int lo = (buffer[pos + 1] & 0x00FF);
        return (hi << 8) + lo;
    }

    // Returns a 16 bit signed value : from -32768 to 32767
    public static int getShortAt(byte[] buffer, int pos) {
        int hi = (buffer[pos]);
        int lo = (buffer[pos + 1] & 0x00FF);
        return ((hi << 8) + lo);
    }

    // Returns a 32 bit unsigned value : from 0 to 4294967295 (2^32-1)
    public static long getDWordAt(byte[] buffer, int pos) {
        long result;
        result = (long) (buffer[pos] & 0x0FF);
        result <<= 8;
        result += (long) (buffer[pos + 1] & 0x0FF);
        result <<= 8;
        result += (long) (buffer[pos + 2] & 0x0FF);
        result <<= 8;
        result += (long) (buffer[pos + 3] & 0x0FF);
        return result;
    }

    // Returns a 32 bit signed value : from 0 to 4294967295 (2^32-1)
    public static int getDIntAt(byte[] buffer, int pos) {
        int result;
        result = buffer[pos];
        result <<= 8;
        result += (buffer[pos + 1] & 0x0FF);
        result <<= 8;
        result += (buffer[pos + 2] & 0x0FF);
        result <<= 8;
        result += (buffer[pos + 3] & 0x0FF);
        return result;
    }

    // Returns a 32 bit floating point
    public static float getFloatAt(byte[] buffer, int pos) {
        int intFloat = getDIntAt(buffer, pos);
        return Float.intBitsToFloat(intFloat);
    }

    // Returns an ASCII string
    public static String getStringAt(byte[] buffer, int pos, int maxLen) {
        byte[] strBuffer = new byte[maxLen];
        System.arraycopy(buffer, pos, strBuffer, 0, maxLen);
        String s;
        try {
            s = new String(strBuffer, "UTF-8"); // the charset is UTF-8
        } catch (UnsupportedEncodingException ex) {
            s = "";
        }
        return s;
    }

    public static String getPrintableStringAt(byte[] buffer, int pos, int maxLen) {
        byte[] strBuffer = new byte[maxLen];
        System.arraycopy(buffer, pos, strBuffer, 0, maxLen);
        for (int c = 0; c < maxLen; c++) {
            if ((strBuffer[c] < 31) || (strBuffer[c] > 126))
                strBuffer[c] = 46; // '.'
        }
        String s;
        try {
            s = new String(strBuffer, "UTF-8"); // the charset is UTF-8
        } catch (UnsupportedEncodingException ex) {
            s = "";
        }
        return s;
    }

    public static Date getDateAt(byte[] buffer, int pos) {
        int year, month, day, hour, min, sec;
        Calendar S7Date = Calendar.getInstance();

        year = S7.bCDtoByte(buffer[pos]);
        if (year < 90)
            year += 2000;
        else
            year += 1900;

        month = S7.bCDtoByte(buffer[pos + 1]) - 1;
        day = S7.bCDtoByte(buffer[pos + 2]);
        hour = S7.bCDtoByte(buffer[pos + 3]);
        min = S7.bCDtoByte(buffer[pos + 4]);
        sec = S7.bCDtoByte(buffer[pos + 5]);

        S7Date.set(year, month, day, hour, min, sec);

        return S7Date.getTime();
    }

    public static void setBitAt(byte[] buffer, int pos, int bit, boolean value) {
        byte[] mask = {
                (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08,
                (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80
        };
        if (bit < 0) bit = 0;
        if (bit > 7) bit = 7;

        if (value)
            buffer[pos] = (byte) (buffer[pos] | mask[bit]);
        else
            buffer[pos] = (byte) (buffer[pos] & ~mask[bit]);
    }

    public static void setWordAt(byte[] buffer, int pos, int value) {
        int word = value & 0x0FFFF;
        buffer[pos] = (byte) (word >> 8);
        buffer[pos + 1] = (byte) (word & 0x00FF);
    }

    public static void setShortAt(byte[] buffer, int pos, int value) {
        buffer[pos] = (byte) (value >> 8);
        buffer[pos + 1] = (byte) (value & 0x00FF);
    }

    public static void setDWordAt(byte[] buffer, int pos, long value) {
        long dWord = value & 0x0FFFFFFFF;
        buffer[pos + 3] = (byte) (dWord & 0xFF);
        buffer[pos + 2] = (byte) ((dWord >> 8) & 0xFF);
        buffer[pos + 1] = (byte) ((dWord >> 16) & 0xFF);
        buffer[pos] = (byte) ((dWord >> 24) & 0xFF);
    }

    public static void setDIntAt(byte[] buffer, int pos, int value) {
        buffer[pos + 3] = (byte) (value & 0xFF);
        buffer[pos + 2] = (byte) ((value >> 8) & 0xFF);
        buffer[pos + 1] = (byte) ((value >> 16) & 0xFF);
        buffer[pos] = (byte) ((value >> 24) & 0xFF);
    }

    public static void setFloatAt(byte[] buffer, int pos, float value) {
        int dInt = Float.floatToIntBits(value);
        setDIntAt(buffer, pos, dInt);
    }

    public static void setDateAt(byte[] buffer, int pos, Date dateTime) {
        int year, month, day, hour, min, sec, dow;
        Calendar s7Date = Calendar.getInstance();
        s7Date.setTime(dateTime);

        year = s7Date.get(Calendar.YEAR);
        month = s7Date.get(Calendar.MONTH) + 1;
        day = s7Date.get(Calendar.DAY_OF_MONTH);
        hour = s7Date.get(Calendar.HOUR_OF_DAY);
        min = s7Date.get(Calendar.MINUTE);
        sec = s7Date.get(Calendar.SECOND);
        dow = s7Date.get(Calendar.DAY_OF_WEEK);

        if (year > 1999)
            year -= 2000;

        buffer[pos] = byteToBCD(year);
        buffer[pos + 1] = byteToBCD(month);
        buffer[pos + 2] = byteToBCD(day);
        buffer[pos + 3] = byteToBCD(hour);
        buffer[pos + 4] = byteToBCD(min);
        buffer[pos + 5] = byteToBCD(sec);
        buffer[pos + 6] = 0;
        buffer[pos + 7] = byteToBCD(dow);
    }

    public static int bCDtoByte(byte b) {
        return ((b >> 4) * 10) + (b & 0x0F);
    }

    public static byte byteToBCD(int value) {
        return (byte) (((value / 10) << 4) | (value % 10));
    }

}
