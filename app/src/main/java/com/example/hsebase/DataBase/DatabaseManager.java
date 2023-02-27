package com.example.hsebase.DataBase;

import static android.os.AsyncTask.execute;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hsebase.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private DatabaseHelper db;
    private static DatabaseManager instance;

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseManager(Context context) {
        db = Room
                .databaseBuilder(context, DatabaseHelper.class, DatabaseHelper.DATABASE_NAME)
                .addCallback(
                        new RoomDatabase.Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        initData(context);
                                    }
                                });
                            }
                        })
                .build();
    }

    public HseDao getHseDao() {
        return db.hseDao();
    }

    private void initData(Context context) {
        try {
            List<GroupEntity> groups = new ArrayList<>();
            GroupEntity group;
            JSONObject jObject;
            Resources res = context.getResources();
            String[] jsons = res.getStringArray(R.array.groups);
            for (String json : jsons) {
                jObject = new JSONObject(json);
                group = new GroupEntity();
                group.id = jObject.getInt("id");
                group.name = jObject.getString("name");
                groups.add(group);
            }
            DatabaseManager.getInstance(context).getHseDao().insertGroup(groups);

            List<TeacherEntity> teachers = new ArrayList<>();
            TeacherEntity teacher;
            jsons = res.getStringArray(R.array.teachers);
            for (String json : jsons) {
                Log.d("initDataBase", json);
                jObject = new JSONObject(json);
                teacher = new TeacherEntity();
                teacher.id = jObject.getInt("id");
                teacher.fio = jObject.getString("fio");
                teachers.add(teacher);
            }
            DatabaseManager.getInstance(context).getHseDao().insertTeacher(teachers);

            List<TimeTableEntity> timeTables = new ArrayList();
            TimeTableEntity timeTable;
            jsons = res.getStringArray(R.array.timeTable);
            for (String json : jsons) {
                jObject = new JSONObject(json);
                timeTable = new TimeTableEntity();
                timeTable.id = jObject.getInt("id");
                timeTable.cabinet = jObject.getString("cabinet");
                timeTable.subGroup = jObject.getString("subGroup");
                timeTable.subjName = jObject.getString("subjName");
                timeTable.corp = jObject.getString("corp");
                timeTable.type = jObject.getString("type");
                timeTable.timeStart = dateFromString(jObject.getString("timeStart"));
                timeTable.timeEnd = dateFromString(jObject.getString("timeEnd"));
                timeTable.groupId = jObject.getInt("groupId");
                timeTable.teacherId = jObject.getInt("teacherId");
                timeTables.add(timeTable);
            }
            DatabaseManager.getInstance(context).getHseDao().insertTimeTable(timeTables);
        } catch (Exception e) {
            Log.e("initDataBase", e.getMessage());
        }
    }

    private Date dateFromString(String val) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return simpleDateFormat.parse(val);
        } catch (ParseException e) {
            //
        }
        return null;
    }
}
