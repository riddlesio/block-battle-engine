package io.riddles.javainterface.game.data;

import java.awt.*;

/**
 * Created by joost on 10/7/16.
 */

public class Board<T> {
    protected T fields[][];

    protected int width;
    protected int height;


    public Board(int width, int height, T[][]) {
        this.fields = new T[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.fields[x][y] = new T();
            }
        }
    }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }


    public T getFieldAt(Point point) {
        return fields[(int)point.getX()][(int)point.getY()];
    }

    public void setFieldAt(Point point, T c) {
        fields[(int)point.getX()][(int)point.getY()] = c;
    }  /* TODO: don't use Point */
}
