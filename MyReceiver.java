package com.example.bony.test;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MyReceiver extends BroadcastReceiver {
    GPSTracker gps;
    static int countPowerOff = 0;
    private Activity activity = null;
    long t1 = System.currentTimeMillis();
    long t2 = System.currentTimeMillis();
    static int starttime = 0;
    Thread thread;
    Handler handler = new Handler();

    public MyReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Button button=(Button) ((Activity) context).findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.stop();
            }
        });
        Log.v("onReceive", "Power button is pressed.");
       int a=countPowerOff+1;
        Toast.makeText(context, "power button clicked "+a, Toast.LENGTH_LONG)
                .show();

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            countPowerOff++;
            if (countPowerOff == 1) t1 = System.currentTimeMillis();
            if (countPowerOff == 2) t2 = System.currentTimeMillis();
        } else {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                Log.v("Time diff", "" + (t2 - t1));

                String onoff = MainActivity.sharedpreferences.getString("onoff", "");
                if (countPowerOff == 2 && onoff.equalsIgnoreCase("1") && (t2 - t1) <= 3000) {

                    thread = new Thread(new Task());
                    thread.start();

                    //countPowerOff=0;
                }
                if (countPowerOff == 2) {
                    countPowerOff = 0;
                }
            }
        }

    }

    class Task implements Runnable {
        @Override
        public void run() {
            Message message = Message.obtain();
            for (int i = 0; i <= 5; i++) {
                message.arg1 = i;

                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {

                    @Override
                    public void run() {

                        gps = new GPSTracker(activity);

                        // check if GPS enabled
                        if (gps.canGetLocation()) {
                            String phoneNo = MainActivity.sharedpreferences.getString("phoneKey","");


                            String numbers [] = phoneNo.split(",");
                            for (String number:numbers) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                String sms = "I am in danger.Plz Help me. My location is in this link  https://www.google.com.bd/maps/@" + latitude + "," + longitude + "," + "18z";

                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(number, null, sms, null, null);

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }



                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            gps.showSettingsAlert();
                        }
                    }
                });
                //bar.setProgress(i);


            }
        }
    }
}



