package com.example.maratbe.dataBase;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public MySharedPreferences(Context context)
    {
        this.context = context;
        sp = context.getSharedPreferences("games", context.MODE_PRIVATE);;

    }

    public void saveTheme(int theme)
    {
        editor = sp.edit();
        editor.putInt("theme", theme);
        editor.apply();
    }

    public int getCurrentTheme()
    {
        return sp.getInt("theme", 0);
    }
}
