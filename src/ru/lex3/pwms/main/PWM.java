package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.WorkHProcessHandler;
import ru.lex3.pwms.moka7.S7;
import ru.lex3.pwms.moka7.S7Client;
import ru.lex3.pwms.moka7.S7CpuInfo;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PWM implements WorkHProcessHandler, Runnable {
    private ArrayList<SensorData> sensors = new ArrayList<>();
    private S7Client plc = new S7Client();
    private S7CpuInfo cpuInfo = new S7CpuInfo();
    private static int i;
    private int j;

    private String ipAddress;
    private int rack;
    private int slot;
    private boolean asyncConnect = true;
    private boolean autoConnect = false;
    public boolean sendData = false;

    private long idleTimeUntilConnect = 3000;

    /**
     * Create object by a quantity
     * measurement sensors of setup
     *
     * @param qtySensors quantity sensors on PWM
     */
    public PWM(int qtySensors) {
        for (int i = 0; i < qtySensors; i++) {
            sensors.add(new SensorData());
        }
        j = i++;
    }

    @Override
    public int connectToPLC(String ipAddress, int rack, int slot) {
        return connectToPLC(S7.PG, ipAddress, rack, slot);
    }

    @Override
    public int connectToPLC(byte connectionType, String ipAddress, int rack, int slot) {
        plc.setConnectionType(connectionType);
        System.out.println(cpuInfo.asName() + ": connecting to " + ipAddress + ", " + rack + ", " + slot);
        int result = plc.connectTo(ipAddress, rack, slot);
        if (result == 0) {
            System.out.println("connected to   : " + ipAddress + " (Rack=" + rack + ", Slot=" + slot + ")");
            System.out.println("PDU ne  gotiated : " + plc.pduLength() + " bytes");
        }
        return result;
    }

    @Override
    public void disconnectFromPLC() {
        System.out.println(cpuInfo.asName() + j + ": disconnecting");
        plc.disconnect();
    }

    @Override
    public boolean isConnected() {
        return plc.connected;
    }

    @Override
    public void getData() {
        byte[] buffer = new byte[34];

        for (SensorData sensor : sensors) {
            plc.readArea(S7.S7_AREA_DB, sensor.dbNr, sensor.start, 34, buffer);
            sensor.currentData = S7.getFloatAt(buffer, 0);
            sensor.lastMeasure = S7.getFloatAt(buffer, 4);
            sensor.scaleMax = S7.getFloatAt(buffer, 8);
            sensor.scaleMin = S7.getFloatAt(buffer, 12);
            for (int i = 0; i < sensor.tollerance.length; i++)
                sensor.tollerance[i] = S7.getFloatAt(buffer, i * 4 + 16);
            sensor.errMeasure = S7.getBitAt(buffer, 33, 0);
            sensor.teaching = S7.getBitAt(buffer, 33, 1);
        }
    }

    @Override
    public void setData() {
        byte[] buffer = new byte[16];

        for (SensorData sensor : sensors) {
            S7.setFloatAt(buffer, 8, sensor.scaleMax);
            S7.setFloatAt(buffer, 12, sensor.scaleMax);
            for (int i = 0; i < sensor.tollerance.length; i++)
                S7.setFloatAt(buffer, 16 + i * 4, sensor.tollerance[i]);
            plc.writeArea(S7.S7_AREA_DB, sensor.dbNr, sensor.start + 8, 16, buffer);
        }
    }

    public ArrayList<SensorData> getSensors() {
        return sensors;
    }

    public S7Client getPlc() {
        return plc;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isConnected()) {
                System.out.println(cpuInfo.asName() + ": geting a data");
                getData();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Class PWM, method run()");
                    e.printStackTrace();
                }
                if (sendData)
                    setData();
            } else {
                if (sendData)
                    sendData = false;
                if (autoConnect) {
                    System.out.println(cpuInfo.asName() + ": autoconnect");
                    try {
                        TimeUnit.MILLISECONDS.sleep(idleTimeUntilConnect);
                    } catch (InterruptedException e) {
                        System.out.println("Class PWM, method run()");
                        e.printStackTrace();
                    }
                    connectToPLC(ipAddress, rack, slot);
                }
            }

        }

    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getRack() {
        return rack;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isAsyncConnect() {
        return asyncConnect;
    }

    public void setAsyncConnect(boolean asyncConnect) {
        this.asyncConnect = asyncConnect;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public long getIdleTimeUntilConnect() {
        return idleTimeUntilConnect;
    }

    public void setIdleTimeUntilConnect(long idleTimeUntilConnect) {
        this.idleTimeUntilConnect = idleTimeUntilConnect;
    }
}
