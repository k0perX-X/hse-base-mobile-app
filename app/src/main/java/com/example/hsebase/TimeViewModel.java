package com.example.hsebase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeViewModel extends AndroidViewModel {

    private GetTime repository;

    public TimeViewModel(@NonNull Application application) {
        super(application);
        repository = new GetTime();
    }

    public LiveData<Date> getTime(){
        return repository.getCurrentTime();
    }
//
//    public LiveData<Date> getCurrentTime() {
//        re
//    }
//
//    public LiveData<String> getStringTimeAndWeek() {
//        Locale locale = new Locale("ru", "RU");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", locale);
//
//        return simpleDateFormat.format(getCurrentTime()) + ", " +
//                getDayStringOld(getCurrentTime(), locale).substring(0, 1).toUpperCase() +
//                getDayStringOld(getCurrentTime(), locale).substring(1);
//    }
//    private String getDayStringOld(Date date, Locale locale) {
//        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
//        return formatter.format(date);
//    }

}
