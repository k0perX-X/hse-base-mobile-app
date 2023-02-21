package com.example.hsebase;

import androidx.lifecycle.LiveData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class GetTime {
    private TimeRequester timeRequester;

    public GetTime(){
        timeRequester = new TimeRequester();
    }

    public LiveData<Date> getCurrentTime() {
        return new TimeRequester();
    }

}
