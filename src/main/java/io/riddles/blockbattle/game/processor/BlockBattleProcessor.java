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
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.game.processor.AbstractProcessor;
import io.riddles.blockbattle.game.move.*;
import io.riddles.javainterface.io.PlayerResponse;

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

    private boolean gameOver;
    private BlockBattlePlayer winner;
    private BlockBattleLogic logic;
    private ShapeOperations shapeOps;
    private BlockBattleBoardOperations boardOps;
    public ShapeFactory shapeFactory;

    /* Constructor */
    public BlockBattleProcessor(PlayerProvider<BlockBattlePlayer> playerProvider) {
        super(playerProvider);
        this.logic = new BlockBattleLogic();
        this.shapeOps = new ShapeOperations();
        this.boardOps = new BlockBattleBoardOperations();
        this.shapeFactory = new ShapeFactory();
    }


    /**
     * Play one round of the game. It takes a BlockBattleState,
     * asks all living players for a response and delivers a new BlockBattleState.
     *
     * Return
     * the BlockBattleState that will be the state for the next round.
     * @param roundNumber The current round number
     * @param state The current BlockBattleState
     * @return The BlockBattleState that will be the start of the next round
     */
    @Override
    public BlockBattleState createNextStateFromResponse(BlockBattleState state, PlayerResponse input, int roundNumber) {

        /* Clone playerStates for next State */
        ArrayList<BlockBattlePlayerState> nextPlayerStates = clonePlayerStates(state.getPlayerStates());
        BlockBattleState nextState = new BlockBattleState(state, state.getPlayerStates(), roundNumber);




        if (!hasGameEnded(nextState)) {
            for (BlockBattlePlayer player : this.players) {
                player.setCurrentShape(nextState.getNextShape().clone());

                BlockBattleBoard board = nextState.getBoard(player.getId());
                //System.out.println("Playing roundNumber " + roundNumber + " with player " + player.getId());

                if(!shapeOps.spawnShape(player.getCurrentShape(), board)) { /* Board is full! */
                    setWinner(state, getOpponentPlayer(player));
                }
                sendRoundUpdatesToPlayer(player, nextState);

                String response = player.requestMove(ActionType.MOVES.toString());
                // parse the response
                BlockBattleMoveDeserializer deserializer = new BlockBattleMoveDeserializer(player);
                ArrayList<BlockBattleMove> moves = deserializer.traverse(response);

                nextState = new BlockBattleState(nextState, moves, roundNumber);

                try {
                    logic.transform(nextState, moves);
                } catch (Exception e) {
                    LOGGER.info(String.format("Unknown response: %s", response));
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


        /* Add final states when we are at maxRounds and there is no winner. */
        int maxRounds = BlockBattleEngine.configuration.getInt("maxRounds");
        if (roundNumber == maxRounds) {
            for (BlockBattlePlayer player : this.players) {

                ArrayList<BlockBattleMove> moves = new ArrayList<>();
                nextState = new BlockBattleState(nextState, moves, roundNumber + 1);

                moves.add(new BlockBattleMove(player, MoveType.DROP));
                try {
                    logic.transform(nextState, moves);
                } catch (Exception e) {
                    LOGGER.info("Something horrible went wrong.");
                }
            }
        }

        return nextState;
    }

    private ArrayList<BlockBattlePlayerState> clonePlayerStates(ArrayList<BlockBattlePlayerState> playerStates) {
        ArrayList<BlockBattlePlayerState> nextPlayerStates = new ArrayList<>();
        for (BlockBattlePlayerState playerState : playerStates) {
            BlockBattlePlayerState nextPlayerState = playerState.clone();
            nextPlayerStates.add(nextPlayerState);
        }
        return nextPlayerStates;
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

    /** hasGameEnded should check all conditions on which a game should end
    *  @return: boolean
    * */
    @Override
    public boolean hasGameEnded(BlockBattleState state) {
        boolean returnVal = false;
        if (this.roundNumber > BlockBattleEngine.configuration.getInt("maxRounds")) returnVal = true;
        if (this.winner != null) returnVal = true;
        if (this.gameOver) returnVal = true;
        return returnVal;
    }

    /** getWinner should check if there is a winner.
    *  @return: if there is a winner, the winning Player, otherwise return null.
    *  */
    @Override
    public BlockBattlePlayer getWinner() {
        return this.winner;
    }


    /** getScore should return the game score if applicable.
    *  @return: double Score
    *  */
    @Override
    public double getScore() {
        return this.roundNumber;
    }

    /** setWinner set the winner in this Processor, and in the state provided.
     *  @param: BlockbattleState
     *  @param: BlockBattlePlayer
     *  */
    public void setWinner(BlockBattleState state, BlockBattlePlayer winner) {
        if (this.winner == null) {
            this.winner = winner;
            state.setWinner(winner);
        } else {
            this.winner = null;
            state.setWinner(null);
        }
        this.gameOver = true;
    }

    /** setShapeFactory is used to set a non-random ShapeFactory for testing purposes.
     * @param ShapeFactory to set.
    *  */
    public void setShapeFactory(ShapeFactory s) { this.shapeFactory = s; }
}
