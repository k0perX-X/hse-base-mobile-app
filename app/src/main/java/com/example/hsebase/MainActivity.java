package com.example.hsebase;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    public Button button1;
    public Button button2;
    public Button button7;

    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03818f75ed94O23aO05637878ecO977";

    protected TextView time;
    protected Date currentTime;

    private OkHttpClient client = new OkHttpClient();

    protected void getTime() {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                parseResponse(response);
            }

            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getTime", e);
            }
        });
    }

    protected void initTime() {
        getTime();
    }

    private void showTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }
        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, EEEE", Locale.forLanguageTag("ru"));
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

    private void parseResponse(Response response) {
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
            runOnUiThread(() -> showTime(dateTime));
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button7 = findViewById(R.id.button7);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStudent();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTeacher();
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
            }
        });
    }

    private void showStudent() {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }

    private void showTeacher() {
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}