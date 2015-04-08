package com.theaigames.game.tetris.field;

import java.awt.Point;


public class Field {

	private int width;
	private int height;
	public Cell grid[][];
	
	public Field(int width, int height) {
		
		this.height = height;
		this.width = width;
		grid = new Cell[width][height];
		
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				grid[x][y] = new Cell(x, y);
			}
		}
	}
	
	// adds solid rows to bottom, returns false if game over
	public boolean addSolidRows(int amount) {
		for(int y=0; y < height + amount; y++) {
			for(int x=0; x < width; x++) {

				int newY = y - amount;
				
				if(newY < 0 && !grid[x][y].isEmpty()) {
					return false;
				}
				
				if(newY >= 0) { // move cells up
					grid[x][newY] = grid[x][y];
					grid[x][newY].setLocation(x, newY);
				}
				
				if(y >= height) { // set solid lines
					grid[x][newY].setSolid();
				}
			}
		}
		
		return true;
	}
	
	public void handleFullRows() {
		for(int y=0; y < height; y++) {
			
			boolean fullRow = true;
			
			for(int x=0; x < width; x++) { // check if line contains only block cells
				if(!grid[x][y].isBlock()) {
					fullRow = false;
					break;
				}
			}
			
			if(fullRow) { // move cells down one line
				for(int oy=y; oy >= 0; oy--) {
					for(int x=0; x < width; x++) {
						grid[x][oy + 1] = grid[x][oy]; 
					}
				}
				for(int x=0; x < width; x++) {
					grid[x][0].setEmpty();
				}
			}
		}
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public Cell getCell(Point p) {
		return grid[p.x][p.y];
	}
	
	public void setBlock(Point p, ShapeType shape) {
		if(isEmpty(p)) {
			Cell cell = getCell(p);
			cell.setBlock();
			cell.setShape(shape);
		}
	}
	
	public boolean isSolid(Point p) {
		return getCell(p).isSolid();
	}
	
	public boolean isEmpty(Point p) {
		return getCell(p).isEmpty();
	}
	
	public boolean isBlock(Point p) {
		return getCell(p).isBlock();
	}
}
