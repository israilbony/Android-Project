package com.example.bony.test;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.test.mock.MockPackageManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE_PERMISSION = 3;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String Phone1 = "phoneKey";
    public static SharedPreferences sharedpreferences;
    final Context context = this;
    //public static SharedPreferences onoffSharedPreferences;
    private static MainActivity ins;
    private Switch mySwitch;
    // GPSTracker class
    GPSTracker gps;
    Button buttonSend;
    EditText textPhoneNo;
    EditText pho;
    EditText textSMS;
    Button location;
    Button sentlocation;
    Button save;
    int i = 0;
    TextView text2;
    public static MainActivity  getInstace(){
        return ins;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);
        //text2=(TextView) findViewById(R.id.textView2);
        location = (Button) findViewById(R.id.location);
        sentlocation = (Button) findViewById(R.id.sentlocation);
        save = (Button) findViewById(R.id.save);
        // pho=(EditText) findViewById(R.id.TextPhoneNo);

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        MyReceiver mReceiver = new MyReceiver (this);
        registerReceiver(mReceiver, filter);

        ////
        mySwitch = (Switch) findViewById(R.id.mySwitch);

        //set the switch to ON
        mySwitch.setChecked(true);
        //attach a listener to check for changes in state
        sharedpreferences = getSharedPreferences(Phone1, Context.MODE_PRIVATE);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    //switchStatus.setText("Switch is currently ON");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("onoff", "1");
                    editor.commit();
                }else{
                    //switchStatus.setText("Switch is currently OFF");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("onoff", "0");
                    editor.commit();
                }

            }
        });

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
            //switchStatus.setText("Switch is currently ON");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("onoff", "1");
            editor.commit();
        }
        else {
            //switchStatus.setText("Switch is currently OFF");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("onoff", "0");
            editor.commit();
        }

        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });

        // Ask user to enable GPS/network in settings


        sentlocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {
                    String phoneNo = textPhoneNo.getText().toString();

                    String numbers [] = phoneNo.split(",");
                    for (String number:numbers) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        String sms = "https://www.google.com.bd/maps/@" + latitude + "," + longitude + "," + "18z";

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, sms, null, null);
                            Toast.makeText(getApplicationContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "SMS faild, please try again later!",
                                    Toast.LENGTH_LONG).show();
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


        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String phoneNo = textPhoneNo.getText().toString();
                String sms = textSMS.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
        sharedpreferences = getSharedPreferences(Phone1, Context.MODE_PRIVATE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
       /* Alert Dialog Code Start*/
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Set Emergency Numbers"); //Set Alert dialog title here
                alert.setMessage("Enter Your Emergency Numbers Here"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(context);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String srt = input.getEditableText().toString();

                        SharedPreferences.Editor editor = sharedpreferences.edit();


                        editor.putString(Phone1,srt);

                        editor.commit();
                        Toast.makeText(MainActivity.this, sharedpreferences.getString(Phone1,""),Toast.LENGTH_LONG).show();
                        Toast.makeText(context,srt,Toast.LENGTH_LONG).show();
                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
       /* Alert Dialog Code End*/
            }// End of onClick(View v)
        }); //button.setOnClickListener



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}