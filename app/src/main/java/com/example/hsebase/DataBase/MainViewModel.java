package com.example.hsebase.DataBase;

import android.app.Application;
//import android.arch.lifecycle.AndroidViewModel;
//import android.arch.lifecycle.LiveData;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final HseRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new HseRepository(application);
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return repository.getGroups();
    }

    public LiveData<List<TeacherEntity>> getTeachers() {
        return repository.getTeachers();
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherByDateAndGroup(Date date, Date nextDate, int groupId) {
        return repository.getTimeTableTeacherByDateAndGroup(date, nextDate, groupId);
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherCurrentByGroup(Date date, int groupId) {
        return repository.getTimeTableTeacherCurrentByGroup(date, groupId);
    }
}

