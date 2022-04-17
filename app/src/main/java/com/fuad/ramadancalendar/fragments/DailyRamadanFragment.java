package com.fuad.ramadancalendar.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset;
import static com.fuad.ramadancalendar.constants.EnumData.*;

import com.fuad.ramadancalendar.R;
import com.fuad.ramadancalendar.widgets.RamadanCalendarWidget;

public class DailyRamadanFragment extends Fragment {

    private TextView tvRamadanNo, tvDate, tvDivision, tvSahr, tvFajr, tvSunrise, tvSunset, tvMagrib, tvItmam, btnTxt, errorMsg;
    private View divider;
    private LinearLayout dateLayout, locationLayout;
    private ProgressBar progressBar;
    private ScrollView times;
    private CardView fullCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_ramadan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("LOCALE", "onViewCreated: "+Locale.getDefault().toString());

        tvRamadanNo = view.findViewById(R.id.ramadan_no);
        divider = view.findViewById(R.id.divider);
        tvDate = view.findViewById(R.id.date);
        tvDivision = view.findViewById(R.id.division_name);
        tvSahr = view.findViewById(R.id.last_sahr);
        tvFajr = view.findViewById(R.id.fajr);
        tvSunrise = view.findViewById(R.id.sunrise);
        tvSunset = view.findViewById(R.id.sunset);
        tvMagrib = view.findViewById(R.id.magrib);
        tvItmam = view.findViewById(R.id.itmam);
        times = view.findViewById(R.id.times);
        dateLayout = view.findViewById(R.id.date_layout);
        locationLayout = view.findViewById(R.id.location_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        errorMsg = view.findViewById(R.id.error_msg);
        fullCalendar = view.findViewById(R.id.full_calendar);
        btnTxt = view.findViewById(R.id.btn_text);

        progressBar.setVisibility(View.VISIBLE);
        times.setVisibility(View.GONE);

        tvDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                getTimes();
            }
        });

        String divisionIndex = getFromSharedPref("division_index", getContext());

        if (!divisionIndex.isEmpty()) {
            tvDivision.setText(setLatitudeLongitude(getContext(), Math.abs(Integer.parseInt(divisionIndex)) % 8));
        } else {
            setLatitudeLongitude(getContext(), 0);
        }

        tvDate.setText(dateFormat.format(new Date()));

        dateLayout.setOnClickListener(v -> changeDate());
        locationLayout.setOnClickListener(v -> divisionDialog());
        fullCalendar.setOnClickListener(v -> switchFragment(new RamadanCalendarFragment()));


        getTimes();
    }

    public void changeDate() {
        Date date;
        try {
            date = dateFormat.parse(tvDate.getText().toString());
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), (view, year, month, day) -> {
                    Date d = new Date(year - 1900, month, day);
                    tvDate.setText(dateFormat.format(d));
                }, date.getYear() + 1900, date.getMonth(), date.getDate());
        datePickerDialog.show();
    }

    private void getTimes() {
//        SimpleDateFormat dFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        RamadanCalendarWidget ramadanCalendarWidget = new RamadanCalendarWidget();

        String currentDate = tvDate.getText().toString();
        try {
            // For Language Change
            String tempDate = dateFormat.format(usDateFormat.parse(firstRamadanDate));

            Date dateBefore = dateFormat.parse(tempDate);
            Date dateAfter = dateFormat.parse(currentDate);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            long daysBetween = (difference / (1000 * 60 * 60 * 24)) + 1;

            if (daysBetween <= 0 || daysBetween > 30) {
                tvRamadanNo.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            } else {
                tvRamadanNo.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                String ramadanNo = numberFormatter.format(daysBetween) + getOrdinalNumber((int) daysBetween);
                tvRamadanNo.setText(String.format("%s %s", ramadanNo, getResources().getString(R.string.ramadan)));
            }
        } catch (ParseException e) {
            tvRamadanNo.setVisibility(View.GONE);
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(tvDate.getText().toString()));
            Calendar[] sunriseSunset = getSunriseSunset(calendar, DIVISION_LATITUDE, DIVISION_LONGITUDE);
            tvSunrise.setText(timeFormat.format(sunriseSunset[0].getTime()));
            tvSunset.setText(timeFormat.format(sunriseSunset[1].getTime()));

            setTimes(sunriseSunset[0].getTime(), sunriseSunset[1].getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setTimes(Date sunrise, Date sunset) {
//        btnTxt.setText("Full Calendar");

        sunrise.setMinutes(sunrise.getMinutes() + SUNRISE_BUFFER);
        sunset.setMinutes(sunset.getMinutes() + SUNSET_BUFFER);

        tvSunrise.setText(timeFormat.format(sunrise));
        tvSunset.setText(timeFormat.format(sunset));

        sunrise.setMinutes(sunrise.getMinutes() - 72);
        tvSahr.setText(timeFormat.format(sunrise));
        sunrise.setMinutes(sunrise.getMinutes() + 5);
        tvFajr.setText(timeFormat.format(sunrise));
        sunset.setMinutes(sunset.getMinutes() + 5);
        tvMagrib.setText(timeFormat.format(sunset));
        sunset.setMinutes(sunset.getMinutes() + 67);
        tvItmam.setText(timeFormat.format(sunset));

        progressBar.setVisibility(View.GONE);
        times.setVisibility(View.VISIBLE);
    }

    public String getOrdinalNumber(int n) {
        String locale = Locale.getDefault().toString();
        if (locale.equals("bn"))
            return (n < 7) ? new String[]{"ম", "য়", "য়", "র্থ", "ম", "ষ্ঠ"}[n] : (n <= 10) ? "ম" : "শ";
        return ((n / 10 != 1) && (n % 10 < 4)) ? new String[]{"th", "st", "nd", "rd"}[n % 10] : "th";
    }

    public void divisionDialog() {
        final ArrayList<String> DIVISIONS = new ArrayList<>(Arrays.asList(getResources().getString(R.string.dhaka), getResources().getString(R.string.chittagong), getResources().getString(R.string.rajshahi), getResources().getString(R.string.khulna), getResources().getString(R.string.barishal), getResources().getString(R.string.sylhet), getResources().getString(R.string.rangpur), getResources().getString(R.string.mymensingh)));

        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_division_select);

        dialog.show();

        final int selectedDivision = DIVISIONS.indexOf(tvDivision.getText().toString());
        RadioButton radioButton = dialog.findViewById(getResources().getIdentifier("division_" + selectedDivision, "id", getContext().getPackageName()));

        if (radioButton != null)
            radioButton.setChecked(true);

        final RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton button = dialog.findViewById(checkedId);
            tvDivision.setText(button.getText().toString());

            int index = radioGroup.indexOfChild(button) / 2;
            Log.d("TAG", "onCheckedChanged: " + index);
            setLatitudeLongitude(getContext(), index);
            setInSharedPref("division_index", index + "", getContext());
            getTimes();
            dialog.dismiss();
        });
    }

    public void switchFragment(Fragment fragment){
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_up,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_down  // popExit
                    )
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}