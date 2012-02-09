package com.artigile.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

@Singleton
public class MazeActivity extends RoboActivity implements SurfaceHolder.Callback {

    @Inject
    private SharedPreferences sharedPrefs;

    @Inject
    private MazeDotState mazeDotState;

    @InjectView(R.id.mazePreview)
    private MazeView mazeView;

    @InjectView(R.id.cameraRecordPreview)
    private SurfaceView cameraSurfaceView;

    @Inject
    private RecorderService recorderService;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        restoreAppProperties();
        mazeView.setMazeDotState(mazeDotState);

        cameraSurfaceView.setZOrderOnTop(false);
        SurfaceHolder mSurfaceHolder = cameraSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mazeDotState.setSurfaceHolder(mSurfaceHolder);



        showNameEnterDialog();
    }

    private void showNameEnterDialog() {
        if (mazeDotState.getUserName() == null || mazeDotState.getUserName().isEmpty()) {
            final EditText input = new EditText(this);

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.introduce_yourself_title))
                    .setMessage(getText(R.string.enter_your_name))
                    .setView(input)
                    .setPositiveButton(getText(R.string.start_game_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mazeDotState.setUserName(input.getText().toString());
                        }
                    })
                    .create().show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, RecorderService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        stopService(new Intent(this, RecorderService.class));
        switch (item.getItemId()) {

            case R.id.adjustSensor:
                Intent intent = new Intent(this, ApplicationConfiguration.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                return true;
            /*     case R.id.about:
           System.out.println("About!!!");
           return true;*/
            default:
                /*  // Don't toast text when a submenu is clicked
                if (!item.hasSubMenu()) {
                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }*/
                break;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mazeDotState.setGameStarted(false);
        return super.onPrepareOptionsMenu(menu);
    }

    private void restoreAppProperties() {
        mazeDotState.setScaryLevel(sharedPrefs.getInt(ApplicationConfiguration.SCARY_LEVEL_PROPERTY_NAME, mazeDotState.getScaryLevel()));
        mazeDotState.setSensivityLevel(sharedPrefs.getFloat(ApplicationConfiguration.SENSOR_SENSIVITY, mazeDotState.getSensivityLevel()));
    }

}

