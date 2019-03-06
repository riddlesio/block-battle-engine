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

package io.riddles.blockbattle.game;

import io.riddles.blockbattle.engine.BlockBattleEngine;
import io.riddles.blockbattle.game.processor.BlockBattleProcessor;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.blockbattle.game.state.BlockBattleStateSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.javainterface.game.AbstractGameSerializer;

/**
 * BlockBattleSerializer takes a BlockBattleState and serialises it and
 * all previous states into a JSON String.
 * Customize this to add all game specific data to the output.
 *
 * @author jim
 */
public class BlockBattleSerializer extends
        AbstractGameSerializer<BlockBattleProcessor, BlockBattleState, BlockBattleStateSerializer> {

    @Override
    public JSONObject visitGame(
            BlockBattleProcessor processor,
            BlockBattleState initialState,
            BlockBattleStateSerializer stateSerializer
    ) {
        JSONObject game = super.visitGame(processor, initialState, stateSerializer);

        JSONObject field = new JSONObject();
        field.put("width", BlockBattleEngine.configuration.getInt("fieldWidth"));
        field.put("height", BlockBattleEngine.configuration.getInt("fieldHeight"));

        game.getJSONObject("settings").put("field", field);

        return game;
    }
}
