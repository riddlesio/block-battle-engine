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

package io.riddles.blockbattle.game.player;

import io.riddles.blockbattle.game.data.Shape;
import io.riddles.javainterface.game.player.AbstractPlayer;

/**
 * ${PACKAGE_NAME}
 *
 * This file is a part of TicTacToe
 *
 * Copyright 2016 - present Riddles.io
 * For license information see the LICENSE file in the project root
 *
 * @author Niko
 */


public class BlockBattlePlayer extends AbstractPlayer {




    public BlockBattlePlayer(int id) {
        super(id);
    }

    public BlockBattlePlayer(BlockBattlePlayer player) {
        super(player.getId());
    }

    public String toString() {
        return "BlockBattlePlayer " + this.getId();
    }


}
