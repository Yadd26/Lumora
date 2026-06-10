package com.lumora.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lumora.app.adapters.TutorialAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentTutorialBinding;
import com.lumora.app.models.Tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TutorialFragment extends Fragment {

    private FragmentTutorialBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private TutorialAdapter adapter;
    private final List<Tutorial> tutorialsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTutorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.rvTutorials.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TutorialAdapter(requireContext(), tutorialsList);
        binding.rvTutorials.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTutorials();
    }

    private void loadTutorials() {
        executorService.execute(() -> {
            List<Tutorial> list = databaseHelper.getTutorials(1);
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tutorialsList.clear();
                    if (list != null && !list.isEmpty()) {
                        tutorialsList.addAll(list);
                        binding.rvTutorials.setVisibility(View.VISIBLE);
                        binding.textEmptyTutorials.setVisibility(View.GONE);
                    } else {
                        binding.rvTutorials.setVisibility(View.GONE);
                        binding.textEmptyTutorials.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                });
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
