package io.riddles.blockbattle.engine;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeFactory;
import io.riddles.blockbattle.game.data.ShapeType;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.processor.BlockBattleLogic;
import io.riddles.blockbattle.game.processor.BlockBattleProcessor;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.blockbattle.game.BlockBattleSerializer;
import io.riddles.javainterface.engine.AbstractEngine;

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

    public BlockBattleEngine() throws TerminalException {

        super(new String[0]);
        setDefaults();
    }

    public BlockBattleEngine(String args[]) throws TerminalException {
        super(args);
        setDefaults();
    }

    public BlockBattleEngine(String wrapperFile, String[] botFiles) throws TerminalException {
        super(wrapperFile, botFiles);
        setDefaults();
    }

    private void setDefaults() {
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


    }


    /* createPlayer creates and initialises a Player for the game.
     * returns: a Player
     */
    @Override
    protected BlockBattlePlayer createPlayer(int id) {
        BlockBattlePlayer p = new BlockBattlePlayer(id);
        return p;
    }

    /* createProcessor creates and initialises a Processor for the game.
     * returns: a Processor
     */
    @Override
    protected BlockBattleProcessor createProcessor() {

        /* We're going for one-based indexes for playerId's so we can use 0's for empty fields.
         * This makes sure existing bots will still work with TicTacToe, when used with the new wrapper.
         */
        for (BlockBattlePlayer player : this.players) {
            player.setId(player.getId() + 1);
            player.setName("player" + player.getId());
        }
        return new BlockBattleProcessor(this.players);
    }

    /* sendGameSettings sends the game settings to a Player
     * returns:
     */
    @Override
    protected void sendGameSettings(BlockBattlePlayer player) {
        String playerNames = "";
        for(BlockBattlePlayer p : this.players) {
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
        BlockBattleState state = new BlockBattleState();
        for (BlockBattlePlayer player : this.players) {
            BlockBattleBoard board = new BlockBattleBoard(configuration.getInt("fieldWidth"), (configuration.getInt("fieldHeight")));
            board.setPlayerId(player.getId());
            state.setBoard(board);
            state.setPlayer(player);
        }
        state.setNextShape(processor.shapeFactory.getNext());
        return state;
    }
}
