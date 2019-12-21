package game.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import game.controller.GameController;
import javafx.application.Platform;

public class GameServer implements Runnable {
	public static final int PORT = 8080;
	
	private String addr;
	private Socket tcp_socket;
	private DataInputStream in;
	private DataOutputStream out;

	private volatile boolean running;
	private volatile boolean is_sending;
	
	private GameController gameController;
	
	public GameServer(String addr) throws Exception {
		this.addr = addr;
		this.running = true;
		this.is_sending = false;
		
		this.tcp_socket = new Socket(this.addr, PORT);
		this.in = new DataInputStream(this.tcp_socket.getInputStream());
		this.out = new DataOutputStream(this.tcp_socket.getOutputStream());
	}
	
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}
	
	//daemon loop
	public void run() {
		this.sendName();
		this.receive();
		this.sendColor();
		
		while(this.running) {
			this.receive();
		}
	}
	
	public void terminate() {
		try {
			this.in.close();
			this.out.close();
			this.tcp_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        running = false;
    }
		
	public boolean snd(byte[] b) {
		if(this.is_sending)
			return false;
		
		this.is_sending = true;
		
		try {
			this.out.write(b);
		} catch (IOException e) {
			this.connexionError();
			return false;
		}
		
		return true;
	}
	
	public byte[] rcv() {
		byte b[] = new byte[Packet.PCKT_MAX_SZ];
		
		try {
			if(this.in.read(b) == -1) {
				this.connexionError();
				return null;
			}
		} catch (IOException e) {
			this.connexionError();
			return null;
		}
		
		if(this.is_sending)
			this.is_sending = false;
		
		return b;
	}
	
	public void connexionError() {
		//Platform.runLater : need to update a GUI component from a non-GUI thread
		Platform.runLater(new Runnable(){
			public void run() {
				gameController.serverLost();
			}
		});
		this.terminate();
	}
	
	public void receive() {
		byte b[] = this.rcv();
		Packet.treatPacket(this.gameController, b, 0);
	}
	
	public void sendPlaceToken(int c) {
		Object[] data = {c};
		this.snd(Packet.createPacket(Packet.PCKT_PLACEX, data));
	}
	
	public void sendName() {
		Object[] data = {this.gameController.getGameModel().getPlayers()[0].getName()};
		this.snd(Packet.createPacket(Packet.PCKT_NAME, data));
	}
	
	public void sendColor() {
		this.snd(Packet.createPacket(Packet.PCKT_COLOR, this.gameController.getGameModel().getPlayers()[0].getColorRGB()));
	}
	
	public void sendReplay() {
		this.snd(Packet.createPacket(Packet.PCKT_REPLAY, null));
	}
}
