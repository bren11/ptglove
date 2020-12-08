package com.ptglove;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class CalibrateScreen extends AppCompatActivity {
    private int num;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private StringBuilder curStr;
    private VideoView video;
    private TextView text;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_screen);

        nextButton = (Button) findViewById(R.id.next_button);
        video = (VideoView) findViewById(R.id.calib_video);
        text = (TextView) findViewById(R.id.calib_text);

        sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE);
        editor = sharedPref.edit();
        num = 0;
        curStr = new StringBuilder();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        setAnimation(R.raw.fist_out);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectBT.btSocket == null || !ConnectBT.btSocket.isConnected()) return;
                SystemClock.sleep(1000);
                switch (num) {
                    case 0: {
                        getValues(0, 14);
                        setAnimation(R.raw.wrist_str_b);
                        text.setText(R.string.calib_wrist_b);
                        break;
                    } case 1: {
                        getValues(14, 15);
                        Log.d("values: ", curStr.toString());
                        editor.putString("mins", curStr.toString());
                        curStr = new StringBuilder();
                        setAnimation(R.raw.finger_thumb);
                        text.setText(R.string.calib_thumb);
                        break;
                    } case 2: {
                        getValues(0, 2);
                        setAnimation(R.raw.fist_in);
                        text.setText(R.string.calib_fist);
                        break;
                    } case 3: {
                        getValues(2, 14);
                        setAnimation(R.raw.wrist_str_f);
                        text.setText(R.string.calib_wrist_f);
                        break;
                    } case 4: {
                        getValues(14, 15);
                        Log.d("values: ", curStr.toString());
                        editor.putString("maxs", curStr.toString());
                        editor.apply();
                        goBack();
                        break;
                    }
                }
                num++;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (ConnectBT.btSocket == null || !ConnectBT.btSocket.isConnected()) {
            ConnectBT connection = new ConnectBT(this);
            connection.execute();
        }
    }

    private void setAnimation(int res) {
        Uri uri = Uri.parse("android.resource://com.ptglove/" + res);

        video.setVideoURI(uri);
        video.requestFocus();
        video.start();
    }

    void goBack() {
        startActivity(new Intent(this, MainActivity.class));
    }

    void getValues(int startInc, int endNoInc) {
        int targetNumber = endNoInc - startInc;
        int newTgt = -1;
        int ret = 0;
        int[] vals = new int[targetNumber];
        try {
            ConnectBT.btSocket.getOutputStream().write("E".toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (targetNumber > (startInc - endNoInc)) {
            SystemClock.sleep(1);

            try {
                if (ConnectBT.btSocket.getInputStream().available() > 0) {
                    int c = (char) ConnectBT.btSocket.getInputStream().read();
                    if (c < 48) {
                        if (newTgt >= startInc && newTgt < endNoInc) {
                            Log.d("values: ", "at " + newTgt + ": " + ret);
                            targetNumber--;
                            vals[newTgt - startInc] = ret;
                        }
                        newTgt = c;
                        ret = 0;
                    } else {
                        ret = ret * 10 + (c - 48);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        for (int i = 0; i < (endNoInc - startInc); i++) {
            curStr.append(vals[i]).append(" ");
        }
        try {
            ConnectBT.btSocket.getOutputStream().write("D".toString().getBytes());
            while (ConnectBT.btSocket.getInputStream().available() > 0) {
                SystemClock.sleep(1);
                ConnectBT.btSocket.getInputStream().read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
