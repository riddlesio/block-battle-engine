package com.theaigames.tetris.field;

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
	
	// adds solid rows to bottom, returns true if game over
	public boolean addSolidRows(int amount) {
		if(amount <= 0)
			return false;
		
		for(int y=0; y < height + amount; y++) {
			for(int x=0; x < width; x++) {

				int newY = y - amount;
				
				if(newY < 0 && !grid[x][y].isEmpty()) {
					return true;
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
		
		return false;
	}
	
	// handles round end by clean up and checking for full rows
	// returns number of full rows
	public int processEndOfRoundField() {
		int rowsRemoved = 0;
		for(int y=0; y < height; y++) {
			boolean fullRow = true;
			
			for(int x=0; x < width; x++) { 
				if(grid[x][y].isShape())
					grid[x][y].setEmpty();
				if(!grid[x][y].isBlock()) // check if line contains only block cells
					fullRow = false;
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
				rowsRemoved++;
			}
		}
		return rowsRemoved;
	}
	
	// removes shape cells from field
	public void cleanField() {
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) { 
				if(grid[x][y].isShape())
					grid[x][y].setEmpty();
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
		if(p.x < 0 || p.x >= width || p.y < 0 || p.y >= height)
			return null;
		return grid[p.x][p.y];
	}
	
	public boolean setShape(Point p, ShapeType shape) {
		Cell cell = getCell(p);
		if(cell != null && cell.isEmpty()) {
			cell.setShape();
			cell.setShapeType(shape);
			return true;
		}
		return false;
	}
	
	public void setBlock(Point p, ShapeType shape) {
		Cell cell = getCell(p);
		if(cell != null && !(cell.isSolid() || cell.isBlock())) {
			cell.setBlock();
			cell.setShapeType(shape);
		} else {
			System.err.printf("Can't set block here. (%s %s)\n", p.toString(), shape);
		}
	}
	
	/**
	 * Returns string representation of the current field
	 * pretty for printing in console
	 */
	public String toString(boolean pretty, boolean forVisualizer) {
		StringBuffer output = new StringBuffer();
		
		String rowConnector = ",";
		String collumnConnector = ";";
		if(pretty) {
			rowConnector = " ";
			collumnConnector = "\n";
		}
		
		String collumnJoin = "";
		for(int y=0; y < height; y++) {
			
			output.append(collumnJoin);
			
			String rowJoin = "";
			for(int x=0; x < width; x++) {
				output.append(rowJoin);
				String cellState = grid[x][y].getState().getCode() + "";
				
				if(forVisualizer && grid[x][y].getShapeType() != ShapeType.NONE) {
					cellState = grid[x][y].getShapeType().toString();
				}
				
				output.append(cellState);
				rowJoin = rowConnector;
			}
			
			collumnJoin = collumnConnector;
		}
		
		return output.toString();
	}
}
