package com.fast_prog.dynate.models;

/**
 * Created by sarathk on 3/1/17.
 */

public class RideTemp {

    private Integer id;
    private String isFromself;
    private String FromName;
    private String FromMobile;
    private String fromISO;
    private String fromMobWithoutISO;
    private String isToself;
    private String ToName;
    private String ToMobile;
    private String toISO;
    private String toMobWithoutISO;
    private String date;
    private String hijriDate;
    private String time;
    private String timeString;
    private String isMessage;

    public RideTemp(){   }

    public RideTemp(Integer id, String isFromself, String FromName, String FromMobile, String fromISO, String fromMobWithoutISO,
                    String isToself, String ToName, String ToMobile, String toISO, String toMobWithoutISO,
                    String date, String hijriDate, String time, String timeString, String isMessage){
        this.id = id;
        this.isFromself = isFromself;
        this.FromName = FromName;
        this.FromMobile = FromMobile;
        this.fromISO = fromISO;
        this.fromMobWithoutISO = fromMobWithoutISO;
        this.isToself = isToself;
        this.ToName = ToName;
        this.ToMobile = ToMobile;
        this.toISO = toISO;
        this.toMobWithoutISO = toMobWithoutISO;
        this.date = date;
        this.hijriDate = hijriDate;
        this.time = time;
        this.timeString = timeString;
        this.isMessage = isMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsFromself() {
        return isFromself;
    }

    public void setIsFromself(String isFromself) {
        this.isFromself = isFromself;
    }

    public String getFromName() {
        return FromName;
    }

    public void setFromName(String fromName) {
        FromName = fromName;
    }

    public String getFromMobile() {
        return FromMobile;
    }

    public void setFromMobile(String fromMobile) {
        FromMobile = fromMobile;
    }

    public String getIsToself() {
        return isToself;
    }

    public void setIsToself(String isToself) {
        this.isToself = isToself;
    }

    public String getToName() {
        return ToName;
    }

    public void setToName(String toName) {
        ToName = toName;
    }

    public String getToMobile() {
        return ToMobile;
    }

    public void setToMobile(String toMobile) {
        ToMobile = toMobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHijriDate() {
        return hijriDate;
    }

    public void setHijriDate(String hijriDate) {
        this.hijriDate = hijriDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFromISO() {
        return fromISO;
    }

    public void setFromISO(String fromISO) {
        this.fromISO = fromISO;
    }

    public String getFromMobWithoutISO() {
        return fromMobWithoutISO;
    }

    public void setFromMobWithoutISO(String fromMobWithoutISO) {
        this.fromMobWithoutISO = fromMobWithoutISO;
    }

    public String getToISO() {
        return toISO;
    }

    public void setToISO(String toISO) {
        this.toISO = toISO;
    }

    public String getToMobWithoutISO() {
        return toMobWithoutISO;
    }

    public void setToMobWithoutISO(String toMobWithoutISO) {
        this.toMobWithoutISO = toMobWithoutISO;
    }

    public String getIsMessage() {
        return isMessage;
    }

    public void setIsMessage(String isMessage) {
        this.isMessage = isMessage;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

}
