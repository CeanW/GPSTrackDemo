package com.ceanwu.gpstrackdemo;

/**
 * Created by Shengyun Wu on 2/7/2017.
 */

public class TrackDetail {

    private int id;
    private double lat; //latitude
    private double lng; //longitude
    private Track track; //the current track belonged(two-way reference to track)

    public TrackDetail() {
    }

    public TrackDetail(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public TrackDetail(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "TrackDetail{" +
                "id=" + id +
                ", lat=" + lat +
                ", lng=" + lng +
                ", track=" + track +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
