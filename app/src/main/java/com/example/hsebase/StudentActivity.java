package com.example.hsebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hsebase.DataBase.GroupEntity;
import com.example.hsebase.DataBase.MainViewModel;
import com.example.hsebase.DataBase.TimeTableEntity;
import com.example.hsebase.DataBase.TimeTableWithTeacherEntity;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StudentActivity extends AppCompatActivity {

    private TextView teacher;
    private TextView status;
    private TextView subject;
    private TextView corp;
    private TextView cabinet;
    private Spinner spinner;
    protected MainViewModel mainViewModel;
    private ArrayAdapter<Group> adapter;
    private static final String timeFormat = "EEEE, dd MMMM, HH:mm";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

    private Date currentTime;
    private TimeViewModel timeViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student);

        time = findViewById(R.id.time);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        timeViewModel.getTime().observe(this, new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {
                currentTime = date;
                showTime();
                initData();
            }
        });


        spinner = findViewById(R.id.groupList);

        List<Group> groups = new ArrayList<>();
        initGroupList(groups);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected,
                                       int selectedItemPosition, long selectedId) {
                Object item = adapter.getItem(selectedItemPosition);
                showTime();
                initData();
                Log.d(TAG, "selectedItem" + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        cabinet = findViewById(R.id.cabinet);
        corp = findViewById(R.id.corp);
        teacher = findViewById(R.id.teacher);

        View scheduleDay = findViewById(R.id.schedule_day);
        scheduleDay.setOnClickListener(v -> showSchedule(ScheduleType.DAY));
        View scheduleWeek = findViewById(R.id.schedule_week);
        scheduleWeek.setOnClickListener(v -> showSchedule(ScheduleType.WEEK));
    }

    private void showTime(){
        time.setText(simpleDateFormat.format(currentTime));
    }

    static class Group {
        private Integer id;
        private String name;

        public Group(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

//    private void initGroupList(List<Group> groups) {
//        String[] directions = new String[]{"ПИ-", "БИ-", "И-", "Э-"};
//
//        String[] years = new String[]{"19-", "20-", "21-", "22-"};
//
//        String[] numbers = new String[]{"1", "2"};
//
//        int i = 0;
//
//        for (String direction : directions) {
//            for (String year : years) {
//                for (String number : numbers) {
//                    groups.add(new Group(i++, direction.concat(year).concat(number)));
//                }
//            }
//        }
//    }

    private void initData() {
        initDataFromTimeTable(null);
    }

    private void initDataFromTimeTable(TimeTableWithTeacherEntity timeTableTeacherEntity) {
        if (timeTableTeacherEntity == null) {
            status.setText(R.string.timetableStatusDefault);
            subject.setText(R.string.timetableSubjectDefault);
            cabinet.setText(R.string.timetableCabinetDefault);
            corp.setText(R.string.timetableCorpDefault);
            teacher.setText(R.string.timetableTeacherDefault);
            return;
        }
        status.setText(R.string.lesson_is_running);
        TimeTableEntity timeTableEntity = timeTableTeacherEntity.timeTableEntity;
        subject.setText(timeTableEntity.subjName);
        cabinet.setText(timeTableEntity.cabinet);
        corp.setText(timeTableEntity.corp);
        teacher.setText(timeTableTeacherEntity.teacherEntity.fio);
    }

    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=d1defccb44ee4927bf7a4e08bbaed06c";

    protected TextView time;

    private Group getSelectedGroup() {
        return (Group) spinner.getSelectedItem();
    }

    private void initGroupList(final List<Group> groups) {

        mainViewModel.getGroups().observe( this, new Observer<List<GroupEntity>>() { @Override
            public void onChanged(@Nullable List<GroupEntity> list) {
                List<Group> groupsResult = new ArrayList<>();
                for (GroupEntity listEntity : list) {
                    groupsResult.add(new Group(listEntity.id, listEntity.name));
                }
                adapter.clear();
                adapter.addAll(groupsResult);
            }
        });
    }

    public static String getDayStringOld(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        return formatter.format(date);
    }

    private void showSchedule(ScheduleType type) {
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof Group)) {
            return;
        }
        showScheduleImpl(ScheduleMode.STUDENT, type, (Group) selectedItem);
    }

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, Group group) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.ARG_TYPE, type);
        intent.putExtra(ScheduleActivity.ARG_MODE, mode);
        intent.putExtra(ScheduleActivity.ARG_ID, group.getId());
        intent.putExtra(ScheduleActivity.SELECTED_ITEM, group.name);
        intent.putExtra(ScheduleActivity.ARG_TIME, currentTime);
        Bundle bundle = new Bundle();
        bundle.putSerializable("value", currentTime);
        intent.putExtras(bundle);
        Log.d("showScheduleImpl", group.getId().toString());
        startActivity(intent);
    }
}