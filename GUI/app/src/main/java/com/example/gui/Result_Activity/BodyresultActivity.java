package com.example.gui.Result_Activity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.gui.DBManger;
import com.example.gui.ui.main.MainActivity;
import com.example.gui.R;

public class BodyresultActivity extends AppCompatActivity {
    DBManger dbManger;
    SQLiteDatabase db;
    static final String DB_name = "status";
    static final String DB_table = "Info";

    EditText weight_TextView, Body_fat_TextView, Skeletal_muscle_TextView, eye_body_TextView;
    String s_weight, s_bodyfat, s_skeletal, s_eyebody;

    Button savebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_result);

        this.init();
        this.setListener();
        this.SetKeyListener();
    }

    public void init() {
        savebtn = findViewById(R.id.exerciseSavebtn);

        weight_TextView = (EditText) findViewById(R.id.weight_TextView);
        Body_fat_TextView = (EditText) findViewById(R.id.Body_fat_TextView);
        Skeletal_muscle_TextView = (EditText) findViewById(R.id.Skeletal_muscle_TextView);
        eye_body_TextView = (EditText) findViewById(R.id.eye_body_TextView);
    }

    public void setListener() {
        dbManger = DBManger.getInstance(this, DB_name, null, 1);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.exerciseSavebtn:
                        s_weight = weight_TextView.getText().toString();
                        s_bodyfat = Body_fat_TextView.getText().toString();
                        s_skeletal = Skeletal_muscle_TextView.getText().toString();
                        s_eyebody = eye_body_TextView.getText().toString();

                        db = dbManger.getWritableDatabase();
                        dbInsert(Integer.parseInt(s_weight), Integer.parseInt(s_bodyfat), Integer.parseInt(s_skeletal), Integer.parseInt(s_eyebody));
                        db.close();
                        dbManger.close();

                        Intent intent;
                        intent = new Intent(BodyresultActivity.this, MainActivity.class);
                        startActivity(intent);
                }
            }
        };
        savebtn.setOnClickListener(listener);
    }

    void dbInsert(Integer i_weight, Integer i_bodyfat, Integer i_skeletal, Integer i_eyebody) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("WEIGHT", i_weight); //PWEIGHT
        contentValues.put("PBODYFAT", i_bodyfat);
        contentValues.put("PMUSCLE", i_skeletal);
        contentValues.put("EYEBODY", i_eyebody);
        db.insert(DB_table, null, contentValues);
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
        View.OnKeyListener keyListener =new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (v.getId()){
                    case R.id.weight_TextView:
                        // 줄바꿈 허용 안함
                        if (keyCode == event.KEYCODE_ENTER)
                            return true;
                        else
                            return false;
                    case R.id.Body_fat_TextView:
                        // 줄바꿈 허용 안함
                        if (keyCode == event.KEYCODE_ENTER)
                            return true;
                        else
                            return false;

                    case R.id.Skeletal_muscle_TextView:
                        // 줄바꿈 허용 안함
                        if (keyCode == event.KEYCODE_ENTER)
                            return true;
                        else
                            return false;

                    case R.id.eye_body_TextView:
                        // 줄바꿈 허용 안함
                        if (keyCode == event.KEYCODE_ENTER)
                            return true;
                        else
                            return false;
                }
                return false;
            }
        };
        weight_TextView.setOnKeyListener(keyListener);
        Body_fat_TextView.setOnKeyListener(keyListener);
        Skeletal_muscle_TextView.setOnKeyListener(keyListener);
        eye_body_TextView.setOnKeyListener(keyListener);
    }
}
