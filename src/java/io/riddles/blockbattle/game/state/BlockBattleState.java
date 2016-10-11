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

package io.riddles.blockbattle.game.state;

import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeType;
import io.riddles.javainterface.game.state.AbstractState;
import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.move.BlockBattleMove;


import java.util.ArrayList;

/**
 * io.riddles.game.game.state.BlockBattleState - Created on 2-6-16
 *
 * BlockBattleState extends AbstractState and is used to store game specific data per state.
 * It can be initialised to store a BlockBattleMove, or multiple BlockBattleMoves in an ArrayList.
 *
 * @author joost
 */
public class BlockBattleState extends AbstractState<BlockBattleMove> {

    private BlockBattleMove move;
    private int points;
    private ShapeType nextShape;
    private String fieldString;
    private int round;
    private Shape currentShape;
    private ArrayList<BlockBattleMove> roundMoves;
    private boolean performedTSpin;
    private boolean fieldCleared;
    private boolean usedSkip;
    private int rowPoints;
    private int combo;
    private int rowsRemoved;
    private int skips;


    private BlockBattleBoard board;
    private String errorMessage;
    private int moveNumber;


    public BlockBattleState() {
        super();
    }

    public BlockBattleState(BlockBattleState previousState, BlockBattleMove move, int roundNumber, String possibleMovesString, String fieldPresentationString) {
        super(previousState, move, roundNumber);
        this.rowPoints = 0;
        this.combo = 0;
        this.skips = 0;
        this.performedTSpin = false;
        this.fieldCleared = false;
        this.usedSkip = false;
        this.board = new BlockBattleBoard(previousState.getBoard());

    }

    public BlockBattleState(BlockBattleState previousState, ArrayList<BlockBattleMove> moves, int roundNumber) {
        super(previousState, moves, roundNumber);
        this.rowPoints = 0;
        this.combo = 0;
        this.skips = 0;
        this.performedTSpin = false;
        this.fieldCleared = false;
        this.usedSkip = false;
        this.board = new BlockBattleBoard(previousState.getBoard());
    }


    public BlockBattleBoard getBoard() {
        return this.board;
    }

    public void setBoard(BlockBattleBoard b) {
        this.board = b;
    }

    public void setMoveNumber(int n) { this.moveNumber = n; }
    public int getMoveNumber() { return this.moveNumber; }

    /* Block Battle specific functions */

    public void setTSpin(boolean performedTSpin) {
        this.performedTSpin = performedTSpin;
    }

    public void setFieldCleared(boolean isFieldCleared) {
        this.fieldCleared = isFieldCleared;
    }

    public void setCurrentShape(Shape shape) {
        this.currentShape = shape;
    }

    public Shape getCurrentShape() {
        return this.currentShape;
    }

    public void addRowPoints(int points) {
        this.rowPoints += points;
    }

    public boolean getTSpin() {
        return this.performedTSpin;
    }

    public boolean getFieldCleared() {
        return this.fieldCleared;
    }
    public int getRowPoints() {
        return this.rowPoints;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public int getCombo() {
        return this.combo;
    }

    public void setSkips(int skips) {
        this.skips = skips;
    }

    public int getSkips() {
        return this.skips;
    }

    public void setUsedSkip(boolean usedSkip) {
        this.usedSkip = usedSkip;
    }

    public boolean getUsedSkip() {
        return this.usedSkip;
    }

    public void setRowsRemoved(int rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }

    public int getRowsRemoved() {
        return this.rowsRemoved;
    }
}
