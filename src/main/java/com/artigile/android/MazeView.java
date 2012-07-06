package com.artigile.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextPaint;
import android.view.*;
import com.google.inject.Singleton;
import roboguice.RoboGuice;

import java.util.List;

/**
 * @author IoaN, 5/4/11 10:01 PM
 */
@Singleton
public class MazeView extends View implements SensorEventListener {
    private SensorManager mSensorManager;
    private Display mDisplay;
    private Bitmap scaledScaryImage;
    private float storedTime;
    private float mSensorX;
    private float mSensorY;
    private Dot dot = RoboGuice.getInjector(getContext()).getInstance(Dot.class);
    private Bitmap metalBall;
    private MazeDotState mazeDotState;

    private SoundPool sound;
    private int soundId;

    public MazeView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
        initHandlers();
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sound.load(getContext(), R.raw.scary, 1);
    }

    private void init() {
        Context context = getContext();
        this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        Bitmap metalBall = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.metalball);
        this.metalBall = Bitmap.createScaledBitmap(metalBall, Dot.RADIUS * 2, Dot.RADIUS * 2, false);
    }


    private void initHandlers() {
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!mazeDotState.isGameStarted()) {
                    storedTime = System.nanoTime();
                    mazeDotState.setGameStarted(true);
                    dot.resetAll();
                    startSimulation();
                    invalidate();
                } else {

                }
                return false;
            }
        });
    }


    public void startSimulation() {
        dot.resetSpeeds();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (storedTime == 0) {
            storedTime = System.nanoTime();
        }

        boolean dotInside = checkIfDotInsideMaze(mazeDotState.getMazeRectangles(), dot.getX(), dot.getY());
        drawDotAndMaze(canvas, dotInside);
        if (checkIfLevelCompleted()) {
            mSensorManager.unregisterListener(this);
            drawText(canvas, getContext().getString(R.string.level_done));
            mazeDotState.increaseLevel();
            mazeDotState.setMazeRectangles(MazeSquaresBuilder.getLabirint1(mDisplay.getHeight(), mDisplay.getWidth(), mazeDotState.getCurrentLevel()));
            mazeDotState.setGameStarted(false);
            if (mazeDotState.getCurrentLevel() == mazeDotState.getScaryLevel()) {
                Intent intent = new Intent(getContext(), RecorderService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showAlertForDing();//there is a ding when user
                getContext().startService(intent);
            }
            return;
        } else if (!dotInside) {
            mSensorManager.unregisterListener(this);
            drawText(canvas, getContext().getString(R.string.level_failed));
            mazeDotState.setGameStarted(false);
        } else if (isScaryCry()) {
            scaryMe(canvas);
        } else if (!mazeDotState.isGameStarted()) {
            drawText(canvas, getContext().getString(R.string.level_start_label));
        } else {
            invalidate();
        }
        storedTime = System.nanoTime();
    }

    private void showAlertForDing() {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.app_name))
                .setMessage(R.string.pre_scary_level_label)
                .setIcon(R.drawable.smile)
                .setPositiveButton(R.string.close_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                })
                .show();
    }

    private void drawText(Canvas canvas, String text) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextSize(45);

        int y = (mDisplay.getHeight() - 50) / 2;
        int x = (mDisplay.getWidth() - text.length() * 20) / 2;
        canvas.drawText(text, x, y, textPaint);
    }

    private void drawDotAndMaze(Canvas canvas, boolean dotInside) {
        Paint blackBackground = new Paint();
        blackBackground.setARGB(255, 20, 17, 5);
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint redDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint levelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        levelPaint.setARGB(200, 255, 255, 255);
        levelPaint.setTextSize(30);
        BitmapShader shader = new BitmapShader(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wood), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mazePaint.setShader(shader);
        dotPaint.setARGB(255, 255, 255, 255);
        redDotPaint.setARGB(255, 255, 0, 0);

        canvas.drawRect(new Rect(0, 0, mDisplay.getWidth(), mDisplay.getHeight()), blackBackground);
        for (Rect rect : mazeDotState.getMazeRectangles()) {
            canvas.drawRect(rect, mazePaint);
        }
        dot.updateCoords(mSensorX * (-1), mSensorY, System.nanoTime() - storedTime);
        if (dot.getX() > mDisplay.getWidth() - Dot.RADIUS) {
            dot.setX(mDisplay.getWidth() - Dot.RADIUS);
            dot.setSpeedX(dot.getSpeedX() * (-0.75F));
        }
        if (dot.getY() > mDisplay.getHeight() - Dot.RADIUS) {
            dot.setY(mDisplay.getHeight() - Dot.RADIUS);
            dot.setSpeedY(Math.abs(dot.getSpeedY()) * (-0.75F));
        }
        if (dot.getX() < Dot.RADIUS) {
            dot.setX(Dot.RADIUS);
            dot.setSpeedX(Math.abs(dot.getSpeedX() * 0.75F));
        }
        if (dot.getY() < Dot.RADIUS) {
            dot.setY(Dot.RADIUS);
            dot.setSpeedY(Math.abs(dot.getSpeedY() * 0.75F));
        }
        canvas.drawBitmap(metalBall, dot.getX() - Dot.RADIUS, dot.getY() - Dot.RADIUS, dotInside ? dotPaint : redDotPaint);
        canvas.drawText("Level: " + mazeDotState.getCurrentLevel(), 10, 40, levelPaint);
    }

    private void scaryMe(Canvas canvas) {
        int streamId = sound.play(soundId, 1, 1, 99999, 0, 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                showApplicationFinalDialog();
            }
        }, 5000);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
            }
        }, 150);
        canvas.drawBitmap(scaledScaryImage, 0, 0, new Paint());
    }

    private void showApplicationFinalDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.game_end_message)
                .setIcon(R.drawable.smile)
                .setPositiveButton(R.string.close_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContext().stopService(new Intent(getContext(), RecorderService.class));
                        System.exit(0);
                    }
                })
                .show();
    }

    private boolean isScaryCry() {
        return mazeDotState.getCurrentLevel() >= mazeDotState.getScaryLevel() && dot.getX() > mDisplay.getWidth() * 2 / 3;
    }

    private boolean checkIfLevelCompleted() {
        if (mazeDotState.getCurrentLevel() > 0 && dot.getY() > 0 && dot.getY() < mDisplay.getWidth() / 4 / mazeDotState.getCurrentLevel() && dot.getX() > mDisplay.getWidth() - Dot.RADIUS * 2) {
            return true;
        }
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap scaryImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.scary);
        scaledScaryImage = Bitmap.createScaledBitmap(scaryImage, mDisplay.getWidth(), mDisplay.getHeight(), false);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                break;
            case Surface.ROTATION_90:
                mSensorX = -event.values[1];
                mSensorY = event.values[0];
                break;
            case Surface.ROTATION_180:
                mSensorX = -event.values[0];
                mSensorY = -event.values[1];
                break;
            case Surface.ROTATION_270:
                mSensorX = event.values[1];
                mSensorY = -event.values[0];
                break;
        }
    }

    private boolean checkIfDotInsideMaze(List<Rect> rects, float dotX, float dotY) {
        for (Rect rect : rects) {
            if (dotX >= rect.left && dotX <= rect.right && dotY >= rect.top && dotY <= rect.bottom) {
                return true;
            }
        }
        return false;
    }

    public void setMazeDotState(MazeDotState mazeDotState) {
        this.mazeDotState = mazeDotState;
        mazeDotState.setMazeRectangles(MazeSquaresBuilder.getLabirint1(mDisplay.getHeight(), mDisplay.getWidth(), mazeDotState.getCurrentLevel()));
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
