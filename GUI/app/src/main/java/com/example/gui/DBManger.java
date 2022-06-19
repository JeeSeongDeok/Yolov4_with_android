package com.example.gui;
/*
 * 참고주소
 * 데이터베이스 - http://blog.naver.com/PostView.nhn?blogId=qbxlvnf11&logNo=221406135285&categoryNo=44&parentCategoryNo=0&viewDate=&currentPage=1&postListTopCurrentPage=1&from=postView
 * */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManger extends SQLiteOpenHelper {
    static final String TABLE_NAME = "Info";
    private static DBManger dbManger = null;

    // Singletone
    public static DBManger getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        if (dbManger == null)
            dbManger = new DBManger(context, name, factory, version);
        return dbManger;
    }
    // Construct
    public DBManger(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);// DB Create
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME +
                "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "AGE INTEGER, " +
                "WEIGHT DOUBLE, " +
                "HEIGHT DOUBLE, " +
                "PBODYFAT INTEGER," +
                "PMUSCLE INTEGER," +
                "EYEBODY TEXT," +
                "SEX INTEGER);";
        db.execSQL(createQuery);
        createQuery = "CREATE TABLE " + "Stats" +
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
        db.execSQL(createQuery);
        createQuery = "create table " + "exercise" +
                "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT, " +
                "STIME TEXT, " +
                "FTIME TEXT, " +
                "TTIME TEXT, " +
                "CONTENT TEXT);";
        db.execSQL(createQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String createQuery = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(createQuery);
    }
}
