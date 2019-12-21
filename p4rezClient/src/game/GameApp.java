package game;

import game.controller.GameController;
import game.model.GameModel;
import game.model.GridModel;
import game.model.PlayerModel;
import game.server.GameServer;
import game.view.GameView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameApp extends Application {
	private Stage fenetre;
	private Scene matchmaking;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		this.fenetre = primaryStage;
		this.launchMatchmaking();
	}
	
	//display "select server & param player" window
	public void launchMatchmaking() throws Exception {
		FXMLLoader loader = new FXMLLoader();
        
		loader.setLocation(GameApp.class.getResource("MatchmakingView.fxml"));
        
		VBox matchmakingView = (VBox) loader.load();
        Scene scene = new Scene(matchmakingView);
		
        MatchmakingController matchmakingController = loader.getController();
		matchmakingController.setGameApp(this);
		
        this.fenetre.setResizable(false);
		this.fenetre.setScene(scene);
		this.fenetre.setTitle("matchmaking");
		this.fenetre.show();
		this.matchmaking = scene;
	}
	
	//display game window
	public void launchGame(Color color, String name, String server_addr) throws Exception {
		GameServer server;
		server = new GameServer(server_addr);
		
		PlayerModel j1 = new PlayerModel(name, color); //you index 0
		PlayerModel j2 = new PlayerModel(); //opponent index 1
		PlayerModel[] tab_j = {j1, j2};
		
		GameModel model = new GameModel(tab_j);
		GameController controller = new GameController(this, model, server);
		
		GameView view = new GameView(model, controller);
        Scene scene = new Scene(view, view.getWdth(), view.getHght());
        
		this.fenetre.setTitle("Puissance" + GridModel.NB_TKN_WIN);
        this.fenetre.setScene(scene);
	}
	
	public Stage getFenetre() {
		return this.fenetre;
	}

	public Scene getMatchmaking() {
		return matchmaking;
	}
}