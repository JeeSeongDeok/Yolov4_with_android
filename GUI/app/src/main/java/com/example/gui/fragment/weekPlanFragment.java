package com.example.gui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gui.DBManger;
import com.example.gui.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

//일주일간 먹은 음식 총 칼로리를 숫자로 표현해서 차트로 보여주기
public class weekPlanFragment extends Fragment {
    private LineChart lineChart;
    int[] val;
    int[] array_val;
    Button button_weight, button_cal;
    SQLiteDatabase db;
    DBManger dbManger;

    // set1.setColor(Color.BLACK, Color.TRANSPARENT);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_weekendplan, container, false);
        dbManger = DBManger.getInstance(getActivity(), "Status", null, 1);
        //linechart
        lineChart = rootView.findViewById(R.id.linechart);
        lineChart.setVisibility(View.INVISIBLE);
        button_weight = (Button)rootView.findViewById(R.id.button_weight);
        button_cal = (Button)rootView.findViewById(R.id.button_cal);
        button_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setVisibility(View.VISIBLE);
                setLineChart();
            }
        });
        button_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setVisibility(View.VISIBLE);
                setLineChart2();
            }
        });

        return rootView;
    }

    public void setLineChart() {
        lineChart.invalidate();
        lineChart.clear();
        lineChart.setTouchEnabled(false); //화면 터치 x
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        //DB 몸무게 적용시키면 아래 지우고 활성화 시키면됨
        //7일치의 데이터있으면 그때부터 적용시키고 싶음.
        array_val = new int[7];
        val = new int[7];
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery("select WEIGHT from Info", null);
        }
        cursor.moveToLast(); //제일 마지막 라인부터
        val[0]=cursor.getInt(0);
        int count = 1;
        while(cursor.moveToPrevious()){
            if(cursor.isNull(0)) {
                array_val[count] = 0;
            } else
                array_val[count] = cursor.getInt(0);

            System.out.println("에러: " + array_val[count]);
            val[count] = array_val[count];
            if(count == 6) break;
            count++;
        }

        //제일 최근게 오른쪽으로 가야해서 배열을 역순으로 재정렬
        for(int i = 0; i<val.length/2;i++) {
            int t;
            t = val[i];
            val[i] = val[val.length-1 -i];
            val[val.length-1-i] = t;
        }

        for(int i = 0; i < val.length; i++){
            values.add(new Entry(i, val[i]));
        }
        cursor.close();

        LineDataSet set1 = new LineDataSet(values, "일주일 몸무게 변화");
        set1.setValueTextColor(Color.WHITE);
        set1.setCircleHoleColor(0xff333333);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.YELLOW);
        // create a data object with the data sets
        LineData lineData = new LineData(set1);
        lineData.setValueTextColor(0xffC3F25E);
        lineData.setValueTextSize(15);
        //X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //Y left
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setTextColor(0xff333333); //Y축 텍스트 컬러 설정
        yAxisLeft.setGridColor(0xff333333); // Y축 줄의 컬러 설정
        yAxisLeft.setTextColor(Color.WHITE);
        //Y right
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setTextColor(Color.WHITE);
        // set data
        lineChart.setDescription(null);
        lineChart.setData(lineData);
    }

    public void setLineChart2() {
        lineChart.invalidate();
        lineChart.clear();
        lineChart.setTouchEnabled(false); //화면 터치 x
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        int []breakfast;
        int[] lunch;
        int[] dinner;
        int[] total;
        //DB 몸무게 적용시키면 아래 지우고 활성화 시키면됨
        //7일치의 데이터있으면 그때부터 적용시키고 싶음.
        breakfast = new int[7];
        lunch = new int[7];
        dinner = new int[7];
        total = new int[7];
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery("select BREAKFAST,LUNCH,DINNER from Stats", null);
        }
        cursor.moveToLast(); //제일 마지막 라인부터
        int count= 0;
        while(cursor.moveToPrevious()){
            if(cursor.isNull(0)) {
                breakfast[count] = 0;
            } else
                breakfast[count] = cursor.getInt(0);
            if(cursor.isNull(1)) {
                lunch[count] = 0;
            } else
                lunch[count] = cursor.getInt(1);
            if(cursor.isNull(2)) {
                dinner[count] = 0;
            } else
                dinner[count] = cursor.getInt(2);
            if(count == 6) break;
            count++;
        }
        for(int i = 0; i<breakfast.length;i++){
            total[i] = breakfast[i] + lunch[i] + dinner[i];
        }

        //제일 최근게 오른쪽으로 가야해서 배열을 역순으로 재정렬
        for(int i = 0; i<total.length/2;i++) {
            int t;
            t = total[i];
            total[i] = total[total.length-1 -i];
            total[total.length-1-i] = t;
        }

        for(int i = 0; i < total.length; i++){
            values.add(new Entry(i, total[i]));
        }
        cursor.close();

        LineDataSet set1 = new LineDataSet(values, "일주일 몸무게 변화");
        set1.setValueTextColor(Color.WHITE);
        set1.setCircleHoleColor(0xff333333);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.YELLOW);
        // create a data object with the data sets
        LineData lineData = new LineData(set1);
        lineData.setValueTextColor(0xffC3F25E);
        lineData.setValueTextSize(15);
        //X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //Y left
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setTextColor(0xff333333); //Y축 텍스트 컬러 설정
        yAxisLeft.setGridColor(0xff333333); // Y축 줄의 컬러 설정
        yAxisLeft.setTextColor(Color.WHITE);
        //Y right
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setTextColor(Color.WHITE);
        // set data
        lineChart.setDescription(null);
        lineChart.setData(lineData);
    }

    public static weekPlanFragment newInstance() {
        return new weekPlanFragment();
    }
}
