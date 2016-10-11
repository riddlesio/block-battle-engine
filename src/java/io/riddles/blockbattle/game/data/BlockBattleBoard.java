package io.riddles.blockbattle.game.data;

import io.riddles.javainterface.game.data.Board;
import io.riddles.javainterface.game.data.Point;

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

public class BlockBattleBoard extends Board<Cell> {

    public BlockBattleBoard() {
        super();
    }

    private int solidRows;

    public static final int EMPTY_FIELD = 0;

    @Override
    public void clear() {
        for(int y=0; y < height; y++) {
            for(int x=0; x < width; x++) {
                this.fields[x][y] = new Cell(x, y);
            }
        }
    }

    public BlockBattleBoard(int w, int h) {
        this.width = w;
        this.height = h;
        this.solidRows = 0;
        this.fields = new Cell[width][height];
        clear();
    }

    public BlockBattleBoard(BlockBattleBoard b) {
        this.width = b.getWidth();
        this.height = b.getHeight();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.fields[x][y] = b.getFieldAt(new Point(x, y)).clone();
            }
        }

    }


    public void initialiseFromString(String input, int w, int h) {
        String[] s = input.split(",");
        this.width = w;
        this.height = h;
        this.fields = new Cell[w][h];
        int x = 0, y = 0;
        for (int i = 0; i < s.length; i++) {
            this.fields[x][y] = new Cell(ShapeType.NONE);
            System.out.println("initialiseFromString is not yet implemented."); /* TODO: implement */
            if (++x == w) {
                x = 0; y++;
            }
        }
    }

    public void dump() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(fields[x][y]);
            }
            System.out.println();
        }
    }

    public int getSolidRows() { return solidRows; }
    public void setSolidRows(int rows) { solidRows = rows; }

    // handles round end by clean up and checking for full rows
    // returns number of full rows
    public int processEndOfRoundField() {
        int rowsRemoved = 0;
        for(int y=0; y < height; y++) {
            boolean fullRow = true;

            for(int x=0; x < width; x++) {
                if(fields[x][y].isShape())
                    fields[x][y].setEmpty();
                if(!fields[x][y].isBlock()) // check if line contains only block cells
                    fullRow = false;
            }

            if(fullRow) { // move cells down one line
                for(int oy = y - 1; oy >= 0; oy--) {
                    for(int x=0; x < width; x++) {
                        fields[x][oy + 1] = fields[x][oy].clone();
                    }
                }
                for(int x=0; x < width; x++) {
                    fields[x][0].setEmpty();
                }
                rowsRemoved++;
            }
        }
        return rowsRemoved;
    }

    // checks whether the whole field is empty
    public boolean isFieldCleared() {
        for(int y=0; y < height; y++)
            for(int x=0; x < width; x++)
                if(fields[x][y].isBlock())
                    return false;
        return true;
    }

    // removes shape cells from field
    public void cleanField() {
        for(int y=0; y < height; y++) {
            for(int x=0; x < width; x++) {
                if(fields[x][y].isShape())
                    fields[x][y].setEmpty();
            }
        }
    }

    public void setShape(Point p, ShapeType shape) {
        Cell cell = getFieldAt(p);
        if(cell != null && cell.isEmpty()) {
            cell.setShape();
            cell.setShapeType(shape);
        }
    }

    public void setBlock(Point p, ShapeType shape) {
        Cell cell = getFieldAt(p);
        if(cell != null) {
            if(!cell.isSolid() && !cell.isBlock()) {
                cell.setBlock();
                cell.setShapeType(shape);
            } else {
                System.err.printf("Can't set block here. (%s %s)\n", p.toString(), shape);
            }
        }
    }

    public void setEmpty(Point p) {
        Cell cell = getFieldAt(p);
        if(cell != null)
            cell.setEmpty();
    }

    /**
     * Returns string representation of the current field
     * pretty for printing in console
     */
    public String toString(boolean pretty, boolean forVisualizer) {
        StringBuffer output = new StringBuffer();

        String rowConnector = ",";
        String collumnConnector = ";";
        if(pretty) {
            rowConnector = " ";
            collumnConnector = "\n";
        }

        String collumnJoin = "";
        for(int y=0; y < height; y++) {

            output.append(collumnJoin);

            String rowJoin = "";
            for(int x=0; x < width; x++) {
                output.append(rowJoin);
                String cellState = fields[x][y].getState().getCode() + "";

                if(forVisualizer && fields[x][y].getShapeType() != ShapeType.NONE) {
                    cellState = fields[x][y].getShapeType().toString();
                }

                output.append(cellState);
                rowJoin = rowConnector;
            }

            collumnJoin = collumnConnector;
        }

        return output.toString();
    }
}

