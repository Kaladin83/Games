package com.example.maratbe.dataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.maratbe.domain.Theme;
import com.example.maratbe.games.R;
import com.google.gson.Gson;

public class MySharedPreferences {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public MySharedPreferences(Context context)
    {
        sp = context.getSharedPreferences("games", Context.MODE_PRIVATE);
    }

    public void saveTheme(Theme theme)
    {
        Gson gson = new Gson();
        String json = gson.toJson(theme);
        editor = sp.edit();
        editor.putString("Theme", json);
        editor.apply();
    }

    public void clearTheme()
    {
        editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public Theme getCurrentTheme()
    {
        Gson gson = new Gson();
        String json = sp.getString("Theme", "");
        return json.equals("")? null: gson.fromJson(json, Theme.class);
    }
}
