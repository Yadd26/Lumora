package com.lumora.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lumora.app.R;
import com.lumora.app.adapters.LearningPathStepAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityLearningPathDetailBinding;
import com.lumora.app.models.LearningPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LearningPathDetailActivity extends AppCompatActivity implements LearningPathStepAdapter.OnStepStatusChangeListener {

    private ActivityLearningPathDetailBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    
    private LearningPath learningPath;
    private LearningPathStepAdapter adapter;

    private final List<String> completedModules = new ArrayList<>();
    private final List<String> inProgressModules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningPathDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        if (getIntent() != null) {
            learningPath = (LearningPath) getIntent().getSerializableExtra("learning_path");
        }

        if (learningPath == null) {
            Toast.makeText(this, "Jalur pembelajaran tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.textToolbarTitle.setText("Jalur " + learningPath.getName());
        binding.textDetailPathTitle.setText(learningPath.getName());
        binding.textDetailPathDesc.setText(learningPath.getDescription());

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadModuleProgress();
    }

    private void setupRecyclerView() {
        binding.rvPathSteps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LearningPathStepAdapter(this, learningPath.getModules(), completedModules, inProgressModules, this);
        binding.rvPathSteps.setAdapter(adapter);
    }

    private void loadModuleProgress() {
        executorService.execute(() -> {
            completedModules.clear();
            inProgressModules.clear();

            for (String moduleName : learningPath.getModules()) {
                String status = databaseHelper.getModuleStatus(1, learningPath.getName(), moduleName);
                if ("Selesai".equals(status)) {
                    completedModules.add(moduleName);
                } else if ("Sedang Dipelajari".equals(status)) {
                    inProgressModules.add(moduleName);
                }
            }

            int progressPercent = databaseHelper.getPathProgressPercentage(1, learningPath.getName(), learningPath.getModules().size());

            runOnUiThread(() -> {
                binding.progressDetailPath.setProgress(progressPercent);
                binding.textDetailPathProgress.setText(progressPercent + "%");
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onStatusChange(String moduleName, String currentStatus) {
        String[] options = {"Belum Dimulai", "Sedang Dipelajari", "Selesai"};
        int checkedItem = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(currentStatus)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this, R.style.ThemeOverlay_Lumora_AlertDialog)
                .setTitle("Ubah Status Modul")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    String selectedStatus = options[which];
                    saveModuleProgress(moduleName, selectedStatus);
                    dialog.dismiss();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void saveModuleProgress(String moduleName, String status) {
        executorService.execute(() -> {
            int progressVal = 0;
            if ("Selesai".equals(status)) {
                progressVal = 100;
            } else if ("Sedang Dipelajari".equals(status)) {
                progressVal = 50;
            }

            databaseHelper.insertOrUpdatePathProgress(1, learningPath.getName(), moduleName, status, progressVal);

            // Record to learning history if finished
            if ("Selesai".equals(status)) {
                databaseHelper.insertLearningHistory(1, "Menyelesaikan Modul " + moduleName, learningPath.getName(), "TUTORIAL");
            }

            runOnUiThread(() -> {
                Toast.makeText(LearningPathDetailActivity.this, "Status modul diperbarui!", Toast.LENGTH_SHORT).show();
                loadModuleProgress();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
