package com.lumora.app.activities;

import android.database.Cursor;
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
import com.lumora.app.models.Module;

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

    private final List<Module> modulesList = new ArrayList<>();

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

        binding.btnDownloadCertificate.setOnClickListener(v -> {
            executorService.execute(() -> {
                String userName = "Scholar Lumora";
                Cursor uCursor = null;
                try {
                    uCursor = databaseHelper.getReadableDatabase().rawQuery("SELECT name FROM users LIMIT 1", null);
                    if (uCursor != null && uCursor.moveToFirst()) {
                        userName = uCursor.getString(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (uCursor != null) uCursor.close();
                }

                final String finalName = userName;
                java.io.File certificateFile = com.lumora.app.utils.CertificateGenerator.generateCertificate(this, finalName, learningPath.getName());
                
                runOnUiThread(() -> {
                    if (certificateFile != null) {
                        Toast.makeText(this, "Sertifikat berhasil dibuat!", Toast.LENGTH_SHORT).show();
                        com.lumora.app.utils.CertificateGenerator.openOrShareCertificate(this, certificateFile);
                    } else {
                        Toast.makeText(this, "Gagal membuat sertifikat PDF.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        setupRecyclerView();
        loadModuleProgress();
    }

    private void setupRecyclerView() {
        binding.rvPathSteps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LearningPathStepAdapter(this, modulesList, this);
        binding.rvPathSteps.setAdapter(adapter);
    }

    private void loadModuleProgress() {
        executorService.execute(() -> {
            List<Module> loaded = databaseHelper.getModulesForPath(1, learningPath.getName());

            // Calculate path progress percentage
            int total = loaded.size();
            int completedCount = 0;
            for (Module m : loaded) {
                if ("Selesai".equals(m.getStatus())) {
                    completedCount++;
                }
            }
            int progressPercent = total > 0 ? (completedCount * 100) / total : 0;

            runOnUiThread(() -> {
                modulesList.clear();
                modulesList.addAll(loaded);
                binding.progressDetailPath.setProgress(progressPercent);
                binding.textDetailPathProgress.setText(progressPercent + "%");
                binding.btnDownloadCertificate.setEnabled(progressPercent >= 100);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onStatusChange(Module module) {
        boolean[] checkedItems = {
            module.getMateriCompleted() == 1,
            module.getTutorialCompleted() == 1,
            module.getLatihanCompleted() == 1,
            module.getQuizCompleted() == 1
        };
        String[] subTasks = {"Baca Materi Lengkap", "Ikuti Tutorial Pendukung", "Latihan Praktis Mandiri", "Selesaikan Kuis Evaluasi"};
        
        new AlertDialog.Builder(this, R.style.ThemeOverlay_Lumora_AlertDialog)
                .setTitle("Sub-Progres: " + module.getName())
                .setMultiChoiceItems(subTasks, checkedItems, (dialog, which, isChecked) -> {
                    if (which == 3) {
                        if (isChecked && module.getQuizCompleted() != 1) {
                            // Redirect to QuizActivity
                            android.content.Intent intent = new android.content.Intent(LearningPathDetailActivity.this, QuizActivity.class);
                            intent.putExtra(QuizActivity.EXTRA_CATEGORY_NAME, learningPath.getName());
                            intent.putExtra("extra_course_id", module.getQuizId());
                            intent.putExtra("extra_module_id", module.getId());
                            startActivity(intent);
                            dialog.dismiss();
                        } else if (!isChecked) {
                            checkedItems[which] = false;
                        }
                    } else {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("Simpan", (dialog, which) -> {
                    int mat = checkedItems[0] ? 1 : 0;
                    int tut = checkedItems[1] ? 1 : 0;
                    int lat = checkedItems[2] ? 1 : 0;
                    int qz = checkedItems[3] ? 1 : 0;
                    saveModuleProgress(module, mat, tut, lat, qz);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void saveModuleProgress(Module module, int mat, int tut, int lat, int qz) {
        executorService.execute(() -> {
            databaseHelper.updateModuleSubProgress(1, module.getId(), mat, tut, lat, qz);

            // Record to learning history if newly finished
            int oldPercent = module.getCompletionPercentage();
            int newCount = mat + tut + lat + qz;
            int newPercent = newCount * 25;
            
            if (newPercent == 100 && oldPercent < 100) {
                databaseHelper.insertLearningHistory(1, "Menyelesaikan Modul " + module.getName(), learningPath.getName(), "TUTORIAL");
                // Award points for completing module: +10 reputation points
                databaseHelper.addUserReputationPoints(1, 10);
            }

            runOnUiThread(() -> {
                Toast.makeText(LearningPathDetailActivity.this, "Progres sub-materi diperbarui!", Toast.LENGTH_SHORT).show();
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
