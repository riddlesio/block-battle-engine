/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.blockbattle.game.data;

import java.awt.*;

public class Shape {

	private ShapeType type;
	private Cell[][] shape;
	private int size;
	private Point location;
	private Cell[] blocks;
	private boolean isFrozen;
	
	public Shape(ShapeType type) {
		this.type = type;
		this.blocks = new Cell[4];
		this.isFrozen = false;
		setShape();
	}
	
	// used for cloning
	private Shape(ShapeType type, int size, Point location, Cell[][] shape, Cell[] blocks, boolean isFrozen) {
		this.type = type;
		this.size = size;
		this.location = location;
		this.shape = shape;
		this.blocks = blocks;
		this.isFrozen = isFrozen;
	}
	
	public Shape clone() {
		if (this.location == null) {
			return new Shape(this.type);
		}

        Cell[][] shapeClone = new Cell[size][size];
        Cell[] blocksClone = new Cell[4];
        int blockNr = 0;
        for (int y=0; y < size; y++) {
            for (int x=0; x < size; x++) {
                shapeClone[x][y] = new Cell(shape[x][y]);
                if (shapeClone[x][y].isShape()) {
                    blocksClone[blockNr] = shapeClone[x][y];
                    blockNr++;
                }
            }
        }

        return new Shape(
            this.type,
            this.size,
            new Point(this.location.x, this.location.y),
            shapeClone,
            blocksClone,
            this.isFrozen
        );
	}

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
        for (Cell block : blocks) {
            block.setShape();
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
        StringBuilder output = new StringBuilder();
        output.append("Shape ");
        for (Cell block : this.blocks) {
            output.
                    append(block.getLocation().x).
                    append(",").
                    append(block.getLocation().y).
                    append(" ");
        }

        return output.toString();
    }
	
	public String getPositionString() {
		return location.x + "," + location.y;
	}
	
	public ShapeType getType() {
		return this.type;
	}

	public void setFrozen() {
	    this.isFrozen = true;
    }
	
	public boolean isFrozen() {
		return this.isFrozen;
	}

	public void setLocation(Point location) {
	    this.location = location;
    }
	
	public Point getLocation() {
		return this.location;
	}

	public int getSize() {
	    return this.size;
    }

    public void setBlocks(Cell[] blocks) {
	    this.blocks = blocks;
    }

    public Cell[] getBlocks() {
	    return this.blocks;
    }

    public void setShape(Cell[][] shape) {
	    this.shape = shape;
    }

    public Cell[][] getShape() {
	    return this.shape;
    }
}
