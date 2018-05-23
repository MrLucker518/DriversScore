package cz.aspone.drivers_score.driversscore.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import java.util.Map;

import cz.aspone.drivers_score.driversscore.R;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public class SavedSharedPreferences {
    private static final String PREF_USER_ID = "userID";
    private static final String PREF_FIRST_LOAD = "firstLoad";
    static final String KEY_PREF_PLATE_COMPARE = "plate_compare";
    static final String KEY_PREF_SYNC_MODE = "sync_mode";
    static final String KEY_PREF_SPECIAL_COMMANDS_ENABLED = "special_commands_enabled";
    static final String KEY_PREF_SPECIAL_COMMAND_1 = "special_command_1";
    static final String KEY_PREF_SPECIAL_COMMAND_2 = "special_command_2";
    static final String KEY_PREF_SPECIAL_COMMAND_3 = "special_command_3";
    static final String KEY_PREF_SPECIAL_COMMAND_4 = "special_command_4";
    static final String KEY_PREF_SPECIAL_COMMAND_5 = "special_command_5";
    static final String KEY_PREF_CAM_INTERVAL = "cam_interval";

    public static void deletePreference(String sName, Context ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(sName, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    static void setUserID(Context ctx, int nUserID) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
        editor.putInt(PREF_USER_ID, nUserID);
        editor.apply();
    }

    public static int getUserID(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        return sp.getInt(PREF_USER_ID, -1);
    }

    public static void setFirstLoad(Context ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("Application", Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREF_FIRST_LOAD, false);
        editor.apply();
    }

    public static boolean getFirstLoad(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("Application", Context.MODE_PRIVATE);
        return sp.getBoolean(PREF_FIRST_LOAD, true);
    }

    public static Map<String, ?> getSettings(Context ctx) {
        PreferenceManager.setDefaultValues(ctx, R.xml.settings, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        return settings.getAll();
    }
}