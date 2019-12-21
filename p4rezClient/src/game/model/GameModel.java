package game.model;

import java.util.Observable;

import javafx.scene.paint.Color;

public class GameModel extends Observable {
	private final GridModel gridModel;
	private PlayerModel[] players;

	private int id_player;
	private int winner;
	private boolean clientQuit;
	
    public GameModel(PlayerModel players[]) {
    	this.gridModel = new GridModel();
        this.players = players;
        this.id_player = -1;
        this.winner = -1;
        this.clientQuit = false;
    }

    public GridModel getGridModel() {
        return gridModel;
    }
    
	public PlayerModel[] getPlayers() {
		return players;
	}

	public int getIdPlayer() {
		return id_player;
	}

	public int getWinner() {
		return winner;
	}
	
	public Color getPlayerColor() {
		return this.players[this.id_player].getColor();
	}
	
	//functions below fires event to GameView
	//
	public void setIdPlayer(int id_player) {
		this.id_player = id_player;
		
		setChanged();
        notifyObservers(new int[]{0, this.id_player});
	}
	
	public void setNextPlayer() {
		this.id_player = (this.id_player + 1) % 2;
		
        setChanged();
        notifyObservers(new int[]{1, this.id_player});
	}
	
	public void setWinner(int winner) {
		this.winner = winner;
		
		setChanged();
        notifyObservers(new int[]{2});
	}
	
	public void setClientQuit() {
		this.clientQuit = true;
		
		setChanged();
        notifyObservers(new int[]{3});
	}
	
	public void setServerLost() {
		if(!this.clientQuit) {
			setChanged();
	        notifyObservers(new int[]{4});
		}
	}
	
	public void setWaitReplay() {
		setChanged();
        notifyObservers(new int[]{5});
	}
	
	public void resetGame() {
		setChanged();
        notifyObservers(new int[]{6});
	}
}