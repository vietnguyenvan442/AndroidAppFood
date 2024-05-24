package com.example.app_food.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.app_food.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sample data (monthly revenues)
        float[] revenues = {100f, 200f, 150f, 300f, 250f, 400f, 350f, 500f, 450f, 600f, 550f, 700f};

        // Create entries for the bar chart
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < revenues.length; i++) {
            entries.add(new BarEntry(i, revenues[i]));
        }

        // Create a dataset from the entries
        BarDataSet dataSet = new BarDataSet(entries, "Monthly Revenues");
        dataSet.setColor(Color.YELLOW);

        // Create a BarData object from the dataset
        BarData barData = new BarData(dataSet);

        // Get the BarChart view
        BarChart barChart = view.findViewById(R.id.barChart);

        // Set the BarData to the BarChart
        barChart.setData(barData);

        // Customize the description (optional)
        Description description = new Description();
        description.setText("Monthly Revenue Chart");
        barChart.setDescription(description);

        // Customize the x-axis
        barChart.getXAxis().setValueFormatter(new MyXAxisValueFormatter());

        // Get the y-axis and set the value formatter
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyValueFormatter());

        // Refresh the chart
        barChart.invalidate();
    }

    private class MyXAxisValueFormatter extends ValueFormatter {
        private final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = Math.round(value);
            if (index >= 0 && index < months.length) {
                return months[index];
            } else {
                return "";
            }
        }
    }

    private class MyValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.format(Locale.getDefault(), "$%.0f", value);
        }
    }
}
