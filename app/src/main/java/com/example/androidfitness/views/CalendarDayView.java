package com.example.androidfitness.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.androidfitness.R;

public class CalendarDayView extends LinearLayout {
    private TextView tvDay;
    private View selectionIndicator;
    private boolean isSelected;

    public CalendarDayView(Context context) {
        super(context);
        init(context);
    }

    public CalendarDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, this, true);
        tvDay = view.findViewById(R.id.tv_day);
        selectionIndicator = view.findViewById(R.id.selection_indicator);
    }

    public void setDay(String day) {
        tvDay.setText(day);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        selectionIndicator.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        tvDay.setTextColor(ContextCompat.getColor(getContext(),
                selected ? R.color.white : R.color.black));

    }

    public boolean isSelected() {
        return isSelected;
    }
}