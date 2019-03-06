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

import java.awt.*;
import java.util.ArrayList;

import io.riddles.blockbattle.engine.BlockBattleEngine;
import io.riddles.blockbattle.game.data.*;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.move.ActionType;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.move.BlockBattleMoveDeserializer;
import io.riddles.blockbattle.game.move.MoveType;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.exception.InvalidMoveException;
import io.riddles.javainterface.game.move.AbstractMoveDeserializer;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.game.processor.SimpleProcessor;


public class BlockBattleProcessor extends SimpleProcessor<BlockBattleState, BlockBattlePlayer> {

    private boolean gameOver;
    private Integer winnerId;

    public BlockBattleProcessor(PlayerProvider<BlockBattlePlayer> playerProvider) {
        super(playerProvider);
    }

    /**
     * Play one round of the game. It takes a BlockBattleState,
     * asks all living players for a response and delivers a new BlockBattleState.
     * the BlockBattleState that will be the state for the next round.
     * @param roundNumber The current round number
     * @param inputState The current BlockBattleState
     * @return The BlockBattleState that will be the start of the next round
     */
    @Override
    public BlockBattleState createNextState(BlockBattleState inputState, int roundNumber) {
        BlockBattleState firstState = inputState.createNextState(roundNumber);
        firstState.addClonedPlayerStates(inputState);
        firstState.updateShapes();

        for (BlockBattlePlayerState playerState : firstState.getPlayerStates()) {
            // Player can lose when shape spawns
            if (!ShapeOperations.spawnShape(playerState.getCurrentShape(), playerState.getBoard())) {
                setWinner(playerState.getOpponentPlayerId());
                return firstState;
            }
        }

        BlockBattleMoveDeserializer deserializer = new BlockBattleMoveDeserializer();

        // Send updates and request moves
        ArrayList<BlockBattleMove> playerMoves = new ArrayList<>();
        for (BlockBattlePlayer player : this.playerProvider.getPlayers()) {
            sendUpdates(firstState, player);
            String response = player.requestMove(ActionType.MOVE);
            playerMoves.add(deserializer.traverse(response));
        }

        BlockBattleState lastState = createMovingStates(firstState, playerMoves);
        fillUpPlayerStates(firstState);

        return createFinalState(lastState);
    }

    private BlockBattleState createMovingStates(BlockBattleState state, ArrayList<BlockBattleMove> moves) {
        ArrayList<BlockBattleState> movingStates = new ArrayList<>();

        for (BlockBattlePlayerState startingPlayerState : state.getPlayerStates()) {
            BlockBattleMove move = moves.get(startingPlayerState.getPlayerId());
            BlockBattlePlayerState previousPlayerState = startingPlayerState;
            BlockBattlePlayerState newPlayerState = startingPlayerState;
            BlockBattleState nextState = state;

            Point lastLocation = new Point(-1, -1);
            MoveType lastMove1 = null;
            MoveType lastMove2 = null;

            for (int i = 0; i <= move.getMoveTypes().size(); i++) {
                nextState = getNextMovingState(nextState, movingStates, i);
                newPlayerState = new BlockBattlePlayerState(previousPlayerState);
                nextState.addPlayerState(newPlayerState);
                previousPlayerState = newPlayerState;

                if (move.getException() != null) {
                    newPlayerState.setException(move.getException());
                    break;
                }

                if (i >= move.getMoveTypes().size()) break;

                // Process the moveType
                MoveType moveType = move.getMoveTypes().get(i);
                newPlayerState.setCurrentMove(moveType);

                lastMove2 = lastMove1;
                lastMove1 = moveType;

                Point currentLocation = newPlayerState.getCurrentShape().getLocation();
                lastLocation = new Point(currentLocation.x, currentLocation.y);

                if (!BlockBattleLogic.executeMoveForPlayer(newPlayerState)) break;
            }

            setFinalMovingState(newPlayerState);

            Shape shape = newPlayerState.getCurrentShape();

            if (newPlayerState.getException() != null) {
                lastMove2 = null;
            }

            boolean tspin = ShapeOperations.checkTSpin(
                    shape, newPlayerState.getBoard(), lastMove1, lastMove2, lastLocation);
            newPlayerState.setTSpin(tspin);

            if (ShapeOperations.isOverflowing(shape)) {
                setWinner(newPlayerState.getOpponentPlayerId());
            }
        }

        return movingStates.get(movingStates.size() - 1);
    }

