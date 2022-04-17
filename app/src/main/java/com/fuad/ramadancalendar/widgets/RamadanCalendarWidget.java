package com.fuad.ramadancalendar.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset;
import static com.fuad.ramadancalendar.constants.EnumData.DIVISION_LATITUDE;
import static com.fuad.ramadancalendar.constants.EnumData.DIVISION_LONGITUDE;
import static com.fuad.ramadancalendar.constants.EnumData.SUNRISE_BUFFER;
import static com.fuad.ramadancalendar.constants.EnumData.SUNSET_BUFFER;
import static com.fuad.ramadancalendar.constants.EnumData.dateFormat;
import static com.fuad.ramadancalendar.constants.EnumData.firstRamadanDate;
import static com.fuad.ramadancalendar.constants.EnumData.timeFormat;

import com.fuad.ramadancalendar.R;
import com.fuad.ramadancalendar.activities.RamadanActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RamadanCalendarWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ramadan_calendar);
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        String[] DAYS = {context.getResources().getString(R.string.sunday), context.getResources().getString(R.string.monday), context.getResources().getString(R.string.tuesday), context.getResources().getString(R.string.wednesday), context.getResources().getString(R.string.thursday), context.getResources().getString(R.string.friday), context.getResources().getString(R.string.saturday)};
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        try {
            Date dateBefore = dateFormat.parse(firstRamadanDate);
            long difference = date.getTime() - dateBefore.getTime();
            long daysBetween = (difference / (1000 * 60 * 60 * 24)) + 1;

            if (daysBetween <= 0 || daysBetween > 30) {
                views.removeAllViews(R.id.ramadan_no);
            } else {
                views.setTextViewText(R.id.ramadan_no, nf.format(daysBetween));

            }
        } catch (ParseException e) {
            views.removeAllViews(R.id.ramadan_no);
            e.printStackTrace();
        }

        Calendar[] sunriseSunset = getSunriseSunset(calendar, DIVISION_LATITUDE, DIVISION_LONGITUDE);

        Date sunrise = sunriseSunset[0].getTime();
        sunrise.setMinutes(sunrise.getMinutes() - 72 + SUNRISE_BUFFER);
        Date sunset = sunriseSunset[1].getTime();
        sunset.setMinutes(sunset.getMinutes() + 72 + SUNSET_BUFFER);


        views.setTextViewText(R.id.day, DAYS[date.getDay()]);
        views.setTextViewText(R.id.date, new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date));
        views.setTextViewText(R.id.sahr, timeFormat.format(sunrise));
        views.setTextViewText(R.id.itmam, timeFormat.format(sunset));

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, RamadanActivity.class),0);
        views.setOnClickPendingIntent(R.id.ramadan_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}