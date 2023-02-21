package com.example.hsebase.DataBase;

//import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HseRepository {
    private DatabaseManager databaseManager;
    private HseDao dao;

    public HseRepository(Context context) {
        databaseManager = DatabaseManager.getInstance(context);
        dao = databaseManager.getHseDao();
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return dao.getAllGroup();
    }

    public LiveData<List<TeacherEntity>> getTeachers() {
        return dao.getAllTeacher();
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherByDateAndGroup(Date date, Date nextDate, int groupId) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(nextDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        nextDate = cal.getTime();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        date = cal.getTime();
        Log.d("getTimeTableTeacherCurrentByGroup", date + " " + nextDate + " " + groupId);
        return dao.getTimeTableTeacherByDateAndGroup(date, nextDate, groupId);
    }
    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherCurrentByGroup(Date date, int groupId) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date nextDay = cal.getTime();
        Log.d("getTimeTableTeacherCurrentByGroup", date + " " + nextDay + " " + groupId);
        return dao.getTimeTableTeacherCurrentByGroup(date, groupId);
    }
}
