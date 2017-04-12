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

import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import org.json.JSONArray;
import org.json.JSONObject;
import io.riddles.javainterface.game.state.AbstractStateSerializer;

/**
 * BlockBattleStateSerializer takes a BlockBattleState and serialises it into a String.
 *
 * @author jim
 */
public class BlockBattleStateSerializer extends AbstractStateSerializer<BlockBattleState> {

    @Override
    public String traverseToString(BlockBattleState state) {
        return visitState(state).toString();
    }

    @Override
    public JSONObject traverseToJson(BlockBattleState state) throws NullPointerException {
        return visitState(state);
    }

    private JSONObject visitState(BlockBattleState state) throws NullPointerException {
        JSONObject stateJson = new JSONObject();

        JSONArray playersJson = new JSONArray();

        JSONObject player1Json = new JSONObject();
        JSONObject player2Json = new JSONObject();

        BlockBattlePlayerState playerStateP0 = state.getPlayerStateById(0);
        BlockBattlePlayerState playerStateP1 = state.getPlayerStateById(1);



        player1Json.put("move", playerStateP0.getCurrentMove());
        player1Json.put("points", playerStateP0.getRowPoints());
        player1Json.put("combo", playerStateP0.getCombo());
        player1Json.put("skips", playerStateP0.getSkips());
        player1Json.put("field", playerStateP0.getBoard().toString(false, true));
        playersJson.put(player1Json);

        player2Json.put("move", playerStateP1.getCurrentMove());
        player2Json.put("points", playerStateP1.getRowPoints());
        player2Json.put("combo", playerStateP1.getCombo());
        player2Json.put("skips", playerStateP1.getSkips());
        player2Json.put("field", playerStateP1.getBoard().toString(false, true));
        playersJson.put(player2Json);

        stateJson.put("nextShape", state.getNextShape().type);
        stateJson.put("round", state.getRoundNumber());
        stateJson.put("players", playersJson);

        return stateJson;
    }
}
