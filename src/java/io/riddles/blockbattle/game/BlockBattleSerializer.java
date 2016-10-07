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

import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.processor.BlockBattleProcessor;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.blockbattle.game.state.BlockBattleStateSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.javainterface.game.AbstractGameSerializer;

/**
 * BlockBattleSerializer takes a BlockBattleState and serialises it and all previous states into a JSON String.
 * Customize this to add all game specific data to the output.
 *
 * @author jim
 */
public class BlockBattleSerializer extends
        AbstractGameSerializer<BlockBattleProcessor, BlockBattleState> {

    private final int SIZE = 9;

    @Override
    public String traverseToString(BlockBattleProcessor processor, BlockBattleState initialState) {
        JSONObject game = new JSONObject();

        // add all states
        JSONArray states = new JSONArray();
        BlockBattleState state = initialState;
        BlockBattleStateSerializer serializer = new BlockBattleStateSerializer();
        while (state.hasNextState()) {
            state = (BlockBattleState) state.getNextState();

            states.put(serializer.traverseToJson(state));
        }

        JSONObject matchdata = new JSONObject();
        matchdata.put("states", states);
        matchdata.put("settings", getSettingsJSON(game, processor));

        game.put("playerData", getPlayerDataJSON(processor));

        game.put("matchData", matchdata);

        // add score
        game.put("score", processor.getScore());

        return game.toString();
    }

    protected JSONObject getSettingsJSON(JSONObject game, BlockBattleProcessor processor) {
        JSONObject settings = new JSONObject();


        JSONObject field = new JSONObject();
        field.put("width", SIZE);
        field.put("height", SIZE);
        settings.put("field", field);

        settings.put("players", getPlayersJSON(processor));

        // add winner
        String winner = "null";
        if (processor.getWinner() != null) {
            winner = processor.getWinner().getId() + "";
        }
        settings.put("winnerplayer", winner);

        return settings;
    }

    protected JSONObject getPlayersJSON(BlockBattleProcessor processor) {

        JSONArray playerNames = new JSONArray();
        for (Object obj : processor.getPlayers()) {
            AbstractPlayer player = (AbstractPlayer) obj;
            playerNames.put(player.getName());
        }

        JSONObject players = new JSONObject();
        players.put("count", processor.getPlayers().size());
        players.put("names", playerNames);

        return players;
    }


    protected JSONArray getPlayerDataJSON(BlockBattleProcessor processor) {

        JSONArray playerData = new JSONArray();
        for (BlockBattlePlayer obj : processor.getPlayers()) {
            BlockBattlePlayer player = obj;
            JSONObject p = new JSONObject();

            p.put("name", player.getName());
            playerData.put(p);
        }

        return playerData;
    }
}
