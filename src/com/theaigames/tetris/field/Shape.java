package com.theaigames.tetris.field;

import java.awt.Point;

public class Shape {

	private ShapeType type;
	private Cell[][] shape;
	private int size;
	private Point location;
	private Cell[] blocks;
	private Field field;
	private boolean isFrozen;
	
	public Shape(ShapeType type, Field field) {
		this.type = type;
		this.field = field;
		this.blocks = new Cell[4];
		this.isFrozen = false;
		
		setShape();
	}
	
	// used for cloning
	public Shape(ShapeType type, int size, Field field, Point location, Cell[][] shape, Cell[] blocks, boolean isFrozen) {
		this.type = type;
		this.size = size;
		this.field = field;
		this.location = location;
		this.shape = shape;
		this.blocks = blocks;
		this.isFrozen = isFrozen;
	}
	
	public Shape clone() {
		Shape clone;
		
		if(this.location == null)
			clone = new Shape(this.type, this.field);
		else {
			Cell[][] shapeClone = new Cell[size][size];
			Cell[] blocksClone = new Cell[4];
			int blockNr = 0;
			for(int y=0; y < size; y++) {
				for(int x=0; x < size; x++) {
					shapeClone[x][y] = shape[x][y].clone();
					if(shapeClone[x][y].isShape()) {
						blocksClone[blockNr] = shapeClone[x][y];
						blockNr++;
					}
				}
			}
			clone = new Shape(this.type, this.size, this.field, (Point) this.location.clone(), shapeClone, blocksClone, isFrozen);
		}
		
		return clone;
	}
	
	// spawns the shape
	public boolean spawnShape() {
		int x = (field.getWidth() - this.size) / 2;
		int y = 0;
		
		if(type == ShapeType.J)
			x++;
		if(type == ShapeType.I)
			y--;
		
		this.location = new Point(x, y);
		setBlockLocations();
			
		if(!setShapeInField())
			return false;
		return true;
	}

	////// Turn actions /////
	
	public String turnLeft() {
		Shape clone = this.clone();
		
		Cell[][] temp = clone.transposeShape();
		for(int y=0; y < size; y++) {
			clone.shape[y] = temp[size - y - 1];
		}
		
		clone.setBlockLocations();
		String error = clone.checkForPositionErrors("turnleft");
		
		if(error.isEmpty())
			takePosition(clone);
		
		return error;
	}
	
