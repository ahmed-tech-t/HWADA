package com.example.hwada.util;

import android.content.Context;
import android.util.Log;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;

import org.checkerframework.checker.units.qual.A;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterFunctions {
    ArrayList<Ad> ads ;
    Context context;

    private static final String TAG = "FilterFunctions";
    public FilterFunctions(ArrayList<Ad> ads,Context context) {
        this.ads = ads;
        this.context =context;
    }

    public ArrayList<Ad> sortAdsByTheCheapest() {
        ArrayList <Ad> temp = new ArrayList<>(ads);
        return temp.stream().sorted(Comparator.comparing(Ad::getPrice)).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Ad> sortAdsByTheExpensive() {
        ArrayList <Ad> temp = new ArrayList<>(ads);
        return temp.stream().sorted(Comparator.comparing(Ad::getPrice).reversed()).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Ad> sortAdsByDate() {
        ArrayList <Ad> temp = new ArrayList<>(ads);
        return temp.stream().sorted(Comparator.comparing(Ad::getTimeStamp).reversed()).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Ad> sortAdsByTheClosest() {
        ArrayList <Ad> temp = new ArrayList<>(ads);
        return temp.stream().sorted(Comparator.comparing(Ad::getDistance)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Ad> sortAdsByRating() {
        ArrayList <Ad> temp = new ArrayList<>(ads);
        return temp.stream().sorted(Comparator.comparing(Ad::getRating).reversed()).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Ad> removeClosedAds(String time , String []days , int dayIndex){
        ArrayList<Ad> temp = ads.stream().filter(ad -> ad.isOpen(time,days,dayIndex)).collect(Collectors.toCollection(ArrayList::new));
        ads = new ArrayList<>(temp);
        return temp;
    }

}
