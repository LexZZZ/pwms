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

import ru.lex3.pwms.interfaces.PLC;
import ru.lex3.pwms.interfaces.PLCConnectionParameters;
import ru.lex3.pwms.interfaces.PLCData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Dave Nardella
 */
public class S7Client implements PLC {
    // WordLength
    private static final byte S7_WL_BYTE = 0x02;
    private static final byte S7_WL_COUNTER = 0x1C;
    private static final byte S7_WL_TIMER = 0x1D;
    // Error Codes
    private static final int ERR_TCP_CONNECTION_FAILED = 0x0001;
    private static final int ERR_TCP_DATA_SEND = 0x0002;
    private static final int ERR_TCP_DATA_RECV = 0x0003;
    private static final int ERR_TCP_DATA_RECV_TOUT = 0x0004;
    private static final int ERR_TCP_CONNECTION_RESET = 0x0005;
    private static final int ERR_ISO_INVALID_PDU = 0x0006;
    private static final int ERR_ISO_CONNECTION_FAILED = 0x0007;
    private static final int ERR_ISO_NEGOTIATING_PDU = 0x0008;
    private static final int ERR_S7_INVALID_PDU = 0x0009;
    private static final int ERR_S7_DATA_READ = 0x000A;
    private static final int ERR_S7_DATA_WRITE = 0x000B;
    private static final int ERR_S7_BUFFER_TOO_SMALL = 0x000C;
    private static final int ERR_S7_FUNCTION_ERROR = 0x000D;
    private static final int ERR_S7_INVALID_PARAMS = 0x000E;

    // Public fields
    private boolean connected = false;
    private int lastError = 0;
    private int recvTimeout = 2000;

    // Privates
    private static final int ISO_TCP = 102; // ISO_TCP Port
    private static final int MIN_PDU_SIZE = 16;
    private static final int DEFAULT_PDU_SIZE_REQUESTED = 480;
    private static final int ISO_H_SIZE = 7; // TPKT+COTP header Size
    private static final int MAX_PDU_SIZE = DEFAULT_PDU_SIZE_REQUESTED + ISO_H_SIZE;


    private Socket tcpSocket;
    private final byte[] PDU = new byte[2048];

    private DataInputStream inStream = null;
    private DataOutputStream outStream = null;

    private String ipAddress;
    private PLCConnectionParameters connectionParameters;

    private byte localTSAP_HI;
    private byte localTSAP_LO;
    private byte remoteTSAP_HI;
    private byte remoteTSAP_LO;
    private byte lastPDUType;

    private short connType = S7.PG;
    private int PDU_LENGTH = 0;

    // Telegrams
    // ISO Connection Request telegram (contains also ISO header and COTP header)
    private static final byte ISO_CR[] = {
            // TPKT (RFC1006 header)
            (byte) 0x03, // RFC 1006 ID (3)
            (byte) 0x00, // Reserved, always 0
            (byte) 0x00, // High part of packet lenght (entire frame, payload and TPDU included)
            (byte) 0x16, // Low part of packet lenght (entire frame, payload and TPDU included)
            // COTP (ISO 8073 header)
            (byte) 0x11, // PDU Size Length
            (byte) 0xE0, // CR - Connection Request ID
            (byte) 0x00, // Dst Reference HI
            (byte) 0x00, // Dst Reference LO
            (byte) 0x00, // Src Reference HI
            (byte) 0x01, // Src Reference LO
            (byte) 0x00, // Class + Options Flags
            (byte) 0xC0, // PDU Max Length ID
            (byte) 0x01, // PDU Max Length HI
            (byte) 0x0A, // PDU Max Length LO
            (byte) 0xC1, // Src TSAP Identifier
            (byte) 0x02, // Src TSAP Length (2 bytes)
            (byte) 0x01, // Src TSAP HI (will be overwritten)
            (byte) 0x00, // Src TSAP LO (will be overwritten)
            (byte) 0xC2, // Dst TSAP Identifier
            (byte) 0x02, // Dst TSAP Length (2 bytes)
            (byte) 0x01, // Dst TSAP HI (will be overwritten)
            (byte) 0x02  // Dst TSAP LO (will be overwritten)
    };

