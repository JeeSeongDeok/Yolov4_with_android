package com.example.gui.adapter;

public class collect_data {
    private String Day, Breakfast, Lunch, Dinner;

    public collect_data(String day, String breakfast, String Lunch, String Dinner){
        this.Day = day;
        this.Breakfast = breakfast;
        this.Lunch = Lunch;
        this.Dinner = Dinner;
    }
    public String getDay(){
        return Day;
    }
    public void setDay(String day){
        this.Day = day;
    }
    public String getBreakfast(){
        return this.Breakfast;
    }
    public String getLunch() {return this.Lunch;}
    public String getDinner(){ return this.Dinner;}
}
