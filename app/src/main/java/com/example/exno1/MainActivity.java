package com.example.exno1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText enterPSWRD;
    private Button submitPSWRD;

    //My 4 conditions:
    //1. last 2 digits from edit text must be less than battery level   V
    //2. time of input password has to be between 9 AM and 7 PM         V
    //3. airplane mode must be on                                       V
    //4. brightness must be exactly 50%                                 V

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        submitPSWRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = getLastTwoDigits(enterPSWRD.getText().toString());
                if (!result.isEmpty()) {
                    int check = Integer.parseInt(result);
                    if ( isBatteryLevelSufficient(check) && isBrightness50() && isAirplaneModeOn() && isTimeInRange()) {
                        Toast.makeText(MainActivity.this, "You got in, YAY!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "One of the parameters is wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Password has to include at least 2 numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isBatteryLevelSufficient(int check) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        int batteryLevel = (int) (batteryPct * 100);
        //Log.d("MAAYA1212", "hi: " + batteryLevel + " " + check);
        return batteryLevel >= check; //return true if last 2 digits from edit text must be less than battery level
    }

    private boolean isBrightness50(){
        int currentBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
        //Log.d("MAAYA1212", "bright " + currentBrightness);

        return currentBrightness == 131;
    }

    private boolean isAirplaneModeOn() {
        return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }


    private boolean isTimeInRange() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour in 24-hour format
        //Log.d("MAAYA1212", "hour: " + hour);

        // Define the time range (e.g., from 9 AM to 7 PM)
        int startHour = 9;
        int endHour = 19;

        // Check if the current hour is within the specified range
        return hour >= startHour && hour < endHour;
    }


    private void findViews() {
        enterPSWRD = findViewById(R.id.TXT_enterPSWRD);
        submitPSWRD = findViewById(R.id.BTN_submitPSWRD);
    }

    public String getLastTwoDigits(String input) {
        StringBuilder digits = new StringBuilder();
        int count = 0;

        // Iterate through the characters of the string in reverse order
        for (int i = input.length() - 1; i >= 0; i--) {
            char c = input.charAt(i);
            // Check if the character is a digit
            if (Character.isDigit(c)) {
                // Append the digit to the StringBuilder
                digits.append(c);
                count++;
                // Stop when we have collected two digits
                if (count == 2) {
                    break;
                }
            }
        }

        // Reverse the StringBuilder to get the correct order of digits
        return digits.reverse().toString();
    }
}
