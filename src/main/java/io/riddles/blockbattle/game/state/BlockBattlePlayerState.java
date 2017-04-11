package io.riddles.blockbattle.game.state;

import io.riddles.blockbattle.game.data.BlockBattleBoard;
import io.riddles.blockbattle.game.data.Shape;
import io.riddles.blockbattle.game.move.BlockBattleMove;
import io.riddles.javainterface.game.data.Board;
import io.riddles.javainterface.game.state.AbstractPlayerState;

public class BlockBattlePlayerState extends AbstractPlayerState<BlockBattleMove> {

    private boolean usedSkip;
    private int rowPoints;
    private int combo;
    private int rowsRemoved;
    private int skips;
    private boolean performedTSpin;
    private boolean fieldCleared;
    private int points;
    private Shape currentShape;


    private BlockBattleBoard board;

    public BlockBattlePlayerState(int playerId) {
        super(playerId);
        this.rowPoints = 0;
        this.combo = 0;
        this.skips = 0;
        this.performedTSpin = false;
        this.fieldCleared = false;
        this.usedSkip = false;
        this.points = 0;
    }



    public BlockBattlePlayerState(BlockBattlePlayerState ps) {
        super(ps.getPlayerId());
        this.setBoard(new BlockBattleBoard(ps.board));
        this.setRowPoints(ps.getRowPoints());
        this.setCombo(ps.getCombo());
        this.setSkips(ps.getSkips());
        this.setPerformedTSpin(ps.getPerformedTSpin());
        this.setFieldCleared(ps.getFieldCleared());
        this.setUsedSkip(ps.getUsedSkip());
        this.setPoints(ps.getPoints());
        this.currentShape = ps.getCurrentShape().clone();
    }

    public int getPlayerId() { return this.playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public BlockBattleBoard getBoard() { return this.board; }
    public void setBoard(BlockBattleBoard b) {
        this.board = b;
    }


    public void setRowPoints(int points) {
        this.rowPoints = points;
    }
    public void addRowPoints(int points) {
        this.rowPoints += points;
    }

    public boolean getTSpin() {
        return this.performedTSpin;
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

    public void setPerformedTSpin(boolean performedTSpin) { this.performedTSpin = performedTSpin; }
    public boolean getPerformedTSpin() {
        return this.performedTSpin;
    }

    public boolean getfieldCleared() {
        return this.fieldCleared;
    }

    public void setRowsRemoved(int rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }
    public int getRowsRemoved() {
        return this.rowsRemoved;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    public int getPoints() { return this.points; }

    public void setTSpin(boolean performedTSpin) {
        this.performedTSpin = performedTSpin;
    }

    public void setFieldCleared(boolean isFieldCleared) {
        this.fieldCleared = isFieldCleared;
    }


    public Shape getCurrentShape() { return this.currentShape; }
    public void setCurrentShape(Shape currentShape) { this.currentShape = currentShape; }
}
