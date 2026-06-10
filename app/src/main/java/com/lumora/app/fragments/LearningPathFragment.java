package com.lumora.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lumora.app.adapters.LearningPathAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentLearningPathBinding;
import com.lumora.app.models.LearningPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LearningPathFragment extends Fragment {

    private FragmentLearningPathBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private LearningPathAdapter adapter;
    private final List<LearningPath> pathsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLearningPathBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerView();
        initializePaths();
    }

    private void setupRecyclerView() {
        binding.rvLearningPaths.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LearningPathAdapter(requireContext(), pathsList);
        binding.rvLearningPaths.setAdapter(adapter);
    }

    private void initializePaths() {
        pathsList.clear();

        // 1. Pemrograman
        pathsList.add(new LearningPath(
                "Pemrograman",
                "Kembangkan fondasi pemrograman kuat menggunakan Java, logika algoritma, pilar OOP, dan struktur data.",
                "Pemrograman",
                "4 Minggu",
                Arrays.asList("Java Dasar & Algoritma", "OOP Java & Pewarisan", "Exception & File Handling", "Collections Framework", "Concurrency & Threading")
        ));

        // 2. Basis Data
        pathsList.add(new LearningPath(
                "Basis Data",
                "Pelajari perancangan database relasional, SQL Query, normalisasi skema 1NF/2NF/3NF, indeks, dan ACID.",
                "Basis Data",
                "3 Minggu",
                Arrays.asList("Skema Relasional & ERD", "Perintah SQL Dasar (DDL/DML)", "Normalisasi Database", "Indexing & Optimasi Query", "Transaksi ACID")
        ));

        // 3. Software Engineering
        pathsList.add(new LearningPath(
                "Software Engineering",
                "Pelajari siklus pengembangan perangkat lunak (SDLC), metodologi Agile/Scrum, perancangan arsitektur, clean code, dan testing.",
                "Software Engineering",
                "3 Minggu",
                Arrays.asList("Pengenalan SDLC & Agile", "Analisis Kebutuhan", "Perancangan UML & Arsitektur", "Clean Code & Refactoring", "Software Testing & QA", "CI/CD & Deployment")
        ));

        // 4. Mobile Development
        pathsList.add(new LearningPath(
                "Mobile Development",
                "Kuasai Android SDK secara mendalam menggunakan Kotlin, UI, navigation, API Retrofit, local Room, MVVM, dan Compose.",
                "Mobile Development",
                "5 Minggu",
                Arrays.asList("Kotlin Dasar", "Android Lifecycle", "ViewBinding & XML Layout", "Navigation Component", "Retrofit API Integration", "Local SQLite & Room", "Jetpack Compose", "Arsitektur MVVM", "Testing Aplikasi Mobile")
        ));

        // 5. Artificial Intelligence
        pathsList.add(new LearningPath(
                "Artificial Intelligence",
                "Bangun pemahaman dasar Machine Learning, regresi linear, neural networks, NLP, CV, dan LLM.",
                "Artificial Intelligence",
                "6 Minggu",
                Arrays.asList("Matematika untuk AI", "Regresi & Klasifikasi", "Jaringan Saraf Tiruan", "Natural Language Processing", "Computer Vision", "Generative AI & LLM")
        ));

        // 6. Cyber Security
        pathsList.add(new LearningPath(
                "Cyber Security",
                "Lindungi sistem menggunakan CIA Triad, enkripsi asimetris RSA, malware analysis, firewall, dan pentest.",
                "Cyber Security",
                "4 Minggu",
                Arrays.asList("Konsep CIA Triad", "Kriptografi & Enkripsi", "Malware & Trojan Analysis", "Social Engineering", "Network Security & Firewall", "Penetration Testing")
        ));

        // 7. Data Science
        pathsList.add(new LearningPath(
                "Data Science",
                "Eksplorasi statistika deskriptif, analisis data tabular Pandas, manipulasi array NumPy, visualisasi Seaborn.",
                "Data Science",
                "4 Minggu",
                Arrays.asList("Statistika Deskriptif", "Python Data Stack", "Data Cleaning Pandas", "Manipulasi Array NumPy", "Visualisasi Data", "Model Prediksi Sederhana")
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPathsProgress();
    }

    private void loadPathsProgress() {
        executorService.execute(() -> {
            for (LearningPath path : pathsList) {
                int percentage = databaseHelper.getPathProgressPercentage(1, path.getName(), path.getModules().size());
                path.setProgress(percentage);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
