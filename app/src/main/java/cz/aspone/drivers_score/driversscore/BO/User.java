package cz.aspone.drivers_score.driversscore.BO;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import cz.aspone.drivers_score.driversscore.DB.DriversScoreDB;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
@Table(database = DriversScoreDB.class)
public class User extends BaseModel {

    @Column
    private int UserID;

    @Column
    @PrimaryKey(autoincrement = true)
    private int Id;

    @Column
    @Unique
    private String Login;

    @Column
    private String Password;

    @Column
    private String Name;

    @Column
    private String Surname;

    @Column
    private UserType Type;

    @Column
    private Date DateChanged;

    public User(String sLogin, String sPassword) {
        this.Login = sLogin;
        this.Password = sPassword;
    }

    public User() {
    }

    public User(int userID) {
        UserID = userID;
    }

    public User(int userID, String login, String password, String name, String surname, UserType type, Date dateChanged) {
        UserID = userID;
        Login = login;
        Password = password;
        Name = name;
        Surname = surname;
        Type = type;
        DateChanged = dateChanged;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public UserType getType() {
        return Type;
    }

    public void setType(UserType type) {
        Type = type;
    }

    public Date getDateChanged() {
        return DateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        DateChanged = dateChanged;
    }
}
