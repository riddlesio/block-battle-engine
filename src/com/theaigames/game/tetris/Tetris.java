package com.theaigames.game.tetris;

import java.util.ArrayList;
import java.util.List;

import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.AbstractGame;
import com.theaigames.game.tetris.field.Field;

public class Tetris extends AbstractGame {
	
	private final int TIMEBANK_MAX = 10000;
	private final int TIME_PER_MOVE = 500;
	private final int FIELD_WIDTH = 10;
	private final int FIELD_HEIGHT = 20;
	
	private List<Player> players;

	@Override
	public void setupGame(ArrayList<IOPlayer> ioPlayers) throws Exception {
		
		System.out.println("Setting up game...");
		
		// create all the players and everything they need
		this.players = new ArrayList<Player>();
		for(int i=0; i<ioPlayers.size(); i++) {
			
			// create the playing field
			Field field = new Field(FIELD_WIDTH, FIELD_HEIGHT);
			
			// create the player
			String playerName = String.format("player%d", i+1);
			Player player = new Player(playerName, ioPlayers.get(i), TIMEBANK_MAX, TIME_PER_MOVE, field);
			this.players.add(player);
			
			// send the settings
			sendSettings(player);
		}
		
		// create the processor
		super.processor = new Processor(this.players);
	}

	@Override
	public void sendSettings(Player player) {
		player.sendSetting("timebank", TIMEBANK_MAX);
		player.sendSetting("time_per_move", TIME_PER_MOVE);
		player.sendSetting("field_width", FIELD_WIDTH);
		player.sendSetting("field_height", FIELD_HEIGHT);
	}

	@Override
	protected void runEngine() throws Exception {
		super.engine.setLogic(this);
		super.engine.start();
	}
	
	public static void main(String args[]) throws Exception
	{
		Tetris game = new Tetris();
		
		game.setupEngine(args);
		game.runEngine();
	}
}
