package cz.aspone.drivers_score.driversscore.BO;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import cz.aspone.drivers_score.driversscore.DB.DriversScoreDB;
import cz.aspone.drivers_score.driversscore.DB.Loader;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
@Table(database = DriversScoreDB.class)
public class Plate extends BaseModel {

    @Column
    private int PlateID;

    @Column
    @PrimaryKey(autoincrement = true)
    private int Id;

    @Column
    @Unique
    private String PlateNumber;

    @Column
    private String CarColor;

    @Column
    private Region Region;

    @Column
    private String BodyType;

    @Column
    private String Maker;

    @Column
    private Date DateChanged;

    @Column
    private int UserID;

    @Column
    @ForeignKey()
    private User UserObject;

    @Column
    private double ScoreAvg;

    public Plate() {
    }

    public Plate(int plateID) {
        PlateID = plateID;
    }

    public Plate(int plateID, String plateNumber, String carColor, Region region, String bodyType, String maker, Date dateChanged, int userID, User user, double scoreAvg) {
        PlateID = plateID;
        PlateNumber = plateNumber;
        CarColor = carColor;
        Region = region;
        BodyType = bodyType;
        Maker = maker;
        DateChanged = dateChanged;
        UserID = userID;
        UserObject = user;
        ScoreAvg = scoreAvg;
    }

    public int getPlateID() {
        return PlateID;
    }

    public void setPlateID(int plateID) {
        PlateID = plateID;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        PlateNumber = plateNumber;
    }

    public String getCarColor() {
        return CarColor;
    }

    public void setCarColor(String carColor) {
        CarColor = carColor;
    }

    public Region getRegion() {
        return Region;
    }

    public void setRegion(Region region) {
        Region = region;
    }

    public String getBodyType() {
        return BodyType;
    }

    public void setBodyType(String bodyType) {
        BodyType = bodyType;
    }

    public String getMaker() {
        return Maker;
    }

    public void setMaker(String maker) {
        Maker = maker;
    }

    public Date getDateChanged() {
        return DateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        DateChanged = dateChanged;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public User getUserObject() {
        return UserObject;
    }

    public void setUserObject(User user) {
        UserObject = user;
    }

    public double getScoreAvg() {
        return ScoreAvg;
    }

    void setScoreAvg(double scoreAvg) {
        ScoreAvg = scoreAvg;
    }

    public double getMyScore() {
        return (double) Loader.getMyScore(PlateNumber);
    }
}
