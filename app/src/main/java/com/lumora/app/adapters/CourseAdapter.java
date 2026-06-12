package com.lumora.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.R;
import com.lumora.app.models.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courseList;
    private final OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textLevel;
        ProgressBar progressCourse;
        TextView textProgress;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_course_title);
            textLevel = itemView.findViewById(R.id.text_course_level);
            progressCourse = itemView.findViewById(R.id.progress_course);
            textProgress = itemView.findViewById(R.id.text_course_progress);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCourseClick(courseList.get(position));
                }
            });
        }

        public void bind(Course course) {
            textTitle.setText(course.getName());
            textLevel.setText(course.getLevel() + " • " + course.getDuration());
            
            // Assume progress is 0 for now until integrated with progress engine
            int progress = 0;
            progressCourse.setProgress(progress);
            textProgress.setText(progress + "%");
        }
    }
}
