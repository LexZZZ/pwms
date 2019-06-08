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

/**
 * @author Davide
 */
public class S7CpuInfo {

    private final int BUF_SIZE = 256;
    protected byte[] buffer = new byte[BUF_SIZE];

    protected void update(byte[] src, int pos) {
        System.arraycopy(src, pos, buffer, 0, BUF_SIZE);
    }

    public String moduleTypeName() {
        return S7.getStringAt(buffer, 172, 32);
    }

    public String serialNumber() {
        return S7.getStringAt(buffer, 138, 24);
    }

    public String asName() {
        return S7.getStringAt(buffer, 2, 24);
    }

    public String copyright() {
        return S7.getStringAt(buffer, 104, 26);
    }

    public String moduleName() {
        return S7.getStringAt(buffer, 36, 24);
    }
}