    // S7 PDU Negotiation Telegram (contains also ISO header and COTP header)
    private static final byte S7_PN[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x19,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, // TPKT + COTP (see above for info)
            (byte) 0x32, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0xf0, (byte) 0x00,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x1e // PDU Length Requested = HI-LO 480 bytes
    };

    // S7 Read/Write Request header (contains also ISO header and COTP header)
    private static final byte S7_RW[] = { // 31-35 bytes
            (byte) 0x03, (byte) 0x00,
            (byte) 0x00, (byte) 0x1f,  // Telegram Length (data Size + 31 or 35)
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, // COTP (see above for info)
            (byte) 0x32,             // S7 Protocol ID
            (byte) 0x01,             // Job Type
            (byte) 0x00, (byte) 0x00,  // Redundancy identification
            (byte) 0x05, (byte) 0x00,  // PDU Reference
            (byte) 0x00, (byte) 0x0e,  // Parameters Length
            (byte) 0x00, (byte) 0x00,  // data Length = Size(bytes) + 4
            (byte) 0x04,             // Function 4 Read Var, 5 Write Var
            (byte) 0x01,             // Items count
            (byte) 0x12,             // Var spec.
            (byte) 0x0a,             // Length of remaining bytes
            (byte) 0x10,             // Syntax ID
            S7_WL_BYTE,               // Transport Size
            (byte) 0x00, (byte) 0x00,  // Num Elements
            (byte) 0x00, (byte) 0x00,  // DB Number (if any, else 0)
            (byte) 0x84,             // Area Type
            (byte) 0x00, (byte) 0x00, (byte) 0x00, // Area Offset
            // WR area
            (byte) 0x00,             // Reserved
            (byte) 0x04,             // Transport size
            (byte) 0x00, (byte) 0x00,  // data Length * 8 (if not timer or counter)
    };
    private static final int SIZE_RD = 31;
    private static final int SIZE_WR = 35;

