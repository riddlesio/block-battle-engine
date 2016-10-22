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
import spock.lang.Ignore
import spock.lang.Specification

/**
 *
 * [description]
 *
 * @author joost
 */

class BlockBattleEngineSpec extends Specification {

    class TestEngine extends BlockBattleEngine {
        protected BlockBattleState finalState = null

        TestEngine(IOHandler ioHandler) {
            super();
            this.ioHandler = ioHandler;
        }

        TestEngine(String wrapperFile, String[] botFiles) {
            super(wrapperFile, botFiles)

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

            this.processor.setShapeFactory(new ShapeFactoryValues());
            this.processor.preGamePhase();


            LOGGER.info("Starting game loop...");

            BlockBattleState initialState = getInitialState();
            this.finalState = this.gameLoop.run(initialState, this.processor);

            String playedGame = getPlayedGame(initialState);
            this.platformHandler.finish(playedGame);
        }
    }

    class ShapeFactoryValues extends ShapeFactory {
        String shapes = "O,I,I,O,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L,L";
        int shapeCounter = 0;
        @Override
        public Shape getNext() {
            String[] shapes = shapes.split(",");
            Shape s = new Shape(ShapeType.valueOf(shapes[shapeCounter]));
            shapeCounter++;
            return s;
        }

        public void setShapes(String s) { this.shapes = s; }
    }


    //@Ignore
    def "test if BlockBattleEngine is created"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
        engine.finalState.getBoard().toString() == "2,0,1,0,0,0,0,0,2,0,0,0,0,0,0,1,1,1,0,0,0,2,0,2,2,0,0,0,0,2,1,1,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,2,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,2,1,0,0,0,2,1,0,0";
    }

    @Ignore
    def "test illegal moves"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot_input_illegal.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
    }

    @Ignore
    def "test out of bounds"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot_input_outofbounds.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
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
        botInputs[1] = "./test/resources/bot_input_garbage.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.run()

        expect:
        engine.finalState instanceof BlockBattleState;
    }
}