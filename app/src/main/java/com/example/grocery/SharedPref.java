package com.example.grocery;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("themeSettings", Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("nightMode", state);
        editor.commit();
    }

    public boolean loadNightModeState(){
        Boolean state = sharedPreferences.getBoolean("nightMode", false);
        return state;
    }

    public void saveVeriables(String edit, int amount){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("edit", edit);
        editor.putInt("amount", amount);
        editor.commit();
    }

    public String loadEdit(){
        String edit = sharedPreferences.getString("edit", "");
        return edit;
    }

    public int loadAmount(){
        int amount = sharedPreferences.getInt("amount", 0);
        return amount;
    }
}
