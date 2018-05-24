package cz.aspone.drivers_score.driversscore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import cz.aspone.drivers_score.driversscore.BO.KeyValuePair;
import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.Region;
import cz.aspone.drivers_score.driversscore.DB.Loader;
import cz.aspone.drivers_score.driversscore.Helpers.DriversScore;
import cz.aspone.drivers_score.driversscore.Helpers.ExceptionHandler;
import cz.aspone.drivers_score.driversscore.Helpers.General;
import cz.aspone.drivers_score.driversscore.Helpers.RenderHelper;
import cz.aspone.drivers_score.driversscore.Helpers.SavedSharedPreferences;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class PlateAddActivity extends AppCompatActivity {

    private Plate plate;
    private TextView tvRegion;
    private ImageView ivCar;
    private EditText etPlateNum, etMaker;
    private RenderHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_plate_add);

        helper = new RenderHelper(this, SavedSharedPreferences.getSettings(PlateAddActivity.this));

        tvRegion = (TextView) findViewById(R.id.tvRegion);
        ivCar = (ImageView) findViewById(R.id.ivCar);

        etPlateNum = (EditText) findViewById(R.id.etPlateNum);

        etPlateNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String test = General.getRegionString(s.toString());
                tvRegion.setText(test);
            }
        });

        etMaker = (EditText) findViewById(R.id.etMaker);

        Spinner spBodyType = (Spinner) findViewById(R.id.spBodyType);
        helper.setBodyTypeSpinner(spBodyType);
        spBodyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                KeyValuePair bodyType = ((KeyValuePair) ((Spinner) findViewById(R.id.spBodyType)).getSelectedItem());

                helper.updateCarImage(ivCar, bodyType, "Image");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spColor = (Spinner) findViewById(R.id.spColor);
        helper.setColorSpinner(spColor);
        spColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                KeyValuePair spColor = ((KeyValuePair) ((Spinner) findViewById(R.id.spColor)).getSelectedItem());

                helper.updateCarImage(ivCar, spColor, "Color");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreate();
            }
        });
        int nPlateID = getIntent().getIntExtra("PlateID", -1);
        if (nPlateID != -1) {
            plate = Loader.loadPlate(nPlateID);

            setTitle(plate.getPlateNumber() + " - Edit");

            etPlateNum.setText(plate.getPlateNumber());
            etPlateNum.setHint("");
            spBodyType.getSelectedItemPosition();
            spBodyType.setSelection(helper.getIndex(spBodyType, plate.getBodyType()));
            spColor.getSelectedItemPosition();
            spColor.setSelection(helper.getIndex(spColor, plate.getCarColor()));

            etMaker.setText(plate.getMaker());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void attemptCreate() {
        etPlateNum.setError(null);
        etMaker.setError(null);

        String sPlateNum = etPlateNum.getText().toString();
        String sMaker = etMaker.getText().toString();
        KeyValuePair Color = ((KeyValuePair) ((Spinner) findViewById(R.id.spColor)).getSelectedItem());
        KeyValuePair BodyType = ((KeyValuePair) ((Spinner) findViewById(R.id.spBodyType)).getSelectedItem());

        String sColor = Color.getValue() == -1 ? "" : Color.getStringValue();
        String sBodyType = BodyType.getValue() == -1 ? "" : BodyType.getStringValue();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sPlateNum)) {
            etPlateNum.setError(getString(R.string.error_field_required));
            focusView = etPlateNum;
            cancel = true;
        }

        if (sPlateNum.length() != 7 && sPlateNum.length() != 8) {
            etPlateNum.setError(getString(R.string.error_plate_number));
            focusView = etPlateNum;
            cancel = true;
        }

        if (Loader.loadPlateByNumber(sPlateNum) != null) {
            etPlateNum.setError(getString(R.string.error_AlreadyExists));
            focusView = etPlateNum;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
        } else {
            if (plate == null)
                plate = new Plate(-1);
            plate.setPlateNumber(sPlateNum);
            plate.setRegion(Region.fromString(sPlateNum.substring(1, 2)));
            plate.setMaker(sMaker);
            plate.setUserObject(((DriversScore) this.getApplication()).getUser());
            plate.setUserID(plate.getUserObject().getUserID());
            plate.setCarColor(sColor);
            plate.setBodyType(sBodyType);
            plate.setDateChanged(new Date());

            plate.save();

            helper.promptSpeechInput(plate);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.setScoreForPlate(plate, requestCode, resultCode, data);
        finish();
    }
}