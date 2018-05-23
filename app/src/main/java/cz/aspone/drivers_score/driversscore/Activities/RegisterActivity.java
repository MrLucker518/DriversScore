package cz.aspone.drivers_score.driversscore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.BO.UserType;
import cz.aspone.drivers_score.driversscore.Helpers.AsyncLoader;
import cz.aspone.drivers_score.driversscore.Helpers.ExceptionHandler;
import cz.aspone.drivers_score.driversscore.Helpers.General;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etLogin, etPassword, etName, etSurname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_register);

        etPassword = (EditText) findViewById(R.id.etPassword);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etLogin.requestFocus();

        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogUp();
            }
        });

        TextView tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void attemptLogUp() {
        etLogin.setError(null);
        etPassword.setError(null);

        String sLogin = etLogin.getText().toString();
        String sPassword = General.encryptPassword(etPassword.getText().toString());
        String sName = etName.getText().toString();
        String sSurname = etSurname.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(sPassword)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(sPassword)) {
            etLogin.setError(getString(R.string.error_field_required));
            focusView = etLogin;
            cancel = true;
        }
        if (TextUtils.isEmpty(sLogin)) {
            etLogin.setError(getString(R.string.error_field_required));
            focusView = etLogin;
            cancel = true;
        }
        if (TextUtils.isEmpty(sName)) {
            etName.setError(getString(R.string.error_field_required));
            focusView = etName;
            cancel = true;
        }
        if (TextUtils.isEmpty(sSurname)) {
            etSurname.setError(getString(R.string.error_field_required));
            focusView = etSurname;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            User user = new User(-1, sLogin, sPassword, sName, sSurname, UserType.User, new Date());

            AsyncLoader asyncLoader = new AsyncLoader(this);

            asyncLoader.createUser(user);
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private boolean isPasswordValid(String sPassword) {
        return sPassword.length() > 4;
    }
}
