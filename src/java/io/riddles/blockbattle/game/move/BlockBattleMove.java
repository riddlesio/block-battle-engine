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

package io.riddles.blockbattle.game.move;

import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.data.MoveType;
import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.game.move.AbstractMove;

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


public class BlockBattleMove extends AbstractMove<BlockBattlePlayer> {

    private MoveType type;
    private String boardRepresentation;

    public BlockBattleMove(BlockBattlePlayer player) {
        super(player);
    }

    public BlockBattleMove(BlockBattlePlayer player, MoveType type) {
        super(player);
        this.type = type;
    }

    public BlockBattleMove(BlockBattlePlayer player, InvalidInputException exception) {
        super(player, exception);
    }

    public MoveType getMoveType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }

    public void setBoardRepresentation(String s) { this.boardRepresentation = s; }
    public String getBoardRepresentation() { return this.boardRepresentation; }

}
