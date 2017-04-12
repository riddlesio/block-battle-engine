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
import io.riddles.javainterface.exception.InvalidMoveException;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.blockbattle.game.move.*;
import io.riddles.javainterface.game.processor.SimpleProcessor;

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
public class BlockBattleProcessor extends SimpleProcessor<BlockBattleState, BlockBattlePlayer> {

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
    public BlockBattleState createNextState(BlockBattleState state, int roundNumber) {

        ArrayList<BlockBattlePlayer> players = this.playerProvider.getPlayers();
        sendUpdates(state, players.get(0));
        sendUpdates(state, players.get(1));


        BlockBattleMoveDeserializer deserializer = new BlockBattleMoveDeserializer();
        String responseP1 = players.get(0).requestMove(ActionType.MOVE);
        String responseP2 = players.get(1).requestMove(ActionType.MOVE);

        BlockBattleMove moveP1 = deserializer.traverse(responseP1);
        BlockBattleMove moveP2 = deserializer.traverse(responseP2);


        state.setRoundNumber(roundNumber);

        while (moveP1.getMoveTypes().size() > 0 || moveP2.getMoveTypes().size() > 0) {
            state = createState(state, moveP1, moveP2);
        }
        logic.finishMoveSet(state.getPlayerStateById(0));
        logic.finishMoveSet(state.getPlayerStateById(1));



        BlockBattlePlayerState playerStateP1 = getActivePlayerState(state.getPlayerStates(), 0);
        BlockBattlePlayerState playerStateP2 = getActivePlayerState(state.getPlayerStates(), 1);
        BlockBattleBoard boardP1 = playerStateP1.getBoard();
        BlockBattleBoard boardP2 = playerStateP2.getBoard();

        // add solid line on certain round number
        if (state.getRoundNumber() % BlockBattleEngine.configuration.getInt("roundsPerSolid") == 0) {
            if (boardOps.addSolidRows(boardP1, 1)) {
                setWinnerId(state, playerStateP2.getPlayerId());
            }
            if (boardOps.addSolidRows(boardP2, 1)) {
                setWinnerId(state, playerStateP1.getPlayerId());
            }
        }

        int rowsRemovedP1 = boardP1.processEndOfRoundField();
        playerStateP1.setRowsRemoved(rowsRemovedP1);
        playerStateP1.setFieldCleared(boardP1.isFieldCleared());

        int rowsRemovedP2 = boardP2.processEndOfRoundField();
        playerStateP2.setRowsRemoved(rowsRemovedP2);
        playerStateP2.setFieldCleared(boardP2.isFieldCleared());

        processPointsForPlayer(state, playerStateP1);
        processPointsForPlayer(state, playerStateP2);


        /* Add final states when we are at maxRounds and there is no winner. */
        /*
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
        */


        Shape nextShape = state.getNextShape();
        playerStateP1.setCurrentShape(nextShape.clone());
        playerStateP2.setCurrentShape(nextShape.clone());
        if (!shapeOps.spawnShape(playerStateP1.getCurrentShape(), boardP2)) {
            setWinnerId(state, 0);
        }
        if (!shapeOps.spawnShape(playerStateP2.getCurrentShape(), boardP1)) {
            setWinnerId(state, 1);
        }
        //boardP1.dump();
        //boardP2.dump();

        state.setNextShape(shapeFactory.getNext());
        return state;
    }

    private BlockBattleState createState(BlockBattleState state, BlockBattleMove moveP1, BlockBattleMove moveP2) {

        ArrayList<BlockBattlePlayerState> nextPlayerStates = clonePlayerStates(state.getPlayerStates());
        BlockBattleState nextState = new BlockBattleState(state, state.getPlayerStates(), state.getRoundNumber(), state.getMoveNumber()+1);


        MoveType moveTypeP1 = null, moveTypeP2 = null;
        if (moveP1.getMoveTypes().size() > 0)
            moveTypeP1 = moveP1.getMoveTypes().remove(0);
        if (moveP2.getMoveTypes().size() > 0)
            moveTypeP2 = moveP2.getMoveTypes().remove(0);

        BlockBattlePlayerState playerStateP1 = getActivePlayerState(nextPlayerStates, 0);
        BlockBattlePlayerState playerStateP2 = getActivePlayerState(nextPlayerStates, 1);
        if (moveTypeP1 != null)
            playerStateP1.setCurrentMove(moveTypeP1.toString());
        if (moveTypeP2 != null)
            playerStateP2.setCurrentMove(moveTypeP2.toString());


        try {
            logic.transform(nextState, playerStateP1, moveTypeP1);
        } catch (Exception e) {
            moveP1.setException(new InvalidMoveException("Error transforming move."));
            playerStateP1.setMove(moveP1);
        }
        try {
            logic.transform(nextState, playerStateP2, moveTypeP2);
        } catch (Exception e) {
            moveP2.setException(new InvalidMoveException("Error transforming move."));
            playerStateP2.setMove(moveP2);
        }

        nextState.setPlayerstates(nextPlayerStates);

        return nextState;
    }






