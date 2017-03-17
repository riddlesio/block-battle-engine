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

import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.data.MoveType;
import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.serialize.Deserializer;

import java.util.ArrayList;

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

public class BlockBattleMoveDeserializer implements Deserializer<ArrayList<BlockBattleMove>> {

    private BlockBattlePlayer player;

    public BlockBattleMoveDeserializer(BlockBattlePlayer player) {
        this.player = player;
    }

    @Override
    public ArrayList<BlockBattleMove> traverse(String string) {
        ArrayList<BlockBattleMove> moves = new ArrayList<BlockBattleMove>();
        try {
            moves = visitMove(string);
        } catch (InvalidInputException ex) {
            moves.add(new BlockBattleMove(this.player, ex));
        } catch (Exception ex) {
            moves.add(new BlockBattleMove(this.player, new InvalidInputException("Failed to parse move")));
        }
        return moves;
    }

    private ArrayList<BlockBattleMove> visitMove(String input) throws InvalidInputException {

        String[] split = input.split(",");
        ArrayList<BlockBattleMove> moves = new ArrayList<BlockBattleMove>();
        for (int i = 0; i < split.length; i++) {
            moves.add(new BlockBattleMove(this.player, visitAssessment(split[i])));
        }
        return moves;
    }

    public MoveType visitAssessment(String input) throws InvalidInputException {
        switch (input) {
            case "left":
                return MoveType.LEFT;
            case "right":
                return MoveType.RIGHT;
            case "turnleft":
                return MoveType.TURNLEFT;
            case "turnright":
                return MoveType.TURNRIGHT;
            case "drop":
                return MoveType.DROP;
            case "skip":
                return MoveType.SKIP;
            case "down":
                return MoveType.DOWN;
            default:
                throw new InvalidInputException("Move isn't valid");
        }
    }
}
