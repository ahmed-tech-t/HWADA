package com.example.hwada.Model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hwada.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.internal.SerializedIr;

public class DaysSchedule implements Parcelable {
    private  String saturday ="saturday";
    private  String sunday ="sunday";
    private  String monday ="monday";
    private  String tuesday ="tuesday";
    private  String wednesday ="wednesday";
    private  String thursday ="thursday";
    private  String friday ="friday";

    private  String saturday_cap ="Saturday";
    private  String sunday_cap ="Sunday";
    private  String monday_cap ="Monday";
    private  String tuesday_cap ="Tuesday";
    private  String wednesday_cap ="Wednesday";
    private  String thursday_cap ="Thursday";
    private  String friday_cap ="Friday";


    Map<String ,ArrayList<WorkingTime>> days ;
    private static  String TAG = "DaysSchedule";
    public DaysSchedule() {
        days = new HashMap();
        days.put(saturday,new ArrayList<>());
        days.put(sunday,new ArrayList<>());
        days.put(monday,new ArrayList<>());
        days.put(tuesday,new ArrayList<>());
        days.put(wednesday,new ArrayList<>());
        days.put(thursday,new ArrayList<>());
        days.put(friday,new ArrayList<>());
    }


    protected DaysSchedule(Parcel in) {
        saturday = in.readString();
        sunday = in.readString();
        monday = in.readString();
        tuesday = in.readString();
        wednesday = in.readString();
        thursday = in.readString();
        friday = in.readString();
        saturday_cap = in.readString();
        sunday_cap = in.readString();
        monday_cap = in.readString();
        tuesday_cap = in.readString();
        wednesday_cap = in.readString();
        thursday_cap = in.readString();
        friday_cap = in.readString();

        days = new HashMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            ArrayList<WorkingTime> value = in.createTypedArrayList(WorkingTime.CREATOR);
            days.put(key, value);
        }
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(saturday);
        dest.writeString(sunday);
        dest.writeString(monday);
        dest.writeString(tuesday);
        dest.writeString(wednesday);
        dest.writeString(thursday);
        dest.writeString(friday);
        dest.writeString(saturday_cap);
        dest.writeString(sunday_cap);
        dest.writeString(monday_cap);
        dest.writeString(tuesday_cap);
        dest.writeString(wednesday_cap);
        dest.writeString(thursday_cap);
        dest.writeString(friday_cap);

        dest.writeInt(days.size());
        for (Map.Entry<String, ArrayList<WorkingTime>> entry : days.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static  Creator<DaysSchedule> CREATOR = new Creator<DaysSchedule>() {
        @Override
        public DaysSchedule createFromParcel(Parcel in) {
            return new DaysSchedule(in);
        }

        @Override
        public DaysSchedule[] newArray(int size) {
            return new DaysSchedule[size];
        }
    };

    public String getDayValFromPosition(int pos){
        switch (pos){
            case 0 :
                return saturday;
            case 1 :
                return sunday;
            case 2 :
                return monday;
            case 3 :
                return tuesday;
            case 4 :
                return wednesday;
            case 5 :
                return thursday;
            case 6 :
                return friday;
        }
        return "";
    }
    public String getDayTitleFromPosition(int pos){
        switch (pos){
            case 0 :
                return saturday_cap;
            case 1 :
                return sunday_cap;
            case 2 :
                return monday_cap;
            case 3 :
                return tuesday_cap;
            case 4 :
                return wednesday_cap;
            case 5 :
                return thursday_cap;
            case 6 :
                return friday_cap;
        }
        return "";
    }

    public int getPosFromDay(String day){
       if(day.equals(saturday))return 0;
       else if(day.equals(sunday))return 1;
       else if(day.equals(monday))return 2;
       else if(day.equals(tuesday))return 3;
       else if(day.equals(wednesday))return 4;
       else if(day.equals(thursday))return 5;
       else if(day.equals(friday))return 6;
        return -1;
    }
    public String getDayTitleFromDay(String day){
        if(day.equals(saturday))return saturday_cap;
        else if(day.equals(sunday))return sunday_cap;
        else if(day.equals(monday))return monday_cap;
        else if(day.equals(tuesday))return tuesday_cap;
        else if(day.equals(wednesday))return wednesday_cap;
        else if(day.equals(thursday))return thursday_cap;
        else if(day.equals(friday))return friday_cap;
        return "";
    }

    public Map<String, ArrayList<WorkingTime>> getDays() {
        return days;
    }

    public void setDays(Map<String, ArrayList<WorkingTime>> days) {
        this.days = days;
    }

   

    @Override
    public String toString() {
        return "DaysSchedule{" +
                "days=" + days ;
    }

 
}
