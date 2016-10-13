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
import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeOperations;
import io.riddles.blockbattle.game.data.ShapeType;
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

    /* Constructor */
    public BlockBattleProcessor(ArrayList<BlockBattlePlayer> players) {
        super(players);
        this.gameOver = false;
        this.logic = new BlockBattleLogic();
        this.shapeOps = new ShapeOperations();
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

        int playerCounter = 0;
        for (BlockBattlePlayer player : this.players) {
            if (!hasGameEnded(nextState)) {

                Shape shape = new Shape(getRandomShape());
                if(!shapeOps.spawnShape(shape, nextState.getBoard(player.getId()))) { /* Board is full! */
                    /* TODO: OPPONENT WINS */
                }
                player.setCurrentShape(shape);

                sendRoundUpdatesToPlayer(player, nextState);

                String response = player.requestMove(ActionType.MOVE.toString());

                // parse the response
                BlockBattleMoveDeserializer deserializer = new BlockBattleMoveDeserializer(player);
                ArrayList<BlockBattleMove> moves = deserializer.traverse(response);

                nextState = new BlockBattleState(nextState, moves, roundNumber);
                nextState.setMoveNumber(roundNumber*2 + playerCounter - 1);



                try {
                    logic.transform(nextState, moves);
                } catch (Exception e) {
                    LOGGER.info(String.format("Unknown response: %s", response));
                }

                // stop game if bot returns nothing
                if (response == null) {
                    this.gameOver = true;
                }

               // nextState.getBoard().dump();


                checkWinner(nextState);
                playerCounter++;
            }

        }
        return nextState;
    }

    /**
     * Get the next shape to be played randomly
     */
    private ShapeType getRandomShape() {
        return ShapeType.getRandom();
    }

    /**
     * Sends all updates the player needs at the start of the round.
     * @param player : player to send the updates to
     */
    private void sendRoundUpdatesToPlayer(BlockBattlePlayer player, BlockBattleState nextState) {

        // game updates
        player.sendUpdate("round", roundNumber);
        player.sendUpdate("this_piece_type", player.getCurrentShape().getType().toString());
        /* TODO */
        //player.sendUpdate("next_piece_type", player.getNextShape().getType().toString());
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
        if (this.roundNumber >= BlockBattleEngine.configuration.getInt("maxRounds")) returnVal = true;
        checkWinner(state);
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

    public void checkWinner(BlockBattleState s) {
        this.winner = null;
        /*
        int winner = s.getBoard().getWinner();
        if (winner > -1) {
            for (BlockBattlePlayer player : this.players) {
                if (player.getId() == winner) {
                    this.winner = player;
                }
            }
        }
        */
    }

    /* getScore should return the game score if applicable.
    *  returns: double Score
    *  */
    @Override
    public double getScore() {
        return 0;
    }

}
