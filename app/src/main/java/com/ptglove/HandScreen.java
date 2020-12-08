package com.ptglove;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.ECField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class HandScreen extends AppCompatActivity {
    private HandDiagram handDiagram;
    private VideoView video;
    private ProgressBar progressBar;
    private MediaPlayer ding;
    private TextView instrText;
    private int[] maxBend, minBend, threshold, curRecord;
    private int[][] targetBend;
    private boolean[] correct, override;
    private int curPos, curReps;
    private SharedPreferences sharedPref, progressData, preferences;
    private SharedPreferences.Editor editor;
    private WorkerThread mWorkerThread;

    private int repsMax;
    private final int pityDelay = 2000;
    private final int prePityDelay = 2000;
    private final int startDelay = 500;
    private final int holdDelay = 4000;
    public static Exercises curExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_screen);

        handDiagram = (HandDiagram) findViewById(R.id.handDiagram);
        video = (VideoView) findViewById(R.id.video);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgressTintMode(PorterDuff.Mode.LIGHTEN);
        ding = MediaPlayer.create(this, R.raw.ding);
        instrText = (TextView) findViewById(R.id.feedback_text);

        sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE);
        progressData = getSharedPreferences("progressData", MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        maxBend = new int[Joints.length()];
        minBend = new int[Joints.length()];
        targetBend = new int[Positions.values().length][Joints.length()];
        threshold = new int[Joints.length()];
        correct = new boolean[Joints.length()];
        override = new boolean[Joints.length()];
        curPos = 0;
        curReps = -1;

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        setAnimation(curExercise.animations[0]);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPref.getString("mins", null) == null || sharedPref.getString("maxs", null) == null) {
            startActivity(new Intent(this, CalibrateScreen.class));
            return;
        }
        String[] mins = sharedPref.getString("mins", null).split(" ");
        String[] maxs = sharedPref.getString("maxs", null).split(" ");

        for (int i = 0; i < Joints.length(); i++) {
            maxBend[i] = Integer.parseInt(maxs[i]);
            minBend[i] = Integer.parseInt(mins[i]);
            threshold[i] = (maxBend[i] - minBend[i]) / 5 + 5;
            correct[i] = true;
            override[i] = false;
        }

        String[] options = getResources().getStringArray(R.array.reps_values);
        repsMax = 0;
        while (true) {
            if (repsMax == options.length) {
                repsMax = 1;
                break;
            }
            if (options[repsMax].equals(preferences.getString("number_of_reps", "one"))) break;
            repsMax++;
        }
        repsMax++;

        for (int i = 0; i < Positions.values().length; i++) {
            if (sharedPref.contains(Positions.values()[i].name + "_target")) {
                String[] tgts = sharedPref.getString(Positions.values()[i].name + "_target", "fail").split(" ");
                for (int j = 0; j < Joints.length(); j++) {
                    targetBend[i][j] = Integer.parseInt(tgts[j]);
                }
            } else {
                for (int j = 0; j < Joints.length(); j++) {
                    targetBend[i][j] = Positions.values()[i].wantMoreBend[j] ? minBend[j] : maxBend[j];
                }
            }
        }

        curPos = 0;
        curReps = 0;
        curRecord = targetBend[curExercise.positions[curPos].ordinal()].clone();
        setAnimation(curExercise.animations[0]);
        instrText.setText(curExercise.positions[curPos].strRes);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (curReps == -1) return;

        mWorkerThread = new WorkerThread(this);
        mWorkerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWorkerThread.running = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ConnectBT.btSocket != null) {
            try {
                ConnectBT.btSocket.getOutputStream().write("D".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void setAnimation(int res) {
        Uri uri = Uri.parse("android.resource://com.ptglove/" + res);

        video.setVideoURI(uri);
        video.requestFocus();
        video.start();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void toComplete() {
        startActivity(new Intent(this, CompletionScreen.class));
    }
    private void toMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private class WorkerThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private Activity activity;
        volatile boolean running = true;

        public WorkerThread(Activity ac) {
            activity = ac;
        }

        public void run() {
            running = true;

            if (ConnectBT.btSocket == null || !ConnectBT.btSocket.isConnected()) {
                ConnectBT connection = new ConnectBT(activity);
                connection.execute();
                while(!connection.isCancelled() && (connection.progress == null || connection.progress.isShowing()));
            }

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            if (ConnectBT.btSocket != null) {
                try {
                    tmpIn = ConnectBT.btSocket.getInputStream();
                    tmpOut = ConnectBT.btSocket.getOutputStream();
                } catch (IOException e) {
                }
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            int newTgt = -1;
            int val = 0;
            long startMilis = System.currentTimeMillis();
            long correctMilis = -1;
            try{
                mmOutStream.write("E".toString().getBytes());
            } catch(Exception e){}
            while (running && ConnectBT.btSocket != null) {
                SystemClock.sleep(1);
                try {
                    if (mmInStream.available() > 0) {
                        int c = (char) mmInStream.read();
                        if (c < 48) {
                            if (newTgt >= 0) {
                                Log.d(" values: ", "num at  " + newTgt + ": " + val + ", expected: " + targetBend[curExercise.positions[curPos].ordinal()][newTgt]);

                                correct[newTgt] = (!curExercise.positions[curPos].careAbout[newTgt]) ||
                                        (curExercise.positions[curPos].wantMoreBend[newTgt] && (val > (targetBend[curExercise.positions[curPos].ordinal()][newTgt] - threshold[newTgt]))) ||
                                        (!curExercise.positions[curPos].wantMoreBend[newTgt] && (val < (targetBend[curExercise.positions[curPos].ordinal()][newTgt] + threshold[newTgt])));

                                if (curExercise.positions[curPos].wantMoreBend[newTgt] && (val > (curRecord[newTgt])) ||
                                   !curExercise.positions[curPos].wantMoreBend[newTgt] && (val < (curRecord[newTgt]))) {
                                    curRecord[newTgt] = (val + curRecord[newTgt] * 9) / 10;
                                }

                                if (curRecord[newTgt] < minBend[newTgt]) {
                                    minBend[newTgt] = curRecord[newTgt];
                                } if (curRecord[newTgt] > maxBend[newTgt]) {
                                    maxBend[newTgt] = curRecord[newTgt];
                                }


                                for (int j = 0; j < correct.length; j++) {
                                    override[j] = false;
                                }
                                for (long i = System.currentTimeMillis(); i >= (startMilis + holdDelay + prePityDelay); i -= pityDelay) {
                                    for (int j = 0; j < correct.length; j++) {
                                        if (!correct[j] && !override[j]) {
                                            override[j] = true;
                                            break;
                                        }
                                    }
                                }

                                if (correct[newTgt] || override[newTgt]) {
                                    handDiagram.changeColor(Joints.values()[newTgt], 0xa03BB143);
                                } else if (curExercise.positions[curPos].wantMoreBend[newTgt]){
                                    handDiagram.changeColor(Joints.values()[newTgt], 0xa0D30000);
                                } else {
                                    handDiagram.changeColor(Joints.values()[newTgt], 0xa0FDD017);
                                }

                                if (newTgt == 1 || newTgt == 4 || newTgt == 7 || newTgt == 10 || newTgt == 13) {
                                    handDiagram.changeColor(Joints.values()[newTgt], 0xa0D30000);
                                } if (newTgt == 5 || newTgt == 6) {
                                    handDiagram.changeColor(Joints.values()[newTgt], 0xa0FDD017);
                                }
                                handDiagram.update();
                                //Log.d(" values: ", startMilis + " | " + correctMilis + " | " + System.currentTimeMillis());
                                if (System.currentTimeMillis() > (startMilis + startDelay)) {
                                    int count = 0;
                                    for (int i = 0; i < correct.length; i++) {
                                        if (!correct[i] && !override[i])
                                            count++;
                                    }
                                    if (count == 0 && correctMilis == -1) {
                                        correctMilis = System.currentTimeMillis();
                                    } else if (correctMilis != -1 && System.currentTimeMillis() > (correctMilis + holdDelay)) {
                                        correctMilis = -1;
                                        startMilis = System.currentTimeMillis();
                                        ding.start();
                                        targetBend[curExercise.positions[curPos].ordinal()] = curRecord.clone();
                                        StringBuilder s = new StringBuilder();
                                        StringBuilder sMin = new StringBuilder();
                                        StringBuilder sMax = new StringBuilder();
                                        for (int j = 0; j < Joints.length(); j++) {
                                            s.append(curRecord[j]).append(" ");
                                            sMin.append(minBend[j]).append(" ");
                                            sMax.append(maxBend[j]).append(" ");
                                        }
                                        editor.putString(curExercise.positions[curPos].name + "_target", s.toString());
                                        editor.putString("mins", sMin.toString());
                                        editor.putString("maxs", sMax.toString());

                                        Calendar cal = Calendar.getInstance();
                                        String key = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE);
                                        if (progressData.contains(key)) {
                                            String[] out = progressData.getString(key, "").split(" ");
                                            for (int k = 0; k < Joints.length(); k++) {
                                                if (Integer.parseInt(out[k]) > curRecord[k]) {
                                                    curRecord[k] = Integer.parseInt(out[k]);
                                                }
                                            }
                                        }
                                        s = new StringBuilder();
                                        for (int j = 0; j < Joints.length(); j++) {
                                            s.append(curRecord[j]).append(" ");
                                        }
                                        SharedPreferences.Editor data = progressData.edit();
                                        data.putString(key, s.toString());

                                        data.apply();
                                        editor.apply();
                                        curPos++;
                                        if (curPos >= curExercise.getLength()) {
                                            curReps++;
                                            curPos = 0;
                                        }

                                        if (curReps >= repsMax) {
                                            toComplete();
                                        } else {
                                            curRecord = targetBend[curExercise.positions[curPos].ordinal()].clone();
                                            instrText.post(new Runnable() {
                                                public void run() {
                                                    setAnimation(curExercise.animations[curPos]);
                                                    instrText.setText(curExercise.positions[curPos].strRes);
                                                }
                                            });
                                        }
                                    } else if (count > 0) {
                                        correctMilis = -1;
                                    }
                                }

                                float setTo = 1000f * curReps / repsMax +
                                        (1000f / repsMax) * curPos / curExercise.getLength();
                                if (correctMilis > 0) {
                                    setTo += (1000f / repsMax / curExercise.getLength()) * (System.currentTimeMillis() - correctMilis) / holdDelay;
                                }
                                progressBar.setProgress((int) setTo, true);
                            }
                            newTgt = c;
                            val = 0;
                        } else {
                            val = val * 10 + (c - 48);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