	public String turnRight() {
		Shape clone = this.clone();
		
		Cell[][] temp = clone.transposeShape();
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				 clone.shape[x][y] = temp[size - x - 1][y];
			}
		}
		
		clone.setBlockLocations();
		String error = clone.checkForPositionErrors("turnright");
		
		if(error.isEmpty()) {
			takePosition(clone);
		}
		
		return error;
	}
	
	private Cell[][] transposeShape() {
		Cell[][] temp = new Cell[size][size];
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				temp[y][x] = shape[x][y];
			}
		}
		return temp;
	}
	
	///////////////////////////
	
	///// Shift actions /////
	
	public String oneDown() {
		Shape clone = this.clone();
		
		clone.location.y++;
		clone.setBlockLocations();
		
		if(clone.isBelowBottom() || clone.hasCollision())
			freezeInField();
		else
			takePosition(clone);
		
		return ""; // can't return an error
	}
	
	public String oneLeft() {
		Shape clone = this.clone();
		
		clone.location.x--;
		clone.setBlockLocations();
		String error = clone.checkForPositionErrors("left");
		
		if(error.isEmpty())
			takePosition(clone);
		else
			oneDown();
		
		return error;
	}
	
	public String oneRight() {
		Shape clone = this.clone();
		
		clone.location.x++;
		clone.setBlockLocations();
		String error = clone.checkForPositionErrors("right");
		
		if(error.isEmpty())
			takePosition(clone);
		else
			oneDown();
		
		return error;
	}
	
	public String drop() {
		while(!isFrozen) {
			oneDown();
		}
		return ""; // can't return an error
	}
	
	//////////////////////////
	
	private void takePosition(Shape shape) {		
		this.shape = shape.shape;
		this.blocks = shape.blocks;
		this.location = shape.location;
		
		field.cleanField();
		setShapeInField();
	}
	
	//// Position checks /////
	
	public boolean hasCollision() {
		for(int i=0; i < blocks.length; i++) {
			if(blocks[i].hasCollision(field))
				return true;
		}
		return false;
	}
	
	public boolean isBelowBottom() {
		for(int i=0; i < blocks.length; i++) {
			if(blocks[i].isBelowBottom(field))
				return true;
		}
		return false;
	}
	
	public boolean isOutOfBoundaries() {
		for(int i=0; i < blocks.length; i++) {
			if(blocks[i].isOutOfBoundaries(field))
				return true;
		}
		return false;
	}
	
	public boolean isOverflowing() {
		for(int i=0; i < blocks.length; i++) {
			if(blocks[i].isOverFlowing())
				return true;
		}
		return false;
	}
	
	private String checkForPositionErrors(String move) {
		String error = String.format("Can't perform %s: ", move);
		
		if(move.equals("left") || move.equals("right")) {
			
			if(isOutOfBoundaries())
				return error += "Piece is on the field boundary. Action changed to 'down'.";
			if(hasCollision())
				return error += "Other blocks are in the way. Action changed to 'down'.";
			
		} else if(move.equals("turnright") || move.equals("turnleft")) {
			
			if(isOutOfBoundaries() || isBelowBottom())
				return error += "Piece would move out of bounds. Move skipped.";
			if(hasCollision())
				return error += "Other blocks are in the way. Move skipped.";
			
		}
		return "";
	}
	
	/////////////////////////////

	public void setBlockLocations() {
		for(int y=0; y < size; y++) {
			for(int x=0; x < size; x++) {
				if(shape[x][y].isShape()) {
					shape[x][y].setLocation(location.x + x, location.y + y);
				}
			}
		}
	}
	
	public void freezeInField() {
		for(int i=0; i < blocks.length; i++) {
			field.setBlock(blocks[i].getLocation(), this.type);
		}
		isFrozen = true;
	}
	
	public boolean setShapeInField() {
		for(int i=0; i < blocks.length; i++) {
			if(!field.setShape(blocks[i].getLocation(), this.type))
				return false;
		}
		return true;
	}
	
	public ShapeType getType() {
		return this.type;
	}
	
	public String getPositionString() {
		return location.x + "," + location.y;
	}
	
	public boolean isFrozen() {
		return this.isFrozen;
	}
	
	// set shape in square box
	private void setShape() {
		switch(this.type) {
			case I:
				this.size = 4;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[0][1];
				this.blocks[1] = this.shape[1][1];
				this.blocks[2] = this.shape[2][1];
				this.blocks[3] = this.shape[3][1];
				break;
			case J:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[1][0];
				this.blocks[1] = this.shape[1][1];
				this.blocks[2] = this.shape[1][2];
				this.blocks[3] = this.shape[0][2];
				break;
			case L:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[1][0];
				this.blocks[1] = this.shape[1][1];
				this.blocks[2] = this.shape[1][2];
				this.blocks[3] = this.shape[2][2];
				break;
			case O:
				this.size = 2;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[0][0];
				this.blocks[1] = this.shape[1][0];
				this.blocks[2] = this.shape[0][1];
				this.blocks[3] = this.shape[1][1];
				break;
			case S:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[1][0];
				this.blocks[1] = this.shape[2][0];
				this.blocks[2] = this.shape[0][1];
				this.blocks[3] = this.shape[1][1];
				break;
			case T:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[0][0];
				this.blocks[1] = this.shape[1][0];
				this.blocks[2] = this.shape[2][0];
				this.blocks[3] = this.shape[1][1];
				break;
			case Z:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[0][0];
				this.blocks[1] = this.shape[1][0];
				this.blocks[2] = this.shape[1][1];
				this.blocks[3] = this.shape[2][1];
				break;
		}
		
		// set type to SHAPE
		for(int i=0; i < blocks.length; i++) {
			this.blocks[i].setShape();
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
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		for(int i=0; i < blocks.length; i++) {
			output.append(blocks[i].getLocation().x + "," + blocks[i].getLocation().y + " ");
		}
		return output.toString();
	}
}
