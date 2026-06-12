package com.lumora.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lumora.app.R;
import com.lumora.app.adapters.CourseAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentLearningCenterBinding;
import com.lumora.app.models.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LearningCenterFragment extends Fragment {

    private FragmentLearningCenterBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    
    private CourseAdapter courseAdapter;
    private final List<Course> courseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLearningCenterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupUI();
        loadData();
    }

    private void setupUI() {
        binding.btnTutorial.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.tutorialFragment);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Modul Tutorial Belum Tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnQuiz.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.quizFragment);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Modul Quiz Belum Tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnExercise.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Latihan akan segera hadir", Toast.LENGTH_SHORT).show();
        });

        binding.btnCertificate.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pusat Sertifikat", Toast.LENGTH_SHORT).show();
        });

        binding.rvLearningPaths.setLayoutManager(new LinearLayoutManager(requireContext()));
        courseAdapter = new CourseAdapter(courseList, course -> {
            // Intent intent = new Intent(requireContext(), CourseDetailActivity.class);
            // intent.putExtra("course_id", course.getId());
            // startActivity(intent);
        });
        binding.rvLearningPaths.setAdapter(courseAdapter);
    }

    private void loadData() {
        if (executorService == null || databaseHelper == null) return;
        
        executorService.execute(() -> {
            int todayDuration = databaseHelper.getTodayStudyDuration(1);
            int streak = databaseHelper.getCurrentStreak(1);
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("lumora_prefs", android.content.Context.MODE_PRIVATE);
            int dailyGoal = prefs.getInt("daily_study_goal_minutes", 30);
            int progressPercent = (dailyGoal > 0) ? (todayDuration * 100) / dailyGoal : 0;
            if (progressPercent > 100) progressPercent = 100;
            
            final int fTodayDuration = todayDuration;
            final int fDailyGoal = dailyGoal;
            final int fProgressPercent = progressPercent;
            final int fStreak = streak;
            
            List<Course> activeCourses = databaseHelper.getAllCourses(1);
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding != null) {
                        binding.textTodayProgressVal.setText(fTodayDuration + " / " + fDailyGoal + " Menit");
                        binding.progressDailyTarget.setProgress(fProgressPercent);
                        binding.textStreakVal.setText("🔥 " + fStreak + " Hari");
                        
                        courseList.clear();
                        if (activeCourses != null) {
                            courseList.addAll(activeCourses);
                        }
                        courseAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        binding = null;
    }
}
