package com.fuad.ramadancalendar.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuad.ramadancalendar.BuildConfig;
import com.fuad.ramadancalendar.R;

import java.util.Calendar;
import java.util.Locale;

public class CreditsFragment extends Fragment {

    TextView versionText, copyrightText, youtubeLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credits, container, false);

        versionText = view.findViewById(R.id.version_text);
        copyrightText = view.findViewById(R.id.copyright_text);
        youtubeLink = view.findViewById(R.id.youtube_link);

        versionText.setText(BuildConfig.VERSION_NAME);
        copyrightText.setText(String.format(Locale.US, "%s %d Cordial Academy", getString(R.string.copyright_icon), Calendar.getInstance().get(Calendar.YEAR)));
        youtubeLink.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}