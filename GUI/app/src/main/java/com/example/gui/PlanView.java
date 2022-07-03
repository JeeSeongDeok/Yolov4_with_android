package com.example.gui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gui.fragment.dayPlanFragment;
import com.example.gui.fragment.weekPlanFragment;
import com.example.gui.fragment.monthPlanFragment;

import java.io.File;

/*
 * 해야하는 일
 * DB 데이터로 전체 칼로리 계산하기
 * 서버 Connect시 Progress Dialog 실행 그 후 끝나기 전까지 Wait 하는 부분 만들기
 * */

public class PlanView extends Fragment  {

    AlertDialog.Builder builder;
    File photofile;
    String mCurrentPhotoPath;
    private FragmentManager fragmentManager;
    private Fragment fa, fb, fc;
    Button plan_button1, plan_button2, plan_button3;

    public PlanView(){/*Construct*/}
    public static PlanView newInstance(){
        return new PlanView();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_totalplan,container,false);
        fragmentManager = getActivity().getSupportFragmentManager();
        fa = new dayPlanFragment();
        fragmentManager.beginTransaction().replace(R.id.plan_frame, fa).commit();
        plan_button1 = (Button)rootView.findViewById(R.id.plan_button1);
        plan_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                dayPlanFragment dayPlanFragment = new dayPlanFragment();
                transaction.replace(R.id.plan_frame, dayPlanFragment);
                transaction.commit();
            }
        });
        plan_button2 = (Button)rootView.findViewById(R.id.plan_button2);
        plan_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                weekPlanFragment weekPlanFragment = new weekPlanFragment();
                transaction.replace(R.id.plan_frame, weekPlanFragment);
                transaction.commit();
            }
        });
        plan_button3 = (Button)rootView.findViewById(R.id.plan_button3);
        plan_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                monthPlanFragment monthPlanFragment = new monthPlanFragment();
                transaction.replace(R.id.plan_frame, monthPlanFragment);
                transaction.commit();
            }
        });

        return rootView;
    }
}
