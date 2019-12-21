package game.controller;

import game.model.GridModel;

public class GridController {
	private final GridModel gridModel;
	private final GameController gameController;
	
	public GridController(GridModel plateauModel, GameController gameController) {
		this.gridModel = plateauModel;
		this.gameController = gameController;
	}
	
	public GridModel getGridModel() {
        return gridModel;
    }
	
	public GameController getGameController() {
		return gameController;
	}
	
	public void click(int c) {
		gameController.requirePlayAt(c);
	}
	
	public void resetGrid() {
    	for(int l = 0; l < GridModel.NB_LINE; l++) {
        	for(int c = 0; c < GridModel.NB_COL; c++) {
            	if(this.gridModel.getGridAt(l, c) != -1) {
            		this.gridModel.setGridAt(l, c, -1);
            	}
            }	
        }
    }
}