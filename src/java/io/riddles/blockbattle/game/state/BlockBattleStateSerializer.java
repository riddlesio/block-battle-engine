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

        BlockBattleMove move = state.getMoves().get(0);

        stateJson.put("movetype", move.getMoveType());
        int winner = 0; /* TODO */
        String winnerString = "";
        if (winner <= 0) {
            winnerString = "";
        } else {
            winnerString = String.valueOf(winner);
        }

        /* TODO: Add for each player */
        //stateJson.put("field", state.getBoard().toString(true, true));
        stateJson.put("winner", winnerString);


        if (move.getException() == null) {
            stateJson.put("exception", JSONObject.NULL);
            stateJson.put("illegalMove", "");
        } else {
            stateJson.put("exception", move.getException().getMessage());
            stateJson.put("illegalMove", move.getException().getMessage());
        }

        /* TODO: We need a BlockBattlePlayer here. */
        /*
        stateJson.put("points", state.getPoints());
        stateJson.put("combo", state.getCombo());
        stateJson.put("skips", state.getSkips());
        */

        return stateJson;
    }
}
