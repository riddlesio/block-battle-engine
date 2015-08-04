package com.theaigames.blockbattle.field;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class Field {

	private int width;
	private int height;
	private int solidRows;
	public Cell grid[][];
	private static final SecureRandom RANDOM = new SecureRandom();
	
	public Field(int width, int height) {
		
		this.height = height;
		this.width = width;
		this.solidRows = 0;
		grid = new Cell[width][height];
		
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				grid[x][y] = new Cell(x, y);
			}
		}
	}
	
	// moves the whole field upwards to make room for new
	// lines from the bottom, returns true if game over.
	// leaves the bottom rows untouched (are changed in the appropriate
	// methods).
	public boolean moveFieldUp(int amount) {
		if(amount <= 0)
			return false;
		
		for(int y = 0; y < height + amount - this.solidRows; y++) {
			for(int x = 0; x < width; x++) {

				int newY = y - amount;
				
				if(newY < 0 && !grid[x][y].isEmpty()) {
					return true;
				}
				
				if(newY >= 0 && y < height) { // move cells up
					grid[x][newY] = grid[x][y].clone();
					grid[x][newY].setLocation(x, newY);
				}
			}
		}
		
		return false;
	}
	
	// adds solid rows to bottom, returns true if game over
	public boolean addSolidRows(int amount) {
		boolean gameOver = moveFieldUp(amount);
		this.solidRows += amount;
		
		// make the bottom rows into solid rows
		for(int y = height - 1; y > height - amount - 1; y--) {
			for(int x = 0; x < width; x++) {
				grid[x][y].setSolid();
			}
		}
		
		return gameOver;
	}
	
	public boolean addGarbageLines(int amount) {
		boolean gameOver = moveFieldUp(amount);
		ArrayList<Integer> exclude = new ArrayList<Integer>();
		
		// make the bottom rows into garbage lines
		for(int y = height - 1; y > height - amount - 1; y--) {
			
			// add random hole, and make sure it is not on the same column
			// for multiple garbage lines
			int emptyCellIndex = RANDOM.nextInt(width - exclude.size());
			exclude.add(emptyCellIndex);
			Collections.sort(exclude);
			for(int ex : exclude) {
				if(emptyCellIndex < ex)
					break;
				emptyCellIndex++;
			}

			for(int x = 0; x < width; x++) {
				if(x == emptyCellIndex)
					grid[x][y].setEmpty();
				else
					grid[x][y].setBlock();
			}
		}
		
		return gameOver;
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
				for(int oy = y - 1; oy >= 0; oy--) {
					for(int x=0; x < width; x++) {
						grid[x][oy + 1] = grid[x][oy].clone(); 
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
	
	public void setShape(Point p, ShapeType shape) {
		Cell cell = getCell(p);
		if(cell != null && cell.isEmpty()) {
			cell.setShape();
			cell.setShapeType(shape);
		}
	}
	
	public void setBlock(Point p, ShapeType shape) {
		Cell cell = getCell(p);
		if(cell != null) {
			if(!cell.isSolid() && !cell.isBlock()) {
				cell.setBlock();
				cell.setShapeType(shape);
			} else {
				System.err.printf("Can't set block here. (%s %s)\n", p.toString(), shape);
			}
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
