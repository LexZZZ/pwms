package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PWM implements Runnable {
    private PLC plc;
    private String deviceName;

    private ArrayList<PLCData> sensors;

    private PLCDataPerformer dataPerformer;
    private PLCConnectionSettingsLoader plcSettingsLoader;
    private PLCConnectionSettingsSaver plcSettingsSaver;

    private UICallback uiCallback;

    private int timeout = 50;
    public PWM(String deviceName) {
        this.deviceName = deviceName;
    }

    public synchronized boolean read() {
        if (plc.isConnected()) {
            for (PLCData plcData : sensors)
                dataPerformer.readDataFromPLC(plcData);
            return true;
        } else
            return false;
    }

    public synchronized boolean write() {
        System.out.println("Write begin");
        if (plc.isConnected()) {
            for (PLCData plcData : sensors)
                dataPerformer.writeDataToPLC(plcData);
            System.out.println("Write end");
            return true;
        } else
            System.out.println("Write end");
            return false;
    }

    public synchronized boolean connect() {
        return plc.connectTo() == 0;
    }

    public ArrayList<PLCData> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<PLCData> sensors) {
        this.sensors = sensors;
    }

    public PLC getPlc() {
        return plc;
    }

    public void setPlc(PLC plc) {
        this.plc = plc;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDataPerformer(PLCDataPerformer dataPerformer) {
        this.dataPerformer = dataPerformer;
    }

    public void setUICallback(UICallback uiCallback){
        this.uiCallback = uiCallback;
    }

    private void waitBeforeRepeat(int timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        //System.out.println("EDT? " + SwingUtilities.isEventDispatchThread());
        System.out.println("Mashine " + getDeviceName() + " was started");
        while (!Thread.currentThread().isInterrupted()) {
            while (!plc.isConnected()) {
                if (plc.getConnectionParameters().isAutoConnect()) {
                    System.out.println("Connect to PLC: " + getDeviceName());
                    if (!connect()) {
                        waitBeforeRepeat((int) plc.getConnectionParameters().getIdleTimeUntilConnect());
                    }
                } else
                    waitBeforeRepeat(timeout);
            }
            read();
            uiCallback.refreshValues();
            waitBeforeRepeat(timeout);
        }
    }
}

