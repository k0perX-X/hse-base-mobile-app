package com.example.hsebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hsebase.DataBase.MainViewModel;
import com.example.hsebase.DataBase.TimeTableWithTeacherEntity;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ScheduleActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "0";
    public static final String ARG_MODE = "1";
    public static final String ARG_ID = "2";
    public static final String ARG_TIME = "3";
    public final static String SELECTED_ITEM = "msg";
    private ScheduleType type;
    private ScheduleMode mode;
    private int id;
    public int DEFAULT_ID = -1;
    private ItemAdapter adapter;
    private List<TimeTableWithTeacherEntity> lessons;
    private RecyclerView recyclerView;
    protected MainViewModel mainViewModel;

    private static final String dateFormat = "EEEE, dd MMMM";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

    protected TextView time;
    private Calendar cal;
    private Date nextDay;

    public static Date currentTime;
//    private TimeViewModel timeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        time = findViewById(R.id.time);

        currentTime = (Date) getIntent().getSerializableExtra(ARG_TIME);
//        Intent intent = this.getIntent();
//        Bundle bundle = intent.getExtras();
//
//        currentTime = (Date) bundle.getSerializable("value");
        type = (ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        Log.d("ScheduleActivity.onCreate", ARG_ID);
        id = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        String item_description = getIntent().getStringExtra(SELECTED_ITEM);

        TextView title = findViewById(R.id.title);
        title.setText(item_description);

        showTime();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onScheduleItemClick);
        cal = new GregorianCalendar();
        nextDay = new Date();
        cal.setTime(currentTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        switch (type) {
            case DAY:
                cal.add(Calendar.DAY_OF_MONTH, 1);
                nextDay = cal.getTime();
                break;
            case WEEK:
                cal.add(Calendar.DAY_OF_MONTH, (9 - cal.get(Calendar.DAY_OF_WEEK)) % 7 == 0 ?
                        7 : 9 - cal.get(Calendar.DAY_OF_WEEK) % 7);
                nextDay = cal.getTime();
                break;
        }

        mainViewModel.getTimeTableTeacherByDateAndGroup(currentTime, nextDay, id)
                .observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<TimeTableWithTeacherEntity> list) {
                        lessons = list;
                        initData();
                    }
                });


//        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);

//        currentTime = timeViewModel.getTime().getValue();
//        timeViewModel.getTime().observe(this, new Observer<Date>() {
//            @Override
//            public void onChanged(@Nullable Date date) {
//                currentTime = date;
//                showTime();
//                initData();
//            }
//        });

//        initData();
    }

    private void showTime() {
        time.setText(simpleDateFormat.format(currentTime));
    }

    private void onScheduleItemClick(ScheduleItem scheduleItem) {
    }

    private void initData() {
        List<ScheduleItem> outList = new ArrayList<>();
        if (lessons != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.forLanguageTag("ru"));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru"));
            ScheduleItemHeader header;
            if (type != ScheduleType.DAY) {
                header = new ScheduleItemHeader();
                header.setTitle(simpleDateFormat.format(currentTime));
                outList.add(header);
            }
            cal.setTime(currentTime);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            nextDay = cal.getTime();
            for (TimeTableWithTeacherEntity lesson : lessons) {
                if (lesson.timeTableEntity.timeStart.after(nextDay)) {
                    header = new ScheduleItemHeader();
                    header.setTitle(simpleDateFormat.format(nextDay));
                    outList.add(header);
                }
                ScheduleItem item = new ScheduleItem();
                item.setStart(timeFormat.format(lesson.timeTableEntity.timeStart));
                item.setStart(timeFormat.format(lesson.timeTableEntity.timeEnd));
                item.setType(lesson.timeTableEntity.type);
                item.setName(lesson.timeTableEntity.subjName);
                item.setPlace(getString(R.string.corp) + lesson.timeTableEntity.corp +
                        getString(R.string.aud) + lesson.timeTableEntity.cabinet);
                item.setTeacher(lesson.teacherEntity.fio);
                outList.add(item);
                Log.d("for", lesson.timeTableEntity.timeStart + " " + lesson.timeTableEntity.timeEnd);
            }
        } else {
            ScheduleItemHeader header = new ScheduleItemHeader();
            if (id == DEFAULT_ID) {
                header.setTitle(getString(R.string.error_timetable));
            } else {
                header.setTitle(getString(R.string.no_lessons));
            }
            outList.add(header);
        }
        adapter.setDataList(outList);
        recyclerView.setAdapter(adapter);
    }


    public static final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_ITEM = 0;
        private final static int TYPE_HEADER = 1;
        private List<ScheduleItem> dataList = new ArrayList<ScheduleItem>();
        private OnItemClick onItemClick;

        public ItemAdapter(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        public void setDataList(List<ScheduleItem> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (viewType == TYPE_ITEM) {
                View contactView = inflater.inflate(R.layout.item_schedule, parent, false);
                return new ViewHolder(contactView, context, onItemClick);
            } else if (viewType == TYPE_HEADER) {
                View contactView = inflater.inflate(R.layout.item_schedule_header, parent, false);
                return new ViewHolderHeader(contactView, context, onItemClick);
            }
            throw new IllegalArgumentException("Invalid view type");
        }

        public int getItemViewType(int position) {
            ScheduleItem data = dataList.get(position);
            if (data instanceof ScheduleItemHeader) {
                return TYPE_HEADER;
            }
            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder ViewHolder, int position) {
            ScheduleItem data = dataList.get(position);
            if (ViewHolder instanceof ViewHolder) {
                ((ViewHolder) ViewHolder).bind(data);
            } else if (ViewHolder instanceof ViewHolderHeader) {
                ((ViewHolderHeader) ViewHolder).bind((ScheduleItemHeader) data);
            }
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private OnItemClick onItemClick;
        private TextView start;
        private TextView end;
        private TextView type;
        private TextView name;
        private TextView place;
        private TextView teacher;

        public ViewHolder(View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.name);
            place = itemView.findViewById(R.id.place);
            teacher = itemView.findViewById(R.id.teacher);
        }

        public void bind(final ScheduleItem data) {
            start.setText(data.getStart());
            end.setText(data.getEnd());
            type.setText(data.getType());
            name.setText(data.getName());
            place.setText(data.getPlace());
            teacher.setText(data.getTeacher());
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        private Context context;
        private OnItemClick onItemClick;
        private TextView title;

        public ViewHolderHeader(View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            title = itemView.findViewById(R.id.title_sh);
        }

        public void bind(final ScheduleItemHeader data) {
            title.setText(data.getTitle());
        }
    }
}