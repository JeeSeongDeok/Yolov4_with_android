package com.example.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SettingView extends Fragment {

    public SettingView() {/*Construct*/}
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_setting, container, false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.

        Button cb;
        ToggleButton tb;
        ImageView iv1;
        ImageView iv2;
        ImageView iv3;
        ImageView iv4;
        ImageView iv5;
        ImageView iv6;

        cb = (Button) rootView.findViewById(R.id.button7);
        tb = (ToggleButton) rootView.findViewById(R.id.togglebutton);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tb.isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(), "잠금을 해제하셨습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "잠금 처리 하셨습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });


        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_dialog, null, false);
                builder.setView(view);

                //dialog내
                RadioButton radioButton1 = (RadioButton)view.findViewById(R.id.radioButton1);
                RadioButton radioButton2 = (RadioButton)view.findViewById(R.id.radioButton2);
                RadioButton radioButton3 = (RadioButton)view.findViewById(R.id.radioButton3);
                RadioButton radioButton4 = (RadioButton)view.findViewById(R.id.radioButton4);
                RadioButton radioButton5 = (RadioButton)view.findViewById(R.id.radioButton5);
                RadioButton radioButton6 = (RadioButton)view.findViewById(R.id.radioButton6);
                RadioButton radioButton7 = (RadioButton)view.findViewById(R.id.radioButton7);
                Button dialog_b = (Button) view.findViewById(R.id.setting_dialog_button);
                AlertDialog dialog = builder.create();

                dialog_b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(radioButton1.isChecked() == true) cb.setText("월요일");
                        else if(radioButton2.isChecked() == true) cb.setText("화요일");
                        else if(radioButton3.isChecked() == true) cb.setText("수요일");
                        else if(radioButton4.isChecked() == true) cb.setText("목요일");
                        else if(radioButton5.isChecked() == true) cb.setText("금요일");
                        else if(radioButton6.isChecked() == true) cb.setText("토요일");
                        else if(radioButton7.isChecked() == true) cb.setText("일요일");

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        int color = ContextCompat.getColor(getActivity(), R.color.white);
        iv1 = (ImageView) rootView.findViewById(R.id.imageView8);
        iv2 = (ImageView) rootView.findViewById(R.id.imageView9);
        iv3 = (ImageView) rootView.findViewById(R.id.imageView10);
        iv4 = (ImageView) rootView.findViewById(R.id.imageView11);
        iv5 = (ImageView) rootView.findViewById(R.id.imageView12);
        iv6 = (ImageView) rootView.findViewById(R.id.imageView13);

        iv1.setColorFilter(color);
        iv2.setColorFilter(color);
        iv3.setColorFilter(color);
        iv4.setColorFilter(color);
        iv5.setColorFilter(color);
        iv6.setColorFilter(color);


        return rootView;
    }
}
