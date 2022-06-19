package com.example.gui;

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

import com.example.gui.adapter.Fragment_Pager_Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import devlight.io.library.ntb.NavigationTabBar;

/*
 * Method - https://github.com/Devlight/NavigationTabBar
 * */
public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    public SharedPreferences prefs;
    SQLiteDatabase db;
    DBManger dbManger;
    CalendarView CalendarView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // UI Load
        initUI();
        initDB();

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
    public void initDB(){
        dbManger = DBManger.getInstance(this, "Status", null, 1);
        db = dbManger.getWritableDatabase();
        // Get Time Data
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = simpleDate.format(mDate);
        // If First start, Insert Date data
        DBFirstRun("Stats", getTime);
    }
    // Today data Presence or absence Function
    public void DBFirstRun(String tableName, String time) {
        Cursor cursor = null;
        String id = null;
        try {
            // Find Today Time data Query: SELECT ID FROM Stats FROM Day = ?
            cursor = db.query(tableName, null, "DAY = ?", new String[]{time.toString()}, null, null, null);
            System.out.println("cursor: " + cursor);
            // Have Today Time data
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    id = cursor.getString(cursor.getColumnIndex("ID"));
                }
            }
            if(id == null){
                ContentValues contentValues = new ContentValues();
                contentValues.put("DAY", time);
                contentValues.put("BREAKFAST", 0);
                contentValues.put("LUNCH", 0);
                contentValues.put("DINNER", 0);
                db.insert(tableName, null, contentValues);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
