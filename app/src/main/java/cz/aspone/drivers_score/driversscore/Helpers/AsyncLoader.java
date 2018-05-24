package cz.aspone.drivers_score.driversscore.Helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;

import cz.aspone.drivers_score.driversscore.Activities.MainActivity;
import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.DB.Loader;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class AsyncLoader {

    private RestAPI api;
    private User user;
    private Activity activity;
    private ProgressDialog statusDialog;

    public AsyncLoader(Activity activity) {
        api = new RestAPI();
        user = new User();
        this.activity = activity;
    }

    public AsyncLoader(Activity activity, User user) {
        api = new RestAPI();
        this.user = user;
        this.activity = activity;
    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(activity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void createUser(User user) {
        if (isOnline()) {
            new AsyncCreateUser().execute(user);
        } else {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), "No Internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            //Toast.makeText(activity, "No Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void logUser(User user) {
        if (isOnline()) {
            new AsyncLogUser().execute(user);
        } else {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), "No Internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            // Toast.makeText(activity, "No Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    void loadAllForUser() {
        if (isOnline()) {
            new AsyncLoadAll().execute(user);
        } else {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), "No Internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            //Toast.makeText(activity, "No Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPlateInfo(String sImage64) {
        if (isOnline()) {
            new AsyncGetPlateInfo().execute(sImage64);
        } else {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), "No Internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            //Toast.makeText(activity, "No Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPlate(String sPlateNum) {
        if (isOnline()) {
            new AsyncGetPlate().execute(sPlateNum);
        } else {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), "No Internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            //Toast.makeText(activity, "No Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private class AsyncCreateUser extends
            AsyncTask<User, JSONObject, User> {

        @Override
        protected User doInBackground(User... params) {

            try {

                user = api.UserUpdate(-1, params[0].getLogin(), params[0].getPassword(), params[0].getName(), params[0].getSurname(), params[0].getType());

            } catch (Exception e) {
                Log.d("AsyncCreateUser", e.getMessage());
            }
            return user;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            Toast.makeText(activity, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(User result) {

            if (user.getUserID() != -1 && user.getUserID() != 0) {

                ((DriversScore) activity.getApplication()).setUser(user);

                SavedSharedPreferences.setUserID(activity, user.getUserID());

                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
            } else {
                Toast.makeText(activity, "Not valid login/password ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AsyncLogUser extends
            AsyncTask<User, JSONObject, User> {

        @Override
        protected User doInBackground(User... params) {

            try {
                user = api.UserAuthentication(params[0].getLogin(), params[0].getPassword());
                user.save();

            } catch (Exception e) {
                Log.d("AsyncLogUser", e.getMessage());

            }
            return user;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            Toast.makeText(activity, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(User result) {

            if (user.getUserID() != -1 && user.getUserID() != 0) {

                ((DriversScore) activity.getApplication()).setUser(user);

                SavedSharedPreferences.setUserID(activity, user.getUserID());

                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
            } else {
                Toast.makeText(activity, "Not valid login/password ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AsyncLoadAll extends
            AsyncTask<User, JSONObject, User> {

        @Override
        protected User doInBackground(User... params) {

            try {
                user = api.UpdateAll(user.getUserID(), Loader.getPlates(), Loader.getScores());
            } catch (Exception e) {
                Log.d("AsyncLoadAll", e.getMessage());
            }
            return user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(activity, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(User result) {
            if (result != null) {
                Toast.makeText(activity, "Successfully synchronized.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Synchronization failed", Toast.LENGTH_SHORT).show();
            }
            ((DriversScore) activity.getApplication()).setSyncInProgress(false);
        }
    }

    private class AsyncGetPlateInfo extends
            AsyncTask<String, JSONObject, Plate> {

        @Override
        protected Plate doInBackground(String... params) {
            Plate result = null;
            try {
                result = api.getPlateNumber(params[0], user);
            } catch (Exception e) {
                Log.d("AsyncGetPlateNumber", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Toast.makeText(activity, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Plate result) {
            DriversScore ds = ((DriversScore) activity.getApplication());

            if (result != null) {
                if (!ds.getLastSawPlateNum().equals(result.getPlateNumber())) {
                    ds.setLastSawPlateNum(result.getPlateNumber());

                    RenderHelper helper = new RenderHelper(activity);
                    //long startTime = System.nanoTime();
                    Plate tmpResult = helper.searchPlate(result.getPlateNumber());
                    //long endTime = System.nanoTime();

                    //long MethodDuration = (endTime - startTime);

                    //Log.d("StartTime", String.valueOf(startTime / 1000000));
                    //Log.d("EndTime", String.valueOf(endTime / 1000000));
                    //Log.d("DurationTime", String.valueOf(MethodDuration / 1000000));

                    if (tmpResult != null) {
                        int nMyScore = Loader.getMyScore(tmpResult.getPlateNumber());
                        if (nMyScore == 0) {
                            helper.promptSpeechInput(result);
                        }
                        helper.stopTimer();
                        helper.setupSpeech(General.formatSpeechOutput("Moje hodnocení je:", (double) nMyScore, "Celkové hodnocení je:", tmpResult.getScoreAvg(), activity));
                        helper.setupTimer(5000);
                    } else {
                        Map<String, ?> settings = SavedSharedPreferences.getSettings(activity);
                        if (Integer.valueOf(settings.get(SavedSharedPreferences.KEY_PREF_PLATE_COMPARE).toString()) != 1) {
                            result.save();
                            helper.promptSpeechInput(result);
                        }
                    }
                }
            }
            ds.setPlateIsProcessing(false);
        }
    }

    private class AsyncGetPlate extends
            AsyncTask<String, JSONObject, Plate> {

        @Override
        protected Plate doInBackground(String... params) {
            Plate result = null;
            try {
                result = api.PlateLoad(params[0]);
            } catch (Exception e) {
                Log.d("PlateLoad", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Toast.makeText(activity, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Plate result) {
            DriversScore ds = ((DriversScore) activity.getApplication());

            if (result != null) {
                if (!ds.getLastSawPlateNum().equals(result.getPlateNumber())) {
                    ds.setLastSawPlateNum(result.getPlateNumber());

                    RenderHelper helper = new RenderHelper(activity);
                    Plate tmpResult = Loader.loadPlateByNumber(result.getPlateNumber());
                    if (tmpResult != null) {
                        int nMyScore = Loader.getMyScore(tmpResult.getPlateNumber());
                        if (nMyScore == 0) {
                            helper.promptSpeechInput(result);
                        }

                        helper.stopTimer();
                        helper.setupSpeech(General.formatSpeechOutput("Moje hodnocení je:", (double) nMyScore, "Celkové hodnocení je:", tmpResult.getScoreAvg(), activity));
                        helper.setupTimer(5000);
                    } else {
                        result.save();
                        helper.promptSpeechInput(result);
                    }
                }
            }
            ds.setPlateIsProcessing(false);

            //long endTime = System.nanoTime();

            //Log.d("EndTimeAsync", String.valueOf(endTime / 1000000));
        }
    }
}