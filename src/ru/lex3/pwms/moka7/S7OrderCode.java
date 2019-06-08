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
public class S7OrderCode {

    public int v1;
    public int v2;
    public int v3;
    protected byte[] buffer = new byte[1024];

    protected void update(byte[] src, int pos, int size) {
        System.arraycopy(src, pos, buffer, 0, size);
        v1 = (byte) src[size - 3];
        v2 = (byte) src[size - 2];
        v3 = (byte) src[size - 1];
    }

    public String Code() {
        return S7.getStringAt(buffer, 2, 20);
    }
}
