package io.riddles.tictactoe

import io.riddles.blockbattle.game.BlockBattleSerializer
import io.riddles.blockbattle.game.data.MoveType
import io.riddles.blockbattle.game.move.BlockBattleMove
import io.riddles.blockbattle.game.move.BlockBattleMoveDeserializer
import io.riddles.blockbattle.game.player.BlockBattlePlayer
import io.riddles.blockbattle.game.processor.BlockBattleProcessor
import io.riddles.blockbattle.game.state.BlockBattleState
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

        BlockBattleEngineSpec.ShapeFactoryValues sf = new BlockBattleEngineSpec.ShapeFactoryValues("O,O,S,O,O,Z,I,S,T,J,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O");
        def engine = new BlockBattleEngineSpec.TestEngine(wrapperInput, botInputs, sf)

        engine.setup()

        BlockBattleSerializer serializer = new BlockBattleSerializer();
        BlockBattleProcessor processor = engine.getProcessor();
        BlockBattleState state = engine.getInitialState();
        engine.run()

        String result = serializer.traverseToString(processor, state);

        expect:
        result == '{"settings":{"field":{"width":16,"height":16}},"players":{"names":["player1","player2"],"winner":"2","count":2},"states":[{"round":0,"players":[{"isCrashed":false,"hasError":false,"position":"5,4","id":1},{"isCrashed":false,"hasError":false,"position":"11,4","id":2}]},{"round":1,"players":[{"isCrashed":false,"hasError":false,"position":"5,5","id":1},{"isCrashed":false,"hasError":false,"position":"10,4","id":2}]},{"round":2,"players":[{"isCrashed":false,"hasError":true,"position":"5,6","id":1,"error":"Invalid input: Move isn\'t valid"},{"isCrashed":false,"hasError":false,"position":"9,4","id":2}]},{"round":3,"players":[{"isCrashed":false,"hasError":false,"position":"5,7","id":1},{"isCrashed":false,"hasError":false,"position":"9,5","id":2}]},{"round":4,"players":[{"isCrashed":false,"hasError":false,"position":"6,7","id":1},{"isCrashed":false,"hasError":false,"position":"9,6","id":2}]},{"round":5,"players":[{"isCrashed":false,"hasError":false,"position":"7,7","id":1},{"isCrashed":false,"hasError":false,"position":"9,7","id":2}]},{"round":6,"players":[{"isCrashed":false,"hasError":false,"position":"8,7","id":1},{"isCrashed":false,"hasError":false,"position":"9,8","id":2}]},{"round":7,"players":[{"isCrashed":false,"hasError":false,"position":"8,6","id":1},{"isCrashed":false,"hasError":false,"position":"8,8","id":2}]},{"round":8,"players":[{"isCrashed":true,"hasError":false,"position":"8,6","id":1},{"isCrashed":false,"hasError":false,"position":"7,8","id":2}]}]}';

    }
}