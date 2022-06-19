package com.example.gui.ui.main;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.gui.CalendarView;
import com.example.gui.DBManger;
import com.example.gui.LoginActivity;
import com.example.gui.R;
import com.example.gui.adapter.Fragment_Pager_Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import devlight.io.library.ntb.NavigationTabBar;

/*
 * MainActivity.java
 * 이 액티비티는 Fragment를 관리함.
 * 다양한 View를 관리할 수 있도록 바텀네비게이션을 사용함.
 * Method - https://github.com/Devlight/NavigationTabBar
 * */
public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    public SharedPreferences prefs;
    com.example.gui.CalendarView CalendarView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // UI Load
        initUI();

        fragmentManager = getSupportFragmentManager();
        CalendarView = new CalendarView();
        transaction = fragmentManager.beginTransaction();

        // First Run check
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        checkFirstRun();
    }
    //UI Function
    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        Fragment_Pager_Adapter adapter = new Fragment_Pager_Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        navigationTabBar.setBgColor(Color.parseColor("#14151E"));
        navigationTabBar.setIsBadged(false);
        navigationTabBar.setIsTitled(false);


        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_cal),
                        Color.parseColor(colors[0]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_box),
                        Color.parseColor(colors[1]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_main),
                        Color.parseColor(colors[2]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_chart),
                        Color.parseColor(colors[3]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main_user),
                        Color.parseColor(colors[4]))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setIsTinted(true);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }

    // First Run Function
    public void checkFirstRun() {
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Intent newIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun", false).apply();
        }
    }
}
