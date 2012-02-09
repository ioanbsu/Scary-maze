package com.artigile.android;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ioanbsu
 * Date: 6/24/11
 * Time: 12:07 PM
 */
public class MazeSquaresBuilder {

    public static List<Rect> getLabirint1(int windowHeight, int windowWidth, int level) {
        List<Rect> labirint1 = new ArrayList<Rect>();
        if (level != 0) {
            int topBottomMazeHeight = (int) Math.floor(windowWidth / level / 4.);
            for (int i = 0; i < level; i++) {
                labirint1.add(new Rect(i * windowWidth / level-1, 0, (int) Math.ceil((windowWidth / level) * (i + 1 / 4.)), windowHeight));
                labirint1.add(new Rect((int) Math.floor((windowWidth / level) * (i + 1 / 4.)), windowHeight - topBottomMazeHeight, (int) Math.ceil((windowWidth / level) * (i + 2 / 4.)), windowHeight));
                labirint1.add(new Rect((int) Math.floor((windowWidth / level) * (i + 2 / 4.)), 0, (int) Math.ceil((windowWidth / level) * (i + 3 / 4.)), windowHeight));
                labirint1.add(new Rect((int) Math.floor((windowWidth / level) * (i + 3 / 4.)), 0, (int) Math.ceil((windowWidth / level) * (i + 4 / 4.))+1, topBottomMazeHeight));
            }
        }
        return labirint1;
    }
}
