package com.flappy.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateView extends TextView {


    public DateView(Context context) {
        super(context);
        setDate();
    }
    public DateView(Context context, AttributeSet attrs){
        super(context, attrs);
        setDate();
    }
    public  DateView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setDate();
    }
    private  void setDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat(getContext().getString(R.string.date_format));
        String today = date.format(Calendar.getInstance().getTime());
        setText(today);
    }
}
