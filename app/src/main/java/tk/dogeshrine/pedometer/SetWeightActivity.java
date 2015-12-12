package tk.dogeshrine.pedometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetWeightActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private float userWeight;
    private SharedPreferences prefs;

    private String unitType;

    private TextView weightInput;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_weight);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        weightInput = (TextView) findViewById(R.id.user_weight);
        spinner = (Spinner) findViewById(R.id.weight_unit);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weight_units, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        unitType = prefs.getString("measurement_units", "NO WAY");
        userWeight = prefs.getFloat("user_weight", 0.0f);

        if (userWeight > 0.0f) {
            weightInput.setText(String.valueOf(userWeight));
        }

        if (TextUtils.equals(unitType, "1")) {
            spinner.setSelection(1);
        }
        else {
            spinner.setSelection(0);
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void saveUserWeight(View view) {
        SharedPreferences.Editor editor = prefs.edit();
        Intent intent;
        weightInput = (EditText) findViewById (R.id.user_weight);
        Float weightValue = Float.valueOf(weightInput.getText().toString());
        int unitSelected = spinner.getSelectedItemPosition();

        if (unitSelected == 1) {
            editor.putFloat("user_weight", convertWeight(weightValue));
        }
        else {
            editor.putFloat("user_weight", weightValue);
        }

        editor.apply();
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private float convertWeight(float weight) {
        return (weight / 0.454f);
    }

}
