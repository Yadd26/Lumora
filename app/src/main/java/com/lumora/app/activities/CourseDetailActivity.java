package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.lumora.app.R;

public class CourseDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_start_module).setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailActivity.this, ModuleDetailActivity.class);
            startActivity(intent);
        });
    }
}