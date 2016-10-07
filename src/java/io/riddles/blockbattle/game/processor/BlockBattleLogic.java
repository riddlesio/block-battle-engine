package io.riddles.blockbattle.game.processor;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.exception.InvalidInputException;

import java.awt.*;

/**
 * Created by joost on 3-7-16.
 */
public class BlockBattleLogic {


    public BlockBattleLogic() {
    }

    /**
     * Takes a BlockBattleState and transforms it with a BlockBattleMove.
     *
     * Return
     * Returns nothing, but transforms the given BlockBattleState.
     * @param BlockBattleState The initial state
     * @param BlockBattleMove The move of the player
     * @return
     */
    public void transform(BlockBattleState state, BlockBattleMove move) throws InvalidInputException {
        if (move.getException() == null) {
            transformMove(state, move);
        } else {
        }
    }

    /**
     * Takes a BlockBattleState and applies the move.
     *
     * Return
     * Returns nothing, but transforms the given BlockBattleState.
     * @param BlockBattleState The initial state
     * @param BlockBattleMove The move of the player
     * @return
     */
    private void transformMove(BlockBattleState state, BlockBattleMove move) {
        BlockBattlePlayer p = move.getPlayer();
        BlockBattleBoard board = state.getBoard();

        int pId = p.getId();
        BlockBattleBoard b = state.getBoard();

        move.setException(new InvalidInputException("Error: BlockBattle doesnt do anything yet"));
    }
}