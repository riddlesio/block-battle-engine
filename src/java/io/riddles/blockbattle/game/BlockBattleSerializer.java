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

import io.riddles.blockbattle.BlockBattle;
import io.riddles.blockbattle.game.data.MoveType;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.processor.BlockBattleProcessor;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.blockbattle.game.state.BlockBattleStateSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.javainterface.game.AbstractGameSerializer;

import java.util.ArrayList;

/**
 * BlockBattleSerializer takes a BlockBattleState and serialises it and all previous states into a JSON String.
 * Customize this to add all game specific data to the output.
 *
 * @author jim
 */
public class BlockBattleSerializer extends
        AbstractGameSerializer<BlockBattleProcessor, BlockBattleState> {

    @Override
    public String traverseToString(BlockBattleProcessor processor, BlockBattleState initialState) {
        JSONObject game = new JSONObject();

        // add all states
        JSONArray states = new JSONArray();
        BlockBattleState state = initialState;
        BlockBattleStateSerializer serializer = new BlockBattleStateSerializer();




        while (state.hasNextState()) {
            state = (BlockBattleState) state.getNextState();
            ArrayList<BlockBattleMove> moves = state.getMoves();
            BlockBattleState state2 = (BlockBattleState) state.getNextState();
            ArrayList<BlockBattleMove> moves2 = new ArrayList<>();
            if (state2 != null) {
                moves2 = state2.getMoves();
            }

            int maxMoves = moves.size();
            if (moves2.size() > moves.size()) {
                maxMoves = moves2.size();
            }

            String boardRep1 = "", boardRep2 = "";
            for (int i = 0; i < maxMoves; i++) {
                String move1 = "skips", move2="skips";
                JSONObject player1JSON = new JSONObject(), player2JSON = new JSONObject();
                BlockBattlePlayer pData1 = state.getPlayer(1);
                BlockBattlePlayer pData2 = state.getPlayer(2);

                if (moves.size() > i) {
                    MoveType moveType = moves.get(i).getMoveType();
                    if (moveType != null) {
                        move1 = moveType.toString();
                    }
                    boardRep1 = moves.get(i).getBoardRepresentation();
                }

                /* TODO: put the correct values here */
                player1JSON.put("move", move1);
                player1JSON.put("skips", pData1.getSkips());
                player1JSON.put("field", boardRep1);
                player1JSON.put("combo", pData1.getCombo());
                player1JSON.put("points", pData1.getPoints());

                if (moves2.size() > i) {
                    MoveType moveType = moves2.get(i).getMoveType();
                    if (moveType != null) {
                        move2 = moveType.toString();
                    }
                    boardRep2 = moves2.get(i).getBoardRepresentation();
                }

                /* TODO: put the correct values here */
                player2JSON.put("move", move2);
                player2JSON.put("skips", pData2.getSkips());
                player2JSON.put("field", boardRep2);
                player2JSON.put("combo", pData2.getCombo());
                player2JSON.put("points", pData2.getPoints());

                JSONArray players = new JSONArray();
                players.put(player1JSON);
                players.put(player2JSON);

                JSONObject subState = new JSONObject();
                subState.put("nextShape", state.getNextShape().getType());
                subState.put("round", 0);
                subState.put("players", players);
                states.put(subState);
            }
        }
        //states.put(serializer.traverseToJson(state));

        JSONObject matchdata = new JSONObject();
        matchdata.put("states", states);
        matchdata.put("settings", getSettingsJSON(initialState, processor));

        game.put("playerData", getPlayerDataJSON(processor));

        game.put("matchData", matchdata);

        // add score
        game.put("score", processor.getScore());

        return game.toString();
    }

    protected JSONObject getSettingsJSON(BlockBattleState initialState, BlockBattleProcessor processor) {
        JSONObject settings = new JSONObject();


        JSONObject field = new JSONObject();
        field.put("width", initialState.getBoards().get(0).getWidth());
        field.put("height", initialState.getBoards().get(0).getHeight());
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
