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

public class BlockBattleMoveDeserializer implements Deserializer<BlockBattleMove> {


    public BlockBattleMoveDeserializer() {
    }

    @Override
    public BlockBattleMove traverse(String string) {
        try {
            return visitMove(string);
        } catch (InvalidInputException ex) {
            return new BlockBattleMove(ex);
        } catch (Exception ex) {
            return new BlockBattleMove(new InvalidInputException("Failed to parse move"));
        }

    }

    private BlockBattleMove visitMove(String input) throws InvalidInputException {
        BlockBattleMove move = new BlockBattleMove();

        String[] split = input.split(",");
        ArrayList<MoveType> moves = new ArrayList<MoveType>();
        for (int i = 0; i < split.length; i++) {
            moves.add(visitAssessment(split[i]));
        }
        move.setMoveTypes(moves);
        return move;
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
