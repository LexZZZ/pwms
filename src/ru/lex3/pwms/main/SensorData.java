package ru.lex3.pwms.main;

public class SensorData {
    public int dbNr = 4;
    public int start = 0;

    public float currentData = 0;
    public float calibratedValue = 0;
    public float lastMeasure = 0;
    public int timeDelayScore = 0;
    public float[] tollerance = {0.3f, 0.3f, 0.3f, 0.3f};
    public float scaleMin = 0;
    public float scaleMax = 20;
    public boolean errMeasure = false;
    public boolean teaching = false;

	public SensorData(int dbNr, int start){
		this.dbNr = dbNr;
		this.start = start;
	}

}
