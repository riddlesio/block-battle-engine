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
import java.security.SecureRandom;


public class BlockBattleBoardOperations {
    private static final SecureRandom RANDOM = new SecureRandom();

    // moves the whole field upwards to make room for new
    // lines from the bottom, leaves the bottom rows untouched (are changed in the appropriate methods).
    /* Returns: true if success, false is board overflows or amount <= 0*/
    private static boolean moveFieldUp(BlockBattleBoard board, int amount) {
        if (amount <= 0) {
            return false;
        }

        boolean overflow = false;

        for (int y = 0; y < board.getHeight() + amount - board.getSolidRows(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {

                int newY = y - amount;
                Cell field = board.getFieldAt(new Point(x,y));
                if (newY < 0 && !field.isEmpty()) {
                    overflow = true;
                }

                if (newY >= 0 && y < board.getHeight()) { // move cells up
                    board.setFieldAt(new Point(x, newY), new Cell(field));
                    board.getFieldAt(new Point(x, newY)).setLocation(x, newY);
                }
            }
        }
        return overflow;
    }

    // adds solid rows to bottom, returns true if failed
    public static boolean addSolidRows(BlockBattleBoard board, int amount) {
        boolean failed = moveFieldUp(board, amount);

        // make the bottom rows into solid rows
        int startingRow = board.getHeight() - board.getSolidRows() - 1;
        for (int y = startingRow; y > startingRow - amount; y--) {
            for (int x = 0; x < board.getWidth(); x++) {
                board.getFieldAt(new Point(x,y)).setSolid();
            }
        }

        board.setSolidRows(board.getSolidRows() + amount);
        return failed;
    }

    /* Returns true if adding garbage rows failed, i.e. game over */
    public static boolean addGarbageLines(BlockBattleBoard board, int amount, boolean firstIsSingle) {
        boolean gameOver = moveFieldUp(board, amount);
//		ArrayList<Integer> exclude = new ArrayList<Integer>();
        int count = 0;

        // make the bottom rows into garbage lines
        int start = board.getHeight() - board.getSolidRows();
        for(int y = start - 1; y > start - amount - 1; y--) {
            // switch between single and double holes in garbage lines
            count++;

            for(int x = 0; x < board.getWidth(); x++) {
                board.getFieldAt(new Point(x, y)).setBlock();
                board.getFieldAt(new Point(x, y)).setShapeType(ShapeType.G);
            }

            int index1 = RANDOM.nextInt(board.getWidth());
            board.getFieldAt(new Point(index1, y)).setEmpty();

            if ((count % 2 == 1 && !firstIsSingle) || (count % 2 == 0 && firstIsSingle)) { // double hole
                int rotate = 1 + RANDOM.nextInt(board.getWidth() - 1);
                int index2 = (index1 + rotate) % board.getWidth();
                board.getFieldAt(new Point(index2, y)).setEmpty();
            }
        }

        return gameOver;
    }
}
