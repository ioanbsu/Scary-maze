package com.artigile.android;

import android.os.Environment;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author IoaN, 7/7/12 6:09 PM
 */
@Singleton
public class FileUtils {

    @Inject
    private MazeDotState mazeDotState;

    public File getOutputMediaFile() {
        File mediaStorageDir = getMagicMazeVideosDir();
        if (mediaStorageDir == null) return null;


        // Create a media file name
        File mediaFile;
        String filePath=mediaStorageDir.getPath() + File.separator + "/" + mazeDotState.getUserName().replace(" ", "_")+ SimpleDateFormat.getDateTimeInstance().format(new Date()) + ".mpeg";
        filePath = filePath.replace(" ","_").replace(",","_").replace(":","_");
        mediaFile = new File(filePath);

        return mediaFile;
    }

    public File getMagicMazeVideosDir() {
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
        return mediaStorageDir;
    }

}