    private ArrayList<BlockBattlePlayerState> clonePlayerStates(ArrayList<BlockBattlePlayerState> playerStates) {
        ArrayList<BlockBattlePlayerState> nextPlayerStates = new ArrayList<>();
        for (BlockBattlePlayerState playerState : playerStates) {
            BlockBattlePlayerState nextPlayerState = new BlockBattlePlayerState(playerState);
            nextPlayerStates.add(nextPlayerState);
        }
        return nextPlayerStates;
    }



    private void processPointsForPlayer(BlockBattleState state, BlockBattlePlayerState playerState) {
        int unusedRowPoints = playerState.getRowPoints() % BlockBattleEngine.configuration.getInt("pointsPerGarbage");
        int rowsRemoved = playerState.getRowsRemoved();

        // calculate row points for this round
        int rowPoints;
        if(playerState.getTSpin()) { // T-spin clears
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
        }
        else {
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
        if(rowsRemoved > 1 || (rowsRemoved == 1 && playerState.getTSpin())) {
            rowPoints += playerState.getCombo(); // add combo points of previous round
            playerState.setCombo(playerState.getCombo() + 1);
        } else if(rowsRemoved < 1 && !playerState.getUsedSkip()) {
            playerState.setCombo(0);
        } else if (!playerState.getUsedSkip()) {
            rowPoints += playerState.getCombo(); // add combo points of previous round
        }

        // check if the whole field is cleared and reward points
        if(playerState.getFieldCleared())
            rowPoints = BlockBattleEngine.configuration.getInt("perfectClearScore");

        playerState.addRowPoints(rowPoints);

        // add unused row points too from previous rounds
        rowPoints += unusedRowPoints;

        // calculate whether the first garbage line has single or double holes
        int totalNrLines = playerState.getRowPoints() / BlockBattleEngine.configuration.getInt("pointsPerGarbage");
        boolean firstIsSingle = false;
        if (totalNrLines % 2 == 0) {
            firstIsSingle = true;
        }

        // add the solid rows to opponent and check for gameover
        int nrGarbageRows = (rowPoints / BlockBattleEngine.configuration.getInt("pointsPerGarbage"));
        BlockBattlePlayerState opponentPlayerState = getOpponentPlayerState(state, playerState);
        if(boardOps.addGarbageLines(opponentPlayerState.getBoard(), nrGarbageRows, firstIsSingle)) {
            setWinnerId(state, playerState.getPlayerId());
        }
    }


    /**
     * When players's size = 2, it will find the other playerstate than 'p'.
     * If player's size != 2, it's outcome is null.
     *
     * @param state
     * @return the opposite playerState or null.
     */
    private BlockBattlePlayerState getOpponentPlayerState(BlockBattleState state, BlockBattlePlayerState p) {
        if (state.getPlayerStates().size() == 2) {
            for (BlockBattlePlayerState ps : state.getPlayerStates()) {
                if (ps.getPlayerId() != p.getPlayerId()) return ps;
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
        if (state.getRoundNumber() > BlockBattleEngine.configuration.getInt("maxRounds")) returnVal = true;
        if (state.getWinnerId() != null) returnVal = true;
        return returnVal;
    }

    @Override
    public Integer getWinnerId(BlockBattleState blockBattleState) {
        return blockBattleState.getWinnerId();
    }

    @Override
    public double getScore(BlockBattleState state) {
        return state.getRoundNumber();
    }

    /** setShapeFactory is used to set a non-random ShapeFactory for testing purposes.
     * @param s, ShapeFactory to set.
    *  */
    public void setShapeFactory(ShapeFactory s) { this.shapeFactory = s; }

    public void sendUpdates(BlockBattleState state, BlockBattlePlayer player) {
        player.sendUpdate("round", state.getRoundNumber());
        BlockBattlePlayerState playerState = getActivePlayerState(state.getPlayerStates(), player.getId());

        player.sendUpdate("this_piece_type", playerState.getCurrentShape().getType().toString());
        player.sendUpdate("next_piece_type", state.getNextShape().getType().toString());
        player.sendUpdate("this_piece_position", playerState.getCurrentShape().getPositionString());

        player.sendUpdate("row_points", player, playerState.getRowPoints());
        player.sendUpdate("combo", player, playerState.getCombo());
        player.sendUpdate("skips", player, playerState.getSkips());

        player.sendUpdate("field", player, playerState.getBoard().toString(false, false));
    }

    private BlockBattlePlayerState getActivePlayerState(ArrayList<BlockBattlePlayerState> playerStates, int id) {
        for (BlockBattlePlayerState playerState : playerStates) {
            if (playerState.getPlayerId() == id) { return playerState; }
        }
        return null;
    }

    private void setWinnerId(BlockBattleState state, int id) {
        state.setWinnerId(id);
    }

}
