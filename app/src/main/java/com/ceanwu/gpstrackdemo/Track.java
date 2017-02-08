package com.ceanwu.gpstrackdemo;

import java.util.ArrayList;

/**
 * Created by Shengyun Wu on 2/7/2017.
 */

public class Track {

    private int id;
    private String track_name;
    private String create_date;
    private String start_loc;
    private String end_loc;
    private ArrayList<TrackDetail> trackDetails;

    public Track() {
    }

    public Track(String track_name, String create_date, String start_loc, String end_loc) {
        this.track_name = track_name;
        this.create_date = create_date;
        this.start_loc = start_loc;
        this.end_loc = end_loc;
    }

    public Track(int id, String track_name, String create_date, String start_loc, String end_loc) {
        this.id = id;
        this.track_name = track_name;
        this.create_date = create_date;
        this.start_loc = start_loc;
        this.end_loc = end_loc;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", track_name='" + track_name + '\'' +
                ", create_date='" + create_date + '\'' +
                ", start_loc='" + start_loc + '\'' +
                ", end_loc='" + end_loc + '\'' +
                ", trackDetails=" + trackDetails +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrack_name() {
        return track_name;
    }

    public void setTrack_name(String track_name) {
        this.track_name = track_name;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getStart_loc() {
        return start_loc;
    }

    public void setStart_loc(String start_loc) {
        this.start_loc = start_loc;
    }

    public String getEnd_loc() {
        return end_loc;
    }

    public void setEnd_loc(String end_loc) {
        this.end_loc = end_loc;
    }

    public ArrayList<TrackDetail> getTrackDetails() {
        return trackDetails;
    }

    public void setTrackDetails(ArrayList<TrackDetail> trackDetails) {
        this.trackDetails = trackDetails;
    }
}
