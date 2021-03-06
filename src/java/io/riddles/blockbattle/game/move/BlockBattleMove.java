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

package io.riddles.blockbattle.game.move;

import io.riddles.javainterface.game.move.AbstractMove;

import java.util.ArrayList;


public class BlockBattleMove extends AbstractMove {

    private ArrayList<MoveType> types;

    public BlockBattleMove() {
        this.types = new ArrayList<>();
    }

    public BlockBattleMove(Exception exception) {
        super(exception);
        this.types = new ArrayList<>();
    }

    public void setMoveTypes(ArrayList<MoveType> types) {
        this.types = types;
    }

    public ArrayList<MoveType> getMoveTypes() {
        return this.types;
    }
}
