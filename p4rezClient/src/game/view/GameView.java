package game.view;

import game.controller.GameController;
import game.model.GameModel;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GameView extends Pane {
	public static final int MARGIN = 5;

	private final GameModel gameModel;
	private final GameController gameController;
    private final GridView gridView;
    
    private Pane infoZone;
    private Label info;
    private Circle infoToken;
    private Button menu;
	private Button replay;
    
    public GameView(GameModel gameModel, GameController gameController) {
        this.gameModel = gameModel;
        this.gridView = new GridView(gameModel.getGridModel(), gameController.getGridController());
        this.gameController = gameController;
        
        this.getChildren().add(this.gridView);
        init();
        
        //listen to event fires by GameModel
        gameModel.addObserver((o, arg) -> {
        	int tab[] = (int[]) arg;
        	switch(tab[0]) {
        		case 0:
        			infoToken.setVisible(true);
            		changePlayer(tab[1]);
        			break;
        		case 1:
        			changePlayer(tab[1]);
        			break;
        		case 2:
        			endGame();
        			break;
        		case 3:
        			clientQuit();
        			break;
        		case 4:
        			serverLost();
        			break;
        		case 5:
        			waitReplay();
        			break;
        		case 6:
        			reset();
        			break;
        	}
        });
    }
    
    public void init() {
    	int sz = GridView.TOKEN_SZ;
    	
    	this.infoZone = createZone(sz);
    	this.info = createInfoText(sz);
    	this.info.setText("attente adversaire");
    	
    	this.infoZone.getChildren().addAll(this.info);
    	this.getChildren().add(this.infoZone);
    	
        this.gridView.initTokenEntries();
        this.gridView.hideTokenEntries();
        
        this.infoToken = createAnimToken(GridView.TOKEN_SZ);
        this.infoToken.setVisible(false);
		this.infoZone.getChildren().addAll(this.infoToken);
		
        this.createButtonsEndGame();
    }

    public Pane createZone(int sz) {
    	Pane zone = new Pane();
    	zone.setTranslateY(gridView.getHght());
    	
    	Rectangle r = new Rectangle(gridView.getWdth(), sz + MARGIN * 2);
    	r.setFill(Color.LIGHTBLUE);
    	
    	zone.getChildren().add(r);
    	return zone;
    }
    
    public Label createInfoText(int sz) {
    	Label txt = new Label();
    	txt.setFont(Font.font("Avenir Heavy", sz / 2));
    	txt.setTextFill(Color.BLACK);
    	txt.layoutXProperty().bind(infoZone.widthProperty().subtract(txt.widthProperty()).divide(2));
    	txt.layoutYProperty().bind(infoZone.heightProperty().subtract(txt.heightProperty()).divide(2));
    	return txt;
    }
    
    public Circle createAnimToken(int sz) {
    	Circle jeton = new Circle(sz/2);
        jeton.setStroke(Color.DARKGRAY);
        jeton.setCenterX(sz/2 + MARGIN);
        jeton.setCenterY(sz/2 + MARGIN);
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), jeton);
        ft.setFromValue(1.0);
        ft.setToValue(0.7);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
        
        return jeton;
    }
    
    public void createButtonsEndGame() {
    	this.menu = new Button("Menu");
    	this.replay = new Button("Replay");
    	
    	menu.setStyle("-fx-font: "+ GridView.TOKEN_SZ / 4 +" 'Avenir Heavy'; -fx-text-fill: white; -fx-base: #E83916;");
    	menu.layoutYProperty().bind(this.infoZone.heightProperty().subtract(menu.heightProperty()).divide(2));
    	menu.setPrefWidth(100);    	
    	menu.setTranslateX(MARGIN);
    	menu.setOnAction(e -> {
    	    this.gameController.menu();
    	});
    	
    	replay.setStyle("-fx-font: "+ GridView.TOKEN_SZ / 4 +" 'Avenir Heavy'; -fx-text-fill: white; -fx-base: #15B277;");
    	replay.layoutYProperty().bind(this.infoZone.heightProperty().subtract(replay.heightProperty()).divide(2));
    	replay.setTranslateX(this.getWdth() - 100 - MARGIN);
    	replay.setPrefWidth(100);
    	replay.setOnAction(e -> {
    	    this.gameController.replay();
    	});
    	
    	this.menu.setVisible(false);
    	this.replay.setVisible(false);
    	
    	this.infoZone.getChildren().addAll(menu, replay);
    }
    

    public int getHght() {
    	return gridView.getHght() + GridView.TOKEN_SZ + MARGIN * 2;
    }
    
    public int getWdth() {
    	return gridView.getWdth();
    }
    
    //functions indirectly called by server 
    //  
    public void changePlayer(int id_player) {
    	int x = 0;
    	
    	if(id_player == 0) {
    		this.info.setText("your turn " + this.gameModel.getPlayers()[0].getName());
        	this.gridView.showTokenEntries();
    	} else {
    		x = getWdth() - MARGIN * 2 - GridView.TOKEN_SZ;
    		this.info.setText(this.gameModel.getPlayers()[1].getName() + " turn");
    		this.gridView.hideTokenEntries();
    	}

		TranslateTransition anim = new TranslateTransition(Duration.seconds(0.2), this.infoToken);
		anim.setToX(x);
		anim.play();
    	
    	this.infoToken.setFill(gameModel.getPlayerColor());
    }
    
    public void endGame() {
    	this.infoToken.setVisible(false);
    	this.gridView.hideTokenEntries();
    	
		int winner = gameModel.getWinner();
		
    	if(winner == 2) {
    		this.info.setText("draw");
    	} else if(winner == 1) {
    		this.info.setText(this.gameModel.getPlayers()[0].getName() + "(you) win");
		} else if(winner == 0) {
			this.info.setText(this.gameModel.getPlayers()[1].getName() + " win");
		}
    	
    	this.menu.setVisible(true);
    	this.replay.setVisible(true);
    }
    

    public void clientQuit() {
    	this.infoToken.setVisible(false);
    	this.gridView.hideTokenEntries();
    	this.info.setText(this.gameModel.getPlayers()[1].getName() + " quit");
    	this.menu.setVisible(true);
    	this.replay.setVisible(false);
    }
    
    public void serverLost() {
    	this.infoToken.setVisible(false);
    	this.menu.setVisible(true);
    	this.gridView.hideTokenEntries();
    	this.info.setText("server lost");
    	this.menu.setVisible(true);
    	this.replay.setVisible(false);
    }
    
    public void waitReplay(){
    	this.replay.setVisible(false);
    	this.info.setText("wait " + this.gameModel.getPlayers()[1].getName());    	
    }
    
    public void reset(){
    	this.menu.setVisible(false);
    	this.info.setText("");
    }
}