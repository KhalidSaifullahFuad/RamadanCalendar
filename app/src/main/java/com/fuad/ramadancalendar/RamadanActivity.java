package com.fuad.ramadancalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.navigation.NavigationView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.fuad.ramadancalendar.EnumData.*;

public class RamadanActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView toolbarTitle;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Integer itemIdWhenClosed;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String locale = getFromSharedPref("locale",getApplicationContext());
        if (locale.isEmpty()) locale = "en";
        setLocale(locale);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ramadan);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // If id to show is marked, perform action
                if (itemIdWhenClosed != null) {
                    displaySelectedScreen(itemIdWhenClosed);
                    // Reset value
                    itemIdWhenClosed = null;
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.my_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DailyRamadanFragment())
                    .commit();
            toolbarTitle.setText(R.string.drawer_item_1);
            navigationView.setCheckedItem(R.id.nav_daily_ramadan);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DailyRamadanFragment())
                .commit();
        toolbarTitle.setText(R.string.drawer_item_1);
        navigationView.setCheckedItem(R.id.nav_daily_ramadan);
        Log.d("THIS IS DEBUGGING", "onStart: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                actionShare();
                return true;
            case R.id.action_feedback:
                actionFeedback();
                return true;
            case R.id.action_credits:
                creditsDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void creditsDialog() {
        final Dialog dialog = new Dialog(RamadanActivity.this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.credits_dialog);
        dialog.show();

        TextView textView = dialog.findViewById(R.id.youtube_link);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void actionShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Salamun Alaikum Tibtum, Install Ramadan Calendar app. Here,  http://sh-ort.app/o2zkp";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ramadan Calendar");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void actionFeedback() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"khalidsaifullahfuad@gmail.com"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        itemIdWhenClosed = item.getItemId(); // Mark action when closed
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.nav_update:
                AppUpdater appUpdater = new AppUpdater(this)
                        .setDisplay(Display.DIALOG)
                        .setUpdateFrom(UpdateFrom.GITHUB)
                        .setGitHubUserAndRepo("KhalidSaifullahFuad", "RamadanCalendar")
                        .showAppUpdated(true);
                appUpdater.start();
                return;
            case R.id.nav_language:
                languageDialog();
                return;
            case R.id.nav_share:
                actionShare();
                return;
            case R.id.nav_feedback:
                actionFeedback();
                return;
            case R.id.nav_credits:
                creditsDialog();
                return;
        }
        final Fragment fragment = checkFragment(itemId);
        getSupportFragmentManager().beginTransaction()
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

    private Fragment checkFragment(int itemId) {
        switch (itemId) {
            case R.id.nav_daily_ramadan:
                toolbarTitle.setText(R.string.drawer_item_1);
                return new DailyRamadanFragment();
            case R.id.nav_ramadan_calendar:
                toolbarTitle.setText(R.string.drawer_item_2);
                return new RamadanCalendarFragment();
            case R.id.nav_ramadan_in_quran:
                toolbarTitle.setText(R.string.drawer_item_3);
                return new RamadanInQuranFragment();
            case R.id.nav_ramadan_dua:
                toolbarTitle.setText(R.string.drawer_item_4);
                return new RamadanDuahFragment();
            case R.id.nav_qibla_compass:
                toolbarTitle.setText(R.string.drawer_item_5);
                return new QiblaCompassFragment();
        }
        return null;
    }

    public void languageDialog() {
        final ArrayList<String> LANGUAGES = new ArrayList<>(Arrays.asList(getResources().getString(R.string.english_language), getResources().getString(R.string.bangla_language)));

        final Dialog dialog = new Dialog(RamadanActivity.this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_dialog);

        dialog.show();

        String language = getFromSharedPref("language", getApplicationContext());
        if(language.isEmpty()) language = "English";
        String selectedLanguage = String.valueOf(LANGUAGES.indexOf(language)+1);
        RadioButton radioButton = dialog.findViewById(getResources().getIdentifier("language_"+selectedLanguage, "id", getApplicationContext().getPackageName()));

        if (radioButton != null)
            radioButton.setChecked(true);

        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = dialog.findViewById(checkedId);
                String locale = "en";
                switch (checkedId){
                    case R.id.language_1:
                        locale = "en";
                        break;
                    case R.id.language_2:
                        locale = "bn";
                }

                setInSharedPref("locale", locale, getApplicationContext());
                setInSharedPref("language", button.getText().toString(), getApplicationContext());
                setLocale(locale);
                dialog.dismiss();
                recreate();
            }
        });
    }

    public void setLocale(String strLocale){
        Locale locale = new Locale(strLocale);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        dFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        tFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        nf = NumberFormat.getInstance(Locale.getDefault());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RamadanActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage(getResources().getString(R.string.app_close_msg))
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Getting the view elements
            TextView textView = alertDialog.getWindow().findViewById(android.R.id.message);
            TextView alertTitle = alertDialog.getWindow().findViewById(R.id.alertTitle);
            Button button1 = alertDialog.getWindow().findViewById(android.R.id.button1);
            Button button2 = alertDialog.getWindow().findViewById(android.R.id.button2);

            // Setting font
            Typeface face = ResourcesCompat.getFont(RamadanActivity.this,R.font.pt_sans);
            textView.setTypeface(face);
            alertTitle.setTypeface(face);
            button1.setTypeface(face);
            button2.setTypeface(face);
        }
    }
}
