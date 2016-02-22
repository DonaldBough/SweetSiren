package edu.purdue.dbough.sweetsignal;

/**
 * Created by donaldbough on 2/20/16.
 */
public class SugarEntry {
    String sugarLevel;
    String time;
    String milliTime;
    String date;

    public SugarEntry(String sugarLevel, String time, String date) {
        this.sugarLevel = sugarLevel;
        this.time = time;
        this.date = date;
    }

    public String getSugarLevel(){return sugarLevel;}
    public String getTime(){return time;}
    public String getDate(){return date;}

}
