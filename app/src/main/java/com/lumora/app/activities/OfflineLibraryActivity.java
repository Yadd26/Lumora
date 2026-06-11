package com.lumora.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.lumora.app.R;

public class OfflineLibraryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_library);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}