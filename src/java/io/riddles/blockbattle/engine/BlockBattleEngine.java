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

package io.riddles.blockbattle.engine;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.processor.BlockBattleProcessor;
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.engine.GameLoopInterface;
import io.riddles.javainterface.engine.SimpleGameLoop;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.blockbattle.game.BlockBattleSerializer;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.IOHandler;

import java.util.ArrayList;

/**
 * BlockBattleEngine:
 * - Creates a Processor, the Players and an initial State
 * - Parses the setup input
 * - Sends settings to the players
 * - Runs a game
 * - Returns the played game at the end of the game
 *
 * Created by joost on 6/27/16.
 */
public class BlockBattleEngine extends AbstractEngine<BlockBattleProcessor, BlockBattlePlayer, BlockBattleState> {

    public BlockBattleEngine(PlayerProvider<BlockBattlePlayer> playerProvider, IOHandler ioHandler) throws TerminalException {
        super(playerProvider, ioHandler);
    }

    /**
     *  createPlayer creates and initialises a Player for the game.
     * returns: a Player
     */
    @Override
    protected BlockBattlePlayer createPlayer(int id) {
        return new BlockBattlePlayer(id);
    }

    @Override
    protected Configuration getDefaultConfiguration() {
        Configuration configuration = new Configuration();
        configuration.put("maxRounds", -1);
        configuration.put("fieldWidth", 10);
        configuration.put("fieldHeight", 20);

        configuration.put("roundsPerSolid", 15);
        configuration.put("solidsAmount", 1);
        configuration.put("doubleTScore", 10);
        configuration.put("singleTScore", 5);
        configuration.put("quadClearScore", 10);
        configuration.put("tripleClearScore", 6);
        configuration.put("doubleClearScore", 3);
        configuration.put("singleClearScore", 0);
        configuration.put("perfectClearScore", 18);
        configuration.put("pointsPerGarbage", 3);
        return configuration;
    }


    /**
     * createProcessor creates and initialises a Processor for the game.
     * returns: a Processor
     */
    @Override
    protected BlockBattleProcessor createProcessor() {
        return new BlockBattleProcessor(this.playerProvider);
    }

    @Override
    protected GameLoopInterface createGameLoop() {
        return new SimpleGameLoop();
    }

    @Override
    protected void sendSettingsToPlayer(BlockBattlePlayer player) {
        player.sendSetting("field_height", configuration.getInt("fieldHeight"));
        player.sendSetting("field_width", configuration.getInt("fieldWidth"));

        if (configuration.getInt("maxRounds") > 0) {
            player.sendSetting("max_rounds", configuration.getInt("maxRounds"));
        }
    }

    /**
     * getPlayedGame creates a serializer and serialises the game
     * returns: String with the serialised game.
     */
    @Override
    protected String getPlayedGame(BlockBattleState state) {
        BlockBattleSerializer serializer = new BlockBattleSerializer();
        return serializer.traverseToString(this.processor, state);
    }

    /**
     * getInitialState creates an initial state to start the game with.
     * returns: BlockBattleState
     */
    @Override
    protected BlockBattleState getInitialState() {
        ArrayList<BlockBattlePlayerState> playerStates = new ArrayList<>();
        int fieldWidth = configuration.getInt("fieldWidth");
        int fieldHeight = configuration.getInt("fieldHeight");

        for (BlockBattlePlayer player : this.playerProvider.getPlayers()) {
            BlockBattlePlayerState playerState = new BlockBattlePlayerState(player.getId());
            BlockBattleBoard board = new BlockBattleBoard(fieldWidth, fieldHeight);

            playerState.setBoard(board);
            playerStates.add(playerState);
        }

        return new BlockBattleState(null, playerStates, 0);
    }
}
