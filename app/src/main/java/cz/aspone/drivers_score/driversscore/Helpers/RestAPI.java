package cz.aspone.drivers_score.driversscore.Helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cz.aspone.drivers_score.driversscore.BO.KeyValuePair;
import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.Region;
import cz.aspone.drivers_score.driversscore.BO.Score;
import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.BO.UserType;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
class RestAPI {

    private static final String DATA_URL = "http://drivers-score.aspone.cz/APIHandler.ashx";
    private static final String ALPR_URL = "https://api.openalpr.com/v2/recognize_bytes";
    private static final String ALPR_SKEY = "sk_5e5c3c482b796fa04a12df72";

    private static String convertStreamToUTF8String(InputStream stream) throws IOException {
        String result = "";
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[4096];
            int readedChars = 0;
            while (readedChars != -1) {
                readedChars = reader.read(buffer);
                if (readedChars > 0)
                    sb.append(buffer, 0, readedChars);
            }
            result = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String load(String contents, String sUrl) throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(60000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream());
        w.write(contents);
        w.flush();
        if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            InputStream iStream = conn.getInputStream();
            return convertStreamToUTF8String(iStream);
        } else {
            return "error";
        }
    }

    public Plate getPlateNumber(String image64, User user) throws Exception {
        JSONObject result;
        ArrayList<KeyValuePair> params = new ArrayList<>();

        params.add(new KeyValuePair("secret_key", ALPR_SKEY));
        params.add(new KeyValuePair("country", "eu"));
        params.add(new KeyValuePair("recognize_vehicle", "1"));
        params.add(new KeyValuePair("return_image", "0"));
        //params.add(new KeyValuePair("state", "cz"));
        params.add(new KeyValuePair("topn", "100"));
        // params.add(new KeyValuePair("prewarp", ""));

        StringBuilder sb = new StringBuilder();
        KeyValuePair kvp;

        for (int i = 0; i < params.size(); i++) {
            kvp = params.get(i);
            sb.append(String.format("%1s=%2s", kvp.getKey(), kvp.getStringValue()));
            if (i < params.size() - 1)
                sb.append("&");
        }
        String r = load(image64, String.format("%1s?%2s", ALPR_URL, sb.toString().replace(" ", "")));
        result = new JSONObject(r);
        return JSONParser.parsePlate(result.getJSONArray("results").getJSONObject(0), user);
    }

    private Object mapObject(Object o) {
        Object finalValue = null;
        if (o.getClass() == String.class) {
            finalValue = o;
        } else if (Number.class.isInstance(o)) {
            finalValue = String.valueOf(o);
        } else if (Region.class.isInstance(o)) {
            finalValue = (o).toString();
        } else if (Date.class.isInstance(o)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", new Locale("en", "USA"));
            finalValue = sdf.format((Date) o);
        } else if (Collection.class.isInstance(o)) {
            Collection<?> col = (Collection<?>) o;
            JSONArray jarray = new JSONArray();
            for (Object item : col) {
                jarray.put(mapObject(item));
            }
            finalValue = jarray;
        } else {
            Map<String, Object> map = new HashMap<>();
            Method[] methods = o.getClass().getMethods();
            for (Method method : methods) {
                if(!method.getName().contains("Object")) {
                    if (method.getDeclaringClass() == o.getClass()
                            && method.getModifiers() == Modifier.PUBLIC
                            && method.getName().startsWith("get")) {
                        String key = method.getName().substring(3);
                        try {
                            Object obj = method.invoke(o, null);
                            Object value = mapObject(obj);
                            map.put(key, value);
                            finalValue = new JSONObject(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return finalValue;
    }

    User UpdateAll(int nUserID, ArrayList<Plate> plates, ArrayList<Score> scores) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface", "RestAPI");
        o.put("method", "UpdateAll");
        p.put("nUserID", mapObject(nUserID));
        p.put("plates", mapObject(plates));
        p.put("scores", mapObject(scores));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s, DATA_URL);
        result = new JSONObject(r);
        return JSONParser.parseAll(result.getJSONObject("Value"));
    }

    Plate PlateLoad(String sPlateNum) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface", "RestAPI");
        o.put("method", "PlateLoad");
        p.put("sPlateNum", mapObject(sPlateNum));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s, DATA_URL);
        result = new JSONObject(r);
        return JSONParser.parsePlate(result.getJSONObject("Value"));
    }

    User UserUpdate(int nUserID, String sLogin, String sPassword, String sName, String sSurname, UserType type) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface", "RestAPI");
        o.put("method", "UserUpdate");
        p.put("nUserID", mapObject(nUserID));
        p.put("sLogin", mapObject(sLogin));
        p.put("sPassword", mapObject(sPassword));
        p.put("sName", mapObject(sName));
        p.put("sSurname", mapObject(sSurname));
        p.put("type", mapObject(type.getValue()));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s, DATA_URL);
        result = new JSONObject(r);
        JSONObject object;
        try {
            object = result.getJSONObject("Value");
        } catch (Exception e) {
            return new User(-1);
        }
        return JSONParser.parseAll(object);
    }

    User UserAuthentication(String sLogin, String sPassword) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface", "RestAPI");
        o.put("method", "UserAuthentication");
        p.put("sLogin", mapObject(sLogin));
        p.put("sPassword", mapObject(sPassword));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s, DATA_URL);
        result = new JSONObject(r);
        JSONObject object;
        try {
            object = result.getJSONObject("Value");
        } catch (Exception e) {
            return new User(-1);
        }
        return JSONParser.parseAll(object);
    }
}


