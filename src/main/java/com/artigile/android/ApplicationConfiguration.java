package com.artigile.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * @author IoaN, 7/3/11 11:45 AM
 */
public class ApplicationConfiguration extends RoboActivity {

    public static final String SCARY_LEVEL_PROPERTY_NAME = "scaryLavel";

    public static final String SENSOR_SENSIVITY = "sensorSensivity";

    @Inject
    private SharedPreferences sharedPrefs;

    @InjectView(R.id.adjustor_seek)
    private SeekBar sensorSencivity;

    @InjectView(R.id.scary_level_text_input)
    private EditText scaryLevelTextBox;

    @Inject
    private MazeDotState mazeDotState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
    }

    @Override
    protected void onStart() {
        super.onStart();
        scaryLevelTextBox.setText(mazeDotState.getScaryLevel() + "");
        sensorSencivity.setProgress((int) (150/mazeDotState.getSensivityLevel()-1));
    }

    public void saveSettings(View view) {
        try {
            mazeDotState.setSensivityLevel(150F / (sensorSencivity.getProgress() + 1));
        } catch (NumberFormatException e) {
            System.out.println("Error on saving sensor sensitivity");
        }
        try {
            mazeDotState.setScaryLevel(Integer.valueOf(scaryLevelTextBox.getText().toString()));
            if (mazeDotState.getScaryLevel() < 1 || mazeDotState.getScaryLevel() > 50) {
                mazeDotState.setScaryLevel(3);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error on saving scary level number");
        }
        mazeDotState.setCurrentLevel(1);
        saveConfiguration();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveConfiguration();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveConfiguration() {
        SharedPreferences.Editor editPref = sharedPrefs.edit();
        editPref.putInt(SCARY_LEVEL_PROPERTY_NAME, mazeDotState.getScaryLevel());
        editPref.putFloat(SENSOR_SENSIVITY, mazeDotState.getSensivityLevel());
        editPref.commit();
    }
}
