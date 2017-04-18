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
	
	public Cell(Cell cell) {
	    if (cell.getLocation() != null) {
	        this.location = new Point(cell.getLocation().x, cell.getLocation().y);
        }
		this.state = cell.getState();
		this.shape = cell.getShapeType();
	}
	
	public boolean isOutOfBoundaries(BlockBattleBoard b) {
        return this.location.x >= b.getWidth() || this.location.x < 0;
    }
	
	public boolean isOverFlowing() {
        return this.location.y < 0;
    }

	/* Checks whether Cell is lower than Board Height */
	public boolean isBelowBottom(BlockBattleBoard b) {
        return this.location.y >= b.getHeight();
    }
	
	public boolean hasCollision(BlockBattleBoard b) {
        Cell cell = b.getFieldAt(this.location);
        return cell != null && (this.state == CellType.SHAPE && (cell.isSolid() || cell.isBlock()));
    }
	
	public void setLocation(int x, int y) {
		if (this.location == null) {
            this.location = new Point();
        }
		
		this.location.setLocation(x, y);
	}
	
	public void setShapeType(ShapeType shape) {
		this.shape = shape;
	}
	
	public void setShape() {
		this.state = CellType.SHAPE;
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
	
	public boolean isShape() {
		return this.state == CellType.SHAPE;
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
	
	public ShapeType getShapeType() {
		return this.shape;
	}
}
