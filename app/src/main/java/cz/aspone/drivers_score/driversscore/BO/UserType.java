package cz.aspone.drivers_score.driversscore.BO;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public enum UserType {

    User("User", 0),
    Admin("Admin", 1);

    private String Key;
    private int Value;

    UserType(String key, int value) {
        Key = key;
        Value = value;
    }

    @Override
    public String toString() {
        return Key;
    }

    public static UserType fromInteger(int x) {
        switch (x) {
            case 0:
                return User;
            case 1:
                return Admin;
        }
        return null;
    }

    public String getKey() {
        return Key;
    }

    public int getValue() {
        return Value;
    }
}