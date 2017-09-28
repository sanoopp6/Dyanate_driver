package com.fast_prog.dynate.models;

/**
 * Created by sarathk on 3/1/17.
 */

public class RideTemp {

    public Integer id;
    public String isFromSelf;
    public String fromName;
    public String fromMobile;
    public String fromISO;
    public String fromMobWithoutISO;
    public String isToSelf;
    public String toName;
    public String toMobile;
    public String toISO;
    public String toMobWithoutISO;
    public String date;
    public String hijriDate;
    public String time;
    public String timeString;
    public String isMessage;

    public RideTemp(){}

    public RideTemp(Integer id, String isFromSelf, String fromName, String fromMobile, String fromISO, String fromMobWithoutISO,
                    String isToSelf, String toName, String ToMobile, String toISO, String toMobWithoutISO,
                    String date, String hijriDate, String time, String timeString, String isMessage){
        this.id = id;
        this.isFromSelf = isFromSelf;
        this.fromName = fromName;
        this.fromMobile = fromMobile;
        this.fromISO = fromISO;
        this.fromMobWithoutISO = fromMobWithoutISO;
        this.isToSelf = isToSelf;
        this.toName = toName;
        this.toMobile = toMobile;
        this.toISO = toISO;
        this.toMobWithoutISO = toMobWithoutISO;
        this.date = date;
        this.hijriDate = hijriDate;
        this.time = time;
        this.timeString = timeString;
        this.isMessage = isMessage;
    }
}
