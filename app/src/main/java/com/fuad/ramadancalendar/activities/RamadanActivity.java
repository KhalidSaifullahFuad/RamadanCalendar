package com.fuad.ramadancalendar.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fuad.ramadancalendar.R;
import com.fuad.ramadancalendar.fragments.CreditsFragment;
import com.fuad.ramadancalendar.fragments.DailyRamadanFragment;
import com.fuad.ramadancalendar.fragments.QiblaCompassFragment;
import com.fuad.ramadancalendar.fragments.RamadanCalendarFragment;
import com.fuad.ramadancalendar.fragments.RamadanDuahFragment;
import com.fuad.ramadancalendar.fragments.RamadanInQuranFragment;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.fuad.ramadancalendar.constants.EnumData.*;

public class RamadanActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView toolbarTitle;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Integer itemIdWhenClosed;
    private NavigationView navigationView;
    private final int UPDATE_REQUEST_CODE = 100;

    private AppUpdateManager mAppUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String locale = getFromSharedPref("locale", getApplicationContext());
        if (locale.isEmpty()) locale = "en";
        setLocale(getApplicationContext(), locale);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ramadan);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        //if(isNetworkConnected()) {
            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE);
                        Log.d("TAG", "onAppUpdateSuccess: "+result.updateAvailability());
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Log.d("TAG", "onAppUpdateFailure: " + e);
                    }
                }
            }).addOnFailureListener(e -> {
                Log.d("TAG", "onAppUpdateFailure: " + e.toString());
            });
       // }

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
            String tag = toolbarTitle.getText().toString();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DailyRamadanFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
            toolbarTitle.setText(R.string.daily_ramadan_calendar);
            navigationView.setCheckedItem(R.id.nav_daily_ramadan);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String title = getResources().getString(R.string.daily_ramadan_calendar);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DailyRamadanFragment(), title)
                .addToBackStack(title)
                .commit();
        toolbarTitle.setText(title);
        navigationView.setCheckedItem(R.id.nav_daily_ramadan);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void creditsDialog() {
        final Dialog dialog = new Dialog(RamadanActivity.this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_credits);
        dialog.show();

        TextView textView = dialog.findViewById(R.id.youtube_link);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void actionShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Salamun Alaikum Tibtum, Install Ramadan Calendar app from Play Store. Here,  https://play.google.com/store/apps/details?id=com.fuad.ramadancalendar";
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

    @SuppressLint("NonConstantResourceId")
    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.nav_update:
                if(isNetworkConnected()) {
                    checkForUpdate();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RamadanActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setMessage("Please check you internet connection and Try again.")
                            .setCancelable(true)
                            .setPositiveButton("OK", (dialog, id) -> dialog.cancel());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                /*AppUpdater appUpdater = new AppUpdater(this)
                        .setDisplay(Display.DIALOG)
                        .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                        .showAppUpdated(true)
                        .setTitleOnUpdateAvailable("Update available")
                        .setContentOnUpdateAvailable("Check out the latest version available!")
                        .setTitleOnUpdateNotAvailable("Update not available")
                        .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                        .setButtonUpdate("Update")
                        .setButtonUpdateClickListener((dialogInterface, i) -> {

                        })
                        .setButtonDismiss("Maybe later")
                        .setButtonDismissClickListener((dialogInterface, i) -> {

                        })
                        .setIcon(R.drawable.ic_update)
                        .setCancelable(false);
                appUpdater.start();
                AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                        .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                        .withListener(new AppUpdaterUtils.UpdateListener() {
                            @Override
                            public void onSuccess(Update update, Boolean isUpdateAvailable) {
                                Log.d("Latest Version", update.getLatestVersion());
                                Log.d("Latest Version Code", String.valueOf(update.getLatestVersionCode()));
                                Log.d("Release notes", update.getReleaseNotes());
                                Log.d("URL", String.valueOf(update.getUrlToDownload()));
                                Log.d("Is update available?", Boolean.toString(isUpdateAvailable));
                            }

                            @Override
                            public void onFailed(AppUpdaterError error) {
                                Log.d("AppUpdater Error", "Something went wrong");
                            }
                        });
                appUpdaterUtils.start();*/
                return;
//            case R.id.nav_language:
//                languageDialog();
//                return;
            case R.id.nav_share:
                actionShare();
                return;
            case R.id.nav_feedback:
                actionFeedback();
                return;
            case R.id.nav_rate_us:
                // first method
               /* ReviewManager manager = ReviewManagerFactory.create(this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ReviewInfo reviewInfo = task.getResult();
                        Task<Void> result = manager.launchReviewFlow(RamadanActivity.this, reviewInfo);
                        result.addOnCompleteListener(task1 -> Toast.makeText(this, "Thank you for Rating!!", Toast.LENGTH_SHORT).show());
                    } else {
                        Log.d("Play Store Review", "displaySelectedScreen: Review Failed");
                    }
                });*/
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return;
        }
        final Fragment fragment = getFragmentBy(itemId);
        setToolbarTitle(fragment);
        String tag = toolbarTitle.getText().toString();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_up,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_down  // popExit
                )
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    private Fragment getFragmentBy(int itemId) {
        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.nav_daily_ramadan, new DailyRamadanFragment());
        fragmentMap.put(R.id.nav_ramadan_calendar, new RamadanCalendarFragment());
        fragmentMap.put(R.id.nav_ramadan_in_quran, new RamadanInQuranFragment());
        fragmentMap.put(R.id.nav_ramadan_dua, new RamadanDuahFragment());
