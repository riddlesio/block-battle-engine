package com.theaigames.game.tetris.field;

public class Shape {

	private ShapeType type;
	private CellType[][] shape;
	private int x;
	private int y;
	
	public Shape(ShapeType type) {
		this.type = type;
		
		setShape();
	}
	
	private CellType[][] initializeShape(int size) {
		CellType[][] newShape = new CellType[size][size];
		for(int y=0; y<size; y++) {
			for(int x=0; x<size; x++) {
				newShape[x][y] = CellType.EMPTY;
			}
		}
		return newShape;
	}
	
	// set shape in square box
	private void setShape() {
		switch(this.type) {
		case I:
			this.shape = initializeShape(4);
			this.shape[0][1] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			this.shape[2][1] = CellType.BLOCK;
			this.shape[3][1] = CellType.BLOCK;
			break;
		case J:
			this.shape = initializeShape(3);
			this.shape[1][0] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			this.shape[1][2] = CellType.BLOCK;
			this.shape[0][2] = CellType.BLOCK;
			break;
		case L:
			this.shape = initializeShape(3);
			this.shape[1][0] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			this.shape[1][2] = CellType.BLOCK;
			this.shape[2][2] = CellType.BLOCK;
			break;
		case O:
			this.shape = initializeShape(2);
			this.shape[0][0] = CellType.BLOCK;
			this.shape[1][0] = CellType.BLOCK;
			this.shape[0][1] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			break;
		case S:
			this.shape = initializeShape(3);
			this.shape[1][0] = CellType.BLOCK;
			this.shape[2][0] = CellType.BLOCK;
			this.shape[1][0] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			break;
		case T:
			this.shape = initializeShape(3);
			this.shape[0][0] = CellType.BLOCK;
			this.shape[1][0] = CellType.BLOCK;
			this.shape[2][0] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			break;
		case Z:
			this.shape = initializeShape(3);
			this.shape[0][0] = CellType.BLOCK;
			this.shape[0][1] = CellType.BLOCK;
			this.shape[1][1] = CellType.BLOCK;
			this.shape[2][1] = CellType.BLOCK;
			break;
		}
	}
}
