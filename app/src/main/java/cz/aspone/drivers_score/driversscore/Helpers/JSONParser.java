package cz.aspone.drivers_score.driversscore.Helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.Region;
import cz.aspone.drivers_score.driversscore.BO.Score;
import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.BO.UserType;
import cz.aspone.drivers_score.driversscore.DB.Loader;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
class JSONParser {

    static User parseAll(JSONObject object) {

        User user = new User();

        try {
            JSONArray jsonArrayInner;
            JSONObject jsonObj;
            Plate plate;
            Score score;

            int userID, plateID;

            ArrayList<JSONArray> jsonArray = new ArrayList<>();
            jsonArray.add(object.getJSONArray("data"));
            jsonArray.add(object.getJSONArray("data1"));
            jsonArray.add(object.getJSONArray("data2"));

            for (int i = 0; i < jsonArray.size(); i++) {
                jsonArrayInner = jsonArray.get(i);
                for (int j = 0; j < jsonArrayInner.length(); j++) {
                    jsonObj = jsonArrayInner.getJSONObject(j);

                    switch (i) {
                        case 0: // data User
                            user.setUserID(jsonObj.getInt("User_ID"));
                            user.setLogin(jsonObj.getString("Login"));
                            user.setPassword(jsonObj.getString("Password"));
                            user.setName(jsonObj.getString("Name"));
                            user.setSurname(jsonObj.getString("Surname"));
                            user.setType(UserType.fromInteger(jsonObj.getInt("Type")));
                            user.setDateChanged(General.getDateFromString(jsonObj.getString("DateChanged")));
                            user.save();
                            break;
                        case 1: // data1 Plates

                            plate = new Plate(jsonObj.getInt("Plate_ID"), jsonObj.getString("PlateNumber")
                                    , jsonObj.getString("CarColor"), Region.fromString(jsonObj.getString("Region"))
                                    , jsonObj.getString("BodyType"), jsonObj.getString("Maker")
                                    , General.getDateFromString(jsonObj.getString("DateChanged")), jsonObj.getInt("User_ID")
                                    , Loader.getUser(), jsonObj.getDouble("Score"));

                            plate.save();
                            break;
                        case 2: // data2 Scores

                            userID = jsonObj.getInt("User_ID");
                            plateID = jsonObj.getInt("Plate_ID");
                            //select s.Score_ID, s.Value, s.DateChanged, s.Plate_ID from Scores s
                            score = new Score(jsonObj.getInt("Score_ID"), jsonObj.getInt("Value")
                                    , General.getDateFromString(jsonObj.getString("DateChanged")), plateID, Loader.loadPlate(plateID), userID, Loader.getUser());
                            score.save();
                            break;
                    }
                }
            }
        } catch (JSONException | ParseException e) {
            Log.d("JSONPr => parseUser", e.getMessage());
        }
        return user;
    }

    static ArrayList<Plate> parsePlates(JSONObject object) {
        ArrayList<Plate> plates = new ArrayList<>();
        Plate plate;
        try {
            JSONArray jsonArray = object.getJSONArray("Value");
            JSONObject jsonObj;

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                plate = new Plate(jsonObj.getInt("Plate_ID"), jsonObj.getString("PlateNumber")
                        , jsonObj.getString("CarColor"), Region.fromString(jsonObj.getString("Region"))
                        , jsonObj.getString("BodyType"), jsonObj.getString("Maker")
                        , General.getDateFromString(jsonObj.getString("DateChanged")), jsonObj.getInt("User_ID")
                        , Loader.getUser(), jsonObj.getDouble("Score"));
                plate.save();
                plates.add(plate);
            }
        } catch (JSONException | ParseException e) {
            Log.d("JSONPr => parseUser", e.getMessage());
        }
        return plates;
    }

    static Plate parsePlate(JSONObject object, User user) {
        Plate plate = new Plate();
        try {
            JSONObject objectVehicle = object.getJSONObject("vehicle");
            plate.setPlateID(-1);
            plate.setPlateNumber(General.getValueWithConfidence(object.getJSONArray("candidates"), "Plate").getKey());
            plate.setUserObject(user);
            plate.setUserID(user.getUserID());
            plate.setCarColor(General.getValueWithConfidence(objectVehicle.getJSONArray("color"), "Color").getKey());
            plate.setRegion(General.getRegionFromString(plate.getPlateNumber()));
            plate.setMaker(General.getValueWithConfidence(objectVehicle.getJSONArray("make"), "Maker").getKey());
            plate.setBodyType(General.getValueWithConfidence(objectVehicle.getJSONArray("body_type"), "BodyType").getKey());
        } catch (JSONException e) {
            Log.d("JSONPr => parsePlate", e.getMessage());
        }
        return plate;
    }

    static Plate parsePlate(JSONObject object) {
        Plate plate = new Plate();
        try {
            plate = new Plate(object.getInt("Plate_ID"), object.getString("PlateNumber")
                    , object.getString("CarColor"), Region.fromString(object.getString("Region"))
                    , object.getString("BodyType"), object.getString("Maker")
                    , General.getDateFromString(object.getString("DateChanged")), object.getInt("User_ID")
                    , Loader.getUser(), object.getDouble("Score"));
        } catch (JSONException | ParseException e) {
            Log.d("JSONPr => parsePlate", e.getMessage());
        }
        return plate;
    }
}