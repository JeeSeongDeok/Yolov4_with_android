package com.example.gui.Result_Activity;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gui.DBManger;
import com.example.gui.MainActivity;
import com.example.gui.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExerciseActivity extends AppCompatActivity {
    DBManger dbManger;
    SQLiteDatabase db;
    static final String DB_name = "status";
    static final String DB_table = "exercise";
    String title, stime, ftime, ttime, content;

    private TimePickerDialog.OnTimeSetListener starttimeSetListener, finishtimeSetListener;

    int starttotalhour, starttotalminute, finishtotalhour, finishtotalminute, totalhour, totalminute;

    Button starttimeButton, finishtimeButton, totaltimeButton;
    TextView starttimeText, finishtimeText, totaltimeText;

    EditText exercise_title_EditText;
    EditText eye_body_EditText;
    Button Save_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        this.Init();
        this.SetListener();
        this.SetKeyListener();
    }

    public void Init(){
        //시간 설정
        starttimeButton = (Button)findViewById(R.id.starttimeButton);
        starttimeText = (TextView)findViewById(R.id.starttimeText);

        finishtimeButton = (Button)findViewById(R.id.finishtimeButton);
        finishtimeText = (TextView)findViewById(R.id.finishtimeText);

        totaltimeButton = (Button)findViewById(R.id.totaltimeButton);
        totaltimeText = (TextView)findViewById(R.id.totaltimeText);
        //EditText
        exercise_title_EditText = (EditText) findViewById(R.id.exercise_title_EditText);
        eye_body_EditText = (EditText) findViewById(R.id.eye_body_EditText);
        //save
        Save_btn = (Button)findViewById(R.id.exerciseSavebtn);
    }

    public void SetListener(){
        starttimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                starttimeText.setText(hourOfDay + "시 " + minute + "분 시작");
                starttotalhour = hourOfDay;
                starttotalminute = minute;
            }
        };
        finishtimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                finishtimeText.setText(hourOfDay + "시 " + minute + "분 끝");
                finishtotalhour = hourOfDay;
                finishtotalminute = minute;
            }
        };

        dbManger = DBManger.getInstance(this, DB_name, null, 1);
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.starttimeButton:
                        TimePickerDialog sdialog = new TimePickerDialog(ExerciseActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, starttimeSetListener, 15, 30, false);
                        sdialog.setTitle("운동 시작 시간");
                        sdialog.show();
                        break;
                    case R.id.finishtimeButton:
                        TimePickerDialog fdialog = new TimePickerDialog(ExerciseActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, finishtimeSetListener, 15, 30, false);
                        fdialog.setTitle("운동 시작 시간");
                        fdialog.show();
                        break;
                    case R.id.totaltimeButton:
                        //Math.abs()
                        if(starttotalminute > finishtotalminute) {
                            totalhour = (finishtotalhour - starttotalhour) - 1;
                        }
                        else{
                            totalhour = finishtotalhour - starttotalhour;
                        }
                        if(starttotalminute > finishtotalminute) {
                            totalminute = 60 - (starttotalminute - finishtotalminute);
                        }
                        else{
                            totalminute = finishtotalminute - starttotalminute;
                        }
                        totaltimeText.setText(Math.abs(totalhour) + "시간 " + Math.abs(totalminute) + "분");
                        totalhour = 0;
                        totalminute = 0;
                        break;
                    case R.id.exerciseSavebtn:
                        title = exercise_title_EditText.getText().toString();
                        stime = starttimeText.getText().toString();
                        ftime = finishtimeText.getText().toString();
                        ttime = totaltimeText.getText().toString();
                        content = eye_body_EditText.getText().toString();

                        db = dbManger.getWritableDatabase();
                        dbInsert(title, stime, ftime, ttime, content);
                        //db.close();
                        //dbManger.close();

                        Intent intent;
                        intent = new Intent(ExerciseActivity.this, MainActivity.class);
                        startActivity(intent);

                        break;
                }
            }
        };
        starttimeButton.setOnClickListener(listener);
        finishtimeButton.setOnClickListener(listener);
        totaltimeButton.setOnClickListener(listener);
        Save_btn.setOnClickListener(listener);

    }

    void dbInsert(String s_title, String i_stime, String i_ftime, String i_ttime, String s_content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE", s_title);
        contentValues.put("STIME", i_stime);
        contentValues.put("FTIME", i_ftime);
        contentValues.put("TTIME", i_ttime);
        contentValues.put("CONTENT", s_content);
        long id = db.insert(DB_table, null, contentValues);
        contentValues = new ContentValues();
        contentValues.put("EXCERCISEDB", id);
        // Get Time Data
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = simpleDate.format(mDate);
        String dayArr[] = {getTime};
        // Need Logic Fix -> EXCERCISEDB가 있으면 거기에서 추가해서 연결시키기
        db.update("Stats", contentValues, "DAY = ?", dayArr);

    }
    // BackGround Touch Function
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // IF BackGround Touch, Turn off Keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    public void SetKeyListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.exercise_title_EditText:
                        // 줄바꿈 허용 안함
                        if (keyCode == event.KEYCODE_ENTER)
                            return true;
                        else
                            return false;

                    case R.id.eye_body_EditText:
                        // 줄바꿈 허용 안함
                        if (keyCode == event.KEYCODE_ENTER)
                            return true;
                        else
                            return false;
                }
                return false;
            }
        };
        exercise_title_EditText.setOnKeyListener(keyListener);
        eye_body_EditText.setOnKeyListener(keyListener);
    }
}
