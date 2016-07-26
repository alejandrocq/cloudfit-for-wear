package com.alejandro_castilla.cloudfitforwear.data;

import android.location.Location;

/**
 * Created by alejandrocq on 26/07/16.
 */
public class GPSLocation {
    private Location location;
    private long time;
    private long timeStamp;

    public GPSLocation(Location l, long time, long timeStamp) {
        this.location = l;
        this.time = time;
        this.timeStamp = timeStamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
