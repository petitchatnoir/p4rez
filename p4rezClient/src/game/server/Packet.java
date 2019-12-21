package game.server;

import game.controller.GameController;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public abstract class Packet {
	//PCKT_TYPE
	public static final int PCKT_INI = 0;
	public static final int PCKT_COLOR = 1;
	public static final int PCKT_NAME = 2;
	public static final int PCKT_PLACEX = 3;
	public static final int PCKT_PLACEXY = 4;
	public static final int PCKT_WIN = 5;
	public static final int PCKT_ERR = 6;
	public static final int PCKT_DRAW = 7;
	public static final int PCKT_CLIENT_QUIT = 8;
	public static final int PCKT_REPLAY = 9;
	
	public static final int PCKT_MAX_SZ = 64;
	public static final char PCKT_DELIM_TKN = '$';
	
	
	//create byte[] buffer containing data to send, char/int cast to byte
	//buffer = [PCKT_DELIM_TKN, PCKT_TYPE, (byte)data, PCKT_DELIM_TKN]
	public static byte[] createPacket(int type, Object data[]) {
		byte[] b = null;
		int i;
		
		switch(type) {
			case PCKT_NAME:
				String name = data[0].toString();
				b = new byte[name.length() + 3];
				b[0] = (byte)PCKT_DELIM_TKN;
				b[1] = (byte)type;
				for(i = 0; i < name.length(); i++) {
					b[i + 2] = (byte)name.charAt(i);
				}
				b[i + 2] = (byte)PCKT_DELIM_TKN;
				break;
			case PCKT_COLOR:
				b = new byte[6];
				b[0] = (byte)PCKT_DELIM_TKN;
				b[1] = (byte)type;
				b[2] = (byte)(int)data[0];
				b[3] = (byte)(int)data[1];
				b[4] = (byte)(int)data[2];
				b[5] = (byte)PCKT_DELIM_TKN;
				break;
			case PCKT_PLACEX:
				b = new byte[4];
				b[0] = (byte)PCKT_DELIM_TKN;
				b[1] = (byte)type;
				b[2] = (byte)(int)data[0];
				b[3] = (byte)PCKT_DELIM_TKN;
				break;
			case PCKT_REPLAY:
				b = new byte[3];
				b[0] = (byte)PCKT_DELIM_TKN;
				b[1] = (byte)type;
				b[2] = (byte)PCKT_DELIM_TKN;
				break;
		}
		
		return b;
	}
	
	//find recursively packets contained in buffer, perform requested actions on related GameController
	//buffer can contain [?,?,Packet,?,?,Packet,?]
	public static void treatPacket(GameController gameController, byte b[], int offset) {
		if(b == null || b[offset] != PCKT_DELIM_TKN)
			return;
		
		Platform.runLater(new Runnable(){
			public void run() {
				int i = offset + 2; //data start index 
				switch((int)b[offset + 1]) {
					case PCKT_INI:
						gameController.initFirstPlayer((int)b[i]);
						break;
						
					case PCKT_PLACEXY:
						gameController.playAt((int)b[i], (int)b[i + 1]);
						break;
						
					case PCKT_DRAW:
						gameController.draw();
						break;
						
					case PCKT_CLIENT_QUIT:
						gameController.clientQuit();
						break;
						
					case PCKT_REPLAY:
						gameController.resetGame();
						break;
				
					case PCKT_NAME:
						String name = "";
						for(;(char)b[i] != PCKT_DELIM_TKN; i++)
							name += (char)b[i];
						gameController.getGameModel().getPlayers()[1].setName(name);
						break;
				
					case PCKT_WIN:
						int arr[] = new int[4*2];
						for(int j = 0; j < 8; j++) {
							arr[j] = b[j + offset + 3];
						}
						gameController.win((int)b[offset + 2], arr);
						break;
				
					case PCKT_COLOR:
						int red, green, blue;
						red = ((int)b[i] < 0) ? 255 + (int)b[i] : (int)b[i];
						i++;
						green = ((int)b[i] < 0) ? 255 + (int)b[i] : (int)b[i];
						i++;
						blue = ((int)b[i] < 0) ? 255 + (int)b[i] : (int)b[i];
						gameController.getGameModel().getPlayers()[1].setColor(Color.rgb(red, green, blue));
						break;
						
					default:
						return;
				}
			}
		});
		
		int cpt;
		
		for(cpt = offset + 1; cpt < PCKT_MAX_SZ && b[cpt] != PCKT_DELIM_TKN; cpt++);
		for(cpt++;cpt < PCKT_MAX_SZ && b[cpt] != PCKT_DELIM_TKN; cpt++);
		
		if(cpt < PCKT_MAX_SZ)
			treatPacket(gameController, b, cpt);
	}	
}
