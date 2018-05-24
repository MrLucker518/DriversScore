package cz.aspone.drivers_score.driversscore.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.DB.Loader;
import cz.aspone.drivers_score.driversscore.Helpers.DriversScore;
import cz.aspone.drivers_score.driversscore.Helpers.ExceptionHandler;
import cz.aspone.drivers_score.driversscore.Helpers.RenderHelper;
import cz.aspone.drivers_score.driversscore.Helpers.SavedSharedPreferences;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class BeginActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_begin);

        String sError = getIntent().getStringExtra("error");

        Button btnBegin = (Button) findViewById(R.id.btnBegin);

        if (sError == null) {
            checkLogged();
        } else {
            btnBegin.setText(R.string.field_return_to_app);
            // just for debugging
            RenderHelper helper = new RenderHelper(this);
            helper.createMessageBox("An error occurred", sError);
        }

        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SavedSharedPreferences.setFirstLoad(BeginActivity.this);
                checkLogged();
            }
        });

    }

    private void clearInitials() {
        DriversScore ds = (DriversScore) this.getApplication();
        ds.setUser(user);
        ds.setPlateIsProcessing(false);
        ds.setFirstSync(true);
        ds.setSyncInProgress(false);
        ds.setLastSawPlateNum("");
    }

    private void checkLogged() {
        user = Loader.getUser();

        Intent intent;
        if (user != null) {
            clearInitials();
            intent = new Intent(BeginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Loader.deleteAllTables(); // just to be sure
            if (!SavedSharedPreferences.getFirstLoad(getApplicationContext())) {
                intent = new Intent(BeginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }
}
