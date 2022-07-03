package com.example.gui.adapter;

/*
* MainActivity used this adapter
* Manage Fragment
* 2021.02.03
*/
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.gui.CollectView;
import com.example.gui.PlanView;
import com.example.gui.SelectView;
import com.example.gui.CalendarView;
import com.example.gui.ui.setting.SettingView;

public class Fragment_Pager_Adapter extends FragmentPagerAdapter {
    public Fragment_Pager_Adapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                return new CalendarView();
            case 1:
                return new CollectView();
            case 2:
                return new SelectView();
            case 3:
                return new PlanView();
            case 4:
                return new SettingView();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
