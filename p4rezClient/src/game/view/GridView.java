package game.view;

import java.util.ArrayList;
import java.util.List;

import game.controller.GridController;
import game.model.GridModel;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class GridView extends Pane {
	public static final int MARGIN = 10;
	public static final int BORDER = 30;
	public static final int TOKEN_SZ = 80;
	private final Color grid_color = Color.CADETBLUE;
	
	private final GridModel gridModel;
    private final GridController gridController;
	
    private int width;
    private int height;
    
    private Pane token_entry;
    private List<Rectangle> list_entries;
    private Circle[][] tokens;
	
	public GridView(GridModel gridModel, GridController gridController) {
		this.gridModel = gridModel;
		this.gridController = gridController;
		
		this.tokens = new Circle[GridModel.NB_LINE][GridModel.NB_COL];
		this.token_entry = new Pane();
        this.getChildren().add(this.token_entry);
        this.getChildren().add(createGrid());
        
		gridModel.addObserver((o, arg) -> {
			int tab[] = (int[]) arg;
			switch(tab[2]) {
				case 10:
					winnerAnim(tab[0], tab[1]);
					break;
				case -1:
					removeToken(tab[0], tab[1]);
					break;
				default:
					insertTokenAnim(tab[0], tab[1], tab[2]);
					break;
			}
        });
    }
	
	public Shape createGrid() {
		int l = GridModel.NB_LINE;
		int c = GridModel.NB_COL;
		
		//Creation shape principale plateau (rectangle) + enregistrement taille
		this.width = c * TOKEN_SZ + (c + 1) * MARGIN + BORDER * 2;
		this.height = l * TOKEN_SZ + (l + 1) * MARGIN + BORDER * 2;
		
		Shape grille = new Rectangle(this.width, this.height);
		
		//Creations du quadrillage de trous
		for(int y = 0; y < l; y++) {
			for(int x = 0; x < c; x++) {
				//RAYON = sz/2
				Circle crcl = new Circle(TOKEN_SZ / 2);
				
				//Centrage systématique avant d'appliquer translations
				crcl.setCenterX(TOKEN_SZ / 2);
				crcl.setCenterY(TOKEN_SZ / 2);
				
				//sz + 5 -> 5px de marge entre trous
				//sz / 4 -> permet centrer quadrillage de trous dans plateau
				crcl.setTranslateX(BORDER + MARGIN + x * (TOKEN_SZ + MARGIN));
				crcl.setTranslateY(BORDER + MARGIN + y * (TOKEN_SZ + MARGIN));
				
				grille = Shape.subtract(grille, crcl);
			}
		}
		
		grille.setFill(grid_color);

		return grille;
	}
	
	public void initTokenEntries(){
		int l = GridModel.NB_LINE;
		int c = GridModel.NB_COL;
		
		//Creation differentes entrees possibles pour un jeton
		List<Rectangle> list = new ArrayList<>();
		
		for(int i = 0; i < c; i++) {
			Rectangle r = new Rectangle(TOKEN_SZ, BORDER + l * TOKEN_SZ + (l + 1) * MARGIN); //largeur, longueur
			r.setTranslateX(BORDER + MARGIN + i * (TOKEN_SZ + MARGIN)); //decalle même manière que pour trous
			
			r.setFill(Color.TRANSPARENT);
			r.setCursor(Cursor.HAND);
			
			//Illumination lors survol souris
			r.setOnMouseEntered(e -> r.setFill(Color.rgb(200,200,50,0.2)));
			r.setOnMouseExited(e -> r.setFill(Color.TRANSPARENT));
			
			//Gestion event -> UTILISATEUR JOUE
			int tmp = i; //si on passe direct i, pas content...
			r.setOnMouseClicked(e -> gridController.click(tmp));	
			
			list.add(r);
		}
		
	    this.list_entries = list;
	    this.token_entry.getChildren().addAll(list);
	}

	public void insertTokenAnim(int l, int c, int id_player) {
		Color color = gridController.getGameController().getGameModel().getPlayers()[id_player].getColor();
		
		//Creation d'un nouveau jeton
		Circle jeton = new Circle(TOKEN_SZ / 2);
		jeton.setCenterX(TOKEN_SZ / 2);
		jeton.setCenterY(TOKEN_SZ / 2);
		jeton.setFill(color);
		
		//On lache le jeton de la pane en haut
		this.token_entry.getChildren().add(jeton);
		jeton.setTranslateX(BORDER + MARGIN + c * (TOKEN_SZ + MARGIN));
		TranslateTransition anim = new TranslateTransition(Duration.seconds(0.4), jeton);
		anim.setToY(BORDER + MARGIN + l * (TOKEN_SZ + MARGIN));
		anim.play();
		
		this.tokens[l][c] = jeton;
	}
	
	public void removeToken(int l, int c) {
		this.token_entry.getChildren().remove(this.tokens[l][c]);
	}
	
	public void winnerAnim(int l, int c) {
		Circle crcl = tokens[l][c];
		FadeTransition ft = new FadeTransition(Duration.millis(500), crcl);
        ft.setFromValue(1.0);
        ft.setToValue(0.4);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
	}
	
	public void hideTokenEntries() {
		this.list_entries.forEach((r) -> {r.setVisible(false);});
	}
	
	public void showTokenEntries() {
		this.list_entries.forEach((r) -> {r.setVisible(true);});
	}
	
	public void removeTokenEntries() {
		this.getChildren().removeAll(this.list_entries);
	}
		
	public int getWdth() {
		return this.width;
	}

	public int getHght() {
		return this.height;
	}
	
	public Circle getTokens(int l, int c) {
		return this.tokens[l][c];
	}
}
