package cz.aspone.drivers_score.driversscore.DB;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

import cz.aspone.drivers_score.driversscore.BO.KeyValuePair;
import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.Plate_Table;
import cz.aspone.drivers_score.driversscore.BO.Score;
import cz.aspone.drivers_score.driversscore.BO.Score_Table;
import cz.aspone.drivers_score.driversscore.BO.User;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
public class Loader {

    public static ArrayList<Plate> getPlates() {
        return (ArrayList<Plate>) SQLite.select().from(Plate.class).queryList();
    }

    public static Plate loadPlate(int nPlateID) {
        return SQLite.select().from(Plate.class).where(Plate_Table.PlateID.eq(nPlateID)).querySingle();
    }

    public static Plate loadPlateByNumber(String sNumber) {
        return SQLite.select().from(Plate.class).where(Plate_Table.PlateNumber.eq(sNumber)).querySingle();
    }

    public static User getUser() {
        return SQLite.select().from(User.class).querySingle();
    }

    public static ArrayList<Score> getScores() {
        return (ArrayList<Score>) SQLite.select().from(Score.class).queryList();
    }

    public static int getMyScore(String sPlateNum) {
        Score score = SQLite.select().from(Score.class).where(Score_Table.PlateNum.eq(sPlateNum)).querySingle();
        if (score != null)
            return score.getValue();
        else
            return 0;
    }

    public static void deleteScore(String sPlateNum) {

        Score score = SQLite.select().from(Score.class).where(Score_Table.PlateNum.eq(sPlateNum)).querySingle();
        if (score != null)
            score.delete();
    }

    public static void deleteAllTables() {
        Delete.tables(Plate.class, Score.class, User.class, KeyValuePair.class);
    }
}

