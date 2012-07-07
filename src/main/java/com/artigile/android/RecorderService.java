package com.artigile.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import roboguice.service.RoboService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: ioanbsu
 * Date: 6/26/11
 * Time: 1:16 PM
 */
@Singleton
public class RecorderService extends RoboService {
    private static final String TAG = "RecorderService";
    private Camera camera;


    private boolean isRecording;
    private MediaRecorder mMediaRecorder;

    @Inject
    private MazeDotState mazeDotState;

    @Override
    public void onCreate() {
        super.onCreate();
        isRecording = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!isRecording)
            if (checkCameraHardware()) {
                startRecording();
            }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (checkCameraHardware()) {
            stopRecording();
            releaseMediaRecorder();
            releaseCamera();
        }
        isRecording = false;
        super.onDestroy();
    }

    private void startRecording() {
        if (!isRecording) {
            if (prepareVideoRecorder()) {
                try {
                    mMediaRecorder.start();
                    isRecording = true;
                } catch (RuntimeException e) {
                    isRecording = false;
                    releaseMediaRecorder();
                }
            } else {
                releaseMediaRecorder();
            }
        }
    }


    private boolean prepareVideoRecorder() {
        configureCamera();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

        // Step 4: Set output file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputFile = getOutputMediaFile();
        if (outputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(outputFile.toString() + "/" + mazeDotState.getUserName().replace(" ", "_") + timeStamp + ".mpeg");

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mazeDotState.getSurfaceHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void stopRecording() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder

            isRecording = false;
        }
    }

    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "MagicMaze");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MagicMaze", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator);

        return mediaFile;
    }

    private void configureCamera() {
        int mCameraId = -1;

        try {//if API >=9 then we try to get info from CameraInfo)
            Camera.CameraInfo info = new Camera.CameraInfo();
            if (Camera.getNumberOfCameras() > 0) {
                mCameraId = 0;
            }
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraId = i;
                    break;
                }
            }
        } catch (Throwable e) {
            Log.i("CameraInfo", "The default camera will be used");
        }
        Camera c = null;
        try {
            if (mCameraId != -1) {
                c = Camera.open(mCameraId); // attempt to get a Camera instance
            } else {
                c = Camera.open();
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        camera=c;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }
}