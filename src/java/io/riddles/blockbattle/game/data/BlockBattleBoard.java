package io.riddles.blockbattle.game.data;

import java.awt.*;

/**
 * ${PACKAGE_NAME}
 *
 * This file is a part of BlockBattle
 *
 * Copyright 2016 - present Riddles.io
 * For license information see the LICENSE file in the project root
 *
 * @author Niko
 */

public class BlockBattleBoard {
    protected int[][] board;

    protected int width = 9;
    protected int height = 9;

    public static final int EMPTY_FIELD = 0;



    public BlockBattleBoard(int w, int h) {
        this.width = w;
        this.height = h;
        this.board = new int[w][h];
        clearBoard();
    }

    public BlockBattleBoard(BlockBattleBoard b) {
        this.width = b.getWidth();
        this.height = b.getHeight();

    }

    public void clearBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board[x][y] = EMPTY_FIELD;
            }
        }
    }


    @Override
    /**
     * Creates comma separated String
     * @param :
     * @return : String
     */
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (counter > 0) {
                    r += ",";
                }
                r += board[x][y];
                counter++;
            }
        }
        return r;
    }


    public void initialiseFromString(String input, int w, int h) {
        String[] s = input.split(",");
        this.width = w;
        this.height = h;
        this.board = new int[w][h];
        int x = 0, y = 0;
        for (int i = 0; i < s.length; i++) {
            this.board[x][y] = Integer.parseInt(s[i]);
            if (++x == w) {
                x = 0; y++;
            }
        }
    }


    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }


    public int getFieldAt(Point c) {
        return board[c.x][c.y];
    }
    public void setFieldAt(Point c, int v) {
        board[c.x][c.y] = v;
    }

    public void dump() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(board[x][y]);
            }
            System.out.println();
        }
    }
}

