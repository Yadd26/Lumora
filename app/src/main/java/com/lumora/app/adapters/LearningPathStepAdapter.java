package com.lumora.app.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lumora.app.R;
import com.lumora.app.models.Module;

import java.util.List;

public class LearningPathStepAdapter extends RecyclerView.Adapter<LearningPathStepAdapter.ViewHolder> {

    public interface OnStepStatusChangeListener {
        void onStatusChange(Module module);
    }

    private final Context context;
    private final List<Module> modulesList;
    private final OnStepStatusChangeListener listener;

    public LearningPathStepAdapter(Context context, List<Module> modulesList, 
                                   OnStepStatusChangeListener listener) {
        this.context = context;
        this.modulesList = modulesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_learning_path_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Module module = modulesList.get(position);
        holder.textStepTitle.setText((position + 1) + ". " + module.getName());
        holder.textStepDesc.setText(module.getDescription());

        // Determine locking: locked if position > 0 and previous module is not completed ("Selesai")
        boolean isLocked = false;
        if (position > 0) {
            Module prevModule = modulesList.get(position - 1);
            if (!"Selesai".equals(prevModule.getStatus())) {
                isLocked = true;
            }
        }

        String status;
        int dotColor;
        
        if (isLocked) {
            status = "Terkunci";
            dotColor = ContextCompat.getColor(context, R.color.outline);
            holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.textStepTitle.setAlpha(0.5f);
            holder.textStepDesc.setAlpha(0.5f);
            holder.btnChangeStatus.setEnabled(false);
            holder.btnChangeStatus.setText("Terkunci");
        } else {
            status = module.getStatus();
            holder.textStepTitle.setAlpha(1.0f);
            holder.textStepDesc.setAlpha(1.0f);
            holder.btnChangeStatus.setEnabled(true);
            holder.btnChangeStatus.setText("Ubah Status");

            if ("Selesai".equals(status)) {
                dotColor = ContextCompat.getColor(context, R.color.success);
                holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
            } else if ("Sedang Dipelajari".equals(status)) {
                dotColor = ContextCompat.getColor(context, R.color.warning);
                holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.warning));
            } else {
                dotColor = ContextCompat.getColor(context, R.color.outline);
                holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            }
        }
        
        // Show progress percentage if not locked
        if (!isLocked && !"Belum Dimulai".equals(status)) {
            holder.textStepStatus.setText(status + " (" + module.getCompletionPercentage() + "%)");
        } else {
            holder.textStepStatus.setText(status);
        }
        
        holder.dotIndicator.setBackgroundTintList(ColorStateList.valueOf(dotColor));

        // Connectors visibility
        holder.lineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.lineBottom.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);

        holder.btnChangeStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusChange(module);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modulesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textStepTitle, textStepDesc, textStepStatus;
        View lineTop, lineBottom, dotIndicator;
        MaterialButton btnChangeStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textStepTitle = itemView.findViewById(R.id.text_step_title);
            textStepDesc = itemView.findViewById(R.id.text_step_desc);
            textStepStatus = itemView.findViewById(R.id.text_step_status);
            lineTop = itemView.findViewById(R.id.line_top);
            lineBottom = itemView.findViewById(R.id.line_bottom);
            dotIndicator = itemView.findViewById(R.id.dot_indicator);
            btnChangeStatus = itemView.findViewById(R.id.btn_change_status);
        }
    }
}
