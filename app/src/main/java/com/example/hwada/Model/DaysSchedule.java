package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import kotlin.jvm.internal.SerializedIr;

public class DaysSchedule implements Parcelable {

    ArrayList<WorkingTime>saturday ;
    ArrayList<WorkingTime> sunday ;
    ArrayList<WorkingTime> monday ;
    ArrayList<WorkingTime>tuesday ;
    ArrayList<WorkingTime> wednesday ;
    ArrayList<WorkingTime> thursday ;
    ArrayList<WorkingTime> friday ;

    public DaysSchedule() {
        saturday =new ArrayList<>();
        sunday =new ArrayList<>();
        monday =new ArrayList<>();
        tuesday =new ArrayList<>();
        wednesday =new ArrayList<>();
        thursday =new ArrayList<>();
        friday =new ArrayList<>();
    }

    protected DaysSchedule(Parcel in) {
        saturday = in.createTypedArrayList(WorkingTime.CREATOR);
        sunday = in.createTypedArrayList(WorkingTime.CREATOR);
        monday = in.createTypedArrayList(WorkingTime.CREATOR);
        tuesday = in.createTypedArrayList(WorkingTime.CREATOR);
        wednesday = in.createTypedArrayList(WorkingTime.CREATOR);
        thursday = in.createTypedArrayList(WorkingTime.CREATOR);
        friday = in.createTypedArrayList(WorkingTime.CREATOR);
    }

    public static final Creator<DaysSchedule> CREATOR = new Creator<DaysSchedule>() {
        @Override
        public DaysSchedule createFromParcel(Parcel in) {
            return new DaysSchedule(in);
        }

        @Override
        public DaysSchedule[] newArray(int size) {
            return new DaysSchedule[size];
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
    public ArrayList<WorkingTime> get(int pos){
        switch (pos){
            case 0:
                return saturday;
            case 1:
                return sunday;
            case 2:
                return monday;
            case 3:
                return tuesday;
            case 4:
                return wednesday;
            case 5:
                return thursday;
            case 6:
                return friday;
            default:
                return null;
        }
    }
    public void set(int pos,ArrayList<WorkingTime> list){
        switch (pos){
            case 0:
                 saturday = list;
                 break;
            case 1:
                sunday =list;
                break;
            case 2:
                monday =list;
                break;
            case 3:
                tuesday =list;
                break;
            case 4:
                wednesday =list;
                break;
            case 5:
                thursday =list;
                break;
            case 6:
                friday =list;
                break;
        }
    }
    public void remove(int pos){
        switch (pos){
            case 0:
                saturday = new ArrayList<>();
                break;
            case 1:
                sunday =new ArrayList<>();
                break;
            case 2:
                monday =new ArrayList<>();
                break;
            case 3:
                tuesday =new ArrayList<>();
                break;
            case 4:
                wednesday =new ArrayList<>();
                break;
            case 5:
                thursday =new ArrayList<>();
                break;
            case 6:
                friday =new ArrayList<>();
                break;
        }
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

    @Override
    public String toString() {
        return "DaysSchedule{" +
                "saturday=" + saturday +
                ", sunday=" + sunday +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                '}';
    }
}
