package com.example.hsebase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TimeRequester extends LiveData<Date> {
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=d1defccb44ee4927bf7a4e08bbaed06c";
    private OkHttpClient client = new OkHttpClient();

    private Date currentTime = new Date();

    public TimeRequester() {
        initTime();
        postValue(currentTime);
    }
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
        postValue(dateTime);
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
            showTime(dateTime);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

}
