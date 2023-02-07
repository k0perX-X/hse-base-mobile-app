package com.example.hsebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        type = (ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        id = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        TextView title = findViewById(R.id.title);
        recyclerView = findViewById(R.id. listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addltenrOecorationjnew DividerItenDecoration( this, LinearLayoutNanager.VERTICAL));
        adapter ■ new IterrAdapterfthis: :onScheduleItenClick);
        recyclerView.setAdapter(adapter);
    }
}