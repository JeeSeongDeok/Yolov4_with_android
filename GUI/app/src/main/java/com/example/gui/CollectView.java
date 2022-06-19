package com.example.gui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gui.adapter.collect_adapter;
import com.example.gui.adapter.collect_data;

import java.util.ArrayList;

public class CollectView extends Fragment {
    private ArrayList<collect_data> dataList;
    SQLiteDatabase db;
    DBManger dbManger;
    public CollectView(){/*Construct*/}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_collect,container,false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.
        // init
        this.init();
        // RecyclerView set
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new collect_adapter(dataList));  // Adapter 등록
        return rootView;
    }
    // Init Function
    public void init(){
        dbManger = DBManger.getInstance(getActivity(), "Status", null, 1);
        db = dbManger.getWritableDatabase();
        dataList = new ArrayList<>();
        DBSearch("Stats");
    }
    public static CollectView newInstance(){
        return new CollectView();
    }
    void DBSearch(String tableName) {
        Cursor cursor = null;
        try {
            cursor = db.query(tableName, null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String day = cursor.getString(cursor.getColumnIndex("DAY"));
                    String breakfast = cursor.getString(cursor.getColumnIndex("BREAKFASTIMG"));
                    String lunch = cursor.getString(cursor.getColumnIndex("LUNCHIMG"));
                    String dinner = cursor.getString(cursor.getColumnIndex("DINNERIMG"));
                    if(breakfast != null || lunch != null || dinner != null)
                        dataList.add(new collect_data(day, breakfast, lunch, dinner));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
