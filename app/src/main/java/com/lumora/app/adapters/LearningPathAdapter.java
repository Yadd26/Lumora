package com.lumora.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.R;
import com.lumora.app.activities.LearningPathDetailActivity;
import com.lumora.app.models.LearningPath;

import java.util.List;

public class LearningPathAdapter extends RecyclerView.Adapter<LearningPathAdapter.ViewHolder> {

    private final Context context;
    private final List<LearningPath> pathsList;

    public LearningPathAdapter(Context context, List<LearningPath> pathsList) {
        this.context = context;
        this.pathsList = pathsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_learning_path, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningPath path = pathsList.get(position);
        holder.textTitle.setText(path.getName());
        holder.textDesc.setText(path.getDescription());
        holder.textDuration.setText(path.getDuration());
        
        int modulesCount = path.getModules() != null ? path.getModules().size() : 0;
        holder.textModulesCount.setText(modulesCount + " Modul Pembelajaran");
        
        holder.progressBar.setProgress(path.getProgress());
        holder.textProgress.setText(path.getProgress() + "%");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LearningPathDetailActivity.class);
            intent.putExtra("learning_path", path);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pathsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDesc, textDuration, textModulesCount, textProgress;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_path_title);
            textDesc = itemView.findViewById(R.id.text_path_description);
            textDuration = itemView.findViewById(R.id.text_path_duration);
            textModulesCount = itemView.findViewById(R.id.text_path_modules_count);
            textProgress = itemView.findViewById(R.id.text_path_progress);
            progressBar = itemView.findViewById(R.id.progress_path);
        }
    }
}
