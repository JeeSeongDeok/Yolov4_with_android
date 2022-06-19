package com.example.gui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

//일주일 단위로 본인의 몸무게 변화를 보여줌
public class monthPlanFragment extends Fragment {
    private LineChart lineChart;
    float val1, val2, val3, val4, val5;
    SQLiteDatabase db;
    DBManger dbManger;
    int[] val;
    int[] val_add;
    int[] array_val;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.view_monthplan, container, false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.
        dbManger = DBManger.getInstance(getActivity(), "Status", null, 1);
        lineChart = rootView.findViewById(R.id.linechart);
        this.setLineChart();

        return rootView;
    }
    public void setLineChart() {
        lineChart.invalidate();
        lineChart.clear();
        lineChart.setTouchEnabled(false); //화면 터치 x
        ArrayList<Entry> values = new ArrayList<>();

        //DB 몸무게 적용시키면 아래 지우고 활성화 시키면됨
        //7일치의 데이터있으면 그때부터 적용시키고 싶음.
        val = new int[35];
        array_val = new int[35];
        val_add = new int[35];
        db = dbManger.getWritableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery("select WEIGHT from Info", null);
        }
        cursor.moveToLast(); //제일 마지막 라인부터
        val_add[0] = cursor.getInt(0);
        int count = 1;
        while(cursor.moveToPrevious()){
            if(cursor.isNull(0)) {
                val_add[count] = 0;
            } else
                val_add[count] = cursor.getInt(0);

            System.out.println("에러: " + val_add[count]);
            val[count] = val_add[count];
            if(count == 34) break;
            count++;
        }

        array_val[0] = val_add[0] +val_add[1] +val_add[2] +val_add[3] +val_add[4] +val_add[5] +val_add[6];
        array_val[1] = val_add[7] + val_add[8] + val_add[9] + val_add[10] + val_add[11] + val_add[12] + val_add[13];
        array_val[2] = val_add[14] + val_add[15] + val_add[16] + val_add[17] + val_add[18] + val_add[19] + val_add[20];
        array_val[3] = val_add[21] + val_add[22] + val_add[23] + val_add[24] + val_add[25] + val_add[26] + val_add[27];
        array_val[4] = val_add[28] + val_add[29] + val_add[30] + val_add[31] + val_add[32] + val_add[33] + val_add[34];
        for(int i = 0; i<4;i++){
            val[i] = array_val[i]/7;
        }
        val[0] = array_val[0]/7;
        val[1] = array_val[1]/7;
        val[2] = array_val[2]/7;
        val[3] = array_val[3]/7;
        val[4] = array_val[4]/7;

//        for(int i = 0; i<val.length/2;i++) {
//            int t;
//            t = val[i];
//            val[i] = val[val.length-1 -i];
//            val[val.length-1-i] = t;
//        }

        for(int i =0 ; i<5; i++){
            values.add(new Entry(i, val[i]));
        }
        cursor.close();

        ////////////////////////////////////////////////////////////////////////////////////////////
//        val1 = (float) 20;
//        val2 = (float) 30;
//        val3 = (float) 40;
//        val4 = (float) 50;
//        val5 = (float) 40;
//
//        values.add(new Entry(0, val1));
//        values.add(new Entry(1, val2));
//        values.add(new Entry(2, val3));
//        values.add(new Entry(3, val4));
//        values.add(new Entry(4, val5));
        ////////////////////////////////////////////////////////////////////////////////////////////

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
        lineChart.setData(lineData);
    }

    public static monthPlanFragment newInstance(){
        return new monthPlanFragment();
    }
}
