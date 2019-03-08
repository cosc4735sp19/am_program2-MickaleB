package com.mickale.pindropdistance;

import android.graphics.Bitmap;

public class MarkerPhoto {

    private String markerID;
    private Bitmap markerPhoto;

    public MarkerPhoto(String markerID, Bitmap markerPhoto){
        this.markerID = markerID;
        this.markerPhoto = markerPhoto;
    }

    public String getMarkerID() {
        return markerID;
    }

    public Bitmap getMarkerPhoto() {
        return markerPhoto;
    }

}