    private void setFinalMovingState(BlockBattlePlayerState playerState) {
        Shape shape = playerState.getCurrentShape();
        BlockBattleBoard board = playerState.getBoard();

        if (!shape.isFrozen()) {
            int initialY = shape.getLocation().y;
            ShapeOperations.drop(shape, board);
            int finalY = shape.getLocation().y;

            if (initialY != finalY && playerState.getException() == null) {
                playerState.setException(new InvalidMoveException(
                        "The piece is still loose in the field. Dropping it"));
            }
        }
    }

    private BlockBattleState getNextMovingState(
                BlockBattleState state, ArrayList<BlockBattleState> states, int index) {
        BlockBattleState nextState;

        if (index >= states.size()) {
            nextState = state.createNextState(state.getRoundNumber());
            states.add(nextState);
        } else {
            nextState = states.get(index);
        }

        return nextState;
    }

    // Fills up the states that are missing a playerstate (because there were less moves)
    // with copies of the last playerstate
    private void fillUpPlayerStates(BlockBattleState firstState) {
        int playerCount = this.playerProvider.getPlayers().size();
        BlockBattleState nextState = firstState;

        while (nextState != null) {
            if (nextState.getPlayerStates().size() < playerCount) {
                int playerId = nextState.getPlayerStates().get(0).getOpponentPlayerId();
                BlockBattleState previousState = (BlockBattleState) nextState.getPreviousState();
                BlockBattlePlayerState playerState = previousState.getPlayerStateById(playerId);
                BlockBattlePlayerState newPlayerState = new BlockBattlePlayerState(playerState);

                newPlayerState.setUsedSkip(playerState.getUsedSkip());
                newPlayerState.setTSpin(playerState.getTSpin());

                nextState.getPlayerStates().add(playerId, newPlayerState);
            }

            nextState = (BlockBattleState) nextState.getNextState();
        }
    }

    private BlockBattleState createFinalState(BlockBattleState state) {
        BlockBattleState finalState = state.createNextState(state.getRoundNumber());
        finalState.addClonedPlayerStates(state);

        // remove rows and store the amount removed
        for (BlockBattlePlayerState playerState : finalState.getPlayerStates()) {
            playerState.setRowsRemoved(playerState.getBoard().processEndOfRoundField());
            playerState.setFieldCleared(playerState.getBoard().isFieldCleared());
        }

        // handle everything that changes after the pieces have been placed
        for (BlockBattlePlayerState playerState : finalState.getPlayerStates()) {
            processPointsForPlayer(finalState, playerState);
            int roundsPerSolid = BlockBattleEngine.configuration.getInt("roundsPerSolid");

            if (finalState.getRoundNumber() % roundsPerSolid == 0) {
                int solids = BlockBattleEngine.configuration.getInt("solidsAmount");

                if (BlockBattleBoardOperations.addSolidRows(playerState.getBoard(), solids)) {
                    setWinner(playerState.getOpponentPlayerId());
                }
            }
        }

        return finalState;
    }

