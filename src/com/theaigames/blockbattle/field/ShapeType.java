package com.theaigames.blockbattle.field;

import java.security.SecureRandom;

public enum ShapeType {
	I, J, L, O, S, T, Z, NONE, GARBAGE;
	
	private static final ShapeType[] VALUES = ShapeType.values();
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int SIZE_SHAPES = VALUES.length - 2;
	
	/**
	 * Gets a random ShapeType, NONE and GARBAGE not included
	 * @return
	 */
	public static ShapeType getRandom() {
		return VALUES[RANDOM.nextInt(SIZE_SHAPES)];
	}
}