    // S7 Get Block Info Request header (contains also ISO header and COTP header)
    private static final byte S7_BI[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x25,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x05,
            (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x43, (byte) 0x03,
            (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00,
            (byte) 0x08, (byte) 0x30,
            (byte) 0x41, // Block Type
            (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, // ASCII Block Number
            (byte) 0x41
    };

    // SZL First telegram request   
    private static final byte S7_SZL_FIRST[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00,
            (byte) 0x05, (byte) 0x00, // Sequence out
            (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x44, (byte) 0x01,
            (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00,
            (byte) 0x04,
            (byte) 0x00, (byte) 0x00, // ID (29)
            (byte) 0x00, (byte) 0x00  // Index (31)
    };

    // SZL Next telegram request 
    private static final byte S7_SZL_NEXT[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x06,
            (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x08, (byte) 0x12, (byte) 0x44, (byte) 0x01,
            (byte) 0x01, // Sequence
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    // Get Date/Time request
    private static final byte S7_GET_DT[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x1d,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x47, (byte) 0x01,
            (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00,
            (byte) 0x00
    };

    // Set Date/Time command
    private static final byte S7_SET_DT[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x27,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x89,
            (byte) 0x03, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x0e, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x47, (byte) 0x02,
            (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00,
            (byte) 0x0a, (byte) 0x00, (byte) 0x19, // Hi part of Year
            (byte) 0x13, // Lo part of Year
            (byte) 0x12, // Month
            (byte) 0x06, // Day
            (byte) 0x17, // Hour
            (byte) 0x37, // Min
            (byte) 0x13, // Sec
            (byte) 0x00, (byte) 0x01 // ms + Day of week
    };

    // S7 STOP request
    private static final byte S7_STOP[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e,
            (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00,
            (byte) 0x00, (byte) 0x29, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09,
            (byte) 0x50, (byte) 0x5f, (byte) 0x50, (byte) 0x52,
            (byte) 0x4f, (byte) 0x47, (byte) 0x52, (byte) 0x41,
            (byte) 0x4d
    };

    // S7 HOT Start request
    private static final byte S7_HOT_START[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x25,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0c,
            (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x00,
            (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xfd, (byte) 0x00, (byte) 0x00, (byte) 0x09,
            (byte) 0x50, (byte) 0x5f, (byte) 0x50, (byte) 0x52,
            (byte) 0x4f, (byte) 0x47, (byte) 0x52, (byte) 0x41,
            (byte) 0x4d
    };

    // S7 COLD Start request
    private static final byte S7_COLD_START[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x27,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0f,
            (byte) 0x00, (byte) 0x00, (byte) 0x16, (byte) 0x00,
            (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xfd, (byte) 0x00, (byte) 0x02, (byte) 0x43,
            (byte) 0x20, (byte) 0x09, (byte) 0x50, (byte) 0x5f,
            (byte) 0x50, (byte) 0x52, (byte) 0x4f, (byte) 0x47,
            (byte) 0x52, (byte) 0x41, (byte) 0x4d
    };

    // S7 Get PLC Status 
    private static final byte S7_GET_STAT[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x2c,
            (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x44, (byte) 0x01,
            (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00,
            (byte) 0x04, (byte) 0x04, (byte) 0x24, (byte) 0x00,
            (byte) 0x00
    };

    // S7 Set Session Password 
    private static final byte S7_SET_PWD[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x25,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x27,
            (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x45, (byte) 0x01,
            (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00,
            (byte) 0x08,
            // 8 Char Encoded Password
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    // S7 Clear Session Password 
    private static final byte S7_CLR_PWD[] = {
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x1d,
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32,
            (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x29,
            (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x45, (byte) 0x02,
            (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00,
            (byte) 0x00
    };

    public S7Client(PLCConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

    public static String getErrorText(int error) {
        switch (error) {
            case ERR_TCP_CONNECTION_FAILED:
                return "TCP Connection failed.";
            case ERR_TCP_DATA_SEND:
                return "TCP Sending error.";
            case ERR_TCP_DATA_RECV:
                return "TCP Receiving error.";
            case ERR_TCP_DATA_RECV_TOUT:
                return "data Receiving timeout.";
            case ERR_TCP_CONNECTION_RESET:
                return "Connection reset by the peer.";
            case ERR_ISO_INVALID_PDU:
                return "Invalid ISO PDU received.";
            case ERR_ISO_CONNECTION_FAILED:
                return "ISO connection refused by the CPU.";
            case ERR_ISO_NEGOTIATING_PDU:
                return "ISO error negotiating the PDU length.";
            case ERR_S7_INVALID_PDU:
                return "Invalid S7 PDU received.";
            case ERR_S7_DATA_READ:
                return "S7 Error reading data from the CPU.";
            case ERR_S7_DATA_WRITE:
                return "S7 Error writing data to the CPU.";
            case ERR_S7_BUFFER_TOO_SMALL:
                return "The buffer supplied to the function is too small.";
            case ERR_S7_FUNCTION_ERROR:
                return "S7 function refused by the CPU.";
            case ERR_S7_INVALID_PARAMS:
                return "Invalid parameters supplied to the function.";
            default:
                return "Unknown error : 0x" + Integer.toHexString(error);
        }
    }

    private int tcpConnect() {
        SocketAddress sockAddr = new InetSocketAddress(ipAddress, ISO_TCP);
        lastError = 0;
        try {
            tcpSocket = new Socket();
            tcpSocket.connect(sockAddr, 5000);
            tcpSocket.setTcpNoDelay(true);
            inStream = new DataInputStream(tcpSocket.getInputStream());
            outStream = new DataOutputStream(tcpSocket.getOutputStream());
        } catch (IOException e) {
            lastError = ERR_TCP_CONNECTION_FAILED;
        }
        return lastError;
    }

    private int waitForData(int size, int timeout) {
        int cnt = 0;
        lastError = 0;
        int sizeAvail;
        boolean expired = false;
        try {
            sizeAvail = inStream.available();
            while ((sizeAvail < size) && (!expired) && (lastError == 0)) {

                cnt++;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    lastError = ERR_TCP_DATA_RECV_TOUT;
                }
                sizeAvail = inStream.available();
                expired = cnt > timeout;
                // If timeout we clean the buffer
                if (expired && (sizeAvail > 0) && (lastError == 0))
                    inStream.read(PDU, 0, sizeAvail);
            }
        } catch (IOException ex) {
            lastError = ERR_TCP_DATA_RECV_TOUT;
        }
        if (cnt >= timeout) {
            lastError = ERR_TCP_DATA_RECV_TOUT;
        }
        return lastError;
    }

    private int recvPacket(byte[] buffer, int start, int size) {
        int bytesRead = 0;
        lastError = waitForData(size, recvTimeout);
        if (lastError == 0) {
            try {
                bytesRead = inStream.read(buffer, start, size);
            } catch (IOException ex) {
                lastError = ERR_TCP_DATA_RECV;
            }
            if (bytesRead == 0)
                lastError = ERR_TCP_CONNECTION_RESET;
        }
        return lastError;
    }

    private void sendPacket(byte[] buffer, int len) {
        lastError = 0;
        try {
            outStream.write(buffer, 0, len);
            outStream.flush();
        } catch (IOException ex) {
            lastError = ERR_TCP_DATA_SEND;
        }
    }

    private void sendPacket(byte[] buffer) {
        sendPacket(buffer, buffer.length);
    }

    private int recvIsoPacket() {
        Boolean done = false;
        int size = 0;
        while ((lastError == 0) && !done) {
            // Get TPKT (4 bytes)
            recvPacket(PDU, 0, 4);
            if (lastError == 0) {
                size = S7.getWordAt(PDU, 2);
                // Check 0 bytes data Packet (only TPKT+COTP = 7 bytes)
                if (size == ISO_H_SIZE)
                    recvPacket(PDU, 4, 3); // Skip remaining 3 bytes and Done is still false
                else {
                    if ((size > MAX_PDU_SIZE) || (size < MIN_PDU_SIZE))
                        lastError = ERR_ISO_INVALID_PDU;
                    else
                        done = true; // a valid Length !=7 && >16 && <247


                }
            }
        }
        if (lastError == 0) {
            recvPacket(PDU, 4, 3); // Skip remaining 3 COTP bytes
            lastPDUType = PDU[5];   // Stores PDU Type, we need it
            // Receives the S7 Payload
            recvPacket(PDU, 7, size - ISO_H_SIZE);
        }
        if (lastError == 0)
            return size;

        else
            return 0;
    }

    private int isoConnect() {
        int size;
        ISO_CR[16] = localTSAP_HI;
        ISO_CR[17] = localTSAP_LO;
        ISO_CR[20] = remoteTSAP_HI;
        ISO_CR[21] = remoteTSAP_LO;

        // Sends the connection request telegram      
        sendPacket(ISO_CR);
        if (lastError == 0) {
            // Gets the reply (if any)
            size = recvIsoPacket();
            if (lastError == 0) {
                if (size == 22) {
                    if (lastPDUType != (byte) 0xD0) // 0xD0 = CC Connection confirm
                        lastError = ERR_ISO_CONNECTION_FAILED;
                } else
                    lastError = ERR_ISO_INVALID_PDU;
            }
        }
        return lastError;
    }

    private int negotiatePduLength() {
        int length;
        // Set PDU Size Requested
        S7.setWordAt(S7_PN, 23, DEFAULT_PDU_SIZE_REQUESTED);
        // Sends the connection request telegram
        sendPacket(S7_PN);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (lastError == 0) {
                // check S7 Error
                if ((length == 27) && (PDU[17] == 0) && (PDU[18] == 0))  // 20 = size of Negotiate Answer
                {
                    // Get PDU Size Negotiated
                    PDU_LENGTH = S7.getWordAt(PDU, 25);
                    if (PDU_LENGTH > 0)
                        return 0;
                    else
                        lastError = ERR_ISO_NEGOTIATING_PDU;
                } else
                    lastError = ERR_ISO_NEGOTIATING_PDU;
            }
        }
        return lastError;
    }

    public void setConnectionType(short connectionType) {
        if (connectionType == 0)
            connType = S7.PG;
        else
            connType = connectionType;
    }

    public int connect() {
        lastError = 0;
        if (!connected) {
            tcpConnect();
            if (lastError == 0) // First stage : TCP Connection
            {
                isoConnect();
                if (lastError == 0) // Second stage : ISO_TCP (ISO 8073) Connection
                {
                    lastError = negotiatePduLength(); // Third stage : S7 PDU negotiation
                }
            }
        }
        connected = lastError == 0;

        // In case the connection is not completely established (TCP connection + ISO connection + PDU negotiation)
        // we close the socket and its IO streams to revert the object back to pre-connect() state
        if (!connected) {
            if (tcpSocket != null) {
                try {
                    tcpSocket.close();
                } catch (IOException ex) {
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ex) {
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                }
            }
            PDU_LENGTH = 0;
        }

        return lastError;
    }

    /**
     * @param connectionParameters - parameters for establish connection
     * @return
     */
    @Override
    public void setConnectionParameters(PLCConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

    @Override
    public PLCConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    @Override
    public int connectTo() {
        return connectToPLC(connectionParameters.strings[0], connectionParameters.ints[0], connectionParameters.ints[1]);
    }

    public int connectToPLC(String address, int rack, int slot) {

        int remoteTSAP = (connType << 8) + (rack * 0x20) + slot;

        setConnectionParams(address, 0x0100, remoteTSAP);
        return connect();

    }

    public int pduLength() {
        return PDU_LENGTH;
    }

    @Override
    public void disconnect() {
        disconnectFromPLC();
    }

    public void disconnectFromPLC() {
        if (connected) {
            try {
                outStream.close();
                inStream.close();
                tcpSocket.close();
                PDU_LENGTH = 0;
            } catch (IOException ex) {
            }
            connected = false;
        }
    }

    public void setConnectionParams(String address, int localTSAP, int remoteTSAP) {
        int locTSAP = localTSAP & 0x0000FFFF;
        int remTSAP = remoteTSAP & 0x0000FFFF;
        ipAddress = address;
        localTSAP_HI = (byte) (locTSAP >> 8);
        localTSAP_LO = (byte) (locTSAP & 0x00FF);
        remoteTSAP_HI = (byte) (remTSAP >> 8);
        remoteTSAP_LO = (byte) (remTSAP & 0x00FF);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }


    @Override
    public int readData(PLCData plcData, byte[] data) {


        return readArea(plcData.getPlcServiceData().data[0], plcData.getPlcServiceData().data[1], plcData.getPlcServiceData().data[2], plcData.getPlcServiceData().data[3], data);
    }

    public int readArea(int area, int dbNumber, int start, int amount, byte[] data) {

        int address;
        int numElements;
        int maxElements;
        int totElements;
        int sizeRequested;
        int length;
        int offset = 0;
        int wordSize = 1;

        lastError = 0;

        // If we are addressing Timers or counters the element size is 2
        if ((area == S7.S7_AREA_CT) || (area == S7.S7_AREA_TM))
            wordSize = 2;

        maxElements = (PDU_LENGTH - 18) / wordSize; // 18 = Reply telegram header
        totElements = amount;

        while ((totElements > 0) && (lastError == 0)) {
            numElements = totElements;
            if (numElements > maxElements)
                numElements = maxElements;

            sizeRequested = numElements * wordSize;

            // Setup the telegram
            System.arraycopy(S7_RW, 0, PDU, 0, SIZE_RD);
            // Set DB Number
            PDU[27] = (byte) area;
            // Set Area
            if (area == S7.S7_AREA_DB)
                S7.setWordAt(PDU, 25, dbNumber);

            // Adjusts Start and word length
            if ((area == S7.S7_AREA_CT) || (area == S7.S7_AREA_TM)) {
                address = start;
                if (area == S7.S7_AREA_CT)
                    PDU[22] = S7_WL_COUNTER;
                else
                    PDU[22] = S7_WL_TIMER;
            } else
                address = start << 3;

            // Num elements
            S7.setWordAt(PDU, 23, numElements);

            // Address into the PLC (only 3 bytes)           
            PDU[30] = (byte) (address & 0x0FF);
            address = address >> 8;
            PDU[29] = (byte) (address & 0x0FF);
            address = address >> 8;
            PDU[28] = (byte) (address & 0x0FF);

            sendPacket(PDU, SIZE_RD);
            if (lastError == 0) {
                length = recvIsoPacket();
                if (lastError == 0) {
                    if (length >= 25) {
                        if ((length - 25 == sizeRequested) && (PDU[21] == (byte) 0xFF)) {
                            System.arraycopy(PDU, 25, data, offset, sizeRequested);
                            offset += sizeRequested;
                        } else
                            lastError = ERR_S7_DATA_READ;
                    } else
                        lastError = ERR_S7_INVALID_PDU;
                }
            }

            totElements -= numElements;
            start += numElements * wordSize;
        }
        connected = lastError == 0;
        return lastError;
    }

    @Override
    public int writeData(PLCData plcData, byte[] data) {
        return writeArea(plcData.getPlcServiceData().data[0], plcData.getPlcServiceData().data[1], plcData.getPlcServiceData().data[4], plcData.getPlcServiceData().data[5], data);
    }

    public int writeArea(int area, int dbNumber, int start, int amount, byte[] data) {

        int address;
        int numElements;
        int maxElements;
        int totElements;
        int dataSize;
        int isoSize;
        int length;
        int offset = 0;
        int wordSize = 1;

        lastError = 0;

        // If we are addressing Timers or counters the element size is 2
        if ((area == S7.S7_AREA_CT) || (area == S7.S7_AREA_TM))
            wordSize = 2;

        maxElements = (PDU_LENGTH - 35) / wordSize; // 18 = Reply telegram header
        totElements = amount;

        while ((totElements > 0) && (lastError == 0)) {
            numElements = totElements;
            if (numElements > maxElements)
                numElements = maxElements;

            dataSize = numElements * wordSize;
            isoSize = SIZE_WR + dataSize;

            // Setup the telegram
            System.arraycopy(S7_RW, 0, PDU, 0, SIZE_WR);
            // Whole telegram Size
            S7.setWordAt(PDU, 2, isoSize);
            // data Length
            length = dataSize + 4;
            S7.setWordAt(PDU, 15, length);
            // Function
            PDU[17] = (byte) 0x05;
            // Set DB Number
            PDU[27] = (byte) area;
            if (area == S7.S7_AREA_DB)
                S7.setWordAt(PDU, 25, dbNumber);

            // Adjusts Start and word length
            if ((area == S7.S7_AREA_CT) || (area == S7.S7_AREA_TM)) {
                address = start;
                length = dataSize;
                if (area == S7.S7_AREA_CT)
                    PDU[22] = S7_WL_COUNTER;
                else
                    PDU[22] = S7_WL_TIMER;
            } else {
                address = start << 3;
                length = dataSize << 3;
            }
            // Num elements
            S7.setWordAt(PDU, 23, numElements);
            // Address into the PLC
            PDU[30] = (byte) (address & 0x0FF);
            address = address >> 8;
            PDU[29] = (byte) (address & 0x0FF);
            address = address >> 8;
            PDU[28] = (byte) (address & 0x0FF);
            // Length
            S7.setWordAt(PDU, 33, length);

            // Copies the data
            System.arraycopy(data, offset, PDU, 35, dataSize);

            sendPacket(PDU, isoSize);
            if (lastError == 0) {
                length = recvIsoPacket();
                if (lastError == 0) {
                    if (length == 22) {
                        if ((S7.getWordAt(PDU, 17) != 0) || (PDU[21] != (byte) 0xFF))
                            lastError = ERR_S7_DATA_WRITE;
                    } else
                        lastError = ERR_S7_INVALID_PDU;
                }
            }

            offset += dataSize;
            totElements -= numElements;
            start += numElements * wordSize;
        }
        connected = lastError == 0;
        return lastError;
    }

    public int getAgBlockInfo(int blockType, int blockNumber, S7BlockInfo block) {
        int length;
        lastError = 0;
        // Block Type
        S7_BI[30] = (byte) blockType;
        // Block Number
        S7_BI[31] = (byte) ((blockNumber / 10000) + 0x30);
        blockNumber = blockNumber % 10000;
        S7_BI[32] = (byte) ((blockNumber / 1000) + 0x30);
        blockNumber = blockNumber % 1000;
        S7_BI[33] = (byte) ((blockNumber / 100) + 0x30);
        blockNumber = blockNumber % 100;
        S7_BI[34] = (byte) ((blockNumber / 10) + 0x30);
        blockNumber = blockNumber % 10;
        S7_BI[35] = (byte) ((blockNumber / 1) + 0x30);

        sendPacket(S7_BI);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 32) // the minimum expected
            {
                if ((S7.getWordAt(PDU, 27) == 0) && (PDU[29] == (byte) 0xFF)) {
                    block.update(PDU, 42);
                } else {
                    lastError = ERR_S7_FUNCTION_ERROR;

                }
            } else
                lastError = ERR_S7_INVALID_PDU;
        }

        return lastError;
    }

    /**
     * @param dbNumber DB Number
     * @param buffer   Destination buffer
     * @param sizeRead How many bytes were read
     * @return
     */
    public int getDB(int dbNumber, byte[] buffer, IntByRef sizeRead) {
        S7BlockInfo block = new S7BlockInfo();
        // Query the DB Length
        lastError = getAgBlockInfo(S7.BLOCK_DB, dbNumber, block);

        if (lastError == 0) {
            int sizeToRead = block.mc7Size();
            // Checks the room
            if (sizeToRead <= buffer.length) {
                lastError = readArea(S7.S7_AREA_DB, dbNumber, 0, sizeToRead, buffer);
                if (lastError == 0)
                    sizeRead.value = sizeToRead;
            } else
                lastError = ERR_S7_BUFFER_TOO_SMALL;
        }
        return lastError;
    }

    public int readSZL(int id, int index, S7Szl szl) {
        int length;
        int dataSZL;
        int offset = 0;
        boolean done = false;
        boolean first = true;
        byte seqIn = 0x00;
        int seqOut = 0x0000;

        lastError = 0;
        szl.dataSize = 0;
        do {
            if (first) {
                S7.setWordAt(S7_SZL_FIRST, 11, ++seqOut);
                S7.setWordAt(S7_SZL_FIRST, 29, id);
                S7.setWordAt(S7_SZL_FIRST, 31, index);
                sendPacket(S7_SZL_FIRST);
            } else {
                S7.setWordAt(S7_SZL_NEXT, 11, ++seqOut);
                PDU[24] = (byte) seqIn;
                sendPacket(S7_SZL_NEXT);
            }
            if (lastError != 0)
                return lastError;

            length = recvIsoPacket();
            if (lastError == 0) {
                if (first) {
                    if (length > 32) // the minimum expected
                    {
                        if ((S7.getWordAt(PDU, 27) == 0) && (PDU[29] == (byte) 0xFF)) {
                            // Gets Amount of this slice
                            dataSZL = S7.getWordAt(PDU, 31) - 8; // Skips extra params (ID, Index ...)
                            done = PDU[26] == 0x00;
                            seqIn = (byte) PDU[24]; // Slice sequence

                            szl.lenthDR = S7.getWordAt(PDU, 37);
                            szl.nDR = S7.getWordAt(PDU, 39);
                            szl.copy(PDU, 41, offset, dataSZL);
                            offset += dataSZL;
                            szl.dataSize += dataSZL;
                        } else
                            lastError = ERR_S7_FUNCTION_ERROR;
                    } else
                        lastError = ERR_S7_INVALID_PDU;
                } else {
                    if (length > 32) // the minimum expected
                    {
                        if ((S7.getWordAt(PDU, 27) == 0) && (PDU[29] == (byte) 0xFF)) {
                            // Gets Amount of this slice
                            dataSZL = S7.getWordAt(PDU, 31);
                            done = PDU[26] == 0x00;
                            seqIn = (byte) PDU[24]; // Slice sequence
                            szl.copy(PDU, 37, offset, dataSZL);
                            offset += dataSZL;
                            szl.dataSize += dataSZL;
                        } else
                            lastError = ERR_S7_FUNCTION_ERROR;
                    } else
                        lastError = ERR_S7_INVALID_PDU;
                }
            }
            first = false;
        }
        while (!done && (lastError == 0));

        return lastError;
    }


    public int getCpuInfo(S7CpuInfo info) {
        S7Szl szl = new S7Szl(1024);

        lastError = readSZL(0x001C, 0x0000, szl);
        if (lastError == 0) {
            info.update(szl.data, 0);
        }
        return lastError;
    }

    public int getCpInfo(S7CpInfo info) {
        S7Szl szl = new S7Szl(1024);

        lastError = readSZL(0x0131, 0x0001, szl);
        if (lastError == 0) {
            info.update(szl.data, 0);
        }
        return lastError;
    }

    public int getOrderCode(S7OrderCode code) {
        S7Szl szl = new S7Szl(1024);

        lastError = readSZL(0x0011, 0x0000, szl);
        if (lastError == 0) {
            code.update(szl.data, 0, szl.dataSize);
        }
        return lastError;
    }

    public int getPlcDateTime(Date dateTime) {
        int length;

        lastError = 0;
        sendPacket(S7_GET_DT);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 30) // the minimum expected
            {
                if ((S7.getWordAt(PDU, 27) == 0) && (PDU[29] == (byte) 0xFF)) {
                    dateTime = S7.getDateAt(PDU, 34);
                } else
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }

        return lastError;
    }

    public int setPlcDateTime(Date dateTime) {
        int length;

        lastError = 0;
        S7.setDateAt(S7_SET_DT, 31, dateTime);

        sendPacket(S7_SET_DT);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 30) // the minimum expected
            {
                if (S7.getWordAt(PDU, 27) != 0)
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }

        return lastError;
    }

    public int setPlcSystemDateTime() {
        return setPlcDateTime(new Date());
    }

    public int plcStop() {
        int length;

        lastError = 0;
        sendPacket(S7_STOP);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 18) // 18 is the minimum expected
            {
                if (S7.getWordAt(PDU, 17) != 0)
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }
        return lastError;
    }

    public int plcHotStart() {
        int length;

        lastError = 0;
        sendPacket(S7_HOT_START);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 18) // the minimum expected
            {
                if (S7.getWordAt(PDU, 17) != 0)
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }
        return lastError;
    }

    public int plcColdStart() {
        int length;

        lastError = 0;
        sendPacket(S7_COLD_START);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 18) // the minimum expected
            {
                if (S7.getWordAt(PDU, 17) != 0)
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }
        return lastError;
    }

    public int getPlcStatus(IntByRef status) {
        int length;

        lastError = 0;
        sendPacket(S7_GET_STAT);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 30) // the minimum expected
            {
                if (S7.getWordAt(PDU, 27) == 0) {
                    switch (PDU[44]) {
                        case S7.S7_CPU_STATUS_UNKNOWN:
                        case S7.S7_CPU_STATUS_RUN:
                        case S7.S7_CPU_STATUS_STOP:
                            status.value = PDU[44];
                            break;
                        default:
                            // Since RUN status is always 0x08 for all CPUs and CPs, STOP status
                            // sometime can be coded as 0x03 (especially for old cpu...)
                            status.value = S7.S7_CPU_STATUS_STOP;
                    }
                } else
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }
        return lastError;
    }

    public int setSessionPassword(String password) {
        byte[] pwd = {0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20};
        int length;

        lastError = 0;
        // Adjusts the Password length to 8
        if (password.length() > 8)
            password = password.substring(0, 8);
        else {
            while (password.length() < 8)
                password = password + " ";
        }

        try {
            pwd = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            lastError = ERR_S7_INVALID_PARAMS;
        }
        if (lastError == 0) {
            // Encodes the password
            pwd[0] = (byte) (pwd[0] ^ 0x55);
            pwd[1] = (byte) (pwd[1] ^ 0x55);
            for (int c = 2; c < 8; c++) {
                pwd[c] = (byte) (pwd[c] ^ 0x55 ^ pwd[c - 2]);
            }
            System.arraycopy(pwd, 0, S7_SET_PWD, 29, 8);
            // Sends the telegrem
            sendPacket(S7_SET_PWD);
            if (lastError == 0) {
                length = recvIsoPacket();
                if (length > 32) // the minimum expected
                {
                    if (S7.getWordAt(PDU, 27) != 0)
                        lastError = ERR_S7_FUNCTION_ERROR;
                } else
                    lastError = ERR_S7_INVALID_PDU;
            }
        }
        return lastError;
    }

    public int clearSessionPassword() {
        int length;

        lastError = 0;
        sendPacket(S7_CLR_PWD);
        if (lastError == 0) {
            length = recvIsoPacket();
            if (length > 30) // the minimum expected
            {
                if (S7.getWordAt(PDU, 27) != 0)
                    lastError = ERR_S7_FUNCTION_ERROR;
            } else
                lastError = ERR_S7_INVALID_PDU;
        }
        return lastError;
    }

    public int getProtection(S7Protection protection) {
        S7Szl szl = new S7Szl(256);

        lastError = readSZL(0x0232, 0x0004, szl);
        if (lastError == 0) {
            protection.update(szl.data);
        }
        return lastError;
    }

}
