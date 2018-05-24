package cz.aspone.drivers_score.driversscore.Helpers;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.BO.User;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public class DriversScore extends Application {

    private User user;
    private boolean PlateIsProcessing;
    private boolean SyncInProgress;
    private boolean FirstSync;
    private Plate updatedPlate;
    private String lastSawPlateNum;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPlateIsProcessing() {
        return PlateIsProcessing;
    }

    public void setPlateIsProcessing(boolean plateIsProcessing) {
        this.PlateIsProcessing = plateIsProcessing;
    }

    public boolean isSyncInProgress() {
        return SyncInProgress;
    }

    public void setSyncInProgress(boolean syncInProgress) {
        SyncInProgress = syncInProgress;
    }

    public boolean isFirstSync() {
        return FirstSync;
    }

    public void setFirstSync(boolean firstSync) {
        FirstSync = firstSync;
    }

    public Plate getUpdatedPlate() {
        return updatedPlate;
    }

    public void setUpdatedPlate(Plate updatedPlate) {
        this.updatedPlate = updatedPlate;
    }

    public String getLastSawPlateNum() {
        return lastSawPlateNum != null ? lastSawPlateNum : "";
    }

    public void setLastSawPlateNum(String lastSawPlateNum) {
        this.lastSawPlateNum = lastSawPlateNum;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
    }
}
