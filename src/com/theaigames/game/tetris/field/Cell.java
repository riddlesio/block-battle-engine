package com.theaigames.game.tetris.field;

import java.awt.Point;

public class Cell {
	
	private Point location;
	private CellType state;
	private ShapeType shape;

	public Cell(int x, int y) {
		this.location = new Point(x, y);
		this.state = CellType.EMPTY;
		this.shape = ShapeType.NONE;
	}
	
	public Cell(ShapeType type) {
		this.location = null;
		this.state = CellType.EMPTY;
		this.shape = type;
	}
	
	public boolean isWithinBoundaries(Field f) {
		if(this.location.x < 0 || this.location.y < 0 
				|| this.location.x >= f.getWidth() || this.location.y >= f.getHeight()) 
			return false;
		return true;
	}
	
	public boolean isOverFlowing() {
		if(this.location.y < 0)
			return true;
		return false;
	}
	
	public boolean hasCollision(Field f) {
		return (this.state == CellType.BLOCK && f.isSolid(this.location));
	}
	
	public void setLocation(int x, int y) {
		if(this.location == null)
			this.location = new Point();
		
		this.location.setLocation(x, y);
	}
	
	public void setShape(ShapeType shape) {
		this.shape = shape;
	}
	
	public void setBlock() {
		this.state = CellType.BLOCK;
	}
	
	public void setSolid() {
		this.state = CellType.SOLID;
		this.shape = ShapeType.NONE;
	}
	
	public void setEmpty() {
		this.state = CellType.EMPTY;
		this.shape = ShapeType.NONE;
	}
	
	public boolean isBlock() {
		return this.state == CellType.BLOCK;
	}
	
	public boolean isSolid() {
		return this.state == CellType.SOLID;
	}
	
	public boolean isEmpty() {
		return this.state == CellType.EMPTY;
	}
	
	public Point getLocation() {
		return this.location;
	}

	public CellType getState() {
		return this.state;
	}
	
	public ShapeType getShape() {
		return this.shape;
	}
}
