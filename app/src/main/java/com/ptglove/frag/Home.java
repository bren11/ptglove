package com.ptglove.frag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.ptglove.Exercises;
import com.ptglove.HandScreen;
import com.ptglove.R;

public class Home extends Fragment {

    private SharedPreferences preferences;

    public Home() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        getView().findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < Exercises.values().length; i++) {
                    if (preferences.contains(Exercises.values()[i].name) && preferences.getBoolean(Exercises.values()[i].name, false)) {
                        HandScreen.curExercise = Exercises.values()[i];
                        startActivity(new Intent(getActivity(), HandScreen.class));
                        break;
                    }
                }
            }
        });
    }
}