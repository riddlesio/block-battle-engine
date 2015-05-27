package com.theaigames.tetris.player;

import java.util.ArrayList;

import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.player.AbstractPlayer;
import com.theaigames.tetris.field.Field;
import com.theaigames.tetris.field.Shape;
import com.theaigames.tetris.moves.Move;
import com.theaigames.tetris.moves.MoveResult;

public class Player extends AbstractPlayer {
	
	private Field field;
	private Shape currentShape;
	private ArrayList<Move> roundMoves;
	private int rowPoints;
	private int combo;
	private ArrayList<MoveResult> playedGame;

	public Player(String name, IOPlayer bot, long maxTimeBank, long timePerMove, Field field) {
		super(name, bot, maxTimeBank, timePerMove);
		this.field = field;
		this.rowPoints = 0;
		this.combo = 0;
		
		this.playedGame = new ArrayList<MoveResult>();
		this.playedGame.add(new MoveResult(0, null, this.field.toString(false, true)));
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
	
	public void addMoveResult(MoveResult moveResult) {
		this.playedGame.add(moveResult);
	}
	
	public ArrayList<MoveResult> getPlayedGame() {
		return this.playedGame;
	}
}
