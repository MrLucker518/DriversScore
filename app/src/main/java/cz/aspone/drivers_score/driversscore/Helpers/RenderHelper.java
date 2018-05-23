package cz.aspone.drivers_score.driversscore.Helpers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import cz.aspone.drivers_score.driversscore.Activities.PlateAddActivity;
import cz.aspone.drivers_score.driversscore.BO.KeyValuePair;
import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.Score;
import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.DB.Loader;
import cz.aspone.drivers_score.driversscore.Fragments.CameraFragment;
import cz.aspone.drivers_score.driversscore.R;

import static android.app.Activity.RESULT_OK;


/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class RenderHelper {
    private Activity activity;
    private Map<String, ?> settings;
    private TextToSpeech tts;
    private Handler handler;
    private Runnable runnable;

    public RenderHelper(Activity activity) {
        this.activity = activity;
    }

    public RenderHelper(Activity activity, Map<String, ?> settings) {
        this.activity = activity;
        this.settings = settings;
    }

    public void setBodyTypeSpinner(Spinner spBodyType) {

        ArrayList<KeyValuePair> bodyTypes = new ArrayList<>();

        bodyTypes.add(new KeyValuePair("bodyType", activity.getResources().getString(R.string.chose_body_type), -1));
        bodyTypes.add(new KeyValuePair("bodyType", "suv-standard", 1));
        bodyTypes.add(new KeyValuePair("bodyType", "truck-standard", 2));
        bodyTypes.add(new KeyValuePair("bodyType", "sedan-sport", 3));
        bodyTypes.add(new KeyValuePair("bodyType", "sedan-standard", 4));
        bodyTypes.add(new KeyValuePair("bodyType", "sedan-compact", 5));
        bodyTypes.add(new KeyValuePair("bodyType", "antique", 6));
        bodyTypes.add(new KeyValuePair("bodyType", "suv-crossover", 7));
        bodyTypes.add(new KeyValuePair("bodyType", "sedan-convertible", 8));
        bodyTypes.add(new KeyValuePair("bodyType", "van-mini", 9));
        bodyTypes.add(new KeyValuePair("bodyType", "van-full", 10));
        bodyTypes.add(new KeyValuePair("bodyType", "sedan-wagon", 11));

        ArrayAdapter<KeyValuePair> bodyTypeArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, bodyTypes);

        spBodyType.setAdapter(bodyTypeArrayAdapter);
    }

    public void setColorSpinner(Spinner spColor) {

        ArrayList<KeyValuePair> colors = new ArrayList<>();

        colors.add(new KeyValuePair("Color", activity.getResources().getString(R.string.chose_color), -1));
        colors.add(new KeyValuePair("Color", "black", 1));
        colors.add(new KeyValuePair("Color", "blue", 2));
        colors.add(new KeyValuePair("Color", "gray", 3));
        colors.add(new KeyValuePair("Color", "white", 4));
        colors.add(new KeyValuePair("Color", "red", 5));
        colors.add(new KeyValuePair("Color", "green", 6));
        colors.add(new KeyValuePair("Color", "silver", 7));
        colors.add(new KeyValuePair("Color", "gold", 8));
        colors.add(new KeyValuePair("Color", "yellow", 9));

        ArrayAdapter<KeyValuePair> teaTypeArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, colors);

        spColor.setAdapter(teaTypeArrayAdapter);
    }

    public int getIndex(Spinner spinner, String sValue) {
        int index = 0;
        String sActualValue;
        for (int i = 0; i < spinner.getCount(); i++) {

            sActualValue = (String) spinner.getItemAtPosition(i);

            if (sValue.equals(sActualValue)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void updateCarImage(ImageView ivCar, KeyValuePair values, String sType) {
        if (values.getValue() != -1) {
            if (sType.equals("Color"))
                ivCar.setColorFilter(General.parseColor(values.getStringValue()));
            if (sType.equals("Image"))
                ivCar.setImageResource(General.getImageResource(values.getStringValue()));
        }
    }

    public void selectImageOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Add Photo!");

        builder.setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
            Intent intent;

            @Override
            public void onClick(DialogInterface dialog, int item) {
                Resources res = activity.getResources();
                switch (res.getStringArray(R.array.photo_options)[item]) {
                    case "Take Photo":
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivityForResult(intent, General.REQ_CODE_CAMERA_INPUT);
                        }
                        break;
                    case "Choose from Gallery":
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(intent, General.REQ_CODE_GALLERY_INPUT);
                        break;
                    case "Manual enter":
                        intent = new Intent(activity, PlateAddActivity.class);
                        activity.startActivityForResult(intent, General.REQ_CODE_MANUAL_INPUT);
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    private String getImage(int requestCode, int resultCode, Intent data) {
        String encodedImage = "";
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case General.REQ_CODE_CAMERA_INPUT: // Camera
                    Bundle extras = data.getExtras();
                    encodedImage = General.encodeImage((Bitmap) extras.get("data"));
                    break;
                case General.REQ_CODE_GALLERY_INPUT: // Gallery
                    String[] filePath = {MediaStore.Images.Media.DATA};

                    Cursor c = activity.getContentResolver().query(data.getData(), filePath, null, null, null);

                    if (c != null) {
                        c.moveToFirst();

                        int columnIndex = c.getColumnIndex(filePath[0]);
                        String picturePath = c.getString(columnIndex);

                        c.close();

                        encodedImage = General.encodeImage(BitmapFactory.decodeFile(picturePath));
                    }
                    break;
            }
        }
        return encodedImage;
    }

    private Score getScoreFromSpeech(int requestCode, int resultCode, Intent data) {
        Score score = new Score(-1);
        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case General.REQ_CODE_SPEECH_INPUT:
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    score.setValue(General.parseIntegerFromArray(result, settings));
                    break;
            }
        }
        return score;
    }

    public void getSpeechFromText(final String sText) {
        tts = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
                        ConvertTextToSpeech(sText);
                }
            }
        });
    }

    private void ConvertTextToSpeech(String sText) {
        String[] splitSpeech = sText.split("\\|\\|");

        for (int i = 0; i < splitSpeech.length; i++) {

            if (i == 0) { // Use for the first splited text to flush on audio stream

                tts.speak(splitSpeech[i].trim(), TextToSpeech.QUEUE_FLUSH, null, "id1");

            } else { // add the new test on previous then play the TTS

                tts.speak(splitSpeech[i].trim(), TextToSpeech.QUEUE_ADD, null, "id1");
            }

            tts.playSilentUtterance(100, TextToSpeech.QUEUE_ADD, null);
        }
    }

    /**
     * Showing google speech input dialog
     */
    public void promptSpeechInput(Plate plate) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, activity.getString(R.string.speech_prompt));
        DriversScore ds = ((DriversScore) activity.getApplication());
        ds.setUpdatedPlate(plate);
        try {
            activity.startActivityForResult(intent, General.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity,
                    activity.getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void getPlateFromResult(int requestCode, int resultCode, Intent data, User user) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case General.REQ_CODE_CAMERA_INPUT:
                case General.REQ_CODE_GALLERY_INPUT:
                    String image = getImage(requestCode, resultCode, data);
                    AsyncLoader loader = new AsyncLoader(activity, user);
                    loader.getPlateInfo(image);
                    break;
                case General.REQ_CODE_SPEECH_INPUT:
                    DriversScore ds = ((DriversScore) activity.getApplication());
                    setScoreForPlate(ds.getUpdatedPlate(), requestCode, resultCode, data);
                    break;
                case General.REQ_CODE_MANUAL_INPUT:
                    break;
            }
        }

    }

    public void setScoreForPlate(Plate plate, int requestCode, int resultCode, Intent data) {
        if (plate != null) {
            Score score = getScoreFromSpeech(requestCode, resultCode, data);
            score.setPlateObject(plate);
            score.setPlateNum(plate.getPlateNumber());
            score.setUserObject(plate.getUserObject());
            score.setUserID(plate.getUserID());
            score.setDateChanged(new Date());
            score.save();
        }
    }

    public void startRecording(final CameraFragment cameraFragment) {

        final int nInterval = (int) (Double.valueOf(settings.get(SavedSharedPreferences.KEY_PREF_CAM_INTERVAL).toString()) * General.TIME_MINUTE);
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                activity.runOnUiThread(new Runnable() {
//                    public void run() {
//                       DriversScore ds = ((DriversScore) activity.getApplication());
//                        if (!ds.isPlateIsProcessing()) {
//                            cameraFragment.takePhoto();
//                            ds.setPlateIsProcessing(true);
//                        }
//                    }
//                });
//            }
//        };

        runnable = new Runnable() {
            @Override
            public void run() {
                DriversScore ds = ((DriversScore) activity.getApplication());
                if (!ds.isPlateIsProcessing()) {
                    cameraFragment.takePhoto();
                    ds.setPlateIsProcessing(true);
                }
                handler.postDelayed(this, nInterval);
            }
        };
        setupTimer(nInterval);
    }

    public void stopRecording() {
        stopTimer();
    }

    public void setupTimer(int nInterval) {

        handler = new Handler();
        handler.postDelayed(runnable, nInterval);

        //timer = new Timer();
        //timer.schedule(timerTask, nInterval, nInterval);
    }

    public void stopTimer() {
        if (handler != null)
            handler.removeCallbacks(runnable);
        // timer.cancel();
        // timerTask.cancel();
    }

    private void setupSync(final int nInterval) {
        runnable = new Runnable() {
            @Override
            public void run() {
                DriversScore ds = ((DriversScore) activity.getApplication());
                if (!ds.isSyncInProgress()) {
                    synchronize();
                    ds.setSyncInProgress(true);
                }
                handler.postDelayed(this, nInterval);
            }
        };
    }

    public void setupSpeech(final String sText)
    {
        runnable = new Runnable() {
            @Override
            public void run() {
                getSpeechFromText(sText);
            }
        };
    }

    public void startSync() {

        switch (Integer.valueOf(settings.get(SavedSharedPreferences.KEY_PREF_SYNC_MODE).toString())) {
            case 0: // After app starts
                DriversScore ds = ((DriversScore) activity.getApplication());
                if (ds.isFirstSync()) {
                    synchronize();
                    ds.setFirstSync(false);
                    ds.setSyncInProgress(true);
                }
                break;
            case 1: // 15 minutes
                stopTimer();
                setupSync(15 * General.TIME_MINUTE);
                setupTimer(15 * General.TIME_MINUTE);
                break;
            case 2: // 30 minutes
                stopTimer();
                setupSync(30 * General.TIME_MINUTE);
                setupTimer(30 * General.TIME_MINUTE);
                break;
            case 3: // 1 hour
                stopTimer();
                setupSync(General.TIME_HOUR);
                setupTimer(General.TIME_HOUR);
                break;
            case 4: // 3 hours
                stopTimer();
                setupSync(3 * General.TIME_HOUR);
                setupTimer(3 * General.TIME_HOUR);
                break;
            case 5: // on demand
            default:
                break;
        }
    }

    public void synchronize() {
        AsyncLoader loader = new AsyncLoader(activity, Loader.getUser());
        loader.loadAllForUser();
    }

    Plate searchPlate(String sPlateNum) {
        TimingLogger timings = new TimingLogger("SpeedTest", "searchPlate");

        Plate plate = null;
        AsyncLoader loader;
        Map<String, ?> settings = SavedSharedPreferences.getSettings(activity);
        try {
            switch (Integer.valueOf(settings.get(SavedSharedPreferences.KEY_PREF_PLATE_COMPARE).toString())) {
                case 0:
                    plate = Loader.loadPlateByNumber(sPlateNum);
                    timings.addSplit("Local Mode");
                    break;
                case 1:
                    loader = new AsyncLoader(activity, Loader.getUser());
                    loader.getPlate(sPlateNum);
                    timings.addSplit("Server Mode");
                    break;
                case 2:
                    plate = Loader.loadPlateByNumber(sPlateNum);

                    if (plate == null) {
                        loader = new AsyncLoader(activity, Loader.getUser());
                        loader.getPlate(sPlateNum);
                    }
                    timings.addSplit("Combined Mode");
                    break;
            }
        } catch (Exception e) {
            Log.d("AsyncGetPlateNumber", e.getMessage());
        }
        timings.dumpToLog();
        return plate;
    }

    public void createMessageBox(String sTitle, String sMessage) {
        AlertDialog.Builder messageBox = new AlertDialog.Builder(activity);
        messageBox.setTitle(sTitle);
        messageBox.setMessage(sMessage);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }
}