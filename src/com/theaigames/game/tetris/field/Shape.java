package com.theaigames.game.tetris.field;

import java.awt.Point;

public class Shape {

	private ShapeType type;
	private Cell[][] shape;
	private int size;
	private Point location;
	private Cell[] blocks;
	private Field field;
	
	public Shape(ShapeType type, Field field) {
		this.type = type;
		this.field = field;
		this.location = null;

		setShape();
	}
	
	public Shape(ShapeType type, Field field, Point location) {
		this.type = type;
		this.field = field;
		this.location = location;
		
		setShape();
		setBlockLocations();
	}
	
	public Shape clone() {
		Shape clone;
		
		if(this.location == null)
			clone = new Shape(this.type, this.field);
		else
			clone = new Shape(this.type, this.field, (Point) this.location.clone());
		
		return clone;
	}
	
	public void spawnShape() {
		this.location = new Point(0, 0);
		setBlockLocations();
	}

	////// Turn actions /////
	
	public void rotateLeft() {
		transpose();
		
		Cell[][] temp = new Cell[size][size];
		for(int y=0; y < size; y++) {
			temp[size - y - 1] = shape[y];
		}
		this.shape = temp;
		
		setBlockLocations();
	}
	
	public void rotateRight() {
		transpose();
		
		Cell[][] temp = new Cell[size][size];
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				temp[size - x - 1][y] = shape[x][y];
			}
		}
		this.shape = temp;
		
		setBlockLocations();
	}
	
	private void transpose() {
		Cell[][] temp = new Cell[size][size];
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				temp[y][x] = shape[x][y];
			}
		}
		this.shape = temp;
	}
	
	///////////////////////////
	
	///// Shift actions /////
	
	public void oneDown() {
		this.location.y++;
		setBlockLocations();
	}
	
	public void oneLeft() {
		this.location.x--;
		setBlockLocations();
	}
	
	public void oneRight() {
		this.location.x++;
		setBlockLocations();
	}
	
	public void dropDown() {
		Shape down = this.clone();
		down.oneDown();
		while(!down.hasCollision() && !down.isWithinBoundaries()) {
			this.dropDown();
			down.dropDown();
		}
	}
	
	//////////////////////////
	
	//// Position checks /////
	
	public boolean hasCollision() {
		for(int i=0; i < blocks.length; i++) {
			if(blocks[i].hasCollision(field))
				return true;
		}
		return false;
	}
	
	public boolean isWithinBoundaries() {
		for(int i=0; i < blocks.length; i++) {
			if(!blocks[i].isWithinBoundaries(field))
				return false;
		}
		return true;
	}
	
	public boolean isOverflowing() {
		for(int i=0; i < blocks.length; i++) {
			if(blocks[i].isOverFlowing())
				return true;
		}
		return false;
	}
	
	/////////////////////////////
	
	public void setBlockLocations() {		
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				if(shape[x][y].getState() == CellType.BLOCK) {
					shape[x][y].setLocation(location.x + x, location.y + y);
				}
			}
		}
	}
	
	public void freeze() {
		for(int i=0; i < blocks.length; i++) {
			field.setBlock(blocks[i].getLocation(), this.type);
		}
	}
	
	//// initialization methods ////
	
	// set shape in square box
	private void setShape() {
		switch(this.type) {
		case I:
			this.size = 4;
			this.shape = initializeShape();
			this.shape[0][1].setBlock();
			this.shape[1][1].setBlock();
			this.shape[2][1].setBlock();
			this.shape[3][1].setBlock();
			break;
		case J:
			this.size = 3;
			this.shape = initializeShape();
			this.shape[1][0].setBlock();
			this.shape[1][1].setBlock();
			this.shape[1][2].setBlock();
			this.shape[0][2].setBlock();
			break;
		case L:
			this.size = 3;
			this.shape = initializeShape();
			this.shape[1][0].setBlock();
			this.shape[1][1].setBlock();
			this.shape[1][2].setBlock();
			this.shape[2][2].setBlock();
			break;
		case O:
			this.size = 2;
			this.shape = initializeShape();
			this.shape[0][0].setBlock();
			this.shape[1][0].setBlock();
			this.shape[0][1].setBlock();
			this.shape[1][1].setBlock();
			break;
		case S:
			this.size = 3;
			this.shape = initializeShape();
			this.shape[1][0].setBlock();
			this.shape[2][0].setBlock();
			this.shape[1][0].setBlock();
			this.shape[1][1].setBlock();
			break;
		case T:
			this.size = 3;
			this.shape = initializeShape();
			this.shape[0][0].setBlock();
			this.shape[1][0].setBlock();
			this.shape[2][0].setBlock();
			this.shape[1][1].setBlock();
			break;
		case Z:
			this.size = 3;
			this.shape = initializeShape();
			this.shape[0][0].setBlock();
			this.shape[0][1].setBlock();
			this.shape[1][1].setBlock();
			this.shape[2][1].setBlock();
			break;
		}
	}
	
	private Cell[][] initializeShape() {
		Cell[][] newShape = new Cell[size][size];
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				newShape[x][y] = new Cell(this.type);
			}
		}
		return newShape;
	}
	
	///////////////////////////////////
}