//        fragmentMap.put(R.id.nav_qibla_compass, new QiblaCompassFragment());
        fragmentMap.put(R.id.nav_credits, new CreditsFragment());

        return fragmentMap.get(itemId);
    }

    private void setToolbarTitle(Fragment fragment) {
        if (fragment instanceof DailyRamadanFragment) {
            toolbarTitle.setText(R.string.daily_ramadan_calendar);
            navigationView.setCheckedItem(R.id.nav_daily_ramadan);
        } else if (fragment instanceof RamadanCalendarFragment) {
            toolbarTitle.setText(R.string.full_ramadan_calendar);
            navigationView.setCheckedItem(R.id.nav_ramadan_calendar);
        } else if (fragment instanceof RamadanInQuranFragment) {
            toolbarTitle.setText(R.string.ramadan_in_the_holy_quran);
            navigationView.setCheckedItem(R.id.nav_ramadan_in_quran);
        } else if (fragment instanceof RamadanDuahFragment) {
            toolbarTitle.setText(R.string.ramadan_duah);
            navigationView.setCheckedItem(R.id.nav_ramadan_dua);
        } else if (fragment instanceof QiblaCompassFragment) {
            toolbarTitle.setText(R.string.qiblah_compass);
//            navigationView.setCheckedItem(R.id.nav_qibla_compass);
        } else if (fragment instanceof CreditsFragment) {
            toolbarTitle.setText(R.string.credits);
            navigationView.setCheckedItem(R.id.nav_credits);
        }
    }

    public void languageDialog() {
        final ArrayList<String> LANGUAGES = new ArrayList<>(Arrays.asList(getResources().getString(R.string.english_language), getResources().getString(R.string.bangla_language)));

        final Dialog dialog = new Dialog(RamadanActivity.this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_language_select);

        dialog.show();

        String locale = Locale.getDefault().toString();
        if(locale.isEmpty()) locale = "en";
        RadioButton radioButton = dialog.findViewById(getResources().getIdentifier("language_"+locale, "id", getApplicationContext().getPackageName()));

        if (radioButton != null)
            radioButton.setChecked(true);

        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton button = dialog.findViewById(checkedId);
            String locale1 = checkedId == R.id.language_bn ? "bn" : "en";

            setInSharedPref("locale", locale1, getApplicationContext());
            setLocale(getApplicationContext(), locale1);
            dialog.dismiss();
            recreate();
        });
    }

    public void checkForUpdate(){
        AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .showAppUpdated(true)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available!")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update")
                .setButtonUpdateClickListener((dialogInterface, i) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                })
                .setCancelable(true);
        appUpdater.start();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (count != 1) {
            getSupportFragmentManager().popBackStack();
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(count - 2).getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            setToolbarTitle(fragment);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RamadanActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(getResources().getString(R.string.app_close_msg))
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, id) -> finishAffinity())
                    .setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel());
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
