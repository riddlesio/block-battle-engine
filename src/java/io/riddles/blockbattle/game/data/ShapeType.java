package io.riddles.blockbattle.game.data;

import java.security.SecureRandom;
import java.util.ArrayList;

public enum ShapeType {
	I, J, L, O, S, T, Z, NONE, G; // G is garbage
	
	private static final ShapeType[] VALUES = ShapeType.values();
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int SIZE_SHAPES = VALUES.length - 2;
	
	/**
	 * Gets a random ShapeType, NONE and G not included
	 * @return
	 */
	public static ShapeType getRandom() {
		//return VALUES[RANDOM.nextInt(SIZE_SHAPES)];
		return getNotSoRandom(0);
	}

	/* For testing purposes only, needs to be implemented differently. */
	public static ShapeType getNotSoRandom(int i) {
		ArrayList<ShapeType> types = new ArrayList<>();
		for( int t = 0; t < 100; t++) {
			types.add(O);
		}
		return types.get(i);
	}
}
