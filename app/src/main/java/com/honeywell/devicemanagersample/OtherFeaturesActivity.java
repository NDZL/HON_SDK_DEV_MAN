package com.honeywell.devicemanagersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.osservice.sdk.CreateListener;
import com.honeywell.osservice.sdk.DeviceManager;
import com.honeywell.osservice.sdk.HonOSException;


public class OtherFeaturesActivity extends AppCompatActivity {
    private DeviceManager mDeviceManager=DeviceManager.getInstance(this);
    private Spinner spinColor;
    private EditText editPeriod;
    private EditText editDuty;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_features);
        spinColor = (Spinner) findViewById(R.id.spinColor);
        editPeriod = (EditText) findViewById(R.id.editPeriod);
        editDuty = (EditText) findViewById(R.id.editDuty);
        textView=(TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.honeywell.intent.action.HALL_SENSOR_STATE_CHANGE");

    }
    public void setRGBLedNotification(View v) {
        if (null != mDeviceManager) {
            try {
                int position = spinColor.getSelectedItemPosition();
                int color = 0;
                switch (position) {
                    case 0: // off
                        color = 0;
                        break;
                    case 1: // red
                        color = 0xFF0000;
                        break;
                    case 2: // green
                        color = 0x00FF00;
                        break;
                    case 3: // blue
                        color = 0x0000FF;
                        break;
                }
                String p = editPeriod.getText().toString();
                if (null == p || p.isEmpty()) {
                    Toast.makeText(this, "Please input period", Toast.LENGTH_LONG).show();
                    return;
                }
                int period = Integer.valueOf(p);
                if (period > 50 || period <= 1) {
                    Toast.makeText(this, "Please make sure 1 < period <= 50", Toast.LENGTH_LONG).show();
                    return;
                }
                String d = editDuty.getText().toString();
                if (null == d || d.isEmpty()) {
                    Toast.makeText(this, "Please input on time", Toast.LENGTH_LONG).show();
                    return;
                }
                int duty = Integer.valueOf(d);
                if (duty > period || duty < 0) {
                    Toast.makeText(this, "Please make sure 0 <= onTime <= period", Toast.LENGTH_LONG).show();
                    return;
                }

                mDeviceManager.setRGBLedNotification(color, period, duty);
                textView.append("Color Entered = " + color + "\n" + "Period Entered = " + period + "\n" + "Duty Entered = " + duty + "\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("SetRGBLEDNotification = " + e.getMessage() + "\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

// Register a receiver for HALL sensor state change intent broadcasts.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.honeywell.intent.action.HALL_SENSOR_STATE_CHANGE");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                textView.append(intent.getStringExtra("hallSensorId")+" = "+intent.getIntExtra("hallSensorState", -1)+"\n");
                Log.d("OSSDK_Demo", "onReceive hallSensorId" + intent.getStringExtra("hallSensorId"));
                Log.d("OSSDK_Demo", "onReceive hallSensorState:" + intent.getIntExtra("hallSensorState", -1));
            }
        }, intentFilter);

    }
}
