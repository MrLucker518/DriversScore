package cz.aspone.drivers_score.driversscore.BO;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import cz.aspone.drivers_score.driversscore.DB.DriversScoreDB;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
@Table(database = DriversScoreDB.class)
public class Score extends BaseModel {

    @Column
    private int ScoreID;

    @Column
    @PrimaryKey(autoincrement = true)
    private int Id;

    @Column
    private int Value;

    @Column
    private Date DateChanged;

    @Column
    private int PlateID;

    @Column
    private String PlateNum;

    @Column
    @ForeignKey()
    private Plate PlateObject;

    @Column
    private int UserID;

    @Column
    @ForeignKey()
    private User UserObject;

    Score() {
    }

    public Score(int scoreID) {
        ScoreID = scoreID;
    }

    public Score(int scoreID, int value, Date dateChanged, int plateID, Plate plate, int userID, User user) {
        ScoreID = scoreID;
        Value = value;
        DateChanged = dateChanged;
        PlateID = plateID;
        PlateObject = plate;
        PlateNum = plate.getPlateNumber();
        UserID = userID;
        UserObject = user;
    }

    public int getScoreID() {
        return ScoreID;
    }

    void setScoreID(int scoreID) {
        ScoreID = scoreID;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public Date getDateChanged() {
        return DateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        DateChanged = dateChanged;
    }

    public int getPlateID() {
        return PlateID;
    }

    void setPlateID(int plateID) {
        PlateID = plateID;
    }

    public Plate getPlateObject() {
        return PlateObject;
    }

    public void setPlateObject(Plate plate) {
        PlateObject = plate;
    }

    public int getUserID() {
        return UserID;
    }

    public String getPlateNum() {
        return PlateNum;
    }

    public void setPlateNum(String plateNum) {
        PlateNum = plateNum;
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

}
