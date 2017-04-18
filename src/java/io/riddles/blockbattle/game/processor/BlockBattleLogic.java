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

package io.riddles.blockbattle.game.processor;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.move.MoveType;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeOperations;
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.javainterface.exception.InvalidMoveException;


public class BlockBattleLogic {

    /**
     * Takes a BlockBattleState and applies the move(s).
     * Returns nothing, but transforms the given BlockBattleState.
     */
    public static boolean executeMoveForPlayer(BlockBattlePlayerState playerState) {
        Shape shape = playerState.getCurrentShape();
        MoveType moveType = playerState.getCurrentMove();
        BlockBattleBoard board = playerState.getBoard();

        if (shape.isFrozen()) {
            playerState.setException(new InvalidMoveException(
                    "Piece was already frozen in place"));
            return false;
        }

        String result = "";
        switch (moveType) {
            case LEFT:
                result = ShapeOperations.oneLeft(shape, board);
                break;
            case RIGHT:
                result = ShapeOperations.oneRight(shape, board);
                break;
            case TURNLEFT:
                result = ShapeOperations.turnLeft(shape, board);
                break;
            case TURNRIGHT:
                result = ShapeOperations.turnRight(shape, board);
                break;
            case DOWN:
                result = ShapeOperations.oneDown(shape, board);
                break;
            case DROP:
                ShapeOperations.drop(shape, board);
                break;
            case SKIP:
                if (playerState.getSkips() > 0) {
                    ShapeOperations.skip(shape, board);
                    playerState.setSkips(playerState.getSkips() - 1);
                    playerState.setUsedSkip(true);
                } else {
                    playerState.setException(new InvalidMoveException(
                            "There are no skips available"));
                }
                break;
        }

        if (!result.isEmpty()) {
            playerState.setException(new InvalidMoveException(result));
        }

        return moveType != MoveType.SKIP;
    }
}