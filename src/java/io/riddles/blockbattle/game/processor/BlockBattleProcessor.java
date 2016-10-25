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

import java.util.ArrayList;

import io.riddles.blockbattle.engine.BlockBattleEngine;
import io.riddles.blockbattle.game.data.*;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.game.processor.AbstractProcessor;
import io.riddles.blockbattle.game.move.*;

/**
 * This file is a part of BlockBattle
 *
 * Copyright 2016 - present Riddles.io
 * For license information see the LICENSE file in the project root
 *
 * BlockBattleProcessor shall process a State and return the result.
 *
 * @author joost
 */
public class BlockBattleProcessor extends AbstractProcessor<BlockBattlePlayer, BlockBattleState> {

    private int roundNumber;
    private boolean gameOver;
    private BlockBattlePlayer winner;
    private BlockBattleLogic logic;
    private ShapeOperations shapeOps;
    private BlockBattleBoardOperations boardOps;
    public ShapeFactory shapeFactory;

    /* Constructor */
    public BlockBattleProcessor(ArrayList<BlockBattlePlayer> players) {
        super(players);
        this.gameOver = false;
        this.logic = new BlockBattleLogic();
        this.shapeOps = new ShapeOperations();
        this.boardOps = new BlockBattleBoardOperations();
        this.shapeFactory = new ShapeFactory();
    }

    /* preGamePhase may be used to set up the Processor before starting the game loop.
    * */
    @Override
    public void preGamePhase() {

    }

    /**
     * Play one round of the game. It takes a BlockBattleState,
     * asks all living players for a response and delivers a new BlockBattleState.
     *
     * Return
     * the BlockBattleState that will be the state for the next round.
     * @param roundNumber The current round number
     * @param BlockBattleState The current state
     * @return The BlockBattleState that will be the start of the next round
     */
    @Override
    public BlockBattleState playRound(int roundNumber, BlockBattleState state) {
        LOGGER.info(String.format("Playing round %d", roundNumber));
        this.roundNumber = roundNumber;

        BlockBattleState nextState = state;

        for (BlockBattlePlayer player : this.players) {
            if (!hasGameEnded(nextState)) {
                player.setCurrentShape(nextState.getNextShape().clone());

                BlockBattleBoard board = nextState.getBoard(player.getId());
                System.out.println("Playing roundNumber " + roundNumber + " with player " + player.getId());

                if(!shapeOps.spawnShape(player.getCurrentShape(), board)) { /* Board is full! */
                    setWinner(state, getOpponentPlayer(player));
                }
                sendRoundUpdatesToPlayer(player, nextState);

                String response = player.requestMove(ActionType.MOVE.toString());
                // parse the response
                BlockBattleMoveDeserializer deserializer = new BlockBattleMoveDeserializer(player);
                ArrayList<BlockBattleMove> moves = deserializer.traverse(response);

                nextState = new BlockBattleState(nextState, moves, roundNumber);
                board = nextState.getBoard(player.getId());

                try {
                    logic.transform(nextState, moves);
                } catch (Exception e) {
                    LOGGER.info(String.format("Unknown response: %s", response));
                }

                // stop game if bot returns nothing
                if (response == null) {
                    this.gameOver = true;
                }
                if (player.getId() == 1) {
                    board.dump();
                }
            }
        }

        // remove rows and store the amount removed
        for (BlockBattlePlayer player : this.players) {
            BlockBattleBoard board = nextState.getBoard(player.getId());
            player.setRowsRemoved(board.processEndOfRoundField());
            player.setFieldCleared(board.isFieldCleared());
        }

        // handle everything that changes after the pieces have been placed
        for (BlockBattlePlayer player : this.players) {

            processPointsForPlayer(nextState, player);

            if (this.roundNumber % BlockBattleEngine.configuration.getInt("roundsPerSolid") == 0) // add solid line on certain round number
                if (boardOps.addSolidRows(nextState.getBoard(player.getId()), 1)) {
                    // set winner if player is out of bounds
                    setWinner(state, getOpponentPlayer(player));
            }
            nextState.setPlayer(player);

        }
        nextState.setNextShape(shapeFactory.getNext());
        return nextState;
    }


    /**
     * Sends all updates the player needs at the start of the round.
     * @param player : player to send the updates to
     */
    private void sendRoundUpdatesToPlayer(BlockBattlePlayer player, BlockBattleState nextState) {

        // game updates
        player.sendUpdate("round", roundNumber);
        player.sendUpdate("this_piece_type", player.getCurrentShape().getType().toString());

        player.sendUpdate("next_piece_type", nextState.getNextShape().getType().toString());
        player.sendUpdate("this_piece_position", player.getCurrentShape().getPositionString());

        // player updates
        player.sendUpdate("row_points", player, player.getRowPoints());
        player.sendUpdate("combo", player, player.getCombo());
        player.sendUpdate("skips", player, player.getSkips());
        player.sendUpdate("field", player, nextState.getBoard(player.getId()).toString(false, false));

        // opponent updates
        BlockBattlePlayer opponent = getOpponentPlayer(player);
        player.sendUpdate("field", opponent, nextState.getBoard(player.getId()).toString(false, false));
        player.sendUpdate("row_points", opponent, opponent.getRowPoints());
        player.sendUpdate("combo", opponent, opponent.getCombo());
        player.sendUpdate("skips", opponent, opponent.getSkips());
    }

