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
public class S7CpInfo {

    public int maxPduLength;
    public int maxConnections;
    public int maxMpiRate;
    public int maxBusRate;

    protected void update(byte[] src, int pos) {
        maxPduLength = S7.getShortAt(src, 2);
        maxConnections = S7.getShortAt(src, 4);
        maxMpiRate = S7.getDIntAt(src, 6);
        maxBusRate = S7.getDIntAt(src, 10);
    }
}
