// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.theaigames.blockbattle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.theaigames.blockbattle.field.Shape;
import com.theaigames.blockbattle.field.ShapeType;
import com.theaigames.blockbattle.moves.Move;
import com.theaigames.blockbattle.moves.MoveType;
import com.theaigames.blockbattle.player.Player;
import com.theaigames.blockbattle.player.PlayerState;
import com.theaigames.game.GameHandler;
import com.theaigames.game.player.AbstractPlayer;

public class Processor implements GameHandler {
	
	private ArrayList<Player> players;
	private int roundNumber;
	private AbstractPlayer winner;
	private boolean gameOver;
	private ShapeType nextShape;
	private int fieldWidth;
	private int fieldHeight;
	
	private final int MAX_MOVES = 40;
	private final int POINTS_PER_SOLID = 4;
	private final double MULTIPLIER = 2;
	
	public Processor(List<Player> players, int fieldWidth, int fieldHeight) {
		this.players = (ArrayList<Player>) players;
		this.roundNumber = 0;
		this.winner = null;
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		
		setNextShape();
		
		// store game start and set opponent for player
		for(Player player : this.players) {
			storePlayerState(player, null);
			player.setOpponent(this.players);
		}
	}

	@Override
	public void playRound(int roundNumber) {
		
		System.out.println("playing round " + roundNumber);
		
		this.roundNumber = roundNumber;
		ShapeType nextShape = this.nextShape;
		
		//set shape for next round
		setNextShape();
		
		// spawn current shape
		for(Player player : this.players) {
			
			//create current shape
			Shape shape = new Shape(nextShape, player.getField());
			
			if(!shape.spawnShape())
				setWinner(player.getOpponent());
			
			player.setCurrentShape(shape);
			
			//first store start of round state
			storePlayerState(player, null);
		}
		
		if(this.gameOver) // game could be over after spawning of shape
			return;
		
		// send updates and ask for moves
		for(Player player : this.players) {
			sendRoundUpdatesToPlayer(player);
			
			ArrayList<Move> moves = parseMoves(player.requestMove("moves"), player);
			player.setRoundMoves(moves);
		}
		
		// execute all moves
		for(Player player : this.players) {
			executeMovesForPlayer(player);
		}
		
		// remove rows and store the amount removed
		for(Player player : this.players) {
			player.setRowsRemoved(player.getField().processEndOfRoundField());
		}
		
		// handle everything that changes after the pieces have been placed
		for(Player player : this.players) {
			addSolidRowsForPlayer(player);
		}
		
		// add final game over state
		if(this.gameOver) {
			for(Player player : this.players) {
				storePlayerState(player, null);
			}
		}
	}

	@Override
	public int getRoundNumber() {
		return roundNumber;
	}

	@Override
	public AbstractPlayer getWinner() {
		return winner;
	}
	
	@Override
	public boolean isGameOver() {
		return (gameOver || winner != null);
	}

	@Override
	public String getPlayedGame() {
		JSONObject output = new JSONObject();
		
		try {
			JSONArray playerNames = new JSONArray();
			for(Player player : this.players) {
				playerNames.put(player.getName());
			}
			
			output.put("settings", new JSONObject()
				.put("field", new JSONObject()
						.put("width", fieldWidth)
						.put("height", fieldHeight))
				.put("players", new JSONObject()
						.put("count", this.players.size()) // could maybe be removed
						.put("names", playerNames))
			);
			
			JSONArray states = new JSONArray();
			JSONObject state = new JSONObject();
			for(int r=0; r<=this.roundNumber; r++) {
				
				// get the number of most moves for this round
				int maxMoves = 0;
				for(Player player : this.players) { 
					int nrOfMoves = player.getPlayedGame().get(r).size();
					if(nrOfMoves > maxMoves)
						maxMoves = nrOfMoves;
				}
				
				for(int i=0; i<maxMoves; i++) {
					
					state = new JSONObject();
					JSONArray playerStates = new JSONArray();
					
					for(Player player : this.players) {
						ArrayList<PlayerState> roundResult = player.getPlayedGame().get(r);
						JSONObject playerState = new JSONObject();
						
						// create player states
						PlayerState result;
						try {
							result = roundResult.get(i);
							playerState.put("move", result.getMoveString());
						} catch (Exception e) { // fill up with idle states
							
							if(r < this.roundNumber)
								result = roundResult.get(roundResult.size() - 1);
							else // special case on the last round with the added final state
								result = roundResult.get(roundResult.size() - 2);
							
							playerState.put("move", "");
						}
						playerState.put("field", result.getFieldString());
						playerState.put("points", result.getPoints());
						playerState.put("combo", result.getCombo());
						
						playerStates.put(playerState);
					}
					
					state.put("round", r);
					state.put("nextShape", this.players.get(0).getPlayedGame().get(r).get(0).getNextShape());
					state.put("players", playerStates);
					states.put(state);
				}
			}
			
			// store the winner in the final state
			states.getJSONObject(states.length() - 1).put("winner", winner.getName());
			if(this.winner == null)
				states.getJSONObject(states.length() - 1).put("winner", "none");
			else
				states.getJSONObject(states.length() - 1).put("winner", winner.getName());
			
			output.put("states", states);
			
		} catch(JSONException e) {
			System.err.println("Can't convert game to JSON. " + e);
		}
		
		return output.toString();
	}

