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

import io.riddles.blockbattle.engine.BlockBattleEngine;

/**
 * ShapeFactory returns random Shapes.
 */
public class ShapeFactory {

    /**
     * Gets a random ShapeType, NONE and G not included
     */
    public static Shape getNext() {
        return new Shape(ShapeType.values()[BlockBattleEngine.random.nextInt(ShapeType.SIZE_SHAPES)]);
    }

}