    private void processPointsForPlayer(BlockBattleState state, BlockBattlePlayer player) {
        int unusedRowPoints = player.getRowPoints() % BlockBattleEngine.configuration.getInt("pointsPerGarbage");
        int rowsRemoved = player.getRowsRemoved();

        // calculate row points for this round
        int rowPoints;
        if(player.getTSpin()) { // T-spin clears
            switch(rowsRemoved) {
                case 2:
                    rowPoints = BlockBattleEngine.configuration.getInt("doubleTScore");
                    player.setSkips(player.getSkips() + 1);
                    break;
                case 1:
                    rowPoints = BlockBattleEngine.configuration.getInt("singleTScore");
                    break;
                default:
                    rowPoints = 0;
                    break;
            }
        }
        else {
            switch(rowsRemoved) { // Normal clears
                case 4:
                    rowPoints = BlockBattleEngine.configuration.getInt("quadClearScore");
                    player.setSkips(player.getSkips() + 1);
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
        if(rowsRemoved > 1 || (rowsRemoved == 1 && player.getTSpin())) {
            rowPoints += player.getCombo(); // add combo points of previous round
            player.setCombo(player.getCombo() + 1);
        } else if(rowsRemoved < 1 && !player.getUsedSkip()) {
            player.setCombo(0);
        } else if (!player.getUsedSkip()) {
            rowPoints += player.getCombo(); // add combo points of previous round
        }

        // check if the whole field is cleared and reward points
        if(player.getFieldCleared())
            rowPoints = BlockBattleEngine.configuration.getInt("perfectClearScore");

        player.addRowPoints(rowPoints);

        // add unused row points too from previous rounds
        rowPoints += unusedRowPoints;

        // calculate whether the first garbage line has single or double holes
        int totalNrLines = player.getRowPoints() / BlockBattleEngine.configuration.getInt("pointsPerGarbage");
        boolean firstIsSingle = false;
        if (totalNrLines % 2 == 0) {
            firstIsSingle = true;
        }

        // add the solid rows to opponent and check for gameover
        int nrGarbageRows = (rowPoints / BlockBattleEngine.configuration.getInt("pointsPerGarbage"));
        if(boardOps.addGarbageLines(state.getBoard(getOpponentPlayer(player).getId()), nrGarbageRows, firstIsSingle)) {
            setWinner(state, player);
        }
    }

    /**
     * When this.players's size = 2, it will find the other player than 'p'.
     * If this.player's size != 2, it's outcome is -1.
     *
     * @param BlockBattlePlayer The player to find the opponent for.
     * @return The id of the opponent player.
     */
    private int getNextPlayerId(BlockBattlePlayer p) {
        if (this.players.size() == 2) {
            for (BlockBattlePlayer player : this.players) {
                if (player.getId() != p.getId()) return player.getId();
            }
        }
        return -1;
    }

    /**
     * When this.players's size = 2, it will find the other player than 'p'.
     * If this.player's size != 2, it's outcome is -1.
     *
     * @param BlockBattlePlayer The player to find the opponent for.
     * @return The BlockBattlePlayer of the opponent player, or null if no opponent can be determined.
     */
    private BlockBattlePlayer getOpponentPlayer(BlockBattlePlayer p) {
        if (this.players.size() == 2) {
            for (BlockBattlePlayer player : this.players) {
                if (player.getId() != p.getId()) return player;
            }
        }
        return null;
    }

    /* hasGameEnded should check all conditions on which a game should end
    *  returns: boolean
    * */
    @Override
    public boolean hasGameEnded(BlockBattleState state) {
        boolean returnVal = false;
        if (this.roundNumber > BlockBattleEngine.configuration.getInt("maxRounds")) returnVal = true;
        if (this.winner != null) returnVal = true;
        return returnVal;
    }

    /* getWinner should check if there is a winner.
    *  returns: if there is a winner, the winning Player, otherwise return null.
    *  */
    @Override
    public BlockBattlePlayer getWinner() {
        return this.winner;
    }


    /* getScore should return the game score if applicable.
    *  returns: double Score
    *  */
    @Override
    public double getScore() {
        return 0;
    }

    public void setWinner(BlockBattleState state, BlockBattlePlayer winner) {
        this.winner = winner;
        state.setWinner(winner);
        System.out.println( "we have a winner");
    }

    public void setShapeFactory(ShapeFactory s) { this.shapeFactory = s; }
}
