package com.artigile.android;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author IoaN, 5/4/11 10:06 PM
 */
@Singleton
public class Dot {
    @Inject
    private Converter converter;

    public static final int RADIUS = 24;

    private static final float STOP_SPEED = 0.0000000001F;

    private float x = 0;
    private float y = 0;

    private float speedX = 0;
    private float speedY = 0;

    public void resetAll() {
        resetCoords();
        resetSpeeds();
    }

    public void resetCoords() {
        x = RADIUS;
        y = RADIUS;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeedX() {
        return convertSpeed(speedX);
    }

    public void setSpeedX(float speedX) {
        this.speedX = convertSpeed(speedX);
    }

    public float getSpeedY() {
        return convertSpeed(speedY);
    }

    public void setSpeedY(float speedY) {
        this.speedY = convertSpeed(speedY);
    }

    public void updateCoords(float mSensorX, float mSensorY, float storedTime) {
        float timeDelta = storedTime / Converter.MILLISECONDS_IN_SEC;
        float accelerationX = converter.calculateAcceleration(getSpeedX(), mSensorX, Converter.CoordinateType.X);
        float accelerationY = converter.calculateAcceleration(getSpeedY(), mSensorY, Converter.CoordinateType.Y);
        speedX = converter.getCurrentSpeed(getSpeedX(), accelerationX, timeDelta, Converter.CoordinateType.X);
        speedY = converter.getCurrentSpeed(getSpeedY(), accelerationY, timeDelta, Converter.CoordinateType.Y);
        x = converter.calculateCoordinate(x, getSpeedX(), accelerationX, timeDelta);
        y = converter.calculateCoordinate(y, getSpeedY(), accelerationY, timeDelta);
    }

    public void resetSpeeds() {
        speedX = 0;
        speedY = 0;
    }

    private float convertSpeed(float speed) {
        /*   if (Math.abs(speed) < 0.01) {
            return 0;
        }*/
        return speed;
    }
}
