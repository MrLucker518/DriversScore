package cz.aspone.drivers_score.driversscore.BO;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cz.aspone.drivers_score.driversscore.DB.DriversScoreDB;

/**
 * Created by ondrej.vondra on 18.01.2018.
 */
@Table(database = DriversScoreDB.class)
public class KeyValuePair extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private int Id;

    @Column
    private String Key;

    @Column
    private int Value;

    @Column
    private String StringValue;

    @Column
    private Double DoubleValue;

    @Column
    private boolean BoolValue;

    KeyValuePair() {
    }

    public KeyValuePair(String key, int value) {
        Key = key;
        Value = value;
    }

    public KeyValuePair(String key, String stringValue) {
        Key = key;
        StringValue = stringValue;
    }

    public KeyValuePair(String key, String stringValue, int value) {
        Key = key;
        StringValue = stringValue;
        Value = value;
    }

    public KeyValuePair(String key, Double doubleValue) {
        Key = key;
        DoubleValue = doubleValue;
    }

    public KeyValuePair(int value, Double doubleValue) {
        Value = value;
        DoubleValue = doubleValue;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public String getStringValue() {
        return StringValue;
    }

    void setStringValue(String stringValue) {
        StringValue = stringValue;
    }

    public Double getDoubleValue() {
        return DoubleValue;
    }

    void setDoubleValue(Double doubleValue) {
        DoubleValue = doubleValue;
    }

    boolean isBoolValue() {
        return BoolValue;
    }

    void setBoolValue(boolean boolValue) {
        BoolValue = boolValue;
    }

    @Override
    public String toString() {
        return StringValue;
    }
}


