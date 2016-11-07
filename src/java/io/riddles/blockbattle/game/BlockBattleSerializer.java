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
        game = addDefaultJSON(game, processor);


        // add all states
        JSONArray states = new JSONArray();
        BlockBattleState state = initialState;
        BlockBattleStateSerializer serializer = new BlockBattleStateSerializer();




        String boardRep1 = "", boardRep2 = "";
        int roundNr = 1;
        String winner = "";

        /* Why do we need pData and pStatData for each player?
            We use pStatData for displaying points, combo and skips.
            If we take this data directly from pData, the points, combo and skips will show up 1 round too soon.
            pStatData is updated after each round.
            */
        BlockBattlePlayer pStatData1 = state.getPlayer(1);
        BlockBattlePlayer pStatData2 = state.getPlayer(2);

        while (state.hasNextState() && winner.isEmpty()) {


            state = (BlockBattleState) state.getNextState();
            ArrayList<BlockBattleMove> moves1 = state.getMoves();
            ArrayList<BlockBattleMove> moves2 = new ArrayList<>();
            if (state.getWinner() != null) {
                winner = "player" + state.getWinner().getId();
            }
            if (state.hasNextState()) {
                state = (BlockBattleState) state.getNextState();
                if (state.getWinner() != null) {
                    winner = "player" + state.getWinner().getId();
                }
                moves2 = state.getMoves();
            }

            int maxMoves = moves1.size();
            if (moves2.size() > moves1.size()) {
                maxMoves = moves2.size();
            }

            for (int i = 0; i < maxMoves; i++) {

                String move1 = "", move2="";
                JSONObject player1JSON = new JSONObject(), player2JSON = new JSONObject();
                BlockBattlePlayer pData1 = state.getPlayer(1);
                BlockBattlePlayer pData2 = state.getPlayer(2);

                if (moves1.size() > i) {
                    MoveType moveType = moves1.get(i).getMoveType();
                    if (moveType != null) {
                        move1 = moveType.toString();
                    }
                    if (moves1.get(i).getBoardRepresentation() != null) {
                        boardRep1 = moves1.get(i).getBoardRepresentation();
                    }
                }

                player1JSON.put("move", move1);
                player1JSON.put("skips", pStatData1.getSkips());
                player1JSON.put("field", boardRep1);
                player1JSON.put("combo", pStatData1.getCombo());
                player1JSON.put("points", pStatData1.getRowPoints());

                if (moves2.size() > i) {
                    MoveType moveType = moves2.get(i).getMoveType();
                    if (moveType != null) {
                        move2 = moveType.toString();
                    }
                    if (moves2.get(i).getBoardRepresentation() != null) {
                        boardRep2 = moves2.get(i).getBoardRepresentation();
                    }
                }

                player2JSON.put("move", move2);
                player2JSON.put("skips", pStatData2.getSkips());
                player2JSON.put("field", boardRep2);
                player2JSON.put("combo", pStatData2.getCombo());
                player2JSON.put("points", pStatData2.getRowPoints());

                JSONArray players = new JSONArray();
                players.put(player1JSON);
                players.put(player2JSON);

                JSONObject subState = new JSONObject();
                subState.put("nextShape", state.getNextShape().getType());
                subState.put("round", roundNr);
                subState.put("players", players);

                if (!winner.isEmpty() && i == maxMoves-1 ) {
                    subState.put("winner", winner);
                }
                states.put(subState);
            }
            pStatData1 = state.getPlayer(1);
            pStatData2 = state.getPlayer(2);
            roundNr++;
        }

        // add score
        game.put("score", processor.getScore());

        game.put("states", states);

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
