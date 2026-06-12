package com.lumora.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.R;
import com.lumora.app.models.LearningHistoryItem;

import java.util.List;

public class LearningHistoryAdapter extends RecyclerView.Adapter<LearningHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<LearningHistoryItem> historyList;

    public LearningHistoryAdapter(Context context, List<LearningHistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_learning_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningHistoryItem item = historyList.get(position);
        holder.textTitle.setText(item.getTitle());
        
        String typeLabel = "AKTIVITAS";
        int iconRes = R.drawable.ic_info;

        if ("BOOK".equalsIgnoreCase(item.getType())) {
            typeLabel = "BUKU";
            iconRes = R.drawable.ic_bookmark;
        } else if ("TUTORIAL".equalsIgnoreCase(item.getType())) {
            typeLabel = "TUTORIAL";
            iconRes = R.drawable.ic_tutorial;
        } else if ("QUIZ".equalsIgnoreCase(item.getType())) {
            typeLabel = "KUIS";
            iconRes = R.drawable.ic_quiz;
        }

        holder.textSubtitle.setText(typeLabel + " • " + item.getCategory());
        holder.imgType.setImageResource(iconRes);
        holder.textDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textSubtitle, textDate;
        ImageView imgType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_history_title);
            textSubtitle = itemView.findViewById(R.id.text_history_subtitle);
            textDate = itemView.findViewById(R.id.text_history_date);
            imgType = itemView.findViewById(R.id.img_history_type);
        }
    }
}
