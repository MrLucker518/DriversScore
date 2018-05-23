package cz.aspone.drivers_score.driversscore.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.aspone.drivers_score.driversscore.Activities.BeginActivity;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class ExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Activity activity;

    public ExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        String LINE_SEPARATOR = "\n";
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK_INT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        RenderHelper helper = new RenderHelper(activity);
        // for debugging
        helper.createMessageBox("an Error occurred in app DriversScore", errorReport.toString());
      //  helper.createMessageBox("an Error occurred in app DriversScore", "Application was returned to last stable state.");

        if (!(activity instanceof BeginActivity)) {
            Intent intent = new Intent(activity, BeginActivity.class);
            intent.putExtra("error", errorReport.toString());
            activity.startActivity(intent);
        }

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}