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
        editor.apply();
    }

    public boolean loadNightModeState(){
        return sharedPreferences.getBoolean("nightMode", false);
    }

    public void saveVariables(String edit, int amount){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("edit", edit);
        editor.putInt("amount", amount);
        editor.apply();
    }

    public String loadEdit(){
        return sharedPreferences.getString("edit", "");
    }

    public int loadAmount(){
        return sharedPreferences.getInt("amount", 0);
    }
}
