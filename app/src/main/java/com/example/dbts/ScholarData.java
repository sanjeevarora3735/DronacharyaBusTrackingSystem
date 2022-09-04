package com.example.dbts;

import com.google.firebase.Timestamp;

public class ScholarData {

    private double PointLatitude, PointLongitude,SharreableLocation_Lat,SharreableLocation_Long;
    private String LocationName, Name, Branch;
    private int Route, Graduation, Mobile, Roll, Semester, SubscriptionFees;
    private boolean Subscribed, Super;
    private Timestamp SubsscriptionStarts, SubssciptionExpires;

    public ScholarData(double pointLatitude, double pointLongitude, double sharreableLocation_Lat, double sharreableLocation_Long, String locationName, String name, String branch, int route, int graduation, int mobile, int roll, int semester, int subscriptionFees, boolean subscribed, boolean aSuper, Timestamp subsscriptionStarts, Timestamp subssciptionExpires) {
        PointLatitude = pointLatitude;
        PointLongitude = pointLongitude;
        SharreableLocation_Lat = sharreableLocation_Lat;
        SharreableLocation_Long = sharreableLocation_Long;
        LocationName = locationName;
        Name = name;
        Branch = branch;
        Route = route;
        Graduation = graduation;
        Mobile = mobile;
        Roll = roll;
        Semester = semester;
        SubscriptionFees = subscriptionFees;
        Subscribed = subscribed;
        Super = aSuper;
        SubsscriptionStarts = subsscriptionStarts;
        SubssciptionExpires = subssciptionExpires;
    }

    public ScholarData() {
    }

    public double getPointLatitude() {
        return PointLatitude;
    }

    public void setPointLatitude(double pointLatitude) {
        PointLatitude = pointLatitude;
    }

    public double getPointLongitude() {
        return PointLongitude;
    }

    public void setPointLongitude(double pointLongitude) {
        PointLongitude = pointLongitude;
    }

    public double getSharreableLocation_Lat() {
        return SharreableLocation_Lat;
    }

    public void setSharreableLocation_Lat(double sharreableLocation_Lat) {
        SharreableLocation_Lat = sharreableLocation_Lat;
    }

    public double getSharreableLocation_Long() {
        return SharreableLocation_Long;
    }

    public void setSharreableLocation_Long(double sharreableLocation_Long) {
        SharreableLocation_Long = sharreableLocation_Long;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public int getRoute() {
        return Route;
    }

    public void setRoute(int route) {
        Route = route;
    }

    public int getGraduation() {
        return Graduation;
    }

    public void setGraduation(int graduation) {
        Graduation = graduation;
    }

    public int getMobile() {
        return Mobile;
    }

    public void setMobile(int mobile) {
        Mobile = mobile;
    }

    public int getRoll() {
        return Roll;
    }

    public void setRoll(int roll) {
        Roll = roll;
    }

    public int getSemester() {
        return Semester;
    }

    public void setSemester(int semester) {
        Semester = semester;
    }

    public int getSubscriptionFees() {
        return SubscriptionFees;
    }

    public void setSubscriptionFees(int subscriptionFees) {
        SubscriptionFees = subscriptionFees;
    }

    public boolean isSubscribed() {
        return Subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        Subscribed = subscribed;
    }

    public boolean isSuper() {
        return Super;
    }

    public void setSuper(boolean aSuper) {
        Super = aSuper;
    }

    public Timestamp getSubsscriptionStarts() {
        return SubsscriptionStarts;
    }

    public void setSubsscriptionStarts(Timestamp subsscriptionStarts) {
        SubsscriptionStarts = subsscriptionStarts;
    }

    public Timestamp getSubssciptionExpires() {
        return SubssciptionExpires;
    }

    public void setSubssciptionExpires(Timestamp subssciptionExpires) {
        SubssciptionExpires = subssciptionExpires;
    }
}
