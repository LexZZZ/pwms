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
    private boolean sendData = false;

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

            if (plc.isConnected()) {

                // System.out.println(cpuInfo.asName() + ": geting a data");
                System.out.println(": geting a data");
                for (PLCData plcData : sensors) {
                    dataPerformer.readDataFromPLC(plcData);
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Class PWM, method run()");
                        e.printStackTrace();
                    }
                    if (sendData)
                        dataPerformer.writeDataToPLC(plcData);
                }
            } else {
                if (sendData)
                    sendData = false;
                if (plc.getConnectionParameters().isAutoConnect()) {
                    //   System.out.println(cpuInfo.asName() + ": autoconnect");
                    System.out.println(": autoconnect");
                    try {
                        TimeUnit.MILLISECONDS.sleep(plc.getConnectionParameters().getIdleTimeUntilConnect());
                    } catch (InterruptedException e) {
                        System.out.println("Class PWM, method run()");
                        e.printStackTrace();
                    }
                    plc.connectTo();
                }
            }

        }

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

}
