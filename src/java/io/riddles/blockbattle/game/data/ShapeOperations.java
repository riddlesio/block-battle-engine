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

import java.awt.*;

import io.riddles.blockbattle.game.move.MoveType;

public class ShapeOperations {

    // spawns the shape to the top of Board
    /* Returns: boolean success */
    public static boolean spawnShape(Shape shape, BlockBattleBoard board) {
        int x = (board.getWidth() - shape.getSize()) / 2;
        int y = -1;
        shape.setLocation(new Point(x, y));
        shape.setBlockLocations();

        if (hasCollision(shape, board)) {
            return false;
        }

        setShapeInField(shape, board);
        return true;
    }

    private static void freezeInField(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.getBlocks().length; i++) {
            board.setBlock(shape.getBlocks()[i].getLocation(), shape.getType());
        }
        shape.setFrozen();
    }

    private static void setShapeInField(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.getBlocks().length; i++) {
            board.setShape(shape.getBlocks()[i].getLocation(), shape.getType());
        }
    }

    //////////// skip action /////////
    public static void skip(Shape shape, BlockBattleBoard board) {

        for(int i=0; i < shape.getBlocks().length; i++) {
            shape.getBlocks()[i].setEmpty();
            board.setEmpty(shape.getBlocks()[i].getLocation());
        }

        shape.setFrozen();
    }

    private static void takePosition(Shape shape1, Shape shape2, BlockBattleBoard board) {
        shape1.setShape(shape2.getShape());
        shape1.setBlocks(shape2.getBlocks());
        shape1.setLocation(shape2.getLocation());

        board.cleanShapeFields();
        setShapeInField(shape1, board);
    }

    public static boolean checkTSpin(Shape shape, BlockBattleBoard board,
                                     MoveType lastMove1, MoveType lastMove2, Point lastLocation) {
        if (shape.getType() != ShapeType.T)
            return false;

        if (lastMove1 == null || lastMove2 == null)
            return false;

        Point location = shape.getLocation();

        if (!(lastMove1 == MoveType.TURNRIGHT || lastMove1 == MoveType.TURNLEFT
                || ((lastMove1 == MoveType.DOWN || lastMove1 == MoveType.DROP)
                && (lastMove2 == MoveType.TURNLEFT || lastMove2 == MoveType.TURNRIGHT)
                && lastLocation.equals(location))))
            return false;


        // check if 3/4 corners of the matrix are Blocks in the field
        Cell[] corners = new Cell[4];
        corners[0] = board.getFieldAt(new Point(location.x, location.y));
        corners[1] = board.getFieldAt(new Point(location.x + 2, location.y));
        corners[2] = board.getFieldAt(new Point(location.x, location.y + 2));
        corners[3] = board.getFieldAt(new Point(location.x + 2, location.y + 2));

        int counter = 0;
        for (Cell corner : corners) {
            if (corner != null && corner.isBlock()) {
                counter++;
            }
        }

        return counter == 3;
    }

    ////// Turn actions /////

    public static String turnRight(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        Cell[][] temp = transposeShape(clone);
        for(int x=0; x < shape.getSize(); x++) {
            clone.getShape()[x] = temp[shape.getSize() - x - 1];
        }

        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "turnright");

        if(error.isEmpty()) {
            takePosition(shape, clone, board);
        }

        return error;
    }

    public static String turnLeft(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        Cell[][] temp = transposeShape(clone);
        for(int y=0; y < shape.getSize(); y++) {
            for(int x=0; x < shape.getSize(); x++) {
                clone.getShape()[x][y] = temp[x][shape.getSize() - y - 1];
            }
        }

        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "turnleft");

        if(error.isEmpty())
            takePosition(shape, clone, board);

        return error;
    }

    public static String oneDown(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        clone.getLocation().y++;
        clone.setBlockLocations();

        if (isBelowBottom(clone, board) || hasCollision(clone, board)) {
            freezeInField(shape, board);
        } else {
            takePosition(shape, clone, board);
        }

        return ""; // can't return an error
    }

    public static String oneLeft(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        clone.getLocation().x--;
        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "left");

        if (error.isEmpty()) {
            takePosition(shape, clone, board);
        } else {
            oneDown(shape, board);
        }

        return error;
    }

    public static String oneRight(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        clone.getLocation().x++;
        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "right");

        if (error.isEmpty()) {
            takePosition(shape, clone, board);
        } else {
            oneDown(shape, board);
        }

        return error;
    }

    public static void drop(Shape shape, BlockBattleBoard board) {
        while (!shape.isFrozen()) {
            oneDown(shape, board);
        }
    }

    private static Cell[][] transposeShape(Shape shape) {
        Cell[][] temp = new Cell[shape.getSize()][shape.getSize()];

        for(int y=0; y < shape.getSize(); y++) {
            for(int x=0; x < shape.getSize(); x++) {
                temp[y][x] = shape.getShape()[x][y];
            }
        }

        return temp;
    }

    private static String checkForPositionErrors(Shape shape, BlockBattleBoard board, String move) {
        String error = String.format("Can't perform %s: ", move);

        if (move.equals("left") || move.equals("right")) {
            if (isOutOfBoundaries(shape, board))
                return error + "Piece is on the field boundary";
            if (hasCollision(shape, board))
                return error + "Other blocks are in the way";
        } else if (move.equals("turnright") || move.equals("turnleft")) {
            if (isOutOfBoundaries(shape, board) || isBelowBottom(shape, board))
                return error + "Piece would move out of bounds";
            if (hasCollision(shape, board))
                return error + "Other blocks are in the way";
        }

        return "";
    }

    //// Position checks /////

    private static boolean hasCollision(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.getBlocks().length; i++) {
            if(shape.getBlocks()[i].hasCollision(board))
                return true;
        }
        return false;
    }

    /* Checks Shape is bellow bottom of Board */
    private static boolean isBelowBottom(Shape shape, BlockBattleBoard board) {
        for (int i=0; i < shape.getBlocks().length; i++) {
            if (shape.getBlocks()[i].isBelowBottom(board))
                return true;
        }
        return false;
    }

    private static boolean isOutOfBoundaries(Shape shape, BlockBattleBoard board) {
        for (int i=0; i < shape.getBlocks().length; i++) {
            if (shape.getBlocks()[i].isOutOfBoundaries(board))
                return true;
        }
        return false;
    }

    public static boolean isOverflowing(Shape shape) {
        for (int i=0; i < shape.getBlocks().length; i++) {
            if (!shape.getBlocks()[i].isEmpty() && shape.getBlocks()[i].isOverFlowing()) {
                return true;
            }
        }
        return false;
    }
}
