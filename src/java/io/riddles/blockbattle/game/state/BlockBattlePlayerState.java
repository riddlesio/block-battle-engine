package io.riddles.blockbattle.game.state;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.move.MoveType;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.javainterface.exception.InvalidMoveException;
import io.riddles.javainterface.game.state.AbstractPlayerState;

public class BlockBattlePlayerState extends AbstractPlayerState<BlockBattleMove> {

    private boolean usedSkip;
    private int rowPoints;
    private int combo;
    private int rowsRemoved;
    private int skips;
    private boolean performedTSpin;
    private boolean fieldCleared;
    private Shape currentShape;
    private MoveType currentMove;
    private BlockBattleBoard board;
    private Exception exception;  // exception on the MoveType

    public BlockBattlePlayerState(int playerId) {
        super(playerId);
        this.rowPoints = 0;
        this.combo = 0;
        this.skips = 0;
        this.performedTSpin = false;
        this.fieldCleared = false;
        this.usedSkip = false;
        this.currentShape = null;
        this.currentMove = null;
        this.exception = null;
    }

    public BlockBattlePlayerState(BlockBattlePlayerState playerState) {
        super(playerState.getPlayerId());
        this.board = new BlockBattleBoard(playerState.getBoard());
        this.currentShape = playerState.getCurrentShape() != null
                ? playerState.getCurrentShape().clone()
                : null;
        this.rowPoints = playerState.getRowPoints();
        this.combo = playerState.getCombo();
        this.skips = playerState.getSkips();
        this.exception = null;
        this.currentMove = null;
        this.performedTSpin = false;
        this.fieldCleared = false;
        this.usedSkip = false;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public BlockBattleBoard getBoard() {
        return this.board;
    }

    public void setBoard(BlockBattleBoard b) {
        this.board = b;
    }

    public void addRowPoints(int points) {
        this.rowPoints += points;
    }

    public boolean getFieldCleared() {
        return this.fieldCleared;
    }

    public int getRowPoints() {
        return this.rowPoints;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public int getCombo() {
        return this.combo;
    }

    public void setSkips(int skips) {
        this.skips = skips;
    }

    public int getSkips() {
        return this.skips;
    }

    public void setUsedSkip(boolean usedSkip) {
        this.usedSkip = usedSkip;
    }

    public boolean getUsedSkip() {
        return this.usedSkip;
    }

    public void setRowsRemoved(int rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }

    public int getRowsRemoved() {
        return this.rowsRemoved;
    }

    public void setTSpin(boolean performedTSpin) {
        this.performedTSpin = performedTSpin;
    }

    public boolean getTSpin() {
        return this.performedTSpin;
    }

    public void setFieldCleared(boolean isFieldCleared) {
        this.fieldCleared = isFieldCleared;
    }

    public MoveType getCurrentMove() {
        return this.currentMove;
    }

    public void setCurrentMove(MoveType currentMove) {
        this.currentMove = currentMove;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getOpponentPlayerId() {
        return 2 - (this.playerId + 1);
    }

    public Shape getCurrentShape() {
        return this.currentShape;
    }

    public void setCurrentShape(Shape currentShape) {
        this.currentShape = currentShape;
    }
}
