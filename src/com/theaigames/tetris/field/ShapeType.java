package com.theaigames.tetris.field;

import java.util.Random;

public enum ShapeType {
	I, J, L, O, S, T, Z, NONE;
	
	private static final ShapeType[] VALUES = ShapeType.values();
	private static final Random RANDOM = new Random();
	private static final int SIZE_SHAPES = VALUES.length - 1;
	
	/**
	 * Gets a random ShapeType, NONE not included
	 * @return
	 */
	public static ShapeType getRandom() {
		return VALUES[RANDOM.nextInt(SIZE_SHAPES)];
	}
}
