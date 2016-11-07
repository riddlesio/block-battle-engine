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

package io.riddles.blockbattle.game.player;

import io.riddles.blockbattle.game.data.Shape;
import io.riddles.javainterface.game.player.AbstractPlayer;

/**
 * ${PACKAGE_NAME}
 *
 * This file is a part of TicTacToe
 *
 * Copyright 2016 - present Riddles.io
 * For license information see the LICENSE file in the project root
 *
 * @author Niko
 */


public class BlockBattlePlayer extends AbstractPlayer {

    private boolean usedSkip;
    private int rowPoints;
    private int combo;
    private int rowsRemoved;
    private int skips;
    private Shape currentShape;
    private boolean performedTSpin;
    private boolean fieldCleared;
    private int points;


    public BlockBattlePlayer(int id) {
        super(id);
        this.rowPoints = 0;
        this.combo = 0;
        this.skips = 0;
        this.performedTSpin = false;
        this.fieldCleared = false;
        this.usedSkip = false;
        this.points = 0;
    }

    public BlockBattlePlayer(BlockBattlePlayer player) {
        super(player.getId());
        this.rowPoints = player.getRowPoints();
        this.combo = player.getCombo();
        this.skips = player.getSkips();
        this.performedTSpin = player.getPerformedTSpin();
        this.fieldCleared = player.getfieldCleared();
        this.usedSkip = player.getUsedSkip();
        this.points = player.getPoints();
    }

    public String toString() {
        return "BlockBattlePlayer " + this.getId();
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
    public boolean getPerformedTSpin() {
        return this.performedTSpin;
    }
    public boolean getfieldCleared() {
        return this.fieldCleared;
    }

    public void setRowsRemoved(int rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }

    public int getRowsRemoved() {
        return this.rowsRemoved;
    }

    public int getPoints() {
        return this.points;
    }

    public void setTSpin(boolean performedTSpin) {
        this.performedTSpin = performedTSpin;
    }

    public void setFieldCleared(boolean isFieldCleared) {
        this.fieldCleared = isFieldCleared;
    }


    public Shape getCurrentShape() { return this.currentShape; }
    public void setCurrentShape(Shape currentShape) { this.currentShape = currentShape; }
}