	/**
	 * Sets the next shape to be played randomly
	 */
	private void setNextShape() {
		this.nextShape = ShapeType.getRandom();
	}
	
	/**
	 * Sends all updates the player needs at the start of the round.
	 * @param player : player to send the updates to
	 */
	private void sendRoundUpdatesToPlayer(Player player) {
		
		// game updates
		player.sendUpdate("round", roundNumber);
		player.sendUpdate("this_piece_type", player.getCurrentShape().getType().toString());
		player.sendUpdate("next_piece_type", nextShape.toString());
		
		// player updates
		player.sendUpdate("row_points", player, player.getRowPoints());
		player.sendUpdate("combo", player, player.getCombo());
		player.sendUpdate("field", player, player.getField().toString(false, false));
		player.sendUpdate("this_piece_position", player, player.getCurrentShape().getPositionString());
		
		// opponent updates
		player.sendUpdate("field", player.getOpponent(), player.getField().toString(false, false));
	}
	
	private ArrayList<Move> parseMoves(String input, Player player) {
		ArrayList<Move> moves = new ArrayList<Move>();
		String[] parts = input.split(",");
		
		for(int i=0; i < parts.length; i++) {
			if(i > MAX_MOVES) {
				player.getBot().addToDump(String.format("Maximum number of moves reached, only the first %s will be executed.", MAX_MOVES));
				break;
			}
			if(parts[i].isEmpty())
				break;
			
			Move move = parseMove(parts[i], player);
			if(move != null)
				moves.add(move);
		}
		
		return moves;
	}
	
	private Move parseMove(String input, Player player) {
		MoveType moveType = MoveType.fromString(input);
		
		if(moveType == null) {
			player.getBot().addToDump(String.format("Cannot parse input: %s", input));
			return null;
		}

		return new Move(player, moveType);
	}
	
	private void executeMovesForPlayer(Player player) {
		Shape shape = player.getCurrentShape();
//		System.out.println("executing moves for player " + player.getName());
		
		for(Move move : player.getRoundMoves()) {
			if(shape.isFrozen()) {
				player.getBot().addToDump("Piece was frozen in place on the previous move. Skipping all next moves.");
				break;
			}
			switch(move.getType()) {
				case LEFT:
					move.setIllegalMove(shape.oneLeft());
					break;
				case RIGHT:
					move.setIllegalMove(shape.oneRight());
					break;
				case TURNLEFT:
					move.setIllegalMove(shape.turnLeft());
					break;
				case TURNRIGHT:
					move.setIllegalMove(shape.turnRight());
					break;
				case DOWN:
					move.setIllegalMove(shape.oneDown());
					break;
				case DROP:
					move.setIllegalMove(shape.drop());
					break;
			}
			
			// add a moveResult to the player's playedGame
			storePlayerState(player, move);
		}
		
		// freeze shape and add extra drop move if the piece is still loose in the field
		if(!shape.isFrozen()) {
			int initialY = shape.getLocation().y;
			shape.drop();
			int finalY = shape.getLocation().y;
			
			if(initialY != finalY) {
				String error = "The piece is still loose in the field. Dropping it.";
				Move move = new Move(player, MoveType.DROP);
				move.setIllegalMove(error);
				
				storePlayerState(player, move);
				player.getBot().addToDump(error);
			}
		}
		
		if(shape.isOverflowing()) {
			setWinner(player.getOpponent());
		}
	}
	
	private void addSolidRowsForPlayer(Player player) {
		
		int unusedRowPoints = player.getRowPoints() % POINTS_PER_SOLID;
		
		// set combo
		if(player.getRowsRemoved() > 0)
			player.setCombo(player.getCombo() + 1);
		else
			player.setCombo(0);
		
		// calculate row points for this round
		int rowPoints = player.getRowsRemoved();
		if(player.getRowsRemoved() == 4)
			rowPoints *= MULTIPLIER;
		if(player.getCombo() > 0)
			rowPoints += player.getCombo() - 1;
		player.addRowPoints(rowPoints);
		
		// add unused row points too from previous rounds
		rowPoints += unusedRowPoints;
		
		// add the solid rows to opponent and check for gameover
		if(player.getOpponent().getField().addSolidRows(rowPoints / POINTS_PER_SOLID)) {
			setWinner(player);
		}
	}
	
	// stores everything needed in a state for the visualizer for given player
	private void storePlayerState(Player player, Move move) {
		player.addPlayerState(this.roundNumber, move, this.nextShape);
	}
	
	// if there was a winner already, set winner to null, so we know it's a draw
	private void setWinner(Player player) {
		this.gameOver = true;
		if(this.winner == null)
			this.winner = player;
		else
			this.winner = null;
	}
}
