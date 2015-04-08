package com.theaigames.game.tetris.field;


public class Field {

	private int width;
	private int height;
	private Cell grid[][];
	
	public Field(int width, int height) {
		
		this.height = height;
		this.width = width;
		grid = new Cell[width][height];
		
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				grid[x][y] = new Cell(x, y);
			}
		}
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public Cell[][] getGrid() {
		return this.grid;
	}
}
