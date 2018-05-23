package cz.aspone.drivers_score.driversscore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.Helpers.AsyncLoader;
import cz.aspone.drivers_score.driversscore.Helpers.ExceptionHandler;
import cz.aspone.drivers_score.driversscore.Helpers.General;
import cz.aspone.drivers_score.driversscore.R;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etPassword, etLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_login);
        // Set up the login form.

        etPassword = (EditText) findViewById(R.id.etPassword);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etLogin.requestFocus();

        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String sLogin = etLogin.getText().toString();
        String sPassword = General.encryptPassword(etPassword.getText().toString());

        if (!TextUtils.isEmpty(sLogin) && !TextUtils.isEmpty(sPassword)) {

            User user = new User(sLogin, sPassword);

            AsyncLoader asyncLoader = new AsyncLoader(this);

            asyncLoader.logUser(user);
        }
    }
}

