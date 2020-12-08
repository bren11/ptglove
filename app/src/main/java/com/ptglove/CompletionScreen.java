package com.ptglove;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class CompletionScreen extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion__screen);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean found = false;
        boolean foundCur = false;
        for (int i = 0; i < Exercises.values().length; i++) {
            if (Exercises.values()[i] == HandScreen.curExercise) {
                foundCur = true;
            } else if (foundCur && preferences.contains(Exercises.values()[i].name) && preferences.getBoolean(Exercises.values()[i].name, false)) {
                HandScreen.curExercise = Exercises.values()[i];
                found = true;
                break;
            }
        }
        if (!found) {
            swapToMain();
        }

        findViewById(R.id.next_ex_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToRun();
            }
        });
    }

    void swapToRun() {
        startActivity(new Intent(this, HandScreen.class));
    }
    void swapToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }
}