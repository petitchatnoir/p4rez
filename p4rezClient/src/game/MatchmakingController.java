package game;

import java.util.Random;

import game.server.DiscoverServer;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MatchmakingController {
	public static final int NAME_MAX_SZ = 10;
	
    @FXML private TableView<String> server_table;
    @FXML private TableColumn<String, String> server_column;
    @FXML private Button select_button;
    @FXML private Text info_text;
    @FXML private TextField player_name;
    @FXML private ColorPicker player_color;
    
    private GameApp gameApp;
    
    private DiscoverServer dscvrRunner;
    private Thread dscvrThread;
 
	public MatchmakingController() throws Exception {
		this.dscvrRunner = new DiscoverServer();
		this.dscvrThread = new Thread(this.dscvrRunner);
		this.dscvrThread.setDaemon(true);
		this.dscvrThread.start();
	}
	
	@FXML
    private void initialize() {
		Random rand = new Random();
		
		this.server_column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
		this.server_table.setPlaceholder(new Label("No server found"));
		this.server_table.setItems(this.dscvrRunner.getFoundServers());

		this.player_name.setText("player" + rand.nextInt(421));
        this.player_color.setValue(Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        
        this.player_name.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (player_name.getText().length() > NAME_MAX_SZ) {
	                String s = player_name.getText().substring(0, NAME_MAX_SZ);
	                player_name.setText(s);
	            }
			}
	    });
        
        this.select_button.setDisable(true);
		this.info_text.setText("");
		
        this.select_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		    	if(server_table.getSelectionModel().getSelectedItem() != null) {
		    		selectServer(dscvrRunner.resolveHostname(server_table.getSelectionModel().getSelectedItem()));
		    	}
		    }
		});
        
		this.server_table.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		        if(server_table.getSelectionModel().getSelectedItem() != null) {
		        	select_button.setDisable(false);
		        	
		        	if(event.getClickCount() == 2) {
			        	selectServer(dscvrRunner.resolveHostname(server_table.getSelectionModel().getSelectedItem()));
			        }
		        }
		    }
		});
    }
		
    public void setGameApp(GameApp gameApp) {
        this.gameApp = gameApp;
    }
    
    public void selectServer(String addr) {
    	info_text.setText("");
    	try {
			this.gameApp.launchGame(this.player_color.getValue(), this.player_name.getText(), addr);
		} catch (Exception e) {
			this.info_text.setText("impossible de se connecter au serveur");
			return;
		}
    	
    	this.dscvrRunner.terminate();
    	while(this.dscvrThread.isAlive());
    }
}
