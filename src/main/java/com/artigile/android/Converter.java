package com.artigile.android;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * User: ioanbsu
 * Date: 6/23/11
 * Time: 5:49 PM
 */
@Singleton
public class Converter {

    @Inject
    private MazeDotState mazeDotState;

    private float INCHES_PER_METER = 39.3700787F;

    public static final float MILLISECONDS_IN_SEC = 1000000000;

    private float frictionCoef = 0.0001f;

    public float calculateCoordinate(float currentCoord, float currentSpeed, float acceleration, float timeDelta) {
        return (currentCoord + currentSpeed * timeDelta + acceleration * timeDelta * timeDelta / 2);
    }


    public float calculateAcceleration(float currentSpeed, float acceleration, CoordinateType coordinateType) {
        return metersToDots(acceleration, coordinateType);
    }


    public float getCurrentSpeed(float initSpeed, float acceleration, float timeDelta, CoordinateType coordinateType) {
        float frictCalculation = 0;
        if (Math.abs(initSpeed) > 0) {
            frictCalculation = frictionCoef * Math.abs(initSpeed) * initSpeed;
        }
        float calculatedSpeed = acceleration * timeDelta + initSpeed;
        if (Math.abs(calculatedSpeed) < Math.abs(frictCalculation)) {
            return 0;
        } else {
            return calculatedSpeed - frictCalculation;
        }
    }


    public float metersToDots(float metersValue, CoordinateType coordinateType) {
        float dpi = 213;
        return metersValue * dpi * INCHES_PER_METER / mazeDotState.getSensivityLevel();
    }


    enum CoordinateType {
        X, Y
    }
}
