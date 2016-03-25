package edu.purdue.dbough.sweetsignal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FragmentManager fragmentManager;
    boolean firstIteration = true;
    long prevTimeInMillis = 0;
    Double prevBloodSugar = 0.0;
    double slopeTotal = 0.0;
    int lowSlopeandSugarCounter = 0;
    boolean highSlopeAndSugarFlag = false;
    SettingsFragment settingsFragment;
    InputFragment inputFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        // Create a new Fragment to be placed in the activity layout
        final HomeFragment homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "home")
                .addToBackStack(null)
                .commit();

        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                settingsFragment = new SettingsFragment().newInstance();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment, "input")
                        .addToBackStack(null)
                        .commit();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void displayInputFrag(View view){
        inputFragment = new InputFragment().newInstance();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, inputFragment, "input")
                .addToBackStack(null)
                .commit();
    }

    //Called from fragment_input.xml
    public void displayHomeFrag(View view){
        inputFragment.saveData(inputFragment.getView()); //Write data to .csv file
        ArrayList<SugarEntry> recentList = latestEntries(view, 9); //Gets entries from last n hours
        calculateTrends(view, recentList);

        // Create a new Fragment to be placed in the activity layout
        final HomeFragment homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment, "home")
                .addToBackStack(null)
                .commit();
    }

    public void hideKeyboard (View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sendSMS(String phoneNo, String sms) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS unable to send",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void Notify(String notificationTitle, String notificationMessage){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.warningflag)
                .setContentTitle("Attention!")
                .setContentText(notificationMessage);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSmallIcon(R.drawable.megaphoneicon);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(9999, mBuilder.build());
    }

    public void Signal (View view, int confidence) {
        Notify("SUGAR WARNING", "BLOOD SUGAR MAY DROP SOON! Code: " + confidence);
        Toast.makeText(getApplicationContext(), "BLOOD SUGAR MAY DROP SOON! Code: " + confidence,
                Toast.LENGTH_LONG).show();
        String[] contactArray = settingsFragment.loadContacts();
        for (String contact : contactArray){
            sendSMS(contact, "You are being alerted because a low blood sugar level was recently detected!");
        }
    }

    public ArrayList<SugarEntry> latestEntries(View view, int hoursBefore) {
            //Get elements from the past amount of hours in: parameter hoursBefore
            String line;
            String[] values;
            int len = 0;
            int i = 0;
            int j = 0;
            int k = 0;
            ArrayList<SugarEntry> list = new ArrayList<>();
            Context context = view.getContext();
            File fileDir = new File(context.getFilesDir() + File.separator);
            File file = new File(fileDir + "BloodSugarLevels.csv");

            //Find the properly formatted time for n hours before curr time
            long earlierInMillis = TimeUnit.HOURS.toMillis(hoursBefore);
            long entryInMillis = 0;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        try {
                FileInputStream fis = new FileInputStream (new File(String.valueOf(file)));
                BufferedReader r = new BufferedReader(new InputStreamReader(fis));

                //Assumes csv contains sugar,time,date
                while((line = r.readLine()) != null) {
                    values = line.split(",");
                    len = values.length;
                    k = len - 1;
                    j = len - 2;
                    i = len - 3;
                    for (int z = len -1; z >= 0; z-= 3) {
                        try {
                            Date entryTime = df.parse(values[j]);
                            entryInMillis = entryTime.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //Add if entry meets time criteria
                        if (entryInMillis > earlierInMillis) {
                            SugarEntry entry = new SugarEntry(values[i], values[j], values[k]);
                            list.add(entry);
                        }
                        else {
                            break;
                        }
                        i-=3;
                        j-=3;
                        k-=3;
                    }
                }
            } catch (IOException e) {
                Toast.makeText(context, "Problem Iterating File", Toast.LENGTH_SHORT).show();
            }
        return list;
    }

    public void calculateTrends(View view, ArrayList<SugarEntry> recentList){
        int targetBloodSugar = 100;
        if (settingsFragment != null)
            targetBloodSugar = settingsFragment.getTargetBloodSugar();

        final double HBC = 1.32 * targetBloodSugar; //Dynamic high blood sugar value
        final double LBC = 0.6 * targetBloodSugar; //Dynamic low blood sugar value
        final double slopeConstant = 0.75;

        double entrySlope = 0;
        long entryInMillis = 0;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Double bloodSugar = 0.0;
        int len = recentList.size();

        //Finds the total slope values at each (time x sugar) point
        for (int i = len - 1; i >= 0; i--) {
            bloodSugar = Double.parseDouble(recentList.get(i).getSugarLevel());
            try {
                Date entryTime = df.parse(recentList.get(i).getTime());
                entryInMillis = entryTime.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (firstIteration) {
                prevTimeInMillis = entryInMillis;
                firstIteration = false;
                continue;
            }
            //Minutes since last entry
            long minutes = TimeUnit.MILLISECONDS.toMinutes(entryInMillis - prevTimeInMillis);
            //if difference is seconds, negative minutes are returned
            if (minutes <= 0)
                continue;
            entrySlope = (bloodSugar - prevBloodSugar)/minutes;
            if (i == 0) {
                slopeTotal += entrySlope;
                prevTimeInMillis = entryInMillis;
                prevBloodSugar = bloodSugar;
            }

            //-----------------Analyze slope averages for future sugar drop ----------------------
            if (slopeTotal <= slopeConstant && slopeTotal >= -slopeConstant){ //Relatively even slope
                if (highSlopeAndSugarFlag) {
                    highSlopeAndSugarFlag = false;
                    Signal(view, 2);
                }

                if (bloodSugar > HBC) {
                    //Sugar is higher than HBC constant, do nothing
                }
                else if (bloodSugar < LBC){ //Low sugar, slope is even
                    if (lowSlopeandSugarCounter > 1) {
                        lowSlopeandSugarCounter = 0;
                        Signal(view, 1);
                    }
                    lowSlopeandSugarCounter++;
                }
                else {
                    //Best case scenario
                }
            }

            //************************************
            //Slope is high or low
            //************************************
            else {
                if (slopeTotal > slopeConstant) {
                    //Increasing slope, counter++ when blood sugar is high here?
                }

                else if (slopeTotal < -slopeConstant){
                    if (highSlopeAndSugarFlag) {
                        Signal(view, 3);
                    }
                    if (bloodSugar > HBC) { //High sugar, declining slope. Bad!
                        highSlopeAndSugarFlag = true;
                    }
                    else if (bloodSugar < LBC){ //Low sugar, declining slope. Worse!
                        Signal(view, 4);
                    }
                    else{
                        highSlopeAndSugarFlag = true;
                    }
                }
                else {
                    highSlopeAndSugarFlag = true;
                }
            }
        }

    } //calculateTrends class

} //MainActivity class
