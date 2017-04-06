package io.riddles.blockbattle.engine;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeFactory;
import io.riddles.blockbattle.game.data.ShapeType;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.processor.BlockBattleLogic;
import io.riddles.blockbattle.game.processor.BlockBattleProcessor;
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.engine.GameLoopInterface;
import io.riddles.javainterface.engine.SimpleGameLoop;
import io.riddles.javainterface.engine.TurnBasedGameLoop;
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

    /* createPlayer creates and initialises a Player for the game.
 * returns: a Player
 */
    @Override
    protected BlockBattlePlayer createPlayer(int id) {
        BlockBattlePlayer p = new BlockBattlePlayer(id);
        return p;
    }

    @Override
    protected Configuration getDefaultConfiguration() {
        Configuration configuration = new Configuration();
        configuration.put("maxRounds", 200); /* Note: in the previous version of Block Battle, maxRounds was set to -1 */
        configuration.put("fieldWidth", 10);
        configuration.put("fieldHeight", 20);

        configuration.put("roundsPerSolid", 15);
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


    /* createProcessor creates and initialises a Processor for the game.
     * returns: a Processor
     */
    @Override
    protected BlockBattleProcessor createProcessor() {

        return new BlockBattleProcessor(playerProvider);
    }

    @Override
    protected GameLoopInterface createGameLoop() {
        return new SimpleGameLoop();
    }

    @Override
    protected void sendSettingsToPlayer(BlockBattlePlayer player) {
        String playerNames = "";
        for(BlockBattlePlayer p : playerProvider.getPlayers()) {
            playerNames += p.getName() + ",";
        }
        playerNames = playerNames.substring(0, playerNames.length()-1);

        player.sendSetting("field_height", configuration.getInt("fieldHeight"));
        player.sendSetting("field_width", configuration.getInt("fieldWidth"));
        player.sendSetting("max_rounds", configuration.getInt("maxRounds"));
        player.sendSetting("player_names", playerNames);
        player.sendSetting("your_bot", player.getName());
    }


    /* getPlayedGame creates a serializer and serialises the game
     * returns: String with the serialised game.
     */
    @Override
    protected String getPlayedGame(BlockBattleState state) {
        BlockBattleSerializer serializer = new BlockBattleSerializer();
        return serializer.traverseToString(this.processor, state);
    }

    /* getInitialState creates an initial state to start the game with.
     * returns: BlockBattleState
     */
    @Override
    protected BlockBattleState getInitialState() {
        ArrayList<BlockBattlePlayerState> playerStates = new ArrayList<>();

        for (BlockBattlePlayer player : playerProvider.getPlayers()) {
            BlockBattlePlayerState playerState = new BlockBattlePlayerState(player.getId());
            BlockBattleBoard board = new BlockBattleBoard(configuration.getInt("fieldWidth"), (configuration.getInt("fieldHeight")));
            playerState.setBoard(board);
            playerState.setPlayerId(playerProvider.getPlayers().get(0).getId());
            playerStates.add(playerState);


        }
        BlockBattleState state = new BlockBattleState(null, playerStates, 0);

        state.setNextShape(processor.shapeFactory.getNext());

        return state;
    }
}
