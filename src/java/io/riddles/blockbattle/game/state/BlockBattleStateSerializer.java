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
        JSONArray players = new JSONArray();

        for (BlockBattlePlayerState playerState : state.getPlayerStates()) {
            JSONObject player = new JSONObject();

            if (playerState.getException() == null) {
                player.put("move", playerState.getCurrentMove());
            } else {
                String message = playerState.getException().getMessage();
                player.put("move", message.replace("Invalid move: ", ""));
            }

            player.put("points", playerState.getRowPoints());
            player.put("combo", playerState.getCombo());
            player.put("skips", playerState.getSkips());
            player.put("field", playerState.getBoard().toString(false, true));
            players.put(player);
        }

        stateJson.put("nextShape", state.getNextShape().getType());
        stateJson.put("round", state.getRoundNumber());
        stateJson.put("players", players);

        return stateJson;
    }
}
