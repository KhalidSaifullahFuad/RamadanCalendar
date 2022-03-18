package com.fuad.ramadancalendar.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset;
import static com.fuad.ramadancalendar.constants.EnumData.*;

import com.fuad.ramadancalendar.R;
import com.fuad.ramadancalendar.viewmodels.Ramadan;
import com.fuad.ramadancalendar.adapters.RamadanAdapter;

public class RamadanCalendarFragment extends Fragment {

    public RecyclerView recyclerView;
    public RamadanAdapter adapter;
    public List<Ramadan> ramadanList;
    public TextView tvDate, tvDivision;
    public static boolean flag = true;

    public RamadanCalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ramadan_calendar, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] DAYS = {getContext().getResources().getString(R.string.sunday), getContext().getResources().getString(R.string.monday), getContext().getResources().getString(R.string.tuesday), getContext().getResources().getString(R.string.wednesday), getContext().getResources().getString(R.string.thursday), getContext().getResources().getString(R.string.friday), getContext().getResources().getString(R.string.saturday)};

        tvDate = view.findViewById(R.id.date);
        tvDivision = view.findViewById(R.id.division_name);

        ramadanList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.month_calendar_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RamadanAdapter(ramadanList, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        String division = getFromSharedPref("division_index", getContext());
        if (!division.isEmpty()) {
            tvDivision.setText(setLatitudeLongitude(getContext(),Integer.parseInt(division)));
        }
        tvDate.setText(dFormatter.format(new Date()));

        Calendar calendar = Calendar.getInstance();
        try {
            // For locale
            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

            // For Language Change
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            String tempDate = dFormatter.format(dateFormat.parse(firstRamadanDate));

            Date date = dFormatter.parse(tempDate);
            for (int i = 0; i < 30; i++) {
                calendar.setTime(date);
                Calendar[] sunriseSunset = getSunriseSunset(calendar, DIVISION_LATITUDE, DIVISION_LONGITUDE);

                Date sunrise = sunriseSunset[0].getTime();
                sunrise.setMinutes(sunrise.getMinutes() - 72 + SUNRISE_BUFFER);
                Date sunset = sunriseSunset[1].getTime();
                sunset.setMinutes(sunset.getMinutes() + 72 + SUNSET_BUFFER);

                Ramadan ramadan = new Ramadan();
                ramadan.setDay(DAYS[date.getDay()]);
                ramadan.setDate(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date));
                ramadan.setSahr(tFormatter.format(sunrise));
                ramadan.setItmam(tFormatter.format(sunset));
                ramadanList.add(ramadan);

                date.setDate(date.getDate() + 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }
}