package game.model;

import java.util.Observable;

public class GridModel extends Observable {
	public static final int NB_LINE = 6;
	public static final int NB_COL = 7;
	public static final int NB_TKN_WIN = 4;
	
	private int[][] grid;
    
    public GridModel() {
    	this.grid = new int[NB_LINE][NB_COL];
    	for(int l = 0; l < NB_LINE; l++) {
        	for(int c = 0; c < NB_COL; c++) {
            	grid[l][c] = -1;
            }	
        }
    }
    
    public void setGridAt(int l, int c, int id_player) {
    	grid[l][c] = id_player;
    	setChanged();
        notifyObservers(new int[]{l, c, id_player});
    }
    
    public int getGridAt(int l, int c) {
    	return this.grid[l][c];
    }
}