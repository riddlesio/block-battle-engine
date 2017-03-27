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
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
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

public class BlockBattleState extends AbstractState<BlockBattlePlayerState> {

    private BlockBattleMove move;

    private int round;


    private Shape nextShape;



    public BlockBattleState(BlockBattleState previousState, ArrayList<BlockBattlePlayerState> playerStates, int roundNumber) {
        super(previousState, playerStates, roundNumber);
        this.nextShape = previousState.getNextShape().clone();
    }

    public Shape getNextShape() { return this.nextShape; }
    public void setNextShape(Shape nextShape) { this.nextShape = nextShape; }



}
