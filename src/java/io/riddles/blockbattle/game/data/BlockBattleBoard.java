/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.blockbattle.game.data;

import io.riddles.javainterface.game.data.Board;

import java.awt.*;


public class BlockBattleBoard extends Board<Cell> {

    private int solidRows;
    private int playerId;

    public void clear() {
        for(int y=0; y < height; y++) {
            for(int x=0; x < width; x++) {
                this.fields[x][y] = new Cell(x, y);
            }
        }
    }

    @Override
    public Cell fieldFromString(String s) {
        return null;
    }

    public BlockBattleBoard(int width, int height) {
        super(width, height);
        this.solidRows = 0;
        this.fields = new Cell[width][height];

        clear();
    }

    public BlockBattleBoard(BlockBattleBoard board) {
        super(board.getWidth(), board.getHeight());
        this.solidRows = board.getSolidRows();
        this.fields = new Cell[this.width][this.height];
        this.playerId = board.playerId;

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.fields[x][y] = new Cell(board.getFields()[x][y]);
            }
        }
    }

//    public void initialiseFromString(String input, int w, int h) {
//        String[] s = input.split(",");
//        this.width = w;
//        this.height = h;
//        this.fields = new Cell[w][h];
//        int x = 0, y = 0;
//        for (int i = 0; i < s.length; i++) {
//            this.fields[x][y] = new Cell(ShapeType.NONE);
//            System.out.println("initialiseFromString is not yet implemented."); /* TODO: implement */
//            if (++x == w) {
//                x = 0; y++;
//            }
//        }
//    }

    public void dump() {
        System.out.println("dump " + this.hashCode());

        System.out.println(this.toString(true, true));
    }

    // handles round end by clean up and checking for full rows
    // returns number of full rows
    public int processEndOfRoundField() {
        int rowsRemoved = 0;
        for (int y = 0; y < height; y++) {
            boolean fullRow = true;

            for (int x=0; x < width; x++) {
                if (fields[x][y].isShape()) {
                    fields[x][y].setEmpty();
                }
                if (!fields[x][y].isBlock()) { // check if line contains only block cells
                    fullRow = false;
                }
            }

            if (fullRow) { // move cells down one line
                for (int oy = y - 1; oy >= 0; oy--) {
                    for (int x = 0; x < width; x++) {
                        fields[x][oy + 1] = new Cell(this.fields[x][oy]);
                    }
                }
                for (int x = 0; x < width; x++) {
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
    public void cleanShapeFields() {
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
        StringBuilder output = new StringBuilder();

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

    public int getSolidRows() {
        return this.solidRows;
    }

    public void setSolidRows(int rows) {
        this.solidRows = rows;
    }
}

