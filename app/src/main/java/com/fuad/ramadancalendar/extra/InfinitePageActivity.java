package com.fuad.ramadancalendar.extra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

import com.fuad.ramadancalendar.R;

public class InfinitePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iinfinite_page);

        setUpViewPager();
    }

    private void setUpViewPager(){

    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentBasedOnPosition(position);
        }

        private Fragment getFragmentBasedOnPosition(int position) {
            int fragmentPos = position % 3; // Assuming you have 3 fragments
            switch(fragmentPos) {
//                case 1:
//                    return Fragment1.newInstance();
//                case 2:
//                    return Fragment2.newInstance();
//                case 3:
//                    return Fragment3.newInstance();
            }
            return null;
        }
    }
}