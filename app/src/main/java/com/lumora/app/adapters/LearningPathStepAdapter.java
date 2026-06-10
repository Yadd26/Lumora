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

import java.util.List;

public class LearningPathStepAdapter extends RecyclerView.Adapter<LearningPathStepAdapter.ViewHolder> {

    public interface OnStepStatusChangeListener {
        void onStatusChange(String moduleName, String currentStatus);
    }

    private final Context context;
    private final List<String> modulesList;
    private final List<String> completedModules;
    private final List<String> inProgressModules;
    private final OnStepStatusChangeListener listener;

    public LearningPathStepAdapter(Context context, List<String> modulesList, 
                                   List<String> completedModules, List<String> inProgressModules,
                                   OnStepStatusChangeListener listener) {
        this.context = context;
        this.modulesList = modulesList;
        this.completedModules = completedModules;
        this.inProgressModules = inProgressModules;
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
        String moduleName = modulesList.get(position);
        holder.textStepTitle.setText((position + 1) + ". " + moduleName);
        
        // Setup module description description based on title keywords
        String desc = getModuleDescription(moduleName);
        holder.textStepDesc.setText(desc);

        // Determine current status
        String status = "Belum Dimulai";
        int dotColor = ContextCompat.getColor(context, R.color.outline);
        
        if (completedModules.contains(moduleName)) {
            status = "Selesai";
            dotColor = ContextCompat.getColor(context, R.color.success);
            holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
        } else if (inProgressModules.contains(moduleName)) {
            status = "Sedang Dipelajari";
            dotColor = ContextCompat.getColor(context, R.color.warning);
            holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.warning));
        } else {
            holder.textStepStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }
        
        holder.textStepStatus.setText(status);
        holder.dotIndicator.setBackgroundTintList(ColorStateList.valueOf(dotColor));

        // Connectors visibility
        holder.lineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.lineBottom.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);

        final String finalStatus = status;
        holder.btnChangeStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusChange(moduleName, finalStatus);
            }
        });
    }

    private String getModuleDescription(String moduleName) {
        if (moduleName.contains("Java") || moduleName.contains("Kotlin") || moduleName.contains("Dasar")) {
            return "Dasar sintaksis, variabel, percabangan, perulangan, dan pemecahan masalah algoritma dasar.";
        } else if (moduleName.contains("OOP") || moduleName.contains("Object")) {
            return "Kelas, objek, pewarisan (inheritance), enkapsulasi, polimorfisme, dan interface.";
        } else if (moduleName.contains("Activity") || moduleName.contains("Lifecycle")) {
            return "Memahami siklus hidup activity Android, penanganan state, dan intent navigasi.";
        } else if (moduleName.contains("Fragment") || moduleName.contains("Navigasi")) {
            return "Menggunakan fragment modular, transaksi fragment manager, dan Android Navigation Component.";
        } else if (moduleName.contains("API") || moduleName.contains("Network") || moduleName.contains("Retrofit")) {
            return "Integrasi web service RESTful menggunakan Retrofit, parsing JSON, dan data callback.";
        } else if (moduleName.contains("Database") || moduleName.contains("SQL") || moduleName.contains("Local")) {
            return "Manajemen data persisten menggunakan SQLite DatabaseHelper, skema relasional, dan transaksi CRUD.";
        } else if (moduleName.contains("AI") || moduleName.contains("Machine") || moduleName.contains("Model")) {
            return "Memahami teori machine learning dasar, model prediksi, regresi linear, dan inferensi neural.";
        } else if (moduleName.contains("Security") || moduleName.contains("Kripto") || moduleName.contains("Cyber")) {
            return "Prinsip keamanan sistem, kriptografi simetris/asimetris, dan enkripsi payload data.";
        } else if (moduleName.contains("Design") || moduleName.contains("Architect") || moduleName.contains("Pattern")) {
            return "Pola arsitektur MVVM/MVC, pemisahan data logic dari view, kode bersih, dan testing unit.";
        }
        return "Modul pembelajaran mendalam mengenai konsep akademik yang bersangkutan.";
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
