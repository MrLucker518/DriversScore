package cz.aspone.drivers_score.driversscore.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.cameraview.CameraView;

import cz.aspone.drivers_score.driversscore.Helpers.AsyncLoader;
import cz.aspone.drivers_score.driversscore.Helpers.DriversScore;
import cz.aspone.drivers_score.driversscore.Helpers.General;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by ondrej.vondra on 03.02.2018.
 */
public class CameraFragment extends Fragment {
    private View view;
    private CameraView mCameraView;
    private Handler mBackgroundHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_camera, container, false);
        prepareCamera();
        return view;
    }

    private void prepareCamera() {
        mCameraView = (CameraView) view.findViewById(R.id.camera);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
    }

    public void takePhoto() {
        if (mCameraView != null)
            mCameraView.takePicture();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            mBackgroundHandler.getLooper().quitSafely();
            mBackgroundHandler = null;
        }
    }

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            String image64 = General.encodeImage(data);
            AsyncLoader loader = new AsyncLoader(getActivity(), ((DriversScore) getActivity().getApplication()).getUser());
            loader.getPlateInfo(image64);
        }
    };
}