    private void processPointsForPlayer(BlockBattleState state, BlockBattlePlayerState playerState) {
        int pointsPerGarbage = BlockBattleEngine.configuration.getInt("pointsPerGarbage");
        int unusedRowPoints = playerState.getRowPoints() % pointsPerGarbage;
        int rowsRemoved = playerState.getRowsRemoved();
        int rowPoints;

        // calculate row points for this round
        if (playerState.getTSpin()) { // T-spin clears
            switch(rowsRemoved) {
                case 2:
                    rowPoints = BlockBattleEngine.configuration.getInt("doubleTScore");
                    playerState.setSkips(playerState.getSkips() + 1);
                    break;
                case 1:
                    rowPoints = BlockBattleEngine.configuration.getInt("singleTScore");
                    break;
                default:
                    rowPoints = 0;
                    break;
            }
        } else {
            switch(rowsRemoved) { // Normal clears
                case 4:
                    rowPoints = BlockBattleEngine.configuration.getInt("quadClearScore");
                    playerState.setSkips(playerState.getSkips() + 1);
                    break;
                case 3:
                    rowPoints = BlockBattleEngine.configuration.getInt("tripleClearScore");
                    break;
                case 2:
                    rowPoints = BlockBattleEngine.configuration.getInt("doubleClearScore");
                    break;
                case 1:
                    rowPoints = BlockBattleEngine.configuration.getInt("singleClearScore");
                    break;
                default:
                    rowPoints = 0;
                    break;
            }
        }

        // set new combo
        if (rowsRemoved > 1 || (rowsRemoved == 1 && playerState.getTSpin())) {
            rowPoints += playerState.getCombo(); // add combo points of previous round
            playerState.setCombo(playerState.getCombo() + 1);
        } else if (rowsRemoved < 1 && !playerState.getUsedSkip()) {
            playerState.setCombo(0);
        } else if (!playerState.getUsedSkip()) {
            rowPoints += playerState.getCombo(); // add combo points of previous round
        }

        // check if the whole field is cleared and reward points
        if (playerState.getFieldCleared()) {
            rowPoints = BlockBattleEngine.configuration.getInt("perfectClearScore");
        }

        playerState.addRowPoints(rowPoints);

        // add unused row points too from previous rounds
        rowPoints += unusedRowPoints;

        // calculate whether the first garbage line has single or double holes
        int totalNrLines = playerState.getRowPoints() / pointsPerGarbage;
        boolean firstIsSingle = false;
        if (totalNrLines % 2 == 0) {
            firstIsSingle = true;
        }

        // add the solid rows to opponent and check for gameover
        int nrGarbageRows = rowPoints / pointsPerGarbage;
        BlockBattlePlayerState opponentPlayerState = state.getPlayerStateById(
                playerState.getOpponentPlayerId());
        if (BlockBattleBoardOperations.addGarbageLines(
                opponentPlayerState.getBoard(), nrGarbageRows, firstIsSingle)) {
            setWinner(playerState.getPlayerId());
        }
    }

    /**
     * hasGameEnded should check all conditions on which a game should end
     */
    @Override
    public boolean hasGameEnded(BlockBattleState state) {
        int maxRounds = BlockBattleEngine.configuration.getInt("maxRounds");

        return (maxRounds > 0 && state.getRoundNumber() >= maxRounds) || this.gameOver;
    }

    @Override
    public Integer getWinnerId(BlockBattleState state) {
        return this.winnerId;
    }

    @Override
    public double getScore(BlockBattleState state) {
        return state.getRoundNumber();
    }

    @Override
    public AbstractMoveDeserializer createMoveDeserializer() {
        return new BlockBattleMoveDeserializer();
    }

    private void sendUpdates(BlockBattleState state, BlockBattlePlayer player) {
        BlockBattlePlayerState playerState = getActivePlayerState(state.getPlayerStates(), player.getId());

        // game updates
        player.sendUpdate("round", state.getRoundNumber());
        player.sendUpdate("this_piece_type", playerState.getCurrentShape().getType().toString());
        player.sendUpdate("next_piece_type", state.getNextShape().getType().toString());
        player.sendUpdate("this_piece_position", playerState.getCurrentShape().getPositionString());

        // player updates
        for (BlockBattlePlayer other : this.playerProvider.getPlayers()) {
            player.sendUpdate("row_points", other, playerState.getRowPoints());
            player.sendUpdate("combo", other, playerState.getCombo());
            player.sendUpdate("skips", other, playerState.getSkips());
            player.sendUpdate("field", other, playerState.getBoard().toString(false, false));
        }
    }

    private BlockBattlePlayerState getActivePlayerState(ArrayList<BlockBattlePlayerState> playerStates, int id) {
        for (BlockBattlePlayerState playerState : playerStates) {
            if (playerState.getPlayerId() == id) {
                return playerState;
            }
        }
        return null;
    }

    private void setWinner(int playerId) {
        this.gameOver = true;

        if (this.winnerId == null || this.winnerId == playerId) {
            this.winnerId = playerId;
        } else {
            this.winnerId = null;
        }
    }
}
