package com.fuad.ramadancalendar.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fuad.ramadancalendar.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class EnumData {
    public static double DIVISION_LATITUDE = 24.3745;
    public static double DIVISION_LONGITUDE = 88.6042;

    public static final int SUNRISE_BUFFER = -1;
    public static final int SUNSET_BUFFER = -1;

    public static final String firstRamadanDate = "01-Apr-2022";
    public static final String endRamadanDate = "11-May-2020";

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    public static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM-yyyy", Locale.getDefault());
    public static SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
    public static SimpleDateFormat usDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public static NumberFormat numberFormatter = NumberFormat.getInstance(Locale.getDefault());

    public static final String[] DIVISION_LATITUDE_LONGITUDE = {"23.8103:90.4125", "22.3569:91.7832", "24.3745:88.6042", "22.8456:89.5403", "22.7010:90.3535", "24.8949:91.8687", "25.7439:89.2752", "24.7471:90.4203"};


    public static String setLatitudeLongitude(Context context, int index) {
        ArrayList<String> DIVISIONS = new ArrayList<>(Arrays.asList(context.getResources().getString(R.string.dhaka), context.getResources().getString(R.string.chittagong), context.getResources().getString(R.string.rajshahi), context.getResources().getString(R.string.khulna), context.getResources().getString(R.string.barishal), context.getResources().getString(R.string.sylhet), context.getResources().getString(R.string.rangpur), context.getResources().getString(R.string.mymensingh)));
        DIVISION_LATITUDE = Double.parseDouble(DIVISION_LATITUDE_LONGITUDE[index].split(":")[0]);
        DIVISION_LONGITUDE = Double.parseDouble(DIVISION_LATITUDE_LONGITUDE[index].split(":")[1]);
        Log.d("TAG", "setLatitudeLongitude: " + index);

        return DIVISIONS.get(index);
    }

    public static void setInSharedPref(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getFromSharedPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }

    public static void setLocale(Context context, String strLocale){
        Locale locale = new Locale(strLocale);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy", locale);
        monthDateFormat = new SimpleDateFormat("MMM-yyyy", locale);
        timeFormat = new SimpleDateFormat("hh:mm a", locale);
        numberFormatter = NumberFormat.getInstance(locale);
    }
}
