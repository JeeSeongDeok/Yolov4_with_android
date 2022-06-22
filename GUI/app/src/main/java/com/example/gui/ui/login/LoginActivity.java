package com.example.gui.ui.login;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gui.DBManger;
import com.example.gui.R;
import com.example.gui.RequestActivity;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity implements Serializable {
    EditText ageEdit, heightEdit, weightEdit;
    RadioGroup radioGroup;
    RadioButton manRadioButton, womanRadioButton;
    Button checkButton;
    String age, height, weight;
    SQLiteDatabase db;
    DBManger dbManger;
    int Sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbManger = DBManger.getInstance(this, "Status", null, 1);
        setupUI();
    }
    // init UI Function
    public void setupUI() {
        ageEdit = (EditText) findViewById(R.id.age_edit);
        heightEdit = (EditText) findViewById(R.id.height_edit);
        weightEdit = (EditText) findViewById(R.id.weight_edit);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        manRadioButton = (RadioButton) findViewById(R.id.man_radioButton);
        womanRadioButton = (RadioButton) findViewById(R.id.woman_radioButton);
        checkButton = (Button) findViewById(R.id.check_button);

        checkButton.setOnClickListener(view -> insertInfo());
    }

    private void insertInfo() {
        if(isAllInsertInfo()) {

        }

        if (ageEdit.getText().toString().length() == 0) {
            Toast.makeText(LoginActivity.this, "나이 입력", Toast.LENGTH_SHORT).show();
        } else if (heightEdit.getText().toString().length() == 0) {
            Toast.makeText(LoginActivity.this, "몸무게 입력", Toast.LENGTH_SHORT).show();
        } else if (weightEdit.getText().toString().length() == 0) {
            Toast.makeText(LoginActivity.this, "신장 입력", Toast.LENGTH_SHORT).show();
        } else if(manRadioButton.isChecked() == false && womanRadioButton.isChecked() == false){
            Toast.makeText(LoginActivity.this, "성별 입력", Toast.LENGTH_SHORT).show();
        }
        else {
            age = ageEdit.getText().toString();
            height = heightEdit.getText().toString();
            weight = weightEdit.getText().toString();
            if (manRadioButton.isChecked() == true)
                Sex = 0;
            else
                Sex = 1;

            Intent intent = new Intent(LoginActivity.this, RequestActivity.class);
            db = dbManger.getWritableDatabase();
            dbInsert(Integer.parseInt(age), Double.parseDouble(weight), Double.parseDouble(height), Sex);
            db.close();
            dbManger.close();
            startActivity(intent);
        }
    }

    private Boolean isAllInsertInfo() {
        if (ageEdit.getText().toString().isEmpty() || heightEdit.getText().toString().isEmpty() || we)
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // IF BackGround Touch, Turn off Keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    void dbInsert(Integer age, Double weight, Double height, Integer Sex) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("AGE", age);
        contentValues.put("WEIGHT", weight);
        contentValues.put("HEIGHT", height);
        contentValues.put("SEX", Sex);
        db.insert("Info", null, contentValues);
    }
    // Set Listener Function
    public void SetListener() {
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.check_button:
                        // Need Age Info
                        if (ageEdit.getText().toString().length() == 0) {
                            Toast.makeText(LoginActivity.this, "나이 입력", Toast.LENGTH_SHORT).show();
                        }
                        // Need Height Info
                        else if (heightEdit.getText().toString().length() == 0) {
                            Toast.makeText(LoginActivity.this, "몸무게 입력", Toast.LENGTH_SHORT).show();
                        }
                        // Need Weight Info
                        else if (weightEdit.getText().toString().length() == 0) {
                            Toast.makeText(LoginActivity.this, "신장 입력", Toast.LENGTH_SHORT).show();
                        }
                        // Need Sex Info
                        else if(manRadioButton.isChecked() == false && womanRadioButton.isChecked() == false){
                            Toast.makeText(LoginActivity.this, "성별 입력", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            age = ageEdit.getText().toString();
                            height = heightEdit.getText().toString();
                            weight = weightEdit.getText().toString();
                            if (manRadioButton.isChecked() == true)
                                Sex = 0;
                            else
                                Sex = 1;

                            Intent intent = new Intent(LoginActivity.this, RequestActivity.class);
                            db = dbManger.getWritableDatabase();
                            dbInsert(Integer.parseInt(age), Double.parseDouble(weight), Double.parseDouble(height), Sex);
                            db.close();
                            dbManger.close();
                            startActivity(intent);
                        }
                }
            }
        };
        checkButton.setOnClickListener(listener);
        View.OnKeyListener keyListener =new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (v.getId()){
                    case R.id.age_edit:
                        // 줄바꿈 허용 안함
                        return keyCode == KeyEvent.KEYCODE_ENTER;
                    case R.id.weight_edit:
                        // 줄바꿈 허용 안함
                        return keyCode == KeyEvent.KEYCODE_ENTER;

                    case R.id.height_edit:
                        // 줄바꿈 허용 안함
                        return keyCode == KeyEvent.KEYCODE_ENTER;
                }
                return false;
            }
        };
        ageEdit.setOnKeyListener(keyListener);
        heightEdit.setOnKeyListener(keyListener);
        weightEdit.setOnKeyListener(keyListener);
    }
}