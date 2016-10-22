package io.riddles.blockbattle.game.data;

import java.security.SecureRandom;
import java.util.ArrayList;

public enum ShapeType {
	I, J, L, O, S, T, Z, NONE, G; // G is garbage
	
	private static final ShapeType[] VALUES = ShapeType.values();
	public static final int SIZE_SHAPES = VALUES.length - 2;

}
