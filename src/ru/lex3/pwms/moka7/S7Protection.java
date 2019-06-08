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

// See §33.19 of "System Software for S7-300/400 System and Standard Functions"
public class S7Protection {
    public int schSchal;
    public int schPar;
    public int schRel;
    public int bartSch;
    public int anlSch;

    protected void update(byte[] src) {
        schSchal = S7.getWordAt(src, 2);
        schPar = S7.getWordAt(src, 4);
        schRel = S7.getWordAt(src, 6);
        bartSch = S7.getWordAt(src, 8);
        anlSch = S7.getWordAt(src, 10);
    }
}
