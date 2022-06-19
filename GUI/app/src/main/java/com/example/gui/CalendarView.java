package com.example.gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gui.addDecorator.SaturdayDecorator;
import com.example.gui.addDecorator.SundayDecorator;
import com.example.gui.addDecorator.onDayDecorator;
import com.example.gui.ui.main.MainActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarView extends Fragment {
    public MaterialCalendarView calendarView;
    com.example.gui.ui.main.MainActivity MainActivity;
    SQLiteDatabase db;
    DBManger dbManger;
    Button calendar_button1, calendar_button2;
    ImageView breakfastImgview, lunchImgview, dinnerImgview;
    TextView exerciseTextview, breakfastTitle, breakfastContent, lunchTitle, lunchContent, dinnerTitle, dinnerContent, newTextview, newTextview2;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_calendar, container, false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.
        // initView
        calendarView=(MaterialCalendarView)rootView.findViewById(R.id.calendarView);
        exerciseTextview = rootView.findViewById(R.id.calendar_exercise);
        breakfastImgview = rootView.findViewById(R.id.breakfastImgview);
        lunchImgview = rootView.findViewById(R.id.lunchImgview);
        dinnerImgview = rootView.findViewById(R.id.dinnerImgview);
        breakfastTitle = rootView.findViewById(R.id.calendar_breakfastTitle);
        breakfastContent = rootView.findViewById(R.id.calendar_breakfastContent);
        lunchTitle = rootView.findViewById(R.id.calendar_lunchTitle);
        lunchContent = rootView.findViewById(R.id.calendar_lunchContent);
        dinnerTitle = rootView.findViewById(R.id.calendar_dinnerTitle);
        dinnerContent = rootView.findViewById(R.id.calendar_dinnerContent);
        newTextview = rootView.findViewById(R.id.calendar_newTextView);
        newTextview2 = rootView.findViewById(R.id.calendar_newTextview2);
        this.initUI();
        this.setCalendarView();
        return rootView;
    }
    public static CalendarView newInstance(){
        return new CalendarView();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity = (MainActivity) getActivity();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity = null;
    }
    public void initUI(){
        // UI Visibility setting
        this.setGone();
        // Time Setting
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = simpleDate.format(mDate);
        // DB init
        dbManger = DBManger.getInstance(getActivity(), "Status", null, 1);
        db = dbManger.getWritableDatabase();
        DBSearch("Stats", getTime);
        // exercise UI Setting
        exerciseTextview.setText("운동 근황");
        // calendarView UI Setting
        calendarView.setSelectedDate(mDate);
    }
    public void setCalendarView(){
        // Calendar state Setting
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2020,1,1)) //최소날
                .setMaximumDate(CalendarDay.from(2050,12,31)) //최대날
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        // Calendar Color Setting
        calendarView.addDecorators(
                new SundayDecorator(), //일요일 빨간색
                new SaturdayDecorator(), //토요일 파란색
                new onDayDecorator()); //오늘 날자에 초록색
        // Calendar Click Listener
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() { //지정 날자 클릭시 효과(날자 하나)
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String month, day;
                // calendar info (2021-3-7) -> DB Info(2021-03-07)
                if (date.getMonth() < 10)
                    month = "0" + (date.getMonth()+1);
                else
                    month = (date.getMonth()+1) + "";
                if (date.getDay() < 10)
                    day = "0" +date.getDay();
                else
                    day = date.getDay() + "";
                // UI Setting
                setGone();
                DBSearch("Stats", date.getYear() + "-" + month + "-" + day);
            }
        });

    }
    // init set Gone
    public void setGone(){
        breakfastTitle.setVisibility(View.GONE);
        breakfastContent.setVisibility(View.GONE);
        breakfastImgview.setVisibility(View.GONE);
        lunchTitle.setVisibility(View.GONE);
        lunchContent.setVisibility(View.GONE);
        lunchImgview.setVisibility(View.GONE);
        dinnerTitle.setVisibility(View.GONE);
        dinnerContent.setVisibility(View.GONE);
        dinnerImgview.setVisibility(View.GONE);
        exerciseTextview.setVisibility(View.GONE);
        newTextview.setVisibility(View.GONE);
        newTextview2.setVisibility(View.GONE);
    }
    // First run data
    public void setVisible(){
        newTextview.setVisibility(View.VISIBLE);
        newTextview2.setVisibility(View.VISIBLE);
    }
    // Find Img Path DB
    void DBSearch(String tableName, String time) {
        Cursor cursor = null;
        try {
            cursor = db.query(tableName, null, "DAY" + " = ?", new String[]{time.toString()}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String tmp;
                    // Bitmap Setting
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    // BREAKFAST
                    if(cursor.getString(cursor.getColumnIndex("BREAKFASTIMG")) != null){
                        // Title set
                        breakfastTitle.setVisibility(View.VISIBLE);
                        breakfastTitle.setText("아침");
                        // Content set
                        // Food, Kcal Find
                        tmp = "음식: " + cursor.getString(cursor.getColumnIndex("BREAKFASTNAME")) +
                                "\n칼로리: " + cursor.getInt(cursor.getColumnIndex("BREAKFAST")) + "Kcal";
                        // If write note
                        if(cursor.getString(cursor.getColumnIndex("BREAKFASTCONTENT")) != null)
                            tmp += "\n기록: " + cursor.getString(cursor.getColumnIndex("BREAKFASTCONTENT"));
                        breakfastContent.setVisibility(View.VISIBLE);
                        breakfastContent.setText(tmp);
                        // bitmap set
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("BREAKFASTIMG")), options);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            breakfastImgview.setVisibility(View.VISIBLE);
                            breakfastImgview.setImageBitmap(bitmap);
                        }catch (Exception e){
                            breakfastImgview.setVisibility(View.GONE);
                            breakfastTitle.setVisibility(View.GONE);
                            breakfastContent.setVisibility(View.GONE);
                        }
                    }
                    // Lunch
                    if(cursor.getString(cursor.getColumnIndex("LUNCHIMG")) != null){
                        // Title set
                        lunchTitle.setVisibility(View.VISIBLE);
                        lunchTitle.setText("점심");
                        // Content set
                        // Food, Kcal Find
                        tmp = "음식: " + cursor.getString(cursor.getColumnIndex("LUNCHNAME")) +
                                "\n칼로리: " + cursor.getInt(cursor.getColumnIndex("LUNCH")) + "Kcal";
                        // If write note
                        if(cursor.getString(cursor.getColumnIndex("LUNCHCONTENT")) != null)
                            tmp += "\n기록: " + cursor.getString(cursor.getColumnIndex("LUNCHCONTENT"));
                        lunchContent.setVisibility(View.VISIBLE);
                        lunchContent.setText(tmp);
                        // bitmap set
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("LUNCHIMG")), options);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            lunchImgview.setVisibility(View.VISIBLE);
                            lunchImgview.setImageBitmap(bitmap);
                        }catch (Exception e){
                            lunchImgview.setVisibility(View.GONE);
                            lunchTitle.setVisibility(View.GONE);
                            lunchContent.setVisibility(View.GONE);
                        }
                    }
                    // Dinner
                    if(cursor.getString(cursor.getColumnIndex("DINNERIMG")) != null){
                        // Title set
                        dinnerTitle.setVisibility(View.VISIBLE);
                        dinnerTitle.setText("저녁");
                        // Content set
                        // Food, Kcal Find
                        tmp = "음식: " + cursor.getString(cursor.getColumnIndex("DINNERNAME")) +
                                "\n칼로리: " + cursor.getInt(cursor.getColumnIndex("DINNER")) + "Kcal";
                        // If write note
                        if(cursor.getString(cursor.getColumnIndex("DINNERCONTENT")) != null)
                            tmp += "\n기록: " + cursor.getString(cursor.getColumnIndex("DINNERCONTENT"));
                        dinnerContent.setVisibility(View.VISIBLE);
                        dinnerContent.setText(tmp);
                        // bitmap set
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("DINNERIMG")), options);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            dinnerImgview.setVisibility(View.VISIBLE);
                            dinnerImgview.setImageBitmap(bitmap);
                        }catch (Exception e){
                            dinnerImgview.setVisibility(View.GONE);
                            dinnerTitle.setVisibility(View.GONE);
                            dinnerContent.setVisibility(View.GONE);
                        }
                    }
                    // breakfast, lunch, dinner == null
                    if(cursor.getString(cursor.getColumnIndex("DINNERIMG")) == null &&
                       cursor.getString(cursor.getColumnIndex("LUNCHIMG")) == null &&
                       cursor.getString(cursor.getColumnIndex("BREAKFASTIMG")) == null){
                        setVisible();
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}