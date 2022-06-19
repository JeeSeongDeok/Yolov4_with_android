package com.example.gui;
/*
 * DBManager.java
 * DB Repository
 * */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBManger extends SQLiteOpenHelper {
    static final String TABLE_NAME = "Info";
    private static DBManger dbManger = null;

    public static DBManger getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        if (dbManger == null)
            dbManger = new DBManger(context, name, factory, version);
        return dbManger;
    }

    public DBManger(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DBManger getInstance() {
        return dbManger;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createInfoTableQuery());
        db.execSQL(createStatsTableQuery());
        db.execSQL(createExerciseTableQuery());
        initDBData(db);
    }

    private String createInfoTableQuery() {
        return "CREATE TABLE " +
                TABLE_NAME +
                "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "AGE INTEGER, " +
                "WEIGHT DOUBLE, " +
                "HEIGHT DOUBLE, " +
                "PBODYFAT INTEGER," +
                "PMUSCLE INTEGER," +
                "EYEBODY TEXT," +
                "SEX INTEGER);";
    }

    private String createStatsTableQuery() {
        return "CREATE TABLE " +
                "Stats" +
                "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DAY TEXT, " +
                "EXCERCISEDB INTEGER, " +
                "BREAKFASTIMG TEXT, " +
                "LUNCHIMG TEXT, " +
                "DINNERIMG TEXT, " +
                "BREAKFAST INTEGER, " +
                "LUNCH INTEGER, " +
                "DINNER INTEGER, " +
                "BREAKFASTNAME TEXT, " +
                "LUNCHNAME TEXT, " +
                "DINNERNAME TEXT, " +
                "BREAKFASTCONTENT TEXT, " +
                "LUNCHCONTENT TEXT," +
                "DINNERCONTENT TEXT);";
    }

    private String createExerciseTableQuery() {
        return "create table " +
                "exercise" +
                "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT, " +
                "STIME TEXT, " +
                "FTIME TEXT, " +
                "TTIME TEXT, " +
                "CONTENT TEXT);";
    }

    private void initDBData(SQLiteDatabase db) {
        Cursor cursor = null;
        String id = null;
        try {
            cursor = db.query("Stats", null, "DAY = ?", new String[]{getTodayDate()}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    id = cursor.getString(cursor.getColumnIndex("ID"));
                }
            }
            if(id == null){
                ContentValues contentValues = new ContentValues();
                contentValues.put("DAY", getTodayDate());
                contentValues.put("BREAKFAST", 0);
                contentValues.put("LUNCH", 0);
                contentValues.put("DINNER", 0);
                db.insert("Stats", null, contentValues);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getTodayDate() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = simpleDate.format(mDate);
        return getTime;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String createQuery = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(createQuery);
    }
}
