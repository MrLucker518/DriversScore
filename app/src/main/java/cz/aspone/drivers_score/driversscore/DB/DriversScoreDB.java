package cz.aspone.drivers_score.driversscore.DB;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
@Database(name = DriversScoreDB.NAME, version = DriversScoreDB.VERSION)
public class DriversScoreDB {
    static final String NAME = "DriversScoreDB";

    static final int VERSION = 1;
}