package com.theaigames.game.tetris.field;

public class Block {

	private int relX; // relative x in shape
	private int relY; // relative y in shape
	
	public Block(int relativeX, int relativeY) {
		this.relX = relativeX;
		this.relY = relativeY;
	}
	
	public int getRelX() {
		return this.relX;
	}
	
	public int getRelY() {
		return this.relY;
	}
}
