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

package io.riddles.tictactoe

import io.riddles.blockbattle.engine.BlockBattleEngine
import io.riddles.blockbattle.game.data.Shape
import io.riddles.blockbattle.game.data.ShapeFactory
import io.riddles.blockbattle.game.data.ShapeType
import io.riddles.blockbattle.game.state.BlockBattleState
import io.riddles.javainterface.exception.TerminalException
import io.riddles.javainterface.io.IOHandler
import org.json.JSONObject
import spock.lang.Ignore
import spock.lang.Specification

/**
 *
 * [description]
 *
 * @author joost
 */

class BlockBattleEngineSpec extends Specification {

    public static class TestEngine extends BlockBattleEngine {
        protected BlockBattleState finalState = null
        protected ShapeFactory sf = new ShapeFactory();
        public String playedGame = "";

        TestEngine(IOHandler ioHandler) {
            super();
            this.ioHandler = ioHandler;
        }
        TestEngine(String wrapperFile, String[] botFiles) {
            super(wrapperFile, botFiles)
        }
        TestEngine(String wrapperFile, String[] botFiles, ShapeFactory sf) {
            super(wrapperFile, botFiles)
            this.sf = sf;
        }

        IOHandler getIOHandler() {
            return this.ioHandler;
        }

        @Override
        public void run() throws TerminalException, InterruptedException {
            LOGGER.info("Starting...");

            setup();

            if (this.processor == null) {
                throw new TerminalException("Processor has not been set");
            }

            LOGGER.info("Running pre-game phase...");

            this.processor.setShapeFactory(sf);
            this.processor.preGamePhase();


            LOGGER.info("Starting game loop...");

            BlockBattleState initialState = getInitialState();
            this.finalState = this.gameLoop.run(initialState, this.processor);

            playedGame = getPlayedGame(initialState);
            this.platformHandler.finish(playedGame);
        }
    }

    class ShapeFactoryValues extends ShapeFactory {
        String shapes = "L,I,I,T,S,O,O,T,T,Z,Z,L,T,I,Z,J,J,O,L,S,T,J,I,I,J";
        int shapeCounter = 0;

        public ShapeFactoryValues(String s) {
            this.shapes = s;
        }

        @Override
        public Shape getNext() {
            String[] shapes = shapes.split(",");
            Shape s = new Shape(ShapeType.valueOf(shapes[shapeCounter]));
            shapeCounter++;
            return s;
        }

        public void setShapes(String s) { this.shapes = s; }
    }


    @Ignore
    def "test if BlockBattleEngine is created"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs, new ShapeFactoryValues())
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        //engine.finalState.getBoard().toString() == "2,0,1,0,0,0,0,0,2,0,0,0,0,0,0,1,1,1,0,0,0,2,0,2,2,0,0,0,0,2,1,1,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,2,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,2,1,0,0,0,2,1,0,0";
    }

    @Ignore
    def "test t spin"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot_input_tspin.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        ShapeFactoryValues sf = new ShapeFactoryValues("O,O,S,O,O,Z,I,S,T,J,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O");
        def engine = new TestEngine(wrapperInput, botInputs, sf)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
    }


    @Ignore
    def "test garbage input"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot_input_garbage.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        ShapeFactoryValues sf = new ShapeFactoryValues("J,O,O,S,T,J,O,O,S,T,J,O,O,S,T,J,O,O,S,T,J,O,O,S,T,J,O,O,S,T,J,O,O,S,T");
        def engine = new TestEngine(wrapperInput, botInputs, sf)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        JSONObject j = new JSONObject(engine.playedGame);
        j.get("winner") == 2;
    }

    @Ignore
    def "test long game"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot_input_tspin.txt"
        botInputs[1] = "./test/resources/bot_input_tspin.txt"

        ShapeFactoryValues sf = new ShapeFactoryValues("O,O,S,O,O,Z,I,S,T,J,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O");
        def engine = new TestEngine(wrapperInput, botInputs, sf)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        JSONObject j = new JSONObject(engine.playedGame);
        j.get("winner") == JSONObject.NULL;
    }

    @Ignore
    def "test combo"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot_input_tspin.txt"
        botInputs[1] = "./test/resources/bot_input_combo.txt"

        ShapeFactoryValues sf = new ShapeFactoryValues("O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O,O");
        def engine = new TestEngine(wrapperInput, botInputs, sf)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        JSONObject j = new JSONObject(engine.playedGame);
        j.get("winner") == 1;
    }

    @Ignore
    def "test a game"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input_game.txt"
        botInputs[1] = "./test/resources/bot_input_combo.txt"

        def engine = new TestEngine(wrapperInput, botInputs, new ShapeFactory())
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        JSONObject j = new JSONObject(engine.playedGame);
        j.get("winner") == 2;
    }

    @Ignore
    def "test a draw game"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot1_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs, new ShapeFactory())
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        JSONObject j = new JSONObject(engine.playedGame);
        j.get("winner") == JSONObject.NULL;

    }
}