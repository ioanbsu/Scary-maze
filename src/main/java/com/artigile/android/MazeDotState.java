package com.artigile.android;

import android.graphics.Rect;
import android.view.SurfaceHolder;
import com.google.inject.Singleton;

import java.util.List;

/**
 * @author IoaN, 2/1/12 7:01 PM
 */
@Singleton
public class MazeDotState {

    private SurfaceHolder surfaceHolder;

    private List<Rect> mazeRectangles;

    private float sensivityLevel = 12F;

    private int scaryLevel = 3;

    private String userName;

    private int currentLevel = 1;

    private boolean gameStarted;

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void increaseLevel() {
        currentLevel++;
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public List<Rect> getMazeRectangles() {
        return mazeRectangles;
    }

    public void setMazeRectangles(List<Rect> mazeRectangles) {
        this.mazeRectangles = mazeRectangles;
    }

    public float getSensivityLevel() {
        return sensivityLevel;
    }

    public void setSensivityLevel(float sensivityLevel) {
        this.sensivityLevel = sensivityLevel;
    }

    public int getScaryLevel() {
        return scaryLevel;
    }

    public void setScaryLevel(int scaryLevel) {
        this.scaryLevel = scaryLevel;
    }

}
