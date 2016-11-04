package io.riddles.blockbattle.game.data;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joost on 10/14/16.
 */
public class ListUtils<T> {
    private static final SecureRandom RANDOM = new SecureRandom();

    public T getRandomItem(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public ArrayList<ShapeType> getRandomShapeType() {

        ArrayList<ShapeType> shapeTypes = new ArrayList<>();

        for (ShapeType s : ShapeType.values()) {
            shapeTypes.add(s);
        }

        return shapeTypes;
    }
}
