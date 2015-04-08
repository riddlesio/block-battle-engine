package com.theaigames.game.tetris.field;

public class Cell {
	
	private int x;
	private int y;
	private CellType state;
	private ShapeType shape;

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		this.state = CellType.EMPTY;
		this.shape = ShapeType.NONE;
	}

}
