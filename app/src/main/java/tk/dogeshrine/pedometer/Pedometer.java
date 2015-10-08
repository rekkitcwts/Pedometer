package tk.dogeshrine.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class Pedometer extends AppCompatActivity {

    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;

    private TextView textSensitive;
    private TextView textViewSteps;

    private Button buttonReset;

    private SensorManager sensorManager;
    private float acceleration;

    private float previousY;
    private float currentY;
    private int numSteps;

    private SeekBar seekBar;
    private int threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        textViewX = (TextView)findViewById(R.id.textViewX);
        textViewY = (TextView)findViewById(R.id.textViewY);
        textViewZ = (TextView)findViewById(R.id.textViewZ);

        textViewSteps = (TextView)findViewById(R.id.textSteps);
        textSensitive = (TextView)findViewById(R.id.textSensitive);

        buttonReset = (Button)findViewById(R.id.buttonReset);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        threshold = 10;
        textSensitive.setText(String.valueOf(threshold));

        previousY = 0;
        currentY = 0;
        numSteps = 0;

        acceleration = 0.0f;

        enableAccelerometerListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedometer, menu);
        return true;
    }

    private void enableAccelerometerListening() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            currentY = y;

            if (Math.abs(currentY - previousY) > threshold) {
                numSteps++;
                textViewSteps.setText(String.valueOf(numSteps));
            }

            textViewX.setText(String.valueOf(x));
            textViewY.setText(String.valueOf(y));
            textViewZ.setText(String.valueOf(z));

            previousY = y;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            threshold = seekBar.getProgress();
            textSensitive.setText(String.valueOf(threshold));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void resetSteps(View v) {
        numSteps = 0;
        textViewSteps.setText(String.valueOf(numSteps));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
