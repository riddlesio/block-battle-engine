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

import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.game.move.AbstractMoveDeserializer;

import java.util.ArrayList;


public class BlockBattleMoveDeserializer extends AbstractMoveDeserializer<BlockBattleMove> {

    @Override
    protected BlockBattleMove createExceptionMove(InvalidInputException exception) {
        return new BlockBattleMove(exception);
    }

    @Override
    protected BlockBattleMove visitMove(String input) throws InvalidInputException {
        BlockBattleMove move = new BlockBattleMove();
        String[] split = input.split(",");
        ArrayList<MoveType> moves = new ArrayList<>();

        for (String aSplit : split) {
            moves.add(vistMoveType(aSplit));
        }

        move.setMoveTypes(moves);

        return move;
    }

    private MoveType vistMoveType(String input) throws InvalidInputException {
        MoveType moveType = MoveType.fromString(input);

        if (moveType == null) {
            throw new InvalidInputException("Move isn't valid");
        }

        return moveType;
    }
}
