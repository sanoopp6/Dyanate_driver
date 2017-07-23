package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 1/9/17.
 */

public class Order implements Serializable {

    private String tripId;
    private String tripNo;
    private String tripFromAddress;
    private String tripFromLat;
    private String tripFromLng;
    private Boolean tripfromSelf;
    private String tripFromName;
    private String tripFromMob;
    private String tripToAddress;
    private String tripToLat;
    private String tripToLng;
    private Boolean tripToSelf;
    private String tripToName;
    private String tripToMob;
    private String vehicleModel;
    private String vehicleType;
    private String scheduleDate;
    private String scheduleTime;
    private String userName;
    private String userMobile;
    private String tripStatus;
    private String tripFilter;
    private String tripSubject;
    private String tripNotes;
    private String vehicleImage;
    private String tripdId;
    private String tripDNo;
    private String tripDStatus;
    private String tripDDmId;
    private String tripDRate;
    private String tripDIsNegotiable;
    private String tripDDateTime;
    private String tripDFilterName;
    private String dmName;
    private String dmMobNumber;
    private String distanceKm;
    private String distance;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getTripFromAddress() {
        return tripFromAddress;
    }

    public void setTripFromAddress(String tripFromAddress) {
        this.tripFromAddress = tripFromAddress;
    }

    public String getTripFromLat() {
        return tripFromLat;
    }

    public void setTripFromLat(String tripFromLat) {
        this.tripFromLat = tripFromLat;
    }

    public String getTripFromLng() {
        return tripFromLng;
    }

    public void setTripFromLng(String tripFromLng) {
        this.tripFromLng = tripFromLng;
    }

    public Boolean getTripfromSelf() {
        return tripfromSelf;
    }

    public void setTripfromSelf(Boolean tripfromSelf) {
        this.tripfromSelf = tripfromSelf;
    }

    public String getTripFromName() {
        return tripFromName;
    }

    public void setTripFromName(String tripFromName) {
        this.tripFromName = tripFromName;
    }

    public String getTripFromMob() {
        return tripFromMob;
    }

    public void setTripFromMob(String tripFromMob) {
        this.tripFromMob = tripFromMob;
    }

    public String getTripToAddress() {
        return tripToAddress;
    }

    public void setTripToAddress(String tripToAddress) {
        this.tripToAddress = tripToAddress;
    }

    public String getTripToLat() {
        return tripToLat;
    }

    public void setTripToLat(String tripToLat) {
        this.tripToLat = tripToLat;
    }

    public String getTripToLng() {
        return tripToLng;
    }

    public void setTripToLng(String tripToLng) {
        this.tripToLng = tripToLng;
    }

    public Boolean getTripToSelf() {
        return tripToSelf;
    }

    public void setTripToSelf(Boolean tripToSelf) {
        this.tripToSelf = tripToSelf;
    }

    public String getTripToName() {
        return tripToName;
    }

    public void setTripToName(String tripToName) {
        this.tripToName = tripToName;
    }

    public String getTripToMob() {
        return tripToMob;
    }

    public void setTripToMob(String tripToMob) {
        this.tripToMob = tripToMob;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripFilter() {
        return tripFilter;
    }

    public void setTripFilter(String tripFilter) {
        this.tripFilter = tripFilter;
    }

    public String getTripSubject() {
        return tripSubject;
    }

    public void setTripSubject(String tripSubject) {
        this.tripSubject = tripSubject;
    }

    public String getTripNotes() {
        return tripNotes;
    }

    public void setTripNotes(String tripNotes) {
        this.tripNotes = tripNotes;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public String getTripdId() {
        return tripdId;
    }

    public void setTripdId(String tripdId) {
        this.tripdId = tripdId;
    }

    public String getTripDNo() {
        return tripDNo;
    }

    public void setTripDNo(String tripDNo) {
        this.tripDNo = tripDNo;
    }

    public String getTripDStatus() {
        return tripDStatus;
    }

    public void setTripDStatus(String tripDStatus) {
        this.tripDStatus = tripDStatus;
    }

    public String getTripDDmId() {
        return tripDDmId;
    }

    public void setTripDDmId(String tripDDmId) {
        this.tripDDmId = tripDDmId;
    }

    public String getTripDIsNegotiable() {
        return tripDIsNegotiable;
    }

    public void setTripDIsNegotiable(String tripDIsNegotiable) {
        this.tripDIsNegotiable = tripDIsNegotiable;
    }

    public String getTripDRate() {
        return tripDRate;
    }

    public void setTripDRate(String tripDRate) {
        this.tripDRate = tripDRate;
    }

    public String getTripDDateTime() {
        return tripDDateTime;
    }

    public void setTripDDateTime(String tripDDateTime) {
        this.tripDDateTime = tripDDateTime;
    }

    public String getTripDFilterName() {
        return tripDFilterName;
    }

    public void setTripDFilterName(String tripDFilterName) {
        this.tripDFilterName = tripDFilterName;
    }

    public String getDmName() {
        return dmName;
    }

    public void setDmName(String dmName) {
        this.dmName = dmName;
    }

    public String getDmMobNumber() {
        return dmMobNumber;
    }

    public void setDmMobNumber(String dmMobNumber) {
        this.dmMobNumber = dmMobNumber;
    }

    public String getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(String distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
