package io.riddles.blockbattle.game.data;

import java.awt.*;

/**
 * Created by joost on 10/11/16.
 */
public class ShapeOperations {


    // spawns the shape to the top of Board
    /* Returns: boolean success */
    public boolean spawnShape(Shape shape, BlockBattleBoard board) {
        int x = (board.getWidth() - shape.size) / 2;
        int y = -1;

        shape.location = new Point(x, y);
        shape.setBlockLocations();

        if(hasCollision(shape, board))
            return false;

        setShapeInField(shape, board);
        return true;
    }

    private void freezeInField(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.blocks.length; i++) {
            board.setBlock(shape.blocks[i].getLocation(), shape.type);
        }
        shape.isFrozen = true;
    }

    private void setShapeInField(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.blocks.length; i++) {
            board.setShape(shape.blocks[i].getLocation(), shape.type);
        }
    }

    //////////// skip action /////////
    public void skip(Shape shape, BlockBattleBoard board) {

        for(int i=0; i < shape.blocks.length; i++) {
            shape.blocks[i].setEmpty();
            board.setEmpty(shape.blocks[i].getLocation());
        }

        shape.isFrozen = true;
    }

    //////////////////////////////////// 
    private void takePosition(Shape shape1, Shape shape2, BlockBattleBoard board) {
        shape1.shape = shape2.shape;
        shape1.blocks = shape2.blocks;
        shape1.location = shape2.location;

        board.cleanShapeFields();
        setShapeInField(shape1, board);
    }

    /* TODO: Needs reimplementation */
    public boolean checkTSpin(Shape shape, BlockBattleBoard board, MoveType lastMove1, MoveType lastMove2, Point lastLocation) {
        if(shape.type != ShapeType.T)
            return false;

        if(lastMove1 == null || lastMove2 == null)
            return false;

        // last move is turn or second to last move is turn

        if(!(lastMove1 == MoveType.TURNRIGHT || lastMove1 == MoveType.TURNLEFT
                || ((lastMove1 == MoveType.DOWN || lastMove1 == MoveType.DROP)
                && (lastMove2 == MoveType.TURNLEFT || lastMove2 == MoveType.TURNRIGHT)
                && (lastLocation.equals(shape.location)))))
            return false;


        // check if 3/4 corners of the matrix are Blocks in the field
        Cell[] corners = new Cell[4];
        corners[0] = board.getFieldAt(new Point(shape.location.x, shape.location.y));
        corners[1] = board.getFieldAt(new Point(shape.location.x + 2, shape.location.y));
        corners[2] = board.getFieldAt(new Point(shape.location.x, shape.location.y + 2));
        corners[3] = board.getFieldAt(new Point(shape.location.x + 2, shape.location.y + 2));

        int counter = 0;
        for(int i = 0; i < corners.length; i++)
            if(corners[i] != null && corners[i].isBlock())
                counter++;

        if(counter == 3)
            return true;

        return false;
    }


    ////// Turn actions /////

    public String turnRight(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        Cell[][] temp = transposeShape(clone);
        for(int x=0; x < shape.size; x++) {
            clone.shape[x] = temp[shape.size - x - 1];
        }

        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "turnright");

        if(error.isEmpty()) {
            takePosition(shape, clone, board);
        }

        return error;
    }

    public String turnLeft(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        Cell[][] temp = transposeShape(clone);
        for(int y=0; y < shape.size; y++) {
            for(int x=0; x < shape.size; x++) {
                clone.shape[x][y] = temp[x][shape.size - y - 1];
            }
        }

        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "turnleft");

        if(error.isEmpty())
            takePosition(shape, clone, board);

        return error;
    }

    public String oneDown(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        clone.location.y++;
        clone.setBlockLocations();

        if(isBelowBottom(clone, board) || hasCollision(clone, board))
            freezeInField(shape, board);
        else
            takePosition(shape, clone, board);

        return ""; // can't return an error
    }

    public String oneLeft(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        clone.location.x--;
        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "left");

        if(error.isEmpty())
            takePosition(shape, clone, board);
        else
            oneDown(shape, board);

        return error;
    }

    public String oneRight(Shape shape, BlockBattleBoard board) {
        Shape clone = shape.clone();

        clone.location.x++;
        clone.setBlockLocations();
        String error = checkForPositionErrors(clone, board, "right");

        if(error.isEmpty())
            takePosition(shape, clone, board);
        else
            oneDown(shape, board);

        return error;
    }

    public void drop(Shape shape, BlockBattleBoard board) {
        while(!shape.isFrozen) {
            oneDown(shape, board);
        }
    }


    public Cell[][] transposeShape(Shape shape) {
        Cell[][] temp = new Cell[shape.size][shape.size];
        for(int y=0; y < shape.size; y++) {
            for(int x=0; x < shape.size; x++) {
                temp[y][x] = shape.shape[x][y];
            }
        }
        return temp;
    }

    private String checkForPositionErrors(Shape shape, BlockBattleBoard board, String move) {
        String error = String.format("Can't perform %s: ", move);

        if(move.equals("left") || move.equals("right")) {
            if(isOutOfBoundaries(shape, board))
                return error += "Piece is on the field boundary. Action changed to 'down'.";
            if(hasCollision(shape, board))
                return error += "Other blocks are in the way. Action changed to 'down'.";

        } else if(move.equals("turnright") || move.equals("turnleft")) {

            if(isOutOfBoundaries(shape, board) || isBelowBottom(shape, board))
                return error += "Piece would move out of bounds. Move skipped.";
            if(hasCollision(shape, board))
                return error += "Other blocks are in the way. Move skipped.";

        }
        return "";
    }

    //// Position checks /////
    public boolean hasCollision(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.blocks.length; i++) {
            if(shape.blocks[i].hasCollision(board))
                return true;
        }
        return false;
    }

    /* Checks Shape is bellow bottom of Board */
    public boolean isBelowBottom(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.blocks.length; i++) {
            if(shape.blocks[i].isBelowBottom(board))
                return true;
        }
        return false;
    }

    public boolean isOutOfBoundaries(Shape shape, BlockBattleBoard board) {
        for(int i=0; i < shape.blocks.length; i++) {
            if(shape.blocks[i].isOutOfBoundaries(board))
                return true;
        }
        return false;
    }

    public boolean isOverflowing(Shape shape) {
        for(int i=0; i < shape.blocks.length; i++) {
            if(!shape.blocks[i].isEmpty() && shape.blocks[i].isOverFlowing())
                return true;
        }
        return false;
    }
}
