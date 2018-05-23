package cz.aspone.drivers_score.driversscore.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.scottyab.aescrypt.AESCrypt;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.aspone.drivers_score.driversscore.BO.KeyValuePair;
import cz.aspone.drivers_score.driversscore.BO.Region;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class General {

    static final int REQ_CODE_CAMERA_INPUT = 1;
    static final int REQ_CODE_GALLERY_INPUT = 2;
    static final int REQ_CODE_SPEECH_INPUT = 3;
    static final int REQ_CODE_MANUAL_INPUT = 5;
    static final int TIME_MINUTE = 60000;
    static final int TIME_HOUR = 3600000;
    private static final String PASSWORD = "myAwessomePasswo3rd";

    static Region getRegionFromString(String sPlate) {
        return Region.fromString(sPlate.substring(1, 2));
    }

    public static String getRegionString(String sPlate) {
        return sPlate.length() == 7 ? getRegionFromString(sPlate).getKey() : "Region";
    }

    static KeyValuePair getValueWithConfidence(JSONArray values, String sType) throws JSONException {
        KeyValuePair result;
        JSONObject jsonObj;
        switch (sType) {
            case "Plate":
                ArrayList<KeyValuePair> plates = new ArrayList<>();
                ArrayList<KeyValuePair> specialPlates = new ArrayList<>();
                String sPlate;
                for (int i = 0; i < values.length(); i++) {
                    jsonObj = values.getJSONObject(i);
                    sPlate = jsonObj.getString("plate");
                    if (sPlate.length() == 7 && sPlate.matches("^\\d[A-Za-z]\\w\\d\\d\\d\\d$"))
                        plates.add(new KeyValuePair(sPlate, jsonObj.getDouble("confidence")));
                    if (sPlate.length() > 7)
                        specialPlates.add(new KeyValuePair(sPlate, jsonObj.getDouble("confidence")));
                }
                if (plates.size() != 0 || specialPlates.size() != 0) {
                    if (plates.size() >= specialPlates.size()) {
                        result = plates.get(0);
                        for (KeyValuePair kvp : plates) {
                            if (kvp.getDoubleValue() > result.getDoubleValue())
                                result = kvp;
                        }
                    } else {
                        result = specialPlates.get(0);
                        for (KeyValuePair kvp : specialPlates) {
                            if (kvp.getDoubleValue() > result.getDoubleValue())
                                result = kvp;
                        }
                    }
                    return result;
                } else {
                    jsonObj = values.getJSONObject(0);
                    return new KeyValuePair(jsonObj.getString("plate"), jsonObj.getDouble("confidence"));
                }
            case "Color":
            default:
                result = new KeyValuePair(values.getJSONObject(0).getString("name"), values.getJSONObject(0).getDouble("confidence"));
                for (int i = 1; i < values.length(); i++) {
                    jsonObj = values.getJSONObject(i);
                    if (jsonObj.getDouble("confidence") > result.getDoubleValue())
                        result = new KeyValuePair(jsonObj.getString("name"), jsonObj.getDouble("confidence"));
                }
                return result;
        }
    }

    static int getImageResource(String sValue) {

        switch (sValue) {
            case "suv-standard":
                return R.drawable.suv_standard;
            case "truck-standard":
                return R.drawable.truck_standard;
            case "sedan-sport":
                return R.drawable.sedan_sport;
            case "sedan-compact":
                return R.drawable.sedan_compact;
            case "antique":
                return R.drawable.antique;
            case "suv-crossover":
                return R.drawable.suv_crossover;
            case "sedan-convertible":
                return R.drawable.sedan_convertible;
            case "van-mini":
                return R.drawable.van_mini;
            case "van-full":
                return R.drawable.van_full;
            case "sedan-wagon":
                return R.drawable.sedan_wagon;
            case "sedan-standard":
            default:
                return R.drawable.sedan_standard;
        }
    }

    static Map<String, Integer> getScoreResources(double nValue) {
        Map<String, Integer> result = new HashMap<>();
        if (nValue >= 1 && nValue < 1.8) {
            result.put("image", R.drawable.ic_face_very_satisfied);
            int value = (int) (((nValue - 1) / 0.8) * 100);
            result.put("color", getColorOfDegradate(0x00ff00, 0x018D36, value));
            //result.put("color", (Integer) new ArgbEvaluator().evaluate((float) ((nValue - 1) / 0.8), 0x00ff00, 0x018D36));
        } else if (nValue >= 1.8 && nValue < 2.6) {
            result.put("image", R.drawable.ic_face_satisfied);
            int value = (int) (((nValue - 1.8) / 0.8) * 100);
            result.put("color", getColorOfDegradate(0x018D36, 0x95C221, value));
            //result.put("color", (Integer) new ArgbEvaluator().evaluate((float) ((nValue - 1.8) / 0.8), 0x018D36, 0x95C221));
        } else if (nValue >= 2.6 && nValue < 3.4) {
            result.put("image", R.drawable.ic_face_neutral);
            int value = (int) (((nValue - 2.6) / 0.8) * 100);
            result.put("color", getColorOfDegradate(0x95C221, 0xFEED02, value));
            //result.put("color", (Integer) new ArgbEvaluator().evaluate((float) ((nValue - 2.6) / 0.8), 0x95C221, 0xFEED02));
        } else if (nValue >= 3.4 && nValue < 4.2) {
            result.put("image", R.drawable.ic_face_sad);
            int value = (int) (((nValue - 3.4) / 0.8) * 100);
            result.put("color", getColorOfDegradate(0xFEED02, 0xEA5921, value));
            //result.put("color", (Integer) new ArgbEvaluator().evaluate((float) ((nValue - 3.4) / 0.8), 0xFEED02, 0xEA5921));
        } else if (nValue >= 4.2 && nValue <= 5) {
            result.put("image", R.drawable.ic_face_very_sad);
            int value = (int) (((nValue - 4.2) / 0.8) * 100);
            result.put("color", getColorOfDegradate(0xEA5921, 0xE40613, value));
            //result.put("color", (Integer) new ArgbEvaluator().evaluate((float) ((nValue - 4.2) / 0.8), 0xEA5921, 0xE40613));
        }
        return result;
    }

    static int parseColor(String sColor) {
        if (sColor.contains("ignore"))
            return Color.BLACK;
        else if (sColor.contains("gold")) {
            return R.color.gold;
        } else {
            return Color.parseColor(sColor.split("-")[0]);
        }
    }

    private static int getColorOfDegradate(int colorStart, int colorEnd, int percent) {
        return Color.rgb(
                getColorOfDegradateCalculation(Color.red(colorStart), Color.red(colorEnd), percent),
                getColorOfDegradateCalculation(Color.green(colorStart), Color.green(colorEnd), percent),
                getColorOfDegradateCalculation(Color.blue(colorStart), Color.blue(colorEnd), percent)
        );
    }

    private static int getColorOfDegradateCalculation(int colorStart, int colorEnd, int percent) {
        return ((Math.min(colorStart, colorEnd) * (100 - percent)) + (Math.max(colorStart, colorEnd) * percent)) / 100;
    }

    static Date getDateFromString(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("en", "USA"));
        return sdf.parse(sDate.replace('T', ' '));
    }

    private static double compareStrings(String stringA, String stringB) {
        return new JaroWinklerDistance().apply(stringA, stringB);
    }

    static int parseIntegerFromArray(ArrayList<String> strings, Map<String, ?> settings) {
        int tmpResult;
        KeyValuePair kvp = new KeyValuePair(0, 0.0);
        double nFirst, nSecond, nThird, nFourth, nFifth;
        for (String value : strings) {
            if (Boolean.valueOf(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMANDS_ENABLED).toString())) {
                nFirst = compareStrings(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_1).toString(), value);
                nSecond = compareStrings(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_2).toString(), value);
                nThird = compareStrings(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_3).toString(), value);
                nFourth = compareStrings(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_4).toString(), value);
                nFifth = compareStrings(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_5).toString(), value);

                if (nFirst > kvp.getDoubleValue())
                    kvp = new KeyValuePair(1, nFirst);
                if (nSecond > kvp.getDoubleValue())
                    kvp = new KeyValuePair(2, nSecond);
                if (nThird > kvp.getDoubleValue())
                    kvp = new KeyValuePair(3, nThird);
                if (nFourth > kvp.getDoubleValue())
                    kvp = new KeyValuePair(4, nFourth);
                if (nFifth > kvp.getDoubleValue())
                    kvp = new KeyValuePair(5, nFifth);
            } else {
                tmpResult = tryParseInt(value);
                if (tmpResult >= 1 && tmpResult <= 5)
                    kvp = new KeyValuePair("Result", tmpResult);
            }
        }
        return kvp.getValue();
    }

    private static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return encodeImage(b);
    }

    public static String encodeImage(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static String translateSpeechOutput(double nScore, Context context) {
        Map<String, ?> settings = SavedSharedPreferences.getSettings(context);

        String result = nScore % 1 > 0 ? String.valueOf(nScore) : String.valueOf((int) nScore);
        if (Boolean.valueOf(settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMANDS_ENABLED).toString())) {
            if (nScore >= 1 && nScore < 1.8) {
                result = settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_1).toString();
            } else if (nScore >= 1.8 && nScore < 2.6) {
                result = settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_2).toString();
            } else if (nScore >= 2.6 && nScore < 3.4) {
                result = settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_3).toString();
            } else if (nScore >= 3.4 && nScore < 4.2) {
                result = settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_4).toString();
            } else if (nScore >= 4.2 && nScore <= 5) {
                result = settings.get(SavedSharedPreferences.KEY_PREF_SPECIAL_COMMAND_5).toString();
            }
        }
        return result;
    }

    public static String formatSpeechOutput(String sMyScorePrompt, double nMyScore, String sTotalScorePrompt, double nTotalScore, Context context) {

        String sMyScore = "", sTotalScore = "";

        if (nMyScore != 0) {
            sMyScore = String.format("%1s %2$s", sMyScorePrompt, translateSpeechOutput(nMyScore, context));
        }
        if (nTotalScore != 0) {
            sTotalScore = String.format("%1s %2$s", sTotalScorePrompt, translateSpeechOutput(nTotalScore, context));
        }

        return sMyScore + (nTotalScore != 0 && nMyScore != 0 ? " || a " : "") + sTotalScore;
    }

    public static String encryptPassword(String sPassword) {
        String encryptedPassword = "";

        try {
            encryptedPassword = AESCrypt.encrypt(PASSWORD, sPassword);
        } catch (GeneralSecurityException e) {
            //handle error
        }
        return encryptedPassword;
    }

    public static String decryptPassword(String sPassword) {
        String decryptedPassword = "";
        try {
            decryptedPassword = AESCrypt.decrypt(PASSWORD, sPassword);
        } catch (GeneralSecurityException e) {
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }
        return decryptedPassword;
    }
}
