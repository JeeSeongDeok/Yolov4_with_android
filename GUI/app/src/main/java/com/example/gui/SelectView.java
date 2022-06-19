package com.example.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gui.Result_Activity.BodyresultActivity;
import com.example.gui.Result_Activity.SearchDialog;
import com.example.gui.Result_Activity.ExerciseActivity;
import com.example.gui.Result_Activity.ResultActivity;
import com.example.gui.Result_Activity.WaterActivity;

public class SelectView extends Fragment {
    Button foodbtn, exercisebtn, waterbtn, bodybtn;
    public SelectView(){/*Construct*/}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_select,container,false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.
        //init
        foodbtn = (Button)rootView.findViewById(R.id.Food_add_btn);
        exercisebtn = (Button) rootView.findViewById(R.id.Exercise_add_btn);
        bodybtn = (Button) rootView.findViewById(R.id.Body_add_btn);
        waterbtn = (Button) rootView.findViewById(R.id.Water_add_btn);
        //Listener
        this.Set_Listener();
        return rootView;
    }
    // Button Listener Set Function
    public void Set_Listener(){
        foodbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                startActivity(intent);
            }
        });
        exercisebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                startActivity(intent);
            }
        });
        bodybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BodyresultDialog dia = new BodyresultDialog(getActivity());
                //dia.call();
                Intent intent = new Intent(getActivity(), BodyresultActivity.class);
                startActivity(intent);
            }
        });
        waterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), WaterActivity.class);
                startActivity(intent);
            }
        });
    }


}
