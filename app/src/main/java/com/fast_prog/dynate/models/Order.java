package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 1/9/17.
 */

public class Order implements Serializable {

    public String tripId;
    public String tripNo;
    public String tripFromAddress;
    public String tripFromLat;
    public String tripFromLng;

    public boolean tripFromSelf;

    public String tripFromName;
    public String tripFromMob;
    public String tripToAddress;
    public String tripToLat;
    public String tripToLng;

    public boolean tripToSelf;

    public String tripToName;
    public String tripToMob;
    public String vehicleModel;
    public String vehicleType;
    public String vehicleImage;
    public String scheduleDate;
    public String scheduleTime;
    public String userName;
    public String userMobile;
    public String tripStatus;
    public String tripFilter;
    public String tripSubject;
    public String tripNotes;
    public String tripDId;
    public String tripDNo;
    public String tripDStatus;
    public String tripDDmId;
    public String tripDRate;
    public String tripDIsNegotiable;
    public String tripDDateTime;
    public String tripDFilterName;
    public String dmName;
    public String dmMobNumber;
    public String distanceKm;
    public String distance;
}
