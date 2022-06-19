package com.example.gui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gui.ui.main.MainActivity;

public class RequestActivity extends AppCompatActivity {
    Button check_btn;
    private final int request_Code=101;
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_authority);
        init();
        setListener();
    }
    public void init(){
        check_btn = findViewById(R.id.check_button);
    }
    public void setListener(){
        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermission()){
                    ActivityCompat.requestPermissions(RequestActivity.this, permissions, request_Code);
                }else{
                    Intent intent = new Intent(RequestActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(checkPermission()){
            Intent intent = new Intent(RequestActivity.this, MainActivity.class);
            startActivity(intent);
        }else{
            ActivityCompat.requestPermissions(RequestActivity.this, permissions, request_Code);
        }
    }
    public boolean checkPermission() {
        for (String pm : permissions){
            if (ActivityCompat.checkSelfPermission(this, pm) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
