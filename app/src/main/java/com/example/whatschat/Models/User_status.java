package com.example.whatschat.Models;

import java.util.ArrayList;

public class User_status {
    private  String name, profileImage;
    private long lastUpdated;
    private ArrayList<status> statuses;

    public User_status() {
    }

    public User_status(String name, String profileImage, long lastUpdated, ArrayList<status> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<status> statuses) {
        this.statuses = statuses;
    }
}
