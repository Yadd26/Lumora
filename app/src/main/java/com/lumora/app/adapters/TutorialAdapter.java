package com.lumora.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.R;
import com.lumora.app.activities.TutorialDetailActivity;
import com.lumora.app.models.Tutorial;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.ViewHolder> {

    private final Context context;
    private final List<Tutorial> tutorialsList;

    public TutorialAdapter(Context context, List<Tutorial> tutorialsList) {
        this.context = context;
        this.tutorialsList = tutorialsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutorial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tutorial tut = tutorialsList.get(position);
        holder.textTitle.setText(tut.getTitle());
        holder.textDesc.setText(tut.getDescription());
        holder.textCategory.setText(tut.getCategory());
        holder.textDifficulty.setText(tut.getDifficulty());
        holder.textTime.setText(tut.getTimeEstimation());

        if ("Selesai".equals(tut.getStatus())) {
            holder.textStatus.setText("• Selesai");
            holder.textStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
            holder.textStatus.setVisibility(View.VISIBLE);
        } else if ("Sedang Dipelajari".equals(tut.getStatus())) {
            holder.textStatus.setText("• Dipelajari");
            holder.textStatus.setTextColor(ContextCompat.getColor(context, R.color.warning));
            holder.textStatus.setVisibility(View.VISIBLE);
        } else {
            holder.textStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TutorialDetailActivity.class);
            intent.putExtra("tutorial_id", tut.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tutorialsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDesc, textCategory, textDifficulty, textTime, textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_tutorial_title);
            textDesc = itemView.findViewById(R.id.text_tutorial_description);
            textCategory = itemView.findViewById(R.id.text_tutorial_category);
            textDifficulty = itemView.findViewById(R.id.text_tutorial_difficulty);
            textTime = itemView.findViewById(R.id.text_tutorial_time);
            textStatus = itemView.findViewById(R.id.text_tutorial_status);
        }
    }
}
