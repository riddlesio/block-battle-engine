package io.riddles.blockbattle.game.state;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.javainterface.game.state.AbstractPlayerState;

/**
 */
public class BlockBattlePlayerState extends AbstractPlayerState<BlockBattleMove> {


    public BlockBattlePlayerState(int playerId) {
        super(playerId);
    }
    private BlockBattleBoard board;



    public BlockBattlePlayerState clone() {
        BlockBattlePlayerState psClone = new BlockBattlePlayerState(this.playerId);
        return psClone;
    }

    public int getPlayerId() { return this.playerId; }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public BlockBattleBoard getBoard() { return this.board; }
    public void setBoard(BlockBattleBoard b) {
        this.board = b;
    }

}
