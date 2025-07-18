package com.example.androidfitness.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfitness.R;
import com.example.androidfitness.activities.ExerciseDetailsActivity;
import com.example.androidfitness.models.Exercise;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Exercise> exerciseList;

    public ExerciseAdapter(Context context, ArrayList<Exercise> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.exerciseName.setText(exercise.getName());

        // Load image dynamically from drawable
        int imageResId = context.getResources().getIdentifier(exercise.getImage(), "drawable", context.getPackageName());
        holder.exerciseImage.setImageResource(imageResId);

        // Click event to open Exercise Details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExerciseDetailsActivity.class);
            intent.putExtra("exercise_id", exercise.getId());
            intent.putExtra("exercise_name", exercise.getName());
            intent.putExtra("exercise_image", exercise.getImage());
            intent.putExtra("exercise_category", exercise.getCategory());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        ImageView exerciseImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseImage = itemView.findViewById(R.id.exerciseImage);
        }
    }
}
