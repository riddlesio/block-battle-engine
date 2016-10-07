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

package io.riddles.blockbattle;

import io.riddles.blockbattle.engine.BlockBattleEngine;
import io.riddles.javainterface.exception.TerminalException;

/**
 * Entry point for BlockBattle. It creates an engine and runs it.
 * When an Exception escalates all the way through, the System exits with a status code.
 *
 * @author jim
 */
public class BlockBattle {

    public static void main(String[] args) throws Exception {
        BlockBattleEngine engine;

        engine = new BlockBattleEngine(args);

        engine.run();
    }
}