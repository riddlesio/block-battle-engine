package io.riddles.blockbattle.game.data;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by joost on 22-10-16.
 */
public class ShapeFactory {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Gets a random ShapeType, NONE and G not included
     * @return
     */
    public Shape getNext() {
        int pick = RANDOM.nextInt(ShapeType.SIZE_SHAPES);
        return new Shape(ShapeType.values()[pick]);
    }

}
