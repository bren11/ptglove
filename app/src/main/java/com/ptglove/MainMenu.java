/*package com.ptglove;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainMenu extends AppCompatActivity {

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Set<BluetoothDevice> pairedDevices;
    private Button calibBtn, runBtn, resetBtn;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        HandScreen.curExercise = Exercises.FOUR_POSITION;

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        calibBtn = (Button) findViewById(R.id.calib_button);
        runBtn = (Button) findViewById(R.id.run_button);
        resetBtn = (Button) findViewById(R.id.reset_button);

        calibBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToCalib(); //method that will be called
            }
        });

        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToRun(); //method that will be called
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("mins");
                editor.remove("maxs");
                for (Positions pos : Positions.values()) {
                    editor.remove(pos.name + "_target");
                }
                editor.apply();
            }
        });

        sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE);
        editor = sharedPref.edit();

        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Adaptor Not Available", Toast.LENGTH_LONG).show();
            finish();
        } else if (!myBluetooth.isEnabled()) {
            //Toast.makeText(getApplicationContext(), "Please Enable Bluetooth On THis Device", Toast.LENGTH_LONG).show();
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        while (!myBluetooth.isEnabled());

        pairedDevices = myBluetooth.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                Log.d("device: ", bt.getName() + " " + bt.getAddress());
                if (bt.getName().equals("473-Glove")) {
                    address = bt.getAddress();
                }
            }
        }
        if (address == null) {
            Toast.makeText(getApplicationContext(), "No Paired Glove Found. Please pair \"473-Glove\"", Toast.LENGTH_LONG).show();
            SystemClock.sleep(2000);
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
        }

        //new MainMenu.ConnectBT().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (btSocket == null || !btSocket.isConnected()) {
            new MainMenu.ConnectBT().execute();
        }
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("D".toString().getBytes());
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    void swapToCalib() {
        startActivity(new Intent(this, com.ptglove.CalibrateScreen.class));
    }

    void swapToRun() {
        startActivity(new Intent(this, HandScreen.class));
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainMenu.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                progress.dismiss();
                runBtn.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to connect to glove. Please turn on glove & connect", Toast.LENGTH_LONG).show();
                    }
                });
                SystemClock.sleep(2000);
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            if (ConnectSuccess) {
                msg("Connected.");
                isBtConnected = true;
                progress.dismiss();
            }
        }
    }
}*/