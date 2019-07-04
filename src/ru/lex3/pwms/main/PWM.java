package ru.lex3.pwms.main;

import ru.lex3.pwms.interfaces.*;
import ru.lex3.pwms.moka7.S7Client;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PWM implements Runnable {
    private PLC plc;

    private ArrayList<PLCData> sensors;
    private PLCDataPerformer dataPerformer;

    private PLCConnectionSettingsLoader plcSettingsLoader;

    private PLCConnectionSettingsSaver plcSettingsSaver;


    public synchronized boolean read() {
        if (plc.isConnected()) {
            for (PLCData plcData : sensors)
                dataPerformer.readDataFromPLC(plcData);
            return true;
        } else
            return false;
    }

    public synchronized boolean write() {
        if (plc.isConnected()) {
            for (PLCData plcData : sensors)
                dataPerformer.writeDataToPLC(plcData);
            return true;
        } else
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

    public void setDataPerformer(PLCDataPerformer dataPerformer) {
        this.dataPerformer = dataPerformer;
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
        while (true) {
            if (!Thread.currentThread().isInterrupted()) {
                if (!plc.isConnected() && plc.getConnectionParameters().isAutoConnect()) {
                    if (!connect()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(plc.getConnectionParameters().getIdleTimeUntilConnect());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Trying to connect");
                }

                if (plc.isConnected())
                    if (read())
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
            }
        }
    }
}
