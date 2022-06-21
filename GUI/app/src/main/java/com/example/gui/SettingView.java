package com.example.gui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SettingView extends Fragment {
    private View rootView;
    private ToggleButton lockToggleBtn;
    private Button calendarSelectBtn;

    public SettingView() {/*Construct*/}
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_setting, container, false);
        setupUI();
        return rootView;
    }

    private void setupUI() {
        calendarSelectBtn = (Button) rootView.findViewById(R.id.calendarSelectBtn);
        lockToggleBtn = (ToggleButton) rootView.findViewById(R.id.lockToggleBtn);

        lockToggleBtn.setOnClickListener(v -> toggleOnClick());
        calendarSelectBtn.setOnClickListener(v-> calendarSetting());
    }

    private void toggleOnClick() {
        if (lockToggleBtn.isChecked()) {
            Toast.makeText(getContext(), "잠금을 해제하셨습니다.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "잠금 처리 하셨습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void calendarSetting() {
        AlertDialog dialog = dialogSetting();
        dialog.show();
    }

    private AlertDialog dialogSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_dialog, null, false);
        builder.setView(view);
        RadioButton monBtn = (RadioButton)view.findViewById(R.id.monBtn);
        RadioButton tuesBtn = (RadioButton)view.findViewById(R.id.tueBtn);
        RadioButton wedBtn = (RadioButton)view.findViewById(R.id.wedBtn);
        RadioButton thursBtn = (RadioButton)view.findViewById(R.id.thursBtn);
        RadioButton friBtn = (RadioButton)view.findViewById(R.id.friBtn);
        RadioButton saturdayBtn = (RadioButton)view.findViewById(R.id.saturBtn);
        RadioButton sundayBtn = (RadioButton)view.findViewById(R.id.sunBtn);
        Button dialog_b = (Button) view.findViewById(R.id.setting_dialog_button);
        AlertDialog dialog = builder.create();

        dialog_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monBtn.isChecked()) calendarSelectBtn.setText("월요일");
                else if(tuesBtn.isChecked()) calendarSelectBtn.setText("화요일");
                else if(wedBtn.isChecked()) calendarSelectBtn.setText("수요일");
                else if(thursBtn.isChecked()) calendarSelectBtn.setText("목요일");
                else if(friBtn.isChecked()) calendarSelectBtn.setText("금요일");
                else if(saturdayBtn.isChecked()) calendarSelectBtn.setText("토요일");
                else if(sundayBtn.isChecked()) calendarSelectBtn.setText("일요일");

                dialog.dismiss();
            }
        });
        return dialog;

    }


}
