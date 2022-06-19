package com.example.gui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gui.DBManger;
import com.example.gui.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

//하루 클릭 시 오늘 하루 먹은 칼로리를 올리고 권장 칼로리 그래프로 보여주기
//전체, 아침, 점심, 저녁으로
public class dayPlanFragment extends Fragment {
    BarChart barChart, breakfast_barChart, lunch_barChart, dinner_barChart;
    TextView cal_text, breakfast_textview, lunch_textview, dinner_textview;
    int breakfast_cal, lunch_cal, dinner_cal;
    int age,weight, height, sex;
    double User_RMR;
    double standard = 1000;
    int t = 0xffC3F25E;
    int b = 0xff04B2D9;
    int l = 0xff04B2D9;
    int d = 0xff04B2D9;
    TextView a;

    SQLiteDatabase db;
    DBManger dbManger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_dayplan, container, false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.
        // init
        cal_text = (TextView) rootView.findViewById(R.id.cal_textview);
        breakfast_textview = (TextView) rootView.findViewById(R.id.breakfast_textview);
        lunch_textview = (TextView) rootView.findViewById(R.id.lunch_textview);
        dinner_textview = (TextView) rootView.findViewById(R.id.dinner_textview);
        dbManger = DBManger.getInstance(getActivity(), "Status", null, 1);
        a = (TextView) rootView.findViewById(R.id.a);
        //Barchart
        barChart = rootView.findViewById(R.id.chart);
        breakfast_barChart = rootView.findViewById(R.id.breakfast_chart);
        lunch_barChart = rootView.findViewById(R.id.lunch_chart);
        dinner_barChart = rootView.findViewById(R.id.dinner_chart);
        breakfast_barchart();
        lunch_barchart();
        dinner_barchart();
        barchart();
        return rootView;
    }

    // Bar Graph Set Function
    public void barchart() {
        ArrayList data_Val;
        data_Val = new ArrayList();
        // Today Cal
        int consum = breakfast_cal + lunch_cal + dinner_cal;
        // X Graph Setting
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelCount(0);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        // Find User Info
        // 일단 남자 기준으로 수정 필요

        //DB
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery("select * from Info", null);
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToFirst();
            age = cursor.getInt(0);
            weight = cursor.getInt(1);
            height = cursor.getInt(2);
            sex = cursor.getInt(3);
        }
        cursor.close();

        consum = breakfast_cal + lunch_cal + dinner_cal;
        User_RMR = 66.47 + (13.75 * weight) + (5 * height) - (6.76 * age);
        cal_text.setText(consum + "/" + User_RMR + " cal Left");
        // Y Graph Setting
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum((float) User_RMR);
        yAxisLeft.setTextColor(Color.WHITE);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        data_Val.add(new BarEntry(0, consum));
        BarDataSet barDataSet = new BarDataSet(data_Val, "");
        barDataSet.setColor(t); // barchart Color Set
        // Chart Animation Setting
        barChart.animateY(3000);
        barChart.setDescription(null);
        barChart.setTouchEnabled(false);
        // Remove lower letter
        Legend i = barChart.getLegend();
        i.setEnabled(false);
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.invalidate();
    }

    public void breakfast_barchart() {
        ArrayList data_Val;
        data_Val = new ArrayList();
        // Today Cal
        // X Graph Setting
        XAxis xAxis = breakfast_barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelCount(0);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        // Find User Info
//        breakfast_cal = 700;

        //DB
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if(db != null) {
            //cursor = db.query("Stats", null, null, null, null, null, null);
            cursor = db.rawQuery("select BREAKFAST from Stats", null);
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToLast();
            breakfast_cal = cursor.getInt(cursor.getColumnIndex("BREAKFAST"));
        }
        cursor.close();

        //먹은 칼로리 / 권장 칼로리
        breakfast_textview.setText(breakfast_cal + "/" + standard + " cal Left");
        // Y Graph Setting
        YAxis yAxisLeft = breakfast_barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum((float) standard);
        yAxisLeft.setTextColor(Color.WHITE);
        YAxis yAxisRight = breakfast_barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        data_Val.add(new BarEntry(0, breakfast_cal));
        BarDataSet barDataSet = new BarDataSet(data_Val, "");
        barDataSet.setColor(b); // barchart Color Set
        // Chart Animation Setting
        breakfast_barChart.animateY(3000);
        breakfast_barChart.setDescription(null);
        breakfast_barChart.setTouchEnabled(false);
        // Remove lower letter
        Legend i = breakfast_barChart.getLegend();
        i.setEnabled(false);
        BarData data = new BarData(barDataSet);
        breakfast_barChart.setData(data);
        breakfast_barChart.invalidate();
    }

    public void lunch_barchart() {
        ArrayList data_Val;
        data_Val = new ArrayList();
        // Today Cal
        // X Graph Setting
        XAxis xAxis = lunch_barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelCount(0);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        // Find User Info
        // 일단 남자 기준으로 수정 필요

        //DB
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery("select LUNCH from Stats", null);
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToLast();
            lunch_cal = cursor.getInt(cursor.getColumnIndex("LUNCH"));
        }
        cursor.close();

        lunch_textview.setText(lunch_cal + "/" + standard + " cal Left");
        // Y Graph Setting
        YAxis yAxisLeft = lunch_barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum((float) standard);
        yAxisLeft.setTextColor(Color.WHITE);
        YAxis yAxisRight = lunch_barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        data_Val.add(new BarEntry(0, lunch_cal));
        BarDataSet barDataSet = new BarDataSet(data_Val, "");
        barDataSet.setColor(l); // barchart Color Set
        // Chart Animation Setting
        lunch_barChart.animateY(3000);
        lunch_barChart.setDescription(null);
        lunch_barChart.setTouchEnabled(false);
        // Remove lower letter
        Legend i = lunch_barChart.getLegend();
        i.setEnabled(false);
        BarData data = new BarData(barDataSet);
        lunch_barChart.setData(data);
        lunch_barChart.invalidate();
    }

    public void dinner_barchart() {
        ArrayList data_Val;
        data_Val = new ArrayList();
        // X Graph Setting
        XAxis xAxis = dinner_barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelCount(0);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        // Find User Info
        // 일단 남자 기준으로 수정 필요

        //DB
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery("select DINNER from Stats", null);
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToLast();
            dinner_cal = cursor.getInt(cursor.getColumnIndex("DINNER"));
        }
        cursor.close();

        dinner_textview.setText(dinner_cal + "/" + standard + " cal Left");
        // Y Graph Setting
        YAxis yAxisLeft = dinner_barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum((float) standard);
        yAxisLeft.setTextColor(Color.WHITE);
        YAxis yAxisRight = dinner_barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        data_Val.add(new BarEntry(0, dinner_cal));
        BarDataSet barDataSet = new BarDataSet(data_Val, "");
        barDataSet.setColor(d); // barchart Color Set
        // Chart Animation Setting
        dinner_barChart.animateY(3000);
        dinner_barChart.setDescription(null);
        dinner_barChart.setTouchEnabled(false);
        // Remove lower letter
        Legend i = dinner_barChart.getLegend();
        i.setEnabled(false);
        BarData data = new BarData(barDataSet);
        dinner_barChart.setData(data);
        dinner_barChart.invalidate();
    }

    public static dayPlanFragment newInstance() {
        return new dayPlanFragment();
    }
}
