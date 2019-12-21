package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DiscoverServer implements Runnable {
	public static final String DISCOVER_ADDR = "255.255.255.255";
	public static final int PORT = 8080;
	public static final int REFRESH_DELAY = 2000;
	public static final int SOCKET_TIMEOUT = 1000;
	public static final int BUF_SZ = 32;
	
	private DatagramSocket udpSocket;
	private volatile boolean running;
	
    private ObservableList<String> foundServers;
    private Map<String, String> mapIPHostname;

	public DiscoverServer() throws Exception {
		this.udpSocket = new DatagramSocket();
		this.udpSocket.setSoTimeout(SOCKET_TIMEOUT);
		this.foundServers = FXCollections.observableArrayList();
		this.mapIPHostname = new HashMap<String, String>();
    }
	
	public ObservableList<String> getFoundServers() {
		return foundServers;
	}
    
    public void terminate() {
        running = false;
    }

    public void run() {
    	this.running = true;
    	long time;
    	boolean sock_tm;
		
    	InetAddress brdAddr;
		try {
			brdAddr = InetAddress.getByName(DISCOVER_ADDR);
		} catch (UnknownHostException e1) {
			return;
		}
    	
		String brd_msg = "";
		byte[] brd_bfr = brd_msg.getBytes();
		DatagramPacket brd_packet = new DatagramPacket(brd_bfr, brd_bfr.length, brdAddr, PORT);
		
		byte[] rcv_bfr = new byte[BUF_SZ];
		DatagramPacket rcv_packet = new DatagramPacket(rcv_bfr, rcv_bfr.length);

		time = 0;
		ArrayList<String> tmp_found = new ArrayList<String>();
		
		//daemon loop
        while (this.running) {
        	//Refresh displayed servers every x seconds
        	if(System.currentTimeMillis() - time > REFRESH_DELAY){
        		//Add only new found servers
        		tmp_found.forEach((value)->{
        			if(!this.foundServers.contains(value))
        				this.foundServers.add(value);
        		});
        		
        		//Remove lost servers
        		Iterator<String> i;
        		for(i = this.foundServers.iterator(); i.hasNext();) {
					String s = i.next();
					if(!tmp_found.contains(s))
						i.remove();
        		}
        		
        		tmp_found.clear();
        		
        		//broadcast discover request
        		try {
					this.udpSocket.send(brd_packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        		time = System.currentTimeMillis();
           	}
        	
        	sock_tm = false;
        	
        	try {
				this.udpSocket.receive(rcv_packet);
			} catch (SocketTimeoutException tme) {
				sock_tm = true;
		    } catch (IOException e) {
		    	this.running = false;
			}
        	
        	//If found servers, save them temporarily 
        	if(!sock_tm) {
        		byte b[] = rcv_packet.getData();
        		String hostname = new String(b, 0, rcv_packet.getLength());
        		String ipaddr = rcv_packet.getAddress().getHostAddress().toString();
        		
        		if(hostname.length() > 0) {
        			tmp_found.add(hostname);
        			this.mapIPHostname.putIfAbsent(hostname, ipaddr);
        		} else {
        			tmp_found.add(ipaddr);
        			this.mapIPHostname.putIfAbsent(ipaddr, ipaddr);
        		}        		
        	}
        }
        
        this.udpSocket.close();
    }
    
    public String resolveHostname(String name) {
    	return this.mapIPHostname.get(name);
    }
}