package com.theaigames.blockbattle.player;

import java.util.ArrayList;
import java.util.HashMap;

import com.theaigames.blockbattle.field.Field;
import com.theaigames.blockbattle.field.Shape;
import com.theaigames.blockbattle.field.ShapeType;
import com.theaigames.blockbattle.moves.Move;
import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.player.AbstractPlayer;

public class Player extends AbstractPlayer {
	
	private Field field;
	private Shape currentShape;
	private ArrayList<Move> roundMoves;
	private int rowPoints;
	private int combo;
	private int rowsRemoved;
	private HashMap<Integer, ArrayList<PlayerState>> playedGame;
	private Player opponent;

	public Player(String name, IOPlayer bot, long maxTimeBank, long timePerMove, Field field) {
		super(name, bot, maxTimeBank, timePerMove);
		this.field = field;
		this.rowPoints = 0;
		this.combo = 0;
		this.playedGame = new HashMap<Integer, ArrayList<PlayerState>>();
	}
	
	public void addPlayerState(int round, Move move, ShapeType nextShape) {
		PlayerState moveResult = new PlayerState(round, move, this.combo, this.rowPoints, nextShape, this.field.toString(false, true));
		
		if(round >= this.playedGame.size()) 
			this.playedGame.put(round, new ArrayList<PlayerState>());
		
		this.playedGame.get(round).add(moveResult);
	}
	
	public void setOpponent(ArrayList<Player> players) {
		for(Player player : players) {
			if(!player.equals(this)) {
				this.opponent = player;
				break;
			}
		}
	}
	
	public Player getOpponent() {
		return this.opponent;
	}

	public Field getField() {
		return this.field;
	}
	
	public void setCurrentShape(Shape shape) {
		this.currentShape = shape;
	}
	
	public Shape getCurrentShape() {
		return this.currentShape;
	}
	
	public void setRoundMoves(ArrayList<Move> moves) {
		this.roundMoves = moves;
	}
	
	public ArrayList<Move> getRoundMoves() {
		return this.roundMoves;
	}
	
	public void addRowPoints(int points) {
		this.rowPoints += points;
	}
	
	public int getRowPoints() {
		return this.rowPoints;
	}
	
	public void setCombo(int combo) {
		this.combo = combo;
	}
	
	public int getCombo() {
		return this.combo;
	}
	
	public void setRowsRemoved(int rowsRemoved) {
		this.rowsRemoved = rowsRemoved;
	}
	
	public int getRowsRemoved() {
		return this.rowsRemoved;
	}
	
	public HashMap<Integer, ArrayList<PlayerState>> getPlayedGame() {
		return this.playedGame;
	}
}