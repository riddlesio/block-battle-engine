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

package com.theaigames.tetris;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.theaigames.game.GameHandler;
import com.theaigames.game.player.AbstractPlayer;
import com.theaigames.tetris.field.Shape;
import com.theaigames.tetris.field.ShapeType;
import com.theaigames.tetris.moves.Move;
import com.theaigames.tetris.moves.MoveResult;
import com.theaigames.tetris.moves.MoveType;
import com.theaigames.tetris.player.Player;

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
	private final double TETRIS_MULTIPLIER = 2;
	
	public Processor(List<Player> players, int fieldWidth, int fieldHeight) {
		this.players = (ArrayList<Player>) players;
		this.roundNumber = -1;
		this.winner = null;
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		
		setNextShape();
	}

	@Override
	public void playRound(int roundNumber) {
		
		System.out.println("playing round " + roundNumber);
		
		this.roundNumber = roundNumber;
		
		// spawn current shape
		for(Player player : this.players) {
			Shape shape = new Shape(nextShape, player.getField());
			
			if(!shape.spawnShape())
				setWinner(getOpponentsForPlayer(player).get(0));
			
			player.setCurrentShape(shape);
//			System.out.println("SHAPE " + shape.getType());
			
			player.addMoveResult(new MoveResult(roundNumber, null, player.getField().toString(false, true)));
		}
		
		if(this.gameOver) // game could be over after spawning of shape
			return;
		
		//set shape for next round
		setNextShape();
		
		// send updates and ask for moves
		for(Player player : this.players) {
			sendRoundUpdatesToPlayer(player);
			
			ArrayList<Move> moves = parseMoves(player.requestMove("moves"), player);
			player.setRoundMoves(moves);
			
//			System.out.println(player.getName() + "\n" + player.getField().toString(true));
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		// execute the moves
		for(Player player : this.players) {
			executeMovesForPlayer(player);
		}
		
		if(this.gameOver) // game could be over because of solid rows
			return;
		
		// handle everything that changes after the pieces have been placed
		for(Player player : this.players) {
			handleFieldChangesForPlayer(player);
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
				.put("maxRound", this.roundNumber)
			);
			
			JSONArray states = new JSONArray();
			for(int i=0; i<getTotalAmountOfMoves(); i++) {
				
				JSONObject state = new JSONObject();
				for(Player player : this.players) {
					MoveResult result = player.getPlayedGame().get(i);
					JSONObject playerState = new JSONObject();
					
					playerState.put("field", result.getFieldString());
					playerState.put("move", result.getMoveString());
					
					state.put(player.getName(), playerState);
					
					if(!state.has("round"))
						state.put("round", result.getRound());
				}
				
				states.put(state);
			}
			
			output.put("states", states);
			
			if(this.winner == null)
				output.put("winner", "");
			else
				output.put("winner", winner.getName());
			
		} catch(JSONException e) {
			System.err.println("Can't convert game to JSON. " + e);
		}
		
		System.out.println(output.toString());
		return output.toString();
	}
	
	private int getTotalAmountOfMoves() throws RuntimeException {
		int nrOfMoves = this.players.get(0).getPlayedGame().size();
		
		for(Player player : this.players) {
			if (nrOfMoves != player.getPlayedGame().size())
				throw new RuntimeException("Players do not have the same amount of moves.");
		}
		
		return nrOfMoves;
	}

	/**
	 * Sets the next shape to be played randomly
	 */
	private void setNextShape() {
		nextShape = ShapeType.getRandom();
	}
	
	/**
	 * Sends all updates the player needs at the start of the round.
	 * @param player : player to send the updates to
	 */
	private void sendRoundUpdatesToPlayer(Player player) {
		
		// game updates
		player.sendUpdate("round", roundNumber);
		player.sendUpdate("row_points", player.getRowPoints());
		player.sendUpdate("this_piece_type", player.getCurrentShape().getType().toString());
		player.sendUpdate("next_piece_type", nextShape.toString());
		
		// player updates
		player.sendUpdate("this_piece_position", player, player.getCurrentShape().getPositionString());
		player.sendUpdate("field", player, player.getField().toString(false, false));
		
		// opponent updates (there is just one)
		for(Player opponent : getOpponentsForPlayer(player))
			player.sendUpdate("field", opponent, player.getField().toString(false, false));
	}
	
	/**
	 * Returns a list of opponent players for given player
	 * @param player : player
	 * @return : list of opponents
	 */
	private ArrayList<Player> getOpponentsForPlayer(Player player) {
		ArrayList<Player> opponents = new ArrayList<Player>();
		for(Player p : this.players) {
			if(!player.equals(p))
				opponents.add(p);
		}
		return opponents;
	}
	
	private ArrayList<Move> parseMoves(String input, Player player) {
		ArrayList<Move> moves = new ArrayList<Move>();
		String[] parts = input.split(",");
		
		for(int i=0; i < parts.length; i++) {
			if(i > MAX_MOVES) {
				player.getBot().addToDump(String.format("Maximum number of moves reached, only the first %s will be executed.", MAX_MOVES));
				break;
			}
			
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
			player.addMoveResult(new MoveResult(this.roundNumber, move, player.getField().toString(false, true)));
//			System.out.println(player.getField().toString(true));
		}
		
		if(!shape.isFrozen()) {
			player.getBot().addToDump("The piece is still loose in the field. Dropping it.");
			shape.drop();
		}
		
		if(shape.isOverflowing())
			setWinner(getOpponentsForPlayer(player).get(0));
	}
	
	private void handleFieldChangesForPlayer(Player player) {
		
		Player opponent = getOpponentsForPlayer(player).get(0);
		int unusedRowPoints = player.getRowPoints() % POINTS_PER_SOLID;
		
		// get the amount of rows removed
		int rowsRemoved = player.getField().processEndOfRoundField();
		
		// set combo
		if(rowsRemoved > 0)
			player.setCombo(player.getCombo() + 1);
		else
			player.setCombo(0);
		
		// calculate row points for this round
		int rowPoints = rowsRemoved;
		if(rowsRemoved == 4)
			rowPoints *= TETRIS_MULTIPLIER;
		rowPoints += player.getCombo();
		player.addRowPoints(rowPoints);
		
		// add unused row points too from previous rounds
		rowPoints += unusedRowPoints;
		
//		System.out.println("points this round: " + rowPoints);
		
		// add the solid rows to opponent and check for gameover
		if(opponent.getField().addSolidRows(rowPoints / POINTS_PER_SOLID))
			setWinner(player);
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
