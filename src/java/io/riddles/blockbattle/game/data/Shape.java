package io.riddles.blockbattle.game.data;


import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.data.MoveType;
import io.riddles.javainterface.game.data.Point;

public class Shape {

	public ShapeType type;
	public Cell[][] shape;
	public int size;
	public Point location;
	public Cell[] blocks;
	public boolean isFrozen;
	
	public Shape(ShapeType type) {
		this.type = type;
		this.blocks = new Cell[4];
		this.isFrozen = false;
		setShape();
	}
	
	// used for cloning
	public Shape(ShapeType type, int size, Point location, Cell[][] shape, Cell[] blocks, boolean isFrozen) {
		this.type = type;
		this.size = size;
		this.location = location;
		this.shape = shape;
		this.blocks = blocks;
		this.isFrozen = isFrozen;
	}
	
	public Shape clone() {
		Shape clone;
		
		if(this.location == null)
			clone = new Shape(this.type);
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
			clone = new Shape(this.type, this.size, (Point) this.location.clone(), shapeClone, blocksClone, isFrozen);
		}
		
		return clone;
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
				this.blocks[0] = this.shape[0][0];
				this.blocks[1] = this.shape[0][1];
				this.blocks[2] = this.shape[1][1];
				this.blocks[3] = this.shape[2][1];
				break;
			case L:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[2][0];
				this.blocks[1] = this.shape[0][1];
				this.blocks[2] = this.shape[1][1];
				this.blocks[3] = this.shape[2][1];
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
				this.blocks[0] = this.shape[1][0];
				this.blocks[1] = this.shape[0][1];
				this.blocks[2] = this.shape[1][1];
				this.blocks[3] = this.shape[2][1];
				break;
			case Z:
				this.size = 3;
				this.shape = initializeShape();
				this.blocks[0] = this.shape[0][0];
				this.blocks[1] = this.shape[1][0];
				this.blocks[2] = this.shape[1][1];
				this.blocks[3] = this.shape[2][1];
				break;
			default:
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
	
	public String getPositionString() {
		return location.x + "," + location.y;
	}
	
	public ShapeType getType() {
		return this.type;
	}
	
	public boolean isFrozen() {
		return this.isFrozen;
	}
	
	public Point getLocation() {
		return this.location;
	}
	
//	public String toString() {
//		StringBuffer output = new StringBuffer();
//		for(int i=0; i < blocks.length; i++) {
//			output.append(blocks[i].getLocation().x + "," + blocks[i].getLocation().y + " ");
//		}
//		return output.toString();
//	}
}
