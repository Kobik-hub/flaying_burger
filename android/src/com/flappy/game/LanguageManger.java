package com.flappy.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageManger {
    private  Context ct;
    private final SharedPreferences sharedPreferences;
    public LanguageManger(Context ctx){
        ct = ctx;
        sharedPreferences =  ct.getSharedPreferences("lang",Context.MODE_PRIVATE);
    }

    public  void updateResource(String code){
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = ct.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        setLang(code);
    }
    public void setLang(String code){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang",code);
        editor.commit();

    }
    public  String getLang(){
        return  sharedPreferences.getString("lang","en");

    }

}
