package com.example.gui.ui.setting;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import com.example.gui.R;

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
        calendarSelectBtn.setOnClickListener(v -> calendarSetting());
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
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String day;
                switch (checkedId) {
                    case R.id.monBtn:
                        day = "월요일";
                        break;
                    case R.id.tuesday:
                        day = "화요일";
                        break;
                    case R.id.wednesday:
                        day = "수요일";
                        break;
                    case R.id.thursday:
                        day = "목요일";
                        break;
                    case R.id.friday:
                        day = "금요일";
                        break;
                    case R.id.saturday:
                        day = "토요일";
                        break;
                    case R.id.sunday:
                        day = "일요일";
                        break;
                    default:
                        day = "Error";
                        break;
                }
                calendarSelectBtn.setText(day);
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;

    }

}
