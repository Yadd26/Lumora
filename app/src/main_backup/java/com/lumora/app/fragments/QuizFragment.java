package com.lumora.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lumora.app.activities.QuizDetailActivity;
import com.lumora.app.adapters.QuizCategoryAdapter;
import com.lumora.app.databinding.FragmentQuizBinding;
import com.lumora.app.models.QuizCategory;
import com.lumora.app.utils.QuizQuestionProvider;

import java.util.List;

/**
 * QuizFragment - Menampilkan daftar kategori pembelajaran akademik dalam bentuk kartu pilihan.
 * Mengarahkan pengguna ke halaman detail kuis saat salah satu kategori dipilih.
 */
public class QuizFragment extends Fragment implements QuizCategoryAdapter.OnCategoryClickListener {

    private FragmentQuizBinding binding;
    private QuizCategoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
    }

    /**
     * Konfigurasi RecyclerView untuk menampilkan 7 kategori kuis akademik.
     */
    private void setupRecyclerView() {
        List<QuizCategory> categories = QuizQuestionProvider.getCategories();
        adapter = new QuizCategoryAdapter(categories, this);
        binding.recyclerQuizCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerQuizCategories.setAdapter(adapter);
    }

    @Override
    public void onCategoryClick(QuizCategory category) {
        // Pindah ke QuizDetailActivity dengan membawa objek Kategori yang dipilih
        Intent intent = new Intent(requireContext(), QuizDetailActivity.class);
        intent.putExtra(QuizDetailActivity.EXTRA_CATEGORY, category);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Cegah memory leak
    }
}
