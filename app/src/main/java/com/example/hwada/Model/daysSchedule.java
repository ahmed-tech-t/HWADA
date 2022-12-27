package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class daysSchedule implements Parcelable {

    ArrayList<WorkingTime>saturday ;
    ArrayList<WorkingTime> sunday ;
    ArrayList<WorkingTime> monday ;
    ArrayList<WorkingTime>tuesday ;
    ArrayList<WorkingTime> wednesday ;
    ArrayList<WorkingTime> thursday ;
    ArrayList<WorkingTime> friday ;

    public daysSchedule() {
        saturday =new ArrayList<>();
        sunday =new ArrayList<>();
        monday =new ArrayList<>();
        tuesday =new ArrayList<>();
        wednesday =new ArrayList<>();
        thursday =new ArrayList<>();
        friday =new ArrayList<>();
    }

    protected daysSchedule(Parcel in) {
        saturday = in.createTypedArrayList(WorkingTime.CREATOR);
        sunday = in.createTypedArrayList(WorkingTime.CREATOR);
        monday = in.createTypedArrayList(WorkingTime.CREATOR);
        tuesday = in.createTypedArrayList(WorkingTime.CREATOR);
        wednesday = in.createTypedArrayList(WorkingTime.CREATOR);
        thursday = in.createTypedArrayList(WorkingTime.CREATOR);
        friday = in.createTypedArrayList(WorkingTime.CREATOR);
    }

    public static final Creator<daysSchedule> CREATOR = new Creator<daysSchedule>() {
        @Override
        public daysSchedule createFromParcel(Parcel in) {
            return new daysSchedule(in);
        }

        @Override
        public daysSchedule[] newArray(int size) {
            return new daysSchedule[size];
        }
    };

    public ArrayList<WorkingTime> getSaturday() {
        return saturday;
    }

    public void setSaturday(ArrayList<WorkingTime> saturday) {
        this.saturday = saturday;
    }

    public ArrayList<WorkingTime> getSunday() {
        return sunday;
    }

    public void setSunday(ArrayList<WorkingTime> sunday) {
        this.sunday = sunday;
    }

    public ArrayList<WorkingTime> getMonday() {
        return monday;
    }

    public void setMonday(ArrayList<WorkingTime> monday) {
        this.monday = monday;
    }

    public ArrayList<WorkingTime> getTuesday() {
        return tuesday;
    }

    public void setTuesday(ArrayList<WorkingTime> tuesday) {
        this.tuesday = tuesday;
    }

    public ArrayList<WorkingTime> getWednesday() {
        return wednesday;
    }

    public void setWednesday(ArrayList<WorkingTime> wednesday) {
        this.wednesday = wednesday;
    }

    public ArrayList<WorkingTime> getThursday() {
        return thursday;
    }

    public void setThursday(ArrayList<WorkingTime> thursday) {
        this.thursday = thursday;
    }

    public ArrayList<WorkingTime> getFriday() {
        return friday;
    }

    public void setFriday(ArrayList<WorkingTime> friday) {
        this.friday = friday;
    }
    public void addOneToSaturday(WorkingTime w){
        saturday.add(w);
    }
    public void addOneToSunday(WorkingTime w){
        sunday.add(w);
    }public void addOneToMonday(WorkingTime w){
        monday.add(w);
    }
    public void addOneToTuesday(WorkingTime w){
        tuesday.add(w);
    }
    public void addOneToWednesday(WorkingTime w){
        wednesday.add(w);
    }
    public void addOneToThursday(WorkingTime w){
        thursday.add(w);
    }
    public void addOneToFriday(WorkingTime w){
        friday.add(w);
    }

    public void removeOneFromSaturday(int pos){
        saturday.remove(pos);
    }
    public void removeOneFromSunday(int pos){
        sunday.remove(pos);
    }
    public void removeOneFromMonday(int pos){
        monday.remove(pos);
    }
    public void removeOneFromTuesday(int pos){
        tuesday.remove(pos);
    }
    public void removeOneFromWednesday(int pos){
        wednesday.remove(pos);
    }
    public void removeOneFromThursday(int pos){
        thursday.remove(pos);
    }
    public void removeOneFromFriday(int pos){
        friday.remove(pos);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeTypedList(saturday);
        dest.writeTypedList(sunday);
        dest.writeTypedList(monday);
        dest.writeTypedList(tuesday);
        dest.writeTypedList(wednesday);
        dest.writeTypedList(thursday);
        dest.writeTypedList(friday);
    }
}
