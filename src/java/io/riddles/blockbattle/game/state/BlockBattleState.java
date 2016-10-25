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

package io.riddles.blockbattle.game.state;

import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.javainterface.game.state.AbstractState;
import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.move.BlockBattleMove;


import java.util.ArrayList;

/**
 * io.riddles.game.game.state.BlockBattleState - Created on 2-6-16
 *
 * BlockBattleState extends AbstractState and is used to store game specific data per state.
 * It can be initialised to store a BlockBattleMove, or multiple BlockBattleMoves in an ArrayList.
 *
 * @author joost
 */
public class BlockBattleState extends AbstractState<BlockBattleMove> {

    private BlockBattleMove move;

    private int round;

    private ArrayList<BlockBattleMove> roundMoves;

    private Shape nextShape;

    private ArrayList<BlockBattleBoard> boards;
    private ArrayList<BlockBattlePlayer> players;

    private BlockBattlePlayer winner;


    public BlockBattleState() {
        super();
        this.boards = new ArrayList<BlockBattleBoard>();
        this.players = new ArrayList<BlockBattlePlayer>();

    }

    public BlockBattleState(BlockBattleState previousState, BlockBattleMove move, int roundNumber) {
        super(previousState, move, roundNumber);
        this.boards = new ArrayList<BlockBattleBoard>();
        this.players = new ArrayList<BlockBattlePlayer>();
        for (BlockBattleBoard board : previousState.getBoards()) {
            BlockBattleBoard newBoard = new BlockBattleBoard(board);
            this.boards.add(newBoard);
        }
        for (BlockBattlePlayer player : previousState.getPlayers()) {
            BlockBattlePlayer newPlayer = new BlockBattlePlayer(player);
            this.players.add(newPlayer);
        }
        this.nextShape = previousState.getNextShape().clone();
    }

    public BlockBattleState(BlockBattleState previousState, ArrayList<BlockBattleMove> moves, int roundNumber) {
        super(previousState, moves, roundNumber);
        this.boards = new ArrayList<BlockBattleBoard>();
        this.players = new ArrayList<BlockBattlePlayer>();

        for (BlockBattleBoard board : previousState.getBoards()) {
            BlockBattleBoard newBoard = new BlockBattleBoard(board);
            this.boards.add(newBoard);
        }
        for (BlockBattlePlayer player : previousState.getPlayers()) {
            BlockBattlePlayer newPlayer = new BlockBattlePlayer(player);
            this.players.add(newPlayer);
        }
        this.nextShape = previousState.getNextShape().clone();
    }

    public ArrayList<BlockBattleBoard> getBoards() { return this.boards; }
    public ArrayList<BlockBattlePlayer> getPlayers() { return this.players; }

    public BlockBattleBoard getBoard(int playerId) {
        for (BlockBattleBoard board : this.boards) {
            if (board.getPlayerId() == playerId) return board;
        }
        return null;
    }

    public void setBoard(BlockBattleBoard b) {
        this.boards.add(b);
    }

    public BlockBattlePlayer getPlayer(int playerId) {
        for (BlockBattlePlayer player : this.players) {
            if (player.getId() == playerId) return player;
        }
        return null;
    }

    /* Makes a clone of BlockBattlePlayer, and stores it so it's data can be retrieved later */
    /* If player with id already exists, it is overwritten. */
    public void setPlayer(BlockBattlePlayer player) {
        BlockBattlePlayer playerClone = new BlockBattlePlayer(player);
        /* If player already exists, remove it first to prevent duplicates */
        if (getPlayer(player.getId()) != null) {
            this.players.remove(getPlayer(player.getId()));
        }
        this.players.add(playerClone);
    }

    public Shape getNextShape() { return this.nextShape; }
    public void setNextShape(Shape nextShape) { this.nextShape = nextShape; }

    public BlockBattlePlayer getWinner() { return this.winner; }
    public void setWinner(BlockBattlePlayer winner) { this.winner = winner; }


}
