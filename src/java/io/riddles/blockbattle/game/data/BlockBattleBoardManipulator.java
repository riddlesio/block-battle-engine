package io.riddles.blockbattle.game.data;

import java.awt.*;

/**
 * Created by joost on 10/7/16.
 */
public class BlockBattleBoardManipulator {

    // moves the whole field upwards to make room for new
    // lines from the bottom, leaves the bottom rows untouched (are changed in the appropriate methods).
    public void moveFieldUp(BlockBattleBoard board, int amount) {
        if(amount <= 0)
            return;

        boolean gameOver = false;

        for(int y = 0; y < board.getHeight() + amount - board.getSolidRows(); y++) {
            for(int x = 0; x < board.getWidth(); x++) {

                int newY = y - amount;
                Cell field = board.getFieldAt(new Point(x,y));
                if(newY < 0 && !field.isEmpty()) {
                    gameOver = true;
                }

                if(newY >= 0 && y < board.getHeight()) { // move cells up
                    board.setFieldAt(new Point(x, newY), field.clone());
                    grid[x][newY].setLocation(x, newY);
                }
            }
        }
    }

    // adds solid rows to bottom, returns true if game over
    public void addSolidRows(int amount) {
        moveFieldUp(amount);

        // make the bottom rows into solid rows
        for(int y = height - solidRows - 1; y > height - solidRows - amount - 1; y--) {
            for(int x = 0; x < width; x++) {
                grid[x][y].setSolid();
            }
        }

        this.solidRows += amount; /* TODO: check where this is used */
    }

    public boolean addGarbageLines(int amount, boolean firstIsSingle) {
        boolean gameOver = moveFieldUp(amount);
//		ArrayList<Integer> exclude = new ArrayList<Integer>();
        int count = 0;

        // make the bottom rows into garbage lines
        for(int y = height - solidRows - 1; y > height - solidRows - amount - 1; y--) {
            // switch between single and double holes in garbage lines
            count++;

            for(int x = 0; x < width; x++) {
                grid[x][y].setBlock();
                grid[x][y].setShapeType(ShapeType.G);
            }

            int index1 = RANDOM.nextInt(width);
            grid[index1][y].setEmpty();

            if ((count % 2 == 1 && !firstIsSingle) || (count % 2 == 0 && firstIsSingle)) { // double hole
                int rotate = 1 + RANDOM.nextInt(width - 1);
                int index2 = (index1 + rotate) % width;
                grid[index2][y].setEmpty();
            }
        }

        return gameOver;
    }
}
