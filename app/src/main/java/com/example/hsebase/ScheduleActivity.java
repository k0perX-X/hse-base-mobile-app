package com.example.hsebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

public class ScheduleActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "0";
    public static final String ARG_MODE = "-1";
    public static final String ARG_ID = "0";
    public final static String SELECTED_ITEM = "msg";
    public int DEFAULT_ID = 0;
    private ItemAdapter adapter;

    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=d1defccb44ee4927bf7a4e08bbaed06c";

    protected TextView time;
    protected Date currentTime;

    private OkHttpClient client = new OkHttpClient();

    protected void getTime(String format) {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                parseResponse(response, format);
            }

            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getTime", e);
            }
        });
    }

    protected void initTime(String format) {
        getTime(format);
    }

    private void showTime(Date dateTime, String format) {
        if (dateTime == null) {
            return;
        }
        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.forLanguageTag("ru"));
        time.setText(simpleDateFormat.format(currentTime));
    }

    public class TimeZone {
        @SerializedName("current_time")
        private String currentTime;

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }
    }

    public class TimeResponse {
        @SerializedName("time_zone")
        private TimeZone timeZone;

        public TimeZone getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }
    }

    private void parseResponse(Response response, String format) {
        Gson gson = new Gson();
        ResponseBody body = response.body();
        try {
            if (body == null) {
                return;
            }
            String string = body.string();
            Log.d(TAG, string);
            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date dateTime = SimpleDateFormat.parse(currentTimeVal);
            // run on UI thread
            runOnUiThread(() -> showTime(dateTime, format));
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ScheduleType type = (ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        ScheduleMode mode = (ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        int id = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        String item_description = getIntent().getStringExtra(SELECTED_ITEM);

        TextView title = findViewById(R.id.title);
        title.setText(item_description);

        time = findViewById(R.id.time);
        initTime("EEEE, dd MMMM");

        RecyclerView recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onScheduleItemClick);
        initData();
        recyclerView.setAdapter(adapter);
    }

    private void onScheduleItemClick(ScheduleItem scheduleItem) {
    }

    private void initData() {
        List<ScheduleItem> list = new ArrayList<>();
        ScheduleItemHeader header = new ScheduleItemHeader();
        header.setTitle("Понедельник, 28 января");
        list.add(header);
        ScheduleItem item = new ScheduleItem();
        item.setStart("10:00");
        item.setEnd("11:00");
        item.setType("Практическое занятие");
        item.setName("Анализ данных (анг)");
        item.setPlace("Ауд. 503, Кочновский пр-д, д.З");
        item.setTeacher("Пред. Гущим Михаил Иванович");
        list.add(item);
        item = new ScheduleItem();
        item.setStart("12:00");
        item.setEnd("13:00");
        item.setType("Практическое занятие");
        item.setName("Анализ данных (анг)");
        item.setPlace("Ауд. 503, Кочновский пр-д, д.З");
        item.setTeacher("Пред. Гущим Михаил Иванович");
        list.add(item);
        adapter.setDataList(list);
    }


    public static final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_ITEM = 0;
        private final static int TYPE_HEADER = 1;
        private List<ScheduleItem> dataList = new ArrayList<>();
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