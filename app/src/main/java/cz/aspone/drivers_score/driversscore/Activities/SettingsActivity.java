package cz.aspone.drivers_score.driversscore.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cz.aspone.drivers_score.driversscore.Fragments.SettingsFragment;
import cz.aspone.drivers_score.driversscore.Helpers.ExceptionHandler;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}