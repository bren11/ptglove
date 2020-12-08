package com.ptglove;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

class ConnectBT extends AsyncTask<Void, Void, Void> {
    private boolean ConnectSuccess = false;
    String address = null;
    public ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    public static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Set<BluetoothDevice> pairedDevices;
    private Activity activity;
    String s;

    ConnectBT(Activity _activity) {
        super();
        activity = _activity;
    }

    @Override
    protected void onPreExecute() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                progress = ProgressDialog.show(activity, "Connecting...", "Please wait!!!");  //show a progress dialog
            }
        });

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            s = "Bluetooth Adaptor Not Available";
            msg();
            SystemClock.sleep(2000);
            return;
        } else if (!myBluetooth.isEnabled()) {
            s = "Please Enable Bluetooth On This Device";
            msg();
            SystemClock.sleep(2000);
            //Toast.makeText(activity.getApplicationContext(), "Please Enable Bluetooth On THis Device", Toast.LENGTH_LONG).show();
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnBTon, 1);
            return;
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
            //Toast.makeText(activity.getApplicationContext(), "No Paired Glove Found. Please pair \"473-Glove\"", Toast.LENGTH_LONG).show();
            s = "No Paired Glove Found. Please pair \"473-Glove\"";
            msg();
            SystemClock.sleep(2000);
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            activity.startActivity(intent);
            this.cancel(true);
        }
    }

    @Override
    protected Void doInBackground(Void... devices) {
        try {
            if ((btSocket == null || !btSocket.isConnected()) && address != null) {
                ConnectSuccess = true;
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection
            }
        } catch (IOException e) {
            progress.dismiss();
            //Toast.makeText(activity, "Failed to connect to glove. Please turn on glove & connect", Toast.LENGTH_LONG).show();
            s = "Failed to connect to glove. Please turn on glove & connect";
            msg();
            SystemClock.sleep(2000);
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            activity.startActivity(intent);
            ConnectSuccess = false;//if the try failed, you can check the exception here
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
        super.onPostExecute(result);
        if (ConnectSuccess) {
            //Toast.makeText(activity, "Connected", Toast.LENGTH_LONG).show();
            s = "Connected";
            msg();
            isBtConnected = true;
        }
        progress.dismiss();

        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void msg() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void disconnect() {
        if (btSocket != null && btSocket.isConnected()) {
            try {
                btSocket.getOutputStream().write("D".toString().getBytes());
                SystemClock.sleep(500);
                btSocket.close();
            } catch (IOException e) {

            }
        }
    }
}