package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 1/4/17.
 */

public class Ride implements Serializable {

    //public String vehicleModelId;
    //public String vehicleModSpinnerId;
    //public String vehicleModelName;
    //public String vehicleTypeId;
    //public String vehicleTypeSpinnerId;
    //public String vehicleSizeSpinnerId;
    //public String vehicleSizeName;
    //public String vehicleTypeName;

    public String vehicleSizeId;
    public String pickUpLatitude;
    public String pickUpLongitude;
    public String pickUpLocation;
    public String dropOffLatitude;
    public String dropOffLongitude;
    public String dropOffLocation;

    public boolean isFromSelf;

    public String fromName;
    public String fromMobile;

    public boolean isToSelf;

    public String toName;
    public String toMobile;
    public String subject;
    public String shipment;
    public String date;
    public String hijriDate;
    public String time;
    public String fromISO;
    public String fromMobWithoutISO;
    public String toISO;
    public String toMobWithoutISO;

    public boolean isMessage;

    public String distanceStr;
}
