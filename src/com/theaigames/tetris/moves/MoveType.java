package com.theaigames.tetris.moves;

public enum MoveType {
	DOWN, LEFT, RIGHT, TURNLEFT, TURNRIGHT, DROP;
	
	public static MoveType fromString(String move) {
		
		if(move != null) {
			for(MoveType type : MoveType.values()) {
				if(move.equalsIgnoreCase(type.toString()))
					return type;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}