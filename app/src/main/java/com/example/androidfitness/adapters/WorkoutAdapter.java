package com.example.androidfitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.androidfitness.models.Workout;
import com.example.androidfitness.R;
import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<Workout> workoutList;
    private List<Workout> filteredList;
    private WorkoutFilter filter;

    public WorkoutAdapter(Context context, List<Workout> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
        this.filteredList = workoutList;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.workoutName);
            holder.image = convertView.findViewById(R.id.workoutImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Workout workout = filteredList.get(position);
        holder.name.setText(workout.getName());
        holder.image.setImageResource(workout.getImageResId());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new WorkoutFilter();
        }
        return filter;
    }

    private static class ViewHolder {
        TextView name;
        ImageView image;
    }

    private class WorkoutFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = workoutList;
                results.count = workoutList.size();
            } else {
                List<Workout> filteredResults = new ArrayList<>();
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Workout workout : workoutList) {
                    if (workout.getName().toLowerCase().contains(filterPattern)) {
                        filteredResults.add(workout);
                    }
                }

                results.values = filteredResults;
                results.count = filteredResults.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<Workout>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}