package io.riddles.blockbattle.game.processor;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.data.MoveType;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.data.ShapeOperations;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.blockbattle.game.player.BlockBattlePlayer;
import io.riddles.blockbattle.game.state.BlockBattlePlayerState;
import io.riddles.blockbattle.game.state.BlockBattleState;
import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.game.data.Point;

import java.util.ArrayList;

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
     * @param BlockBattleState The initial state
     * @param BlockBattleMove The move of the player
     * @return
     */
    public void transform(BlockBattleState state, BlockBattlePlayerState playerState) throws InvalidInputException {
        executeMovesForPlayer(state, playerState);
    }

    /**
     * Takes a BlockBattleState and applies the move(s).
     *
     * Return
     * Returns nothing, but transforms the given BlockBattleState.
     * @param BlockBattleState The initial state
     * @param ArrayList<BlockBattleMove> The move(s) of the player
     * @return
     */
    private void executeMovesForPlayer(BlockBattleState state, BlockBattlePlayerState playerState) {
        if (moves.size() > 0) {
            BlockBattlePlayer player = moves.get(0).getPlayer();
            BlockBattleBoard board = state.getBoard(player.getId());

            Shape shape = player.getCurrentShape();
            BlockBattleMove lastMove1 = null;
            BlockBattleMove lastMove2 = null;
            Point lastLocation = new Point(-1, -1);
            player.setUsedSkip(false);

            for (BlockBattleMove move : moves) {

                lastLocation = new Point(shape.getLocation().x, shape.getLocation().y);
                if (shape.isFrozen()) {
                    move.setException(new InvalidInputException("Piece was frozen in place on the previous move. Skipping all next moves."));
                    break;
                }
                String result = "";
                if (move.getMoveType() == null) { /* Drop it anyway */
                    shapeOps.drop(shape, board);
                } else {
                    switch (move.getMoveType()) {
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
                            if (player.getSkips() > 0) {
                                shapeOps.skip(shape, board);
                                player.setSkips(player.getSkips() - 1);
                                player.setUsedSkip(true);
                            }
                            break;

                    }
                }
                move.setBoardRepresentation(board.toString(false, true));

                if (!result.isEmpty()) {
                    move.setException(new InvalidInputException(result));
                }

                lastMove2 = lastMove1;
                lastMove1 = move;

                if (move.getMoveType() == MoveType.SKIP)
                    break;

                //System.out.println(move);
                //board.dump();
            } /* End moves for loop */


            // freeze shape and add extra drop move if the piece is still loose in the field
            if (!shape.isFrozen()) {
                int initialY = shape.getLocation().y;
                shapeOps.drop(shape, board);
                int finalY = shape.getLocation().y;

                if (initialY != finalY) {
                    String error = "The piece is still loose in the field. Dropping it.";
                    if (lastMove1 != null && lastMove1.getMoveType() == MoveType.SKIP)
                        error = "Can't perform 'skip'. There were no skips available.";
                    BlockBattleMove move = new BlockBattleMove(player, MoveType.DROP);
                    move.setException(new InvalidInputException(error));
                    move.setBoardRepresentation(board.toString(false, true));
                    moves.add(move);
                    player.setTSpin(false);

                } else {
                    player.setTSpin(shapeOps.checkTSpin(shape, board, lastMove1, lastMove2, lastLocation));
                }
            } else {
                player.setTSpin(shapeOps.checkTSpin(shape, board, lastMove1, lastMove2, lastLocation));
            }

            if (shapeOps.isOverflowing(shape)) {
                /* OTHER PLAY WINS */ /* TODO: Move this to processor */
            }

        }
    }
}