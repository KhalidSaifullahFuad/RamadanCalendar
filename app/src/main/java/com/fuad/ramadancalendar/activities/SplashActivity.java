package com.fuad.ramadancalendar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.fuad.ramadancalendar.R;

public class SplashActivity extends AppCompatActivity {

    int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, RamadanActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }, SPLASH_TIME_OUT);
    }
}