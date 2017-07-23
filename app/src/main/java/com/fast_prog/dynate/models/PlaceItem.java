package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 11/22/16.
 */

public class PlaceItem implements Serializable {

    private String plName;
    private String pLatitude;
    private String pLongitude;
    private String pVicinity;

    public String getPlName() {
        return plName;
    }

    public void setPlName(String plName) {
        this.plName = plName;
    }

    public String getpLatitude() {
        return pLatitude;
    }

    public void setpLatitude(String pLatitude) {
        this.pLatitude = pLatitude;
    }

    public String getpLongitude() {
        return pLongitude;
    }

    public void setpLongitude(String pLongitude) {
        this.pLongitude = pLongitude;
    }

    public String getpVicinity() {
        return pVicinity;
    }

    public void setpVicinity(String pVicinity) {
        this.pVicinity = pVicinity;
    }
}
