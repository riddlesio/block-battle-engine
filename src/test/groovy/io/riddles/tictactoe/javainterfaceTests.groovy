package io.riddles.tictactoe

import io.riddles.blockbattle.game.move.MoveType
import io.riddles.blockbattle.game.move.BlockBattleMove
import io.riddles.blockbattle.game.move.BlockBattleMoveDeserializer
import io.riddles.blockbattle.game.player.BlockBattlePlayer
import org.json.JSONArray
import org.json.JSONObject
import spock.lang.Ignore
import spock.lang.Specification

class javainterfaceTests extends Specification {


    @Ignore
    def "MoveDeserializer must return one of ArrayList<BlockBattleMove> when receiving valid input"() {
        /* Test is disabled, test with ArrayList of BlockBattleMoves must be written */
        println("MoveDeserializer")

        given:
        BlockBattlePlayer player = new BlockBattlePlayer(1);
        BlockBattleMoveDeserializer deserializer = new BlockBattleMoveDeserializer(player);


        expect:
        BlockBattleMove move = deserializer.traverse(input);
        result == move.getMoveType();


        where:
        input   | result
        "skip"      | MoveType.SKIP
        "down"      | MoveType.DOWN
        "right"     | MoveType.RIGHT
        "left"      | MoveType.LEFT
        "drop"      | MoveType.DROP
    }


    @Ignore
    def "Test JSON addDefaultJSON output"() {
        println("addDefaultJSON")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new BlockBattleEngineSpec.TestEngine(wrapperInput, botInputs)
        engine.run()


        expect:
        JSONObject j = new JSONObject(engine.playedGame);
        j.get("settings") instanceof JSONObject;
        j.get("settings").get('players') instanceof JSONObject;
        j.get("settings").get('players').get('names') instanceof JSONArray;
        j.get("settings").get('players').get('count') instanceof Integer;
        j.get("score") instanceof Integer;
        if (j.get("winner") != null) {
            j.get("winner") instanceof Integer;
        }
    }
}