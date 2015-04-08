package com.theaigames.game.tetris;

import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.AbstractPlayer;
import com.theaigames.game.tetris.field.Field;

public class Player extends AbstractPlayer {
	
	private Field field;

	public Player(String name, IOPlayer bot, long maxTimeBank, long timePerMove, Field field) {
		super(name, bot, maxTimeBank, timePerMove);
		this.field = field;
	}

	public Field getField() {
		return this.field;
	}
}
