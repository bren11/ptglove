package com.ptglove.frag;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ptglove.Joints;
import com.ptglove.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Progress extends Fragment {

    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList<Entry> lineEntries;
    SharedPreferences progressData, preferences, sharedPref;

    public Progress() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    private class Format extends ValueFormatter {
        @Override
        public String getPointLabel(Entry entry) {
            return (String)entry.getData();
        }
    }

    class MyComparator implements Comparator<Entry> {
        @Override
        public int compare(Entry a, Entry b) {
            String[] dateA = ((String)a.getData()).split("/");
            String[] dateB = ((String)b.getData()).split("/");
            Log.d("vals: ", dateA[0] + " " + dateA[1] + " " + dateB[0] + " " + dateB[1]);
            if (Integer.parseInt(dateA[0]) == Integer.parseInt(dateB[0]))
                return Integer.parseInt(dateA[1]) - Integer.parseInt(dateB[1]);
            return Integer.parseInt(dateA[0]) - Integer.parseInt(dateB[0]);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lineChart = getActivity().findViewById(R.id.lineChart);
        progressData = getActivity().getSharedPreferences("progressData", MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref = getActivity().getSharedPreferences("sharedPref", MODE_PRIVATE);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineEntries = new ArrayList<>();

        if (!sharedPref.contains("mins") || !sharedPref.contains("maxs")) return;
        String[] mins = sharedPref.getString("mins", null).split(" ");
        String[] maxs = sharedPref.getString("maxs", null).split(" ");

        String mode = preferences.getString("progress_finger", "");
        String[] options = getResources().getStringArray(R.array.finger_values);
        int finger_choice;
        for (finger_choice = options.length - 1; finger_choice > 0;finger_choice--) {
            if (mode.equals(options[finger_choice])) break;
        }

        for (Map.Entry<String, ?> entry : progressData.getAll().entrySet()) {
            String[] vals = ((String)entry.getValue()).split(" ");
            if (vals.length < Joints.length()) return;
            float[] finger_angles = new float[Joints.length()];
            for (int i = 0; i < vals.length; i++) {
                float diff = Integer.parseInt(maxs[i]) - Integer.parseInt(mins[i]);
                if (diff == 0f) diff = 1f;
                finger_angles[i] = (Integer.parseInt(vals[i]) - Integer.parseInt(mins[i])) / diff * 90f;
                if (finger_angles[i] > 90f) finger_angles[i] = 90f;
            }
            float val = 0;
            if (finger_choice <= 0) {
                for (float i : finger_angles) {
                    val += i;
                }
                val /= Joints.length();
            } else if (finger_choice == 1) {
                val += (finger_angles[0] + finger_angles[1]) / 2f;
            } else if (finger_choice == 2) {
                val += (finger_angles[2] + finger_angles[3] + finger_angles[4]) / 3f;
            } else if (finger_choice == 3) {
                val += (finger_angles[5] + finger_angles[6] + finger_angles[7]) / 3f;
            } else if (finger_choice == 4) {
                val += (finger_angles[8] + finger_angles[9] + finger_angles[10]) / 3f;
            } else if (finger_choice == 5) {
                val += (finger_angles[11] + finger_angles[12] + finger_angles[13]) / 3f;
            } else if (finger_choice == 6) {
                val = finger_angles[14];
            } else if (finger_choice < 20) {
                val = finger_angles[finger_choice - 6];
            }
            lineEntries.add(new Entry(0, val, entry.getKey()));
        }
        MyComparator comp = new MyComparator();
        lineEntries.sort(comp);
        for (int i = 0; i < lineEntries.size(); i++) {
            Entry e = lineEntries.get(i);
            e.setX(i);
            lineEntries.set(i, e);
        }

        lineChart.getDescription().setEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineDataSet = new LineDataSet(lineEntries, "");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        Format formatter = new Format();
        lineData.setValueFormatter(formatter);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(18f);
    }
}