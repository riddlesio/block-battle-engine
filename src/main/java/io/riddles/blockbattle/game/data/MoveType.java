package io.riddles.blockbattle.game.data;

/**
 * ${PACKAGE_NAME}
 *
 * This file is a part of BlockBattle
 *
 * Copyright 2016 - present Riddles.io
 * For license information see the LICENSE file in the project root
 *
 * @author Niko
 */

public enum MoveType {
    DOWN, LEFT, RIGHT, TURNLEFT, TURNRIGHT, DROP, SKIP;

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
