package io.riddles.blockbattle.game.processor;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.data.MoveType;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeOperations;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.exception.InvalidInputException;

import java.awt.*;

/**
 * Created by joost on 3-7-16.
 */
public class BlockBattleLogic {


    private ShapeOperations shapeOps;
    public BlockBattleLogic() {
        shapeOps = new ShapeOperations();
    }

    /**
     * Takes a BlockBattleState and transforms it with a BlockBattleMove.
     *
     * Return
     * Returns nothing, but transforms the given BlockBattleState.
     * @param state  The BlockBattleState
     * @param playerState
     * @param moveType
     * @return
     */
    public void transform(BlockBattleState state, BlockBattlePlayerState playerState, MoveType moveType) throws InvalidInputException {
        executeMoveForPlayer(state, playerState, moveType);
    }

    /**
     * Takes a BlockBattleState and applies the move(s).
     *
     * Return
     * Returns nothing, but transforms the given BlockBattleState.
     * @param state  The BlockBattleState
     * @param playerState
     * @param moveType
     * @return
     */
    private void executeMoveForPlayer(BlockBattleState state, BlockBattlePlayerState playerState, MoveType moveType) {
        BlockBattleMove bbMove = playerState.getMove();
        BlockBattleBoard board = playerState.getBoard();

        Shape shape = playerState.getCurrentShape();
        MoveType lastMove1 = null;
        MoveType lastMove2 = null;
        Point lastLocation = new Point(-1, -1);
        playerState.setUsedSkip(false);

        lastLocation = new Point(shape.getLocation().x, shape.getLocation().y);
        if (shape.isFrozen()) {
            bbMove.setException(new InvalidInputException("Piece was frozen in place on the previous move. Skipping all next moves."));
            return;
        }
        String result = "";
        switch (moveType) {
            case LEFT:
                result = shapeOps.oneLeft(shape, board);
                break;
            case RIGHT:
                result = shapeOps.oneRight(shape, board);
                break;
            case TURNLEFT:
                result = shapeOps.turnLeft(shape, board);
                break;
            case TURNRIGHT:
                result = shapeOps.turnRight(shape, board);
                break;
            case DOWN:
                result = shapeOps.oneDown(shape, board);
                break;
            case DROP:
                shapeOps.drop(shape, board);
                break;
            case SKIP:
                if (playerState.getSkips() > 0) {
                    shapeOps.skip(shape, board);
                    playerState.setSkips(playerState.getSkips() - 1);
                    playerState.setUsedSkip(true);
                }
                break;
            default:
                shapeOps.drop(shape, board);
        }

        if (!result.isEmpty()) {
            bbMove.setException(new InvalidInputException(result));
        }

        lastMove2 = lastMove1;
        lastMove1 = moveType;

        if (moveType == MoveType.SKIP)
            return;

        //System.out.println(move);
        //board.dump();


        // freeze shape and add extra drop move if the piece is still loose in the field
/*
        if (!shape.isFrozen()) {
            int initialY = shape.getLocation().y;
            shapeOps.drop(shape, board);
            int finalY = shape.getLocation().y;

            if (initialY != finalY) {
                String error = "The piece is still loose in the field. Dropping it.";
                if (lastMove1 != null && lastMove1 == MoveType.SKIP)
                    error = "Can't perform 'skip'. There were no skips available.";
                MoveType move = MoveType.DROP;
                bbMove.setException(new InvalidInputException(error));
                moves.add(move);
                playerState.setTSpin(false);
            } else {
                playerState.setTSpin(shapeOps.checkTSpin(shape, board, lastMove1, lastMove2, lastLocation));
            }
        } else {
            playerState.setTSpin(shapeOps.checkTSpin(shape, board, lastMove1, lastMove2, lastLocation));
        }
            */
    }
}