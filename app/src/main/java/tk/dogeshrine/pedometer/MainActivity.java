package tk.dogeshrine.pedometer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;

    private TextView weightUnit;
    private TextView weightValue;

    private Button startCounterButton;

    /**
     * {non-Javadoc}
     *
     * Variables for the pedometer's step-counter
     */
    private boolean counterOn;
    private SensorManager sensorManager;
    private float acceleration;

    private TextView textViewSteps;

    private float previousY;
    private float currentY;
    private int numSteps;

    private SeekBar seekBar;
    private int threshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        weightValue = (TextView) findViewById(R.id.weight);
        weightUnit = (TextView) findViewById(R.id.weight_unit);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String units = prefs.getString("measurement_units", "NO WAY");
        Float weight = prefs.getFloat("user_weight", 0.0f);
        if (TextUtils.equals(units, "1")) {
            weightValue.setText(String.valueOf(convertWeight(weight, 1)));
            weightUnit.setText(" kgs");
        }
        else {
            weightValue.setText(String.valueOf(weight));
            weightUnit.setText(" lbs");
        }

        textViewSteps = (TextView) findViewById(R.id.textSteps);

        startCounterButton = (Button) findViewById(R.id.stepDetectionButton);

        counterOn = false;
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setProgress(9);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        threshold = 10;

        previousY = 0;
        currentY = 0;
        numSteps = 0;

        acceleration = 0.0f;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_calibrate) {
            boolean flag = displayGpsStatus();
            if (flag) {
                Toast.makeText(MainActivity.this, "GPS on", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "GPS off", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_setweight) {
            intent = new Intent(this, SetWeightActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        return Settings.Secure.isLocationProviderEnabled(contentResolver,
                LocationManager.GPS_PROVIDER);
    }

    private float convertWeight(float weight, int conversionType) {
        if (conversionType == 1) {
            // pounds to kilograms
            return weight * (0.454f);
        }
        else if (conversionType == 2) {
            // kilograms to pounds
            return (weight / 0.454f);
        }
        else {
            return 0.0f;
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            threshold = seekBar.getProgress() + 1;
        }
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float y = event.values[1];

            currentY = y;

            if (Math.abs(currentY - previousY) > threshold) {
                numSteps++;
                textViewSteps.setText(String.valueOf(numSteps));
            }

            previousY = y;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void toggleStepDetection(View view) {
        if (counterOn) {
            counterOn = false;
            startCounterButton.setText(getString(R.string.start_detection));
            Toast.makeText(MainActivity.this, "Step detector now off.", Toast.LENGTH_SHORT).show();
            sensorManager.unregisterListener(sensorEventListener);
        }
        else {
            counterOn = true;
            startCounterButton.setText(getString(R.string.stop_detection));
            Toast.makeText(MainActivity.this, "Step detector now on.", Toast.LENGTH_SHORT).show();
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}
