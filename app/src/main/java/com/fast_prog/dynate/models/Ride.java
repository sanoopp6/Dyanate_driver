package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 1/4/17.
 */

public class Ride implements Serializable {

    String vehicleModelId;
    String vehicleModSpinnerId;
    String vehicleModelName;
    String vehicleTypeId;
    String vehicleTypeSpinnerId;
    String vehicleSizeId;
    String vehicleSizeSpinnerId;
    String vehicleSizeName;
    String vehicleTypeName;
    String pickUpLatitude;
    String pickUpLongitude;
    String pickUpLocation;
    String dropOffLatitude;
    String dropOffLongitude;
    String dropOffLocation;
    Boolean isFromself;
    String FromName;
    String FromMobile;
    Boolean isToself;
    String ToName;
    String ToMobile;
    String subject;
    String shipment;
    String date;
    String hijriDate;
    String time;
    String fromISO;
    String fromMobWithoutISO;
    String toISO;
    String toMobWithoutISO;
    Boolean isMessage;
    String distanceStr;

    public String getVehicleSizeId() {
        return vehicleSizeId;
    }

    public void setVehicleSizeId(String vehicleSizeId) {
        this.vehicleSizeId = vehicleSizeId;
    }

    public String getVehicleSizeSpinnerId() {
        return vehicleSizeSpinnerId;
    }

    public void setVehicleSizeSpinnerId(String vehicleSizeSpinnerId) {
        this.vehicleSizeSpinnerId = vehicleSizeSpinnerId;
    }

    public String getVehicleSizeName() {
        return vehicleSizeName;
    }

    public void setVehicleSizeName(String vehicleSizeName) {
        this.vehicleSizeName = vehicleSizeName;
    }

    public String getVehicleModelId() {
        return vehicleModelId;
    }

    public void setVehicleModelId(String vehicleModelId) {
        this.vehicleModelId = vehicleModelId;
    }

    public String getVehicleModSpinnerId() {
        return vehicleModSpinnerId;
    }

    public void setVehicleModSpinnerId(String vehicleModSpinnerId) {
        this.vehicleModSpinnerId = vehicleModSpinnerId;
    }

    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public void setVehicleModelName(String vehicleModelName) {
        this.vehicleModelName = vehicleModelName;
    }

    public String getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getVehicleTypeSpinnerId() {
        return vehicleTypeSpinnerId;
    }

    public void setVehicleTypeSpinnerId(String vehicleTypeSpinnerId) {
        this.vehicleTypeSpinnerId = vehicleTypeSpinnerId;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public String getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(String pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public String getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(String pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDropOffLatitude() {
        return dropOffLatitude;
    }

    public void setDropOffLatitude(String dropOffLatitude) {
        this.dropOffLatitude = dropOffLatitude;
    }

    public String getDropOffLongitude() {
        return dropOffLongitude;
    }

    public void setDropOffLongitude(String dropOffLongitude) {
        this.dropOffLongitude = dropOffLongitude;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public Boolean getFromself() {
        return isFromself;
    }

    public void setFromself(Boolean fromself) {
        isFromself = fromself;
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

    public Boolean getToself() {
        return isToself;
    }

    public void setToself(Boolean toself) {
        isToself = toself;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
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

    public String getToISO() {
        return toISO;
    }

    public void setToISO(String toISO) {
        this.toISO = toISO;
    }

    public String getFromMobWithoutISO() {
        return fromMobWithoutISO;
    }

    public void setFromMobWithoutISO(String fromMobWithoutISO) {
        this.fromMobWithoutISO = fromMobWithoutISO;
    }

    public String getToMobWithoutISO() {
        return toMobWithoutISO;
    }

    public void setToMobWithoutISO(String toMobWithoutISO) {
        this.toMobWithoutISO = toMobWithoutISO;
    }

    public Boolean getMessage() {
        return isMessage;
    }

    public void setMessage(Boolean message) {
        isMessage = message;
    }

    public String getDistanceStr() {
        return distanceStr;
    }

    public void setDistanceStr(String distanceStr) {
        this.distanceStr = distanceStr;
    }
}
