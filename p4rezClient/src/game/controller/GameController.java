package game.controller;

import game.GameApp;
import game.model.GameModel;
import game.model.GridModel;
import game.server.GameServer;

public class GameController {
	private final GameApp app;
	
	private final GameModel gameModel;
	private final GridController gridController;
	
	private final GameServer gameServer;

	private Thread server_thread;
    
    public GameController(GameApp app, GameModel gameModel, GameServer gameServer) {
    	this.app = app;
    	this.gameModel = gameModel;
        this.gridController = new GridController(gameModel.getGridModel(), this);
        
        //link game server to game
        this.gameServer = gameServer;
        this.gameServer.setGameController(this);
        
        //run it in a thread
        this.server_thread = new Thread(this.gameServer);
        this.server_thread.setDaemon(true);
        this.server_thread.start(); 
    }

    public GridController getGridController() {
        return gridController;
    }

	public GameModel getGameModel() {
		return gameModel;
	}

    public GameServer getGameServer() {
		return gameServer;
	}
	
    //player actions
    //
    public void requirePlayAt(int c) {
    	this.gameServer.sendPlaceToken(c);
    }
    
	public void replay() {
		this.gameServer.sendReplay();
		this.gameModel.setWaitReplay();
	}
	
	public void menu() {
		this.gameServer.terminate();
		
		try {
			this.app.getFenetre().setScene(this.app.getMatchmaking());
			this.app.getFenetre().setTitle("matchmaking");
			this.app.getFenetre().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	//server actions
	//
    public void initFirstPlayer(int id_player) {
    	this.gameModel.setIdPlayer(id_player);
    }
    
	public void playAt(int l, int c) {
		this.gridController.getGridModel().setGridAt(l, c, this.gameModel.getIdPlayer());
		this.gameModel.setNextPlayer();
	}
	
	public void win(int winner, int tkn[]) {
		for(int i = 0; i < GridModel.NB_TKN_WIN; i++)
			this.gridController.getGridModel().setGridAt(tkn[i*2], tkn[i*2+1], 10); //highlight concerned tokens
		
		this.gameModel.setWinner(winner);
	}
	
	public void draw() {
		this.gameModel.setWinner(2);
	}
	
	public void clientQuit() {
		this.gameModel.setClientQuit();
		this.gameServer.terminate();
	}
	
	public void serverLost() {	
		if(this.gameModel.getWinner() == -1) {
			this.gameModel.setServerLost();
			this.gameServer.terminate();
		}
	}
	
	public void resetGame() {
		this.gridController.resetGrid();
		this.gameModel.resetGame();
	}
}
